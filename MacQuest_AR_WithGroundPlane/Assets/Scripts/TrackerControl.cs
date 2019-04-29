using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Vuforia;
using UnityEngine.SceneManagement;

public class TrackerControl : MonoBehaviour {

	// Use this for initialization
	void Start () {
        InitializeTracker();
	}
	
	// Update is called once per frame
	void Update () {
		
	}

    public void InitializeTracker()
    {
        PositionalDeviceTracker positionalDeviceTracker = TrackerManager.Instance.InitTracker<PositionalDeviceTracker>();
        if (positionalDeviceTracker != null)
        {
            // Positional device tracker is not null and is therefore supported on device. No need to proceed.
            Debug.Log("Positional Tracker Enabled");
            return;
        }

        // No positional device tracker supported on this device so initialize the rotaitonal tracker.
        RotationalDeviceTracker rotationalDeviceTracker = TrackerManager.Instance.InitTracker<RotationalDeviceTracker>();
        Debug.Log("Rotational Tracker Enabled");
    }
}
