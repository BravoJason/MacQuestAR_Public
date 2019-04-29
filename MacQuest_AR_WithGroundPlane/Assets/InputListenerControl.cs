using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Vuforia;

public class InputListenerControl : MonoBehaviour {

	// Use this for initialization
	void Start () {
        GetComponent<AnchorInputListenerBehaviour>().enabled = false;
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
