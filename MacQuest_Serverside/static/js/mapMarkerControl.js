let g_SubEventMarkers = {};

function makeMarkerID(pid, sid) {

    return `${pid}-${sid}`;
}

function splitID(id_str) {
    let idArray = id_str.split("-");
    return {"pid": idArray[0], "sid": idArray[1]};
}


function addSubMarker(lat, lng, pid, sid, popupText, floor) {

    let markerID = makeMarkerID(pid, sid);
    if (!checkHasMarker(markerID)) {
        let popup = new mapboxgl.Popup({offset: 25, closeOnClick: false})
            .setHTML(`<b>Event Name:</b>${popupText}`);


        let markerObj = {};
        markerObj.marker = new mapboxgl.Marker()
            .setLngLat([lng, lat])
            .setPopup(popup)
            .addTo(map);
        markerObj.isShow = true;
        markerObj.floor = floor.toString();
        markerObj.popup = popup;
        g_SubEventMarkers[markerID] = markerObj;
    }

}

function showSubEventPopup(pid, sid) {
    let markerID = makeMarkerID(pid, sid);
    closeAllSubeventPopup();
    if (checkHasMarker(markerID)) {
        let popup = g_SubEventMarkers[markerID].marker.getPopup();
        if (popup.isOpen() === false) {
            let idObj = splitID(markerID);
            showSubEventMarker(pid, sid);
            toggleSubEventMarkerPopup(idObj.pid, idObj.sid)
        }
    }
}


function closeSubEventPopup(pid, sid) {
    let markerID = makeMarkerID(pid, sid);
    if (checkHasMarker(markerID)) {
        let popup = g_SubEventMarkers[markerID].marker.getPopup();
        if (popup.isOpen() === true) {
            g_SubEventMarkers[markerID].marker.togglePopup();
        }
    }
}

function closeAllSubeventPopup() {
    for (let key in g_SubEventMarkers) {
        let idObj = splitID(key);

        closeSubEventPopup(idObj.pid, idObj.sid);
    }
}

function checkHasMarker(id) {
    return g_SubEventMarkers.hasOwnProperty(id) === true;
}

function toggleSubEventMarkerPopup(pid, sid) {
    let markerID = makeMarkerID(pid, sid);
    if (checkHasMarker(markerID)) {

        g_SubEventMarkers[markerID].marker.togglePopup();
        map.jumpTo({
            center: g_SubEventMarkers[markerID].marker.getLngLat()
        });
        changeLayers(g_SubEventMarkers[markerID].floor);
    }
}


function removeAllMarkers() {
    for (let key in g_SubEventMarkers) {
        let idObj = splitID(key);
        removeAllParentMarker(idObj.pid);

    }
}

function removeAllParentMarker(pid) {
    for (let key in g_SubEventMarkers) {
        let idObj = splitID(key);
        if (idObj.pid === pid.toString()) {
            g_SubEventMarkers[key].marker.remove();
            delete g_SubEventMarkers[key];
        }
    }
}

function showSubEventMarker(pid, sid) {
    let markerID = makeMarkerID(pid, sid);
    if (g_SubEventMarkers.hasOwnProperty(markerID)) {
        if (g_SubEventMarkers[markerID].isShow === false) {
            g_SubEventMarkers[markerID].marker.addTo(map);
        }
        g_SubEventMarkers[markerID].isShow = true;
    }

}

function hideSubEventMarker(pid, sid) {
    let markerID = makeMarkerID(pid, sid);
    if (g_SubEventMarkers.hasOwnProperty(markerID)) {
        if (g_SubEventMarkers[markerID].isShow === true) {
            g_SubEventMarkers[markerID].marker.remove();
        }

        g_SubEventMarkers[markerID].isShow = false;

    }
}


function displayMarkerByFloor(floor) {
    for (let key in g_SubEventMarkers) {
        let idObj = splitID(key);
        let markerFloor = g_SubEventMarkers[key].floor
        if (markerFloor === floor.toString()) {
            showSubEventMarker(idObj.pid, idObj.sid);
        } else {
            hideSubEventMarker(idObj.pid, idObj.sid);
        }
    }
}







