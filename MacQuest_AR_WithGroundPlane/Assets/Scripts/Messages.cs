using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class Messages : MonoBehaviour
{

    public TextMeshPro messageText;

    public TextMeshProUGUI nameText;
    public TextMeshProUGUI descriptionText;

    public bool followCamera;

    public void SetText(string text)
    {
        messageText.text = text;
    }

    public void setLabels(string name, string description){
        nameText.text = name;
        descriptionText.text = description;
    }

    public void SetFollowCamera(bool b)
    {
        followCamera = b;
    }


    void Update()
    {
        //make sure the bubble is always facing the camera
        if (followCamera){
            transform.LookAt(Camera.main.transform);
        }

    }
}
