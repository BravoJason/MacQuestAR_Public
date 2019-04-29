using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PositionScript : MonoBehaviour {

	// Use this for initialization
	void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
        Debug.Log("Camera position" + transform.position);
        Debug.Log("Camera rotation" + transform.rotation);
        Debug.Log("Camera angle: " + transform.rotation.eulerAngles.y);

        Debug.Log("Up " + transform.up);
	}
}
