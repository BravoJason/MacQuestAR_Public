using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class toolsLib : MonoBehaviour
{
    public static GameObject tempObj;




    public static float convertStringToFloat(string strFloat)
    {
        return float.Parse(strFloat.Trim(), System.Globalization.CultureInfo.InvariantCulture);
    }

    public static double conertStringToDouble(string strFloat)
    {
        return double.Parse(strFloat.Trim(), System.Globalization.CultureInfo.InvariantCulture);
    }


    public static Vector3 getRotatedLocation(Vector3 oringalLocation, Vector3 rotatedCenter, Vector3 axis, float rotatedAngle)
    {
        if(tempObj == null){
            tempObj = new GameObject();
        }

        
        tempObj.transform.position = oringalLocation;
        tempObj.transform.RotateAround(rotatedCenter, axis, rotatedAngle);
        return tempObj.transform.position;

    }

    IEnumerator waitForTime(int second)
    {
        yield return new WaitForSeconds(second);
    }

    public HeadingInfo updateHeadingInfo()
    {
        HeadingInfo trueHeading = headingControl.headingInfo;

        //while(trueHeading.isValid == false){
            //StartCoroutine(waitForTime(1));
            //trueHeading = headingControl.headingInfo;
        //}

        return trueHeading;

    }


    public static string[] phraseURLFromLabelDescription(string description, string[] markers){

        bool hasWebURL = false;
        string url = null;
        string str_ = description;
        string strLowerCase = description.ToLower();
        //Search the beginning of the web marker.
        //Format: <w>Http://www.google.ca/</w>This is the description.
        int webMarkerBegin = strLowerCase.IndexOf(markers[0], System.StringComparison.CurrentCulture);
        int webMarkerEnd = strLowerCase.IndexOf(markers[1], System.StringComparison.CurrentCulture);
        if (webMarkerBegin != -1 && webMarkerEnd != -1)
        {
            //Find the web URL location in the description.
            url = strLowerCase.Substring(webMarkerBegin + markers[0].Length, webMarkerEnd - (webMarkerBegin + markers[0].Length));
            url = url.Trim();
            hasWebURL = true;

        }

        if (hasWebURL == true)
        {
            //Remove web URL from description.
            string pureDescription = str_.Substring(0, webMarkerBegin);
            var temp_ = str_.Substring(webMarkerEnd + markers[1].Length, str_.Length - (webMarkerEnd + markers[1].Length) );
            pureDescription = pureDescription + temp_;
            str_ = pureDescription;
        }

        string[] retStr = { url, str_ };
        return retStr;

    }
        


        
}