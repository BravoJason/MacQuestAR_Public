using System.Collections.Generic;
using UnityEngine;
using Mapbox.Unity.Utilities;
using Mapbox.Utils;


/*
 * Communicate function:
 * setLabelObjectList
 * setUserLocation
 * setVisibilityToTrue
 * setVisibilityToFalse
 * createLabels
 * 
 */

public class LabelMaker : MonoBehaviour
{

    GameObject buildingLabel;

    public static Vector2d userCurrentLocation;

    public static Dictionary<int, GameObject> labellMap;

    public static LabelObjectList labelListObject;

    public bool isGroundPlaneActive = false;
    GameObject groundPlane;


    public delegate void moveLocation();

    public event moveLocation onMoveLocation;

    //43.258829, -79.920479
    string jsonTestData =
        "{'id': 0, 'url': 'someurl.com', 'event_name': 'Test 1', 'longitude': -79.2980, 'latitude': 43.29800, 'holder': 'Daniel', 'date': 'date-time value', 'description': 'Test 1 description' , 'isVisible':true}";


    //43.258877, -79.920423
    string jsonUserLocation = "{'longitude':-79.2990, 'latitude':43.29900}";


    // Use this for initialization
    void Start()
    {
        init();
        //test();
    }




    /*

    // Update is called once per frame
    void LateUpdate()
    {
        updateLabelLocation();
    }
    */

    IEnumerator<GameObject> RepeatLerp(GameObject gameObject, Vector3 end, float time)
    {
        float i = 0.0f;
        float rate = (1 / time) * 1.0f;

        Vector3 start = gameObject.transform.position;

        while (i < 1.0f)
        {
            i += Time.deltaTime * rate;

            gameObject.transform.position = Vector3.Lerp(start, end, i);
            yield return null;
        }

    }

    //Function to get input label Json.
    public void setLabelObjectList(string str)
    {
        LabelObjectList labelObjList = Mapbox.Json.JsonConvert.DeserializeObject<LabelObjectList>(str);
        labelListObject = labelObjList;
        //onMoveLocation();

    }

    //Function to get user current location.
    public void setUserLocation(string str)
    {
        UserLocationObject userLocationObj = Mapbox.Json.JsonConvert.DeserializeObject<UserLocationObject>(str);
        setUserCurrentLocation(userLocationObj.latitude, userLocationObj.longitude);
        onMoveLocation();

    }

    //Function to get user origin location.
    public void setUserOrigin(string str)
    {
        UserLocationObject userLocationObj = Mapbox.Json.JsonConvert.DeserializeObject<UserLocationObject>(str);
        setUserCurrentLocation(userLocationObj.latitude, userLocationObj.longitude);

    }

    //Create label objects according to the label list.
    // mode = "info" - building information
    // mode = "event" - event information.
    public void createLabels()
    {
        foreach (LabelObject label in labelListObject.labelList)
        {
            if (label.isVisible == true)
            {
                string labelText = "";

                labelText = "<u><b>Info:</b></u>" + label.description;

                Debug.Log("Label text is" + labelText);

                if (!labellMap.ContainsKey(label.id))
                {
                    labellMap.Add(label.id, createLabelObject(label.event_name, labelText, new Vector2d(label.latitude, label.longitude)));
                    Debug.Log("LabelMap added" + label.id);
                }

            }
        }


    }

    //Delete label.

    public void clearLabels()
    {
        foreach (var label in labellMap.Values)
        {
            Destroy(label);
        }
        labellMap = new Dictionary<int, GameObject>();
    }


    //Create label object.
    GameObject createLabelObject(string name, string description, Vector2d latLng)
    {
        Vector2d relativeLocation = calcLabelRelativeLocation(latLng.x, latLng.y);
        var obj = Instantiate(buildingLabel, new Vector3((float)relativeLocation.x, 0f, (float)relativeLocation.y), Quaternion.identity);
        Debug.Log("descript set to: " + description);
        setLabelText(obj, name, description);
        obj.transform.position = toolsLib.getRotatedLocation(obj.transform.position, new Vector3(0, 0, 0), Vector3.up, -headingControl.headingInfo.heading);

        obj.transform.position += new Vector3(0, 10, 0);

        if (isGroundPlaneActive)
        {
            //obj.transform.parent = groundPlane.transform;
        }
        return obj;
    }

    //Function to set Label text.
    void setLabelText(GameObject obj, string name, string descript)
    {
        obj.GetComponent<Messages>().setLabels(name, descript);

    }

    /*public void foundGroundPlane(){
        clearLabels();
        createLabels("info");
    }*/


    //Init the label creator.
    private void init()
    {
        labellMap = new Dictionary<int, GameObject>();
        buildingLabel = (GameObject)Resources.Load("Prefabs/buildingLabel_minimal");

        userCurrentLocation = new Vector2d(0f, 0f);
        labelListObject = new LabelObjectList();
        labelListObject.labelList = new List<LabelObject>();
        onMoveLocation += updateLabelLocation;

        groundPlane = GameObject.FindWithTag("GroundPlane");
        //isGroundPlaneActive = SupportedDevices.IsGroundPlaneSupported();
    }



    //Set user current planer coordinate.
    public void setUserCurrentLocation(double lat, double lng)
    {
        userCurrentLocation = Conversions.GeoToWorldPosition(lat, lng, new Vector2d(0f, 0f));
    }

    //Get label relative location (Compare with userCurrentLocation).
    public Vector2d calcLabelRelativeLocation(double lat, double lng)
    {
        return Conversions.GeoToWorldPosition(lat, lng, userCurrentLocation);
    }

    //Update label location.
    void updateLabelLocation()
    {

        if (labelListObject != null && !isGroundPlaneActive)
        {
            foreach (LabelObject labelObj in labelListObject.labelList)
            {

                if (labelObj.isVisible == true)
                {
                    //If the label is visible update info.
                    //Check label exist or not.
                    if (labellMap.ContainsKey(labelObj.id))
                    {
                        Vector3 oldLocation = labellMap[labelObj.id].transform.position;
                        Vector2d newLocation2d = calcLabelRelativeLocation(labelObj.latitude, labelObj.longitude);
                        Vector3 newLocation = new Vector3((float)newLocation2d.x, 0f, (float)newLocation2d.y);
                        newLocation = toolsLib.getRotatedLocation(newLocation, new Vector3(0, 0, 0), Vector3.up, headingControl.headingInfo.isValid ? -headingControl.headingInfo.heading : 0);



                        Debug.Log("Rotate Debug: Old:" + oldLocation + " New Location: " + newLocation);
                        //Label exists, only update location.
                        labellMap[labelObj.id].transform.position = Vector3.Lerp(oldLocation, newLocation, 1f);

                        if (!isGroundPlaneActive)
                        {
                            StartCoroutine(RepeatLerp(labellMap[labelObj.id], newLocation, .2f));
                        }



                        Debug.Log("ROTATE: Label: " + labellMap[labelObj.id].transform.position);



                    }
                    else
                    {
                        //Label not exists, create a new object.

                        //Problem here
                        labellMap.Add(labelObj.id, createLabelObject(labelObj.event_name, labelObj.description, new Vector2d(labelObj.latitude, labelObj.longitude)));


                    }
                }
                else
                {
                    //If the label is invisible.
                    //Check label exist or not.
                    if (labellMap.ContainsKey(labelObj.id))
                    {
                        //Label exists. Delete it.
                        Destroy(labellMap[labelObj.id]);
                        labellMap.Remove(labelObj.id);
                    }
                }

            }
            Debug.Log("Label location updated.");
        }
        Debug.Log("Label list is empty, wait for info.");

    }



    bool isLabelExist(LabelObject label)
    {
        if (labellMap[label.id] == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    //Function to set visibility to true by input id string.
    //id format: "[1,2,3,4]";
    public void setVisibilityToTrue(string id)
    {
        List<int> idList = Mapbox.Json.JsonConvert.DeserializeObject<List<int>>(id);
        LabelObject tempLabelObj;
        foreach (int labelId in idList)
        {
            tempLabelObj = findLabelById(labelId);
            if (tempLabelObj != null)
            {
                //Label exist.
                //Set the value as true.
                tempLabelObj.isVisible = true;
            }

        }

    }

    //Function to set visibility to false by input id string.
    //id format: "[1,2,3,4]";
    public void setVisibilityToFalse(string id)
    {
        List<int> idList = Mapbox.Json.JsonConvert.DeserializeObject<List<int>>(id);
        LabelObject tempLabelObj;
        foreach (int labelId in idList)
        {
            tempLabelObj = findLabelById(labelId);
            if (tempLabelObj != null)
            {
                //Label exist.
                //Set the value as true.
                tempLabelObj.isVisible = false;
            }

        }

    }

    //Find item by id in label object list.
    LabelObject findLabelById(int id)
    {
        LabelObject retLabelObj = null;
        foreach (LabelObject label in labelListObject.labelList)
        {
            if (label.id == id)
            {
                retLabelObj = label;
                break;
            }
        }
        return retLabelObj;
    }




    void test()
    {
        setLabelObjectList(jsonTestData);
        setUserLocation(jsonUserLocation);
        createLabels();

        //foundGroundPlane();
        //clearLabels();
    }

    private void OnDisable()
    {
        onMoveLocation -= updateLabelLocation;
    }


}