using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;
using Vuforia;
using System.Threading;
using ZXing;
using ZXing.QrCode;
using ZXing.Common;

/* QR Code Reader - Created by Daniel Rubinstein 2018
 * Features:
 * Reads QR code with url to JSON for event.
 * Creates a user defined target from the url
 * Draws an AR object.
 */

//Object used to parse from JSON to actual label text.

[Serializable]
public class TestObject
{
    public string origin;
}

[Serializable]
public class RoomObject
{
    public int id;
    public int parentEvent_id;
    public string name;
    public int floor;
    public double location_lng;
    public double location_lat;
    public string room;
    public string location_building;
    public string desc_show_in_app;
    public string start_time;
    public string end_time;
    public bool desc_has_url;
    public string desc_url;
    public string desc_show_in_website;
    public string action_title;
    public bool action_has_action;
    public string action_desc;
    public bool action_has_url;
    public string action_url;
    public bool isVisible;

}

[AddComponentMenu("System/QRScanner")]
public class QRScanner : MonoBehaviour
{
    private bool cameraInitialized;
    private BarcodeReader barCodeReader;

    public bool scanThreadDone = false;
    Thread scanThread;
    string url;
    string urlText;

    public bool scanning = false;
    bool urlFound = false;


    GameObject targetBuilder;
    GameObject label;

    int attemptCount = 0;

    void Start()
    {
        if (SupportedDevices.IsGroundPlaneSupported())
        {
            enabled = false;
        }

        //Setup the camera / barcode reader.
        barCodeReader = new BarcodeReader();
        StartCoroutine(InitializeCamera());

        //Find the GameObjects to be used.
        targetBuilder = GameObject.FindWithTag("targetBuilder");
        label = GameObject.FindWithTag("label");

        //Setup Auto Focus on the Vuforia camera. 
        VuforiaARController.Instance.RegisterVuforiaStartedCallback(OnVuforiaStarted);
        VuforiaARController.Instance.RegisterOnPauseCallback(OnPaused);

        //test();

    }

    //Auto Focus Code
    private void OnVuforiaStarted()
    {
        setFocus();
    }

    public void setFocus()
    {
        var focusMode = CameraDevice.Instance.SetFocusMode(
                            CameraDevice.FocusMode.FOCUS_MODE_CONTINUOUSAUTO);

        //For clean the debug info
        //Debug.Log("Focus mode:" + focusMode);

        if (!focusMode)
        {
            focusMode = CameraDevice.Instance.SetFocusMode(
                CameraDevice.FocusMode.FOCUS_MODE_TRIGGERAUTO);

            if (!focusMode)
            {
                focusMode = CameraDevice.Instance.SetFocusMode(
                    CameraDevice.FocusMode.FOCUS_MODE_MACRO);
                if (!focusMode)
                {
                    focusMode = CameraDevice.Instance.SetFocusMode(
                        CameraDevice.FocusMode.FOCUS_MODE_INFINITY);
                    if (!focusMode)
                    {
                        focusMode = CameraDevice.Instance.SetFocusMode(
                            CameraDevice.FocusMode.FOCUS_MODE_NORMAL);
                    }
                }
            }
        }
    }

    //Auto Focus Code
    private void OnPaused(bool paused)
    {
        if (!paused) // resumed
        {
            // Set again autofocus mode when app is resumed
            CameraDevice.Instance.SetFocusMode(
                CameraDevice.FocusMode.FOCUS_MODE_CONTINUOUSAUTO);
        }
    }

    //Initializes Camera
    private IEnumerator InitializeCamera()
    {
        // Waiting a little seem to avoid the Vuforia's crashes.
        yield return new WaitForSeconds(3f);

        //Read everything in GRAYSCALE as it makes the code significantly faster. 
        //Trying to read in colour results in a laggy camera.
        var isFrameFormatSet = CameraDevice.Instance.SetFrameFormat(Image.PIXEL_FORMAT.GRAYSCALE, true);
        //For clean the debug info
        //Debug.Log(String.Format("FormatSet : {0}", isFrameFormatSet));

        // Force autofocus. Alternative option to the code above.

        /*var isAutoFocus = CameraDevice.Instance.SetFocusMode(CameraDevice.FocusMode.FOCUS_MODE_TRIGGERAUTO);
        if (!isAutoFocus)
        {
           CameraDevice.Instance.SetFocusMode(CameraDevice.FocusMode.FOCUS_MODE_NORMAL);
        }
          Debug.Log(String.Format("AutoFocus : {0}", isAutoFocus));*/
        cameraInitialized = true;
    }

    //Thread for scanning each frame. This is done on a seperate thread to redue camera lag.
    private void scanComputationThread()
    {
        if (cameraInitialized && scanning)
        {
            try
            {
                var cameraFeed = CameraDevice.Instance.GetCameraImage(Image.PIXEL_FORMAT.GRAYSCALE);
                if (cameraFeed == null)
                {
                }
                var data = barCodeReader.Decode(cameraFeed.Pixels, cameraFeed.BufferWidth, cameraFeed.BufferHeight, RGBLuminanceSource.BitmapFormat.Unknown);
                if (data != null)
                {
                    // QRCode detected.
                    //For clean the debug info
                    //Debug.Log("URL Found: " + data.Text);


                    url = data.Text;

                    scanning = false;
                    urlFound = true;
                    data = null;  // clear data
                }
                else
                {
                    //For clean the debug info
                    //Debug.Log("No QR code detected !");
                    url = null;
                    attemptCount++;

                    if (attemptCount == 100)
                    {
                        //For clean the debug info
                        //Debug.Log("No QR Code detected, must re-scan");
                        scanThreadDone = false;
                        scanning = false;
                    }
                }
            }
            catch (Exception e)
            {
                Debug.LogError(e.Message);
            }
        }

        scanThreadDone = true;
    }

    //Function to be called with "true" to start scan. Used mostly in MacQuest to start scanning.
    void scanQR(string m)
    {
        attemptCount = 0;
        scanning = Boolean.Parse(m);
        scanThreadDone = Boolean.Parse(m);
    }

    //Scan each thread if scanning is needed. Look to revise the scanning method by not using 2 bools Just leave scanThreadDone as false when you find a QR code.
    //Also draw AR object if qr code found.
    private void Update()
    {

        if (scanThreadDone)
        {
            scanThreadDone = false;
            scanThread = new Thread(new ThreadStart(scanComputationThread));
            scanThread.Start();
        }

        if (urlFound)
        {
            urlFound = false;
            StartCoroutine(readURL());
        }
    }

    //Reads a specific URL.
    public void readFromExternalUrl(string u)
    {

        url = u;

        if (u == "")
        {
            return;
        }
        else
        {
            scanning = false;
            urlFound = false;

            StartCoroutine(readFromURL());
        }


    }

    //Read QR code, parse JSON from url, and draw object.
    IEnumerator readURL()
    {
        // Makes get request.
        string auth = "access-name:wiserlab1835";
        auth = System.Convert.ToBase64String(System.Text.Encoding.GetEncoding("ISO-8859-1").GetBytes(auth));
        auth = "Basic " + auth;

        var headers = new Hashtable();
        headers.Add("Authorization", auth);

        using (WWW www = new WWW(url, null, headers))
        {
            //Waits for the response from the request.
            yield return www;
            try
            {

                //For clean the debug info
                //Debug.Log("Text from QR: " + www.text);

                //Setup the label text to be displayed.
                RoomObject labelObj = JsonUtility.FromJson<RoomObject>(www.text);
                if (labelObj != null)
                {
                    String urlTitle = labelObj.name;
                    urlText = "<u><b>Info:</b></u>" + labelObj.desc_show_in_app + "\n" +
                              "<u><b>Building:</b></u>" + labelObj.location_building + " " + labelObj.room + "\n" +
                              "<u><b>Floor:</b></u>" + labelObj.floor + "\n";

                    //Create the label object.
                    label.GetComponent<Messages>().setLabels(urlTitle, urlText); // change this text
                    targetBuilder.GetComponent<UDTEventHandler>().BuildNewTarget();
                }
                else
                {
                    String urlTitle = "Error";
                    urlText = "<u><b>Info:</b></u>" + "Please check the network or the QR code." + "\n" +
                              "<u><b>Building:</b></u>" + "None" + "\n" +
                              "<u><b>Floor:</b></u>" + "None" + "\n";
                    label.GetComponent<Messages>().setLabels(urlTitle, urlText); // change this text
                    targetBuilder.GetComponent<UDTEventHandler>().BuildNewTarget();
                }
            }
            catch (System.Exception e)
            {
                String urlTitle = "Error";
                urlText = "<u><b>Info:</b></u>" + "Please check the network or the QR code." + "\n" +
                          "<u><b>Building:</b></u>" + "None" + "\n" +
                          "<u><b>Floor:</b></u>" + "None" + "\n";
                label.GetComponent<Messages>().setLabels(urlTitle, urlText); // change this text
                targetBuilder.GetComponent<UDTEventHandler>().BuildNewTarget();
            }



        }


    }

    List<GameObject> labelList = new List<GameObject>();

    IEnumerator readFromURL()
    {
        // Makes get request.
        string auth = "access-name:wiserlab1835";
        auth = System.Convert.ToBase64String(System.Text.Encoding.GetEncoding("ISO-8859-1").GetBytes(auth));
        auth = "Basic " + auth;

        var headers = new Hashtable();
        headers.Add("Authorization", auth);



        bool readSuccessful = true;

        using (WWW www = new WWW(url, null, headers))
        {
            //Waits for the response from the request.
            yield return www;

            try{

            if (www != null)
            {
                //For clean the debug info
                //Debug.Log("Text from QR: " + www.text);



                //Setup the label text to be displayed.
                RoomObject labelObj = JsonUtility.FromJson<RoomObject>(www.text);
                if (labelObj != null)
                {
                    String urlTitle = labelObj.name;

                    var toolsLibObj = new toolsLib();

                        Debug.Log("LabelDescription: Final" + labelObj.desc_show_in_app);


                        urlText = "<u><b>Info:</b></u>" + labelObj.desc_show_in_app + "\n" + 
                                  "<u><b>Building:</b></u>" + labelObj.location_building + " " + labelObj.room + "\n" +
                                  "<u><b>Floor:</b></u>" + labelObj.floor + "\n";



                    //Create the label object.
                    GameObject qrLabel = Instantiate((GameObject)Resources.Load("Prefabs/buildingLabel_minimal"));
                    qrLabel.GetComponent<Messages>().setLabels(urlTitle, urlText); // change this text

                    qrLabel.transform.position = GameObject.FindWithTag("MainCamera").transform.position + GameObject.FindWithTag("MainCamera").transform.forward * 30;
                    labelList.Add(qrLabel);
                }
                else
                {
                    readSuccessful = false;
                }

            }
            else
            {
                readSuccessful = false;
            }
            if (readSuccessful == false)
            {
                String urlTitle = "Error";
                urlText = "<u><b>Info:</b></u>" + "Please check the network or the QR code." + "\n" +
                          "<u><b>Building:</b></u>" + "None" + "\n" +
                          "<u><b>Floor:</b></u>" + "None" + "\n";
                //Create the label object.
                GameObject qrLabel = Instantiate((GameObject)Resources.Load("Prefabs/buildingLabel_minimal"));
                qrLabel.GetComponent<Messages>().setLabels(urlTitle, urlText); // change this textåå

                qrLabel.transform.position = GameObject.FindWithTag("MainCamera").transform.position + GameObject.FindWithTag("MainCamera").transform.forward * 30;
                labelList.Add(qrLabel);
            }
        }catch (System.Exception e)
        {
                String urlTitle = "Error";
                urlText = "<u><b>Info:</b></u>" + "Please check the network or the QR code." + "\n" +
                          "<u><b>Building:</b></u>" + "None" + "\n" +
                          "<u><b>Floor:</b></u>" + "None" + "\n";
                //Create the label object.
                GameObject qrLabel = Instantiate((GameObject)Resources.Load("Prefabs/buildingLabel_minimal"));
                qrLabel.GetComponent<Messages>().setLabels(urlTitle, urlText); // change this textåå

                qrLabel.transform.position = GameObject.FindWithTag("MainCamera").transform.position + GameObject.FindWithTag("MainCamera").transform.forward * 30;
                labelList.Add(qrLabel);
        }



        }


    }

    public void test()
    {
        url = "http://35.183.49.87/eventapi/api/subevent/2/";

        StartCoroutine(readFromURL());
    }


    public void OnDestroy()
    {
        if (labelList != null)
        {
            foreach (var obj in labelList)
            {
                Destroy(obj);
                //For clean the debug info
                //Debug.Log("UnityDebug: " + "Destroy Event obj.");
            }
        }



    }


}