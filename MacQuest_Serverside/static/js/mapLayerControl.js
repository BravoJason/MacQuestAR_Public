//Fill layer ID array.
const layer_fills_ID = {
    '-1': 'layer-1fill',
    '0': 'layer0fill',
    '1': 'layer1fill',
    '2': 'layer2fill',
    '3': 'layer3fill',
    '4': 'layer4fill',
    '5': 'layer5fill',
    '6': 'layer6fill',
    '7': 'layer7fill',
    "building": "campusoutline"
};

//Room layer ID array.
const layer_rooms_ID = {
    '-1': 'layer-1rooms',
    '0': 'layer0rooms',
    '1': 'layer1rooms',
    '2': 'layer2rooms',
    '3': 'layer3rooms',
    '4': 'layer4rooms',
    '5': 'layer5rooms',
    '6': 'layer6rooms',
    '7': 'layer7rooms'
};

//Label layer ID array.
const layer_labels_ID = {
    '-1': 'layer-1labels',
    '0': 'layer0labels',
    '1': 'layer1labels',
    '2': 'layer2labels',
    '3': 'layer3labels',
    '4': 'layer4labels',
    '5': 'layer5labels',
    '6': 'layer6labels',
    '7': 'layer7labels'
};

//Elevator layer ID array.
const layer_elevator_ID = {
    '-1': 'layer-1elevator',
    '0': 'layer0elevator',
    '1': 'layer1elevator',
    '2': 'layer2elevator',
    '3': 'layer3elevator',
    '4': 'layer4elevator',
    '5': 'layer5elevator',
    '6': 'layer6elevator',
    '7': 'layer7elevator'
};

//Staircase layer ID array.
const layer_staircase_ID =
    {
        '-1': 'layer-1staircase',
        '0': 'layer0staircase',
        '1': 'layer1staircase',
        '2': 'layer2staircase',
        '3': 'layer3staircase',
        '4': 'layer4staircase',
        '5': 'layer5staircase',
        '6': 'layer6staircase',
        '7': 'layer7staircase'
    };

//Staircase layer ID array.
const layer_washroom_ID =
    {
        '-1': 'layer-1washroom',
        '0': 'layer0washroom',
        '1': 'layer1washroom',
        '2': 'layer2washroom',
        '3': 'layer3washroom',
        '4': 'layer4washroom',
        '5': 'layer5washroom',
        '6': 'layer6washroom',
        '7': 'layer7washroom'
    };

const layerIndex = {
    '-1': '-1',
    '0': '0',
    '1': '1',
    '2': '2',
    '3': '3',
    '4': '4',
    '5': '5',
    '6': '6',
    '7': '7'
};

const floorToLayerIndexArray = {
    'B2': '-1',
    'B1': '0',
    '1': '1',
    '2': '2',
    '3': '3',
    '4': '4',
    '5': '5',
    '6': '6',
    '7': '7'
};

const featureLayer = {
    "-1": {
        "id": "Layer-1-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer-1roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "0": {
        "id": "Layer0-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer0roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "1": {
        "id": "Layer1-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer1roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "2": {
        "id": "Layer2-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer2roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "3": {
        "id": "Layer3-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer3roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "4": {
        "id": "Layer4-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer4roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "5": {
        "id": "Layer5-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer5roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "6": {
        "id": "Layer6-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer6roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "7": {
        "id": "Layer7-rooms-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "layer7roomsNEW",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
    "building": {
        "id": "Layer-building-highlighted",
        "type": "fill",
        "source": "composite",
        "source-layer": "campusOutline",
        "paint": {
            "fill-outline-color": "#484896",
            "fill-color": "#6e599f",
            "fill-opacity": 0.75
        },
        "filter": ["in", "ogc_fid", ""]
    },
};


//Function to set layer visibility.
function setVisibility(layerNumber, visibility) {
    if (visibility === true) {
        map.setLayoutProperty(layer_fills_ID[layerNumber.toString()], "visibility", "visible");
        map.setLayoutProperty(layer_rooms_ID[layerNumber.toString()], "visibility", "visible");
        map.setLayoutProperty(layer_labels_ID[layerNumber.toString()], "visibility", "visible");
        map.setLayoutProperty(layer_elevator_ID[layerNumber.toString()], "visibility", "visible");
        map.setLayoutProperty(layer_staircase_ID[layerNumber.toString()], "visibility", "visible");
        map.setLayoutProperty(layer_washroom_ID[layerNumber.toString()], "visibility", "visible");
        map.setLayoutProperty(featureLayer[layerNumber.toString()].id, "visibility", "visible")
    } else {
        map.setLayoutProperty(layer_fills_ID[layerNumber.toString()], "visibility", "none");
        map.setLayoutProperty(layer_rooms_ID[layerNumber.toString()], "visibility", "none");
        map.setLayoutProperty(layer_labels_ID[layerNumber.toString()], "visibility", "none");
        map.setLayoutProperty(layer_elevator_ID[layerNumber.toString()], "visibility", "none");
        map.setLayoutProperty(layer_staircase_ID[layerNumber.toString()], "visibility", "none");
        map.setLayoutProperty(layer_washroom_ID[layerNumber.toString()], "visibility", "none");
        map.setLayoutProperty(featureLayer[layerNumber.toString()].id, "visibility", "none")
    }

}

//Function to change the map layers.
function changeLayers(layerNumber) {
    layerNumber = layerNumber.toString()
    previousFllor = currentFloor;
    currentFloor = layerNumber;
    if (previousFllor === currentFloor) {
        cleanHighlightRoom(previousFllor);
    }
    for (let key in layerIndex) {
        if (key !== layerNumber.toString()) {

            setVisibility(layerIndex[key], false);
        } else {
            setVisibility(layerIndex[key], true);
        }
    }
    displayMarkerByFloor(layerNumber);
}

//Function to convert floor number to layer number.
function floorToLayer(floorNumber) {
    return floorToLayerIndexArray[floorNumber];
}

//Function to convert Lat Lng to screen pixel.
function latLngToPixel(Lat, Lng) {
    Lng = parseFloat(Lng);
    Lat = parseFloat(Lat);

    if (Debug_control === true) {
        console.log("highlightClickBuilding");
    }
    const latLongPoint = [Lng, Lat];
    const bbox = map.project(latLongPoint)
    return bbox;
}


function highlightRoomByClick(e, currentFloor) {
    const bbox = e.point;
    if (Debug_control === true) {
        console.log(bbox);
    }
    highlightRoom(bbox, currentFloor);
}


function highlightRoomByLatLng(Lat, Lng, currentFloor) {
    const bbox = latLngToPixel(Lat, Lng);
    highlightRoom(bbox, currentFloor);
}

function highlightRoom(bbox, currentFloor) {

    const features = map.queryRenderedFeatures(bbox, {layers: [layer_fills_ID[currentFloor]]});
    console.log(features)
    const filter = features.reduce(function (memo, feature) {
        memo.push(feature.properties.ogc_fid);
        return memo;
    }, ['in', 'ogc_fid']);
    map.setFilter(featureLayer[currentFloor].id, filter);
}

function highlightBuildingByLatLng(Lat, Lng) {
    const bbox = latLngToPixel(Lat, Lng);
    highlightBuilding(bbox)
}

function highlightBuildingByClick(e) {
    const bbox = e.point;
    highlightBuilding(bbox);
}

function highlightBuilding(bbox) {
    const features = map.queryRenderedFeatures(bbox, {layers: [layer_fills_ID["building"]]});
    const filter = features.reduce(function (memo, feature) {
        memo.push(feature.properties.ogc_fid);
        return memo;
    }, ['in', 'ogc_fid']);
    map.setFilter(featureLayer["building"].id, filter);
}

function cleanHighlightBuilding() {
    const features = map.queryRenderedFeatures({layers: [layer_fills_ID["building"]]});
    const filter_clear = features.reduce(function (memo, feature) {
        memo.push(-9999);
        return memo;
    }, ['in', 'ogc_fid']);
    map.setFilter(featureLayer["building"].id, filter_clear);
}


function cleanHighlightRoom(currentFloor) {
    const features = map.queryRenderedFeatures({layers: [layer_fills_ID[currentFloor]]});
    const filter_clear = features.reduce(function (memo, feature) {
        memo.push(-9999);
        return memo;
    }, ['in', 'ogc_fid']);
    map.setFilter(featureLayer[currentFloor].id, filter_clear);
}

//Function to highlight room with delay time.
function highlightRoomWithDelaytime(lat, lon, floor, delayTime)
{
     setTimeout(highlightRoomByLatLng, delayTime, lat, lon, floor)
}

//Function to highlight room with delay time.
function highlightBuildingWithDelaytime(lat, lon, delayTime)
{
     setTimeout(highlightBuildingByLatLng, delayTime, lat, lon)
}



