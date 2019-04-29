using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Vuforia;
using UnityEngine.SceneManagement;

public class TrackerControler : MonoBehaviour
{

    // Use this for initialization
    void Start()
    {
        InitializeTracker("");
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void InitializeTracker(string m)
    {
        if (m.Equals("QR")){
            activateQR();
            Debug.Log("QR Initialize");
        }

        else if (SupportedDevices.IsGroundPlaneSupported())
        {
            PositionalDeviceTracker positionalDeviceTracker = TrackerManager.Instance.InitTracker<PositionalDeviceTracker>();
            Debug.Log("Positional Tracker Enabled");
            GetComponent<navCreatorGroundV2>().enabled = true;
            return;
        }
        else{
            // No positional device tracker supported on this device so initialize the rotaitonal tracker.
            RotationalDeviceTracker rotationalDeviceTracker = TrackerManager.Instance.GetTracker<RotationalDeviceTracker>();

            GetComponent<navCreatorNoGround>().enabled = true;

            Debug.Log("Rotational Tracker Enabled");
        }



    }

    public void stopTrackers(){
        if (SupportedDevices.IsGroundPlaneSupported())
        {
            TrackerManager.Instance.GetTracker<PositionalDeviceTracker>().Stop();
            Debug.Log("Positional Tracker Disabled");
            return;
        }

        // No positional device tracker supported on this device so stop the rotaitonal tracker.
        TrackerManager.Instance.GetTracker<RotationalDeviceTracker>().Stop();

        Debug.Log("Rotational Tracker Disabled");

        //Application.Quit();
    }

    public void activateQR(){

        GameObject.FindWithTag("MainCamera").GetComponent<QRScanner>().setFocus();

    }


}
