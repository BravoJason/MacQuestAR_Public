using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class HeadingInfo{
    public float heading;
    public float pitch;
    public bool isValid;

    public HeadingInfo(){
        heading = 0.0f;
        pitch = 0.0f;
        isValid = false;
        
    }
}

public class headingControl : MonoBehaviour
{





    public static HeadingInfo headingInfo;

    // Use this for initialization
    void Start()
    {
       


    }


    public void setHeading(string strHeading)
    {
        headingInfo = Mapbox.Json.JsonConvert.DeserializeObject<HeadingInfo>(strHeading);

        if (headingInfo.pitch >= -60 && headingInfo.pitch <= 60){
            headingInfo.isValid = true;
        }

        //Debug.Log("Heading: Unity: " + headingInfo.heading + " pitch: " + headingInfo.pitch + " isValid: " + headingInfo.isValid);

    }





}

/*
 * old set heading
 * heading = float.Parse(strHeading.Trim());
        headingReady = true;

        if (firstHeading){
            initHeading = heading;
            firstHeading = false;
        }
        else if(Mathf.Abs(heading - initHeading) >= 40){
            initHeading = heading;
        }*/