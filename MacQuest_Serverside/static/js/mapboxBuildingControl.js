//Function to get click building info.
function getClickBuildingInfo(e) {

    //var clickPoint = new mapboxgl.Point(e.originalEvent.clientX, e.originalEvent.clientY);
    var clickPoint = e.point;
    var buildingInfo = {};
    buildingInfo.isRoom = false;
    buildingInfo.isOutdoor = true;
    buildingInfo.isBuilding = false;

    var features = map.queryRenderedFeatures(clickPoint);

    if (Debug_control === true) {
        console.log("clickPoint:");
        console.log(clickPoint);
        console.log("getCenterBuildingFloor called.");
        console.log("Click point feature:");
        console.log(features);
        console.log("Zoom level:");
        console.log(map.getZoom());

    }

    features.forEach(function (feature) {
        switch (feature.layer.id) {
            case "campusoutline":
                buildingInfo.type = "building";
                buildingInfo.buildingShortName = feature.properties.shortname;
                buildingInfo.buildingFullName = feature.properties.name;
                buildingInfo.nBasementFloor = feature.properties.num_basement;
                buildingInfo.nFloor = feature.properties.num_floor;
                buildingInfo.currentFloor = currentFloor;
                buildingInfo.isOutdoor = false;
                buildingInfo.isBuilding = true;
                break;
            case layer_fills_ID[layerIndex[currentFloor]]:
                buildingInfo.isRoom = true;
                buildingInfo.room = {};
                buildingInfo.room.roomNumber = feature.properties.name;
                buildingInfo.room.roomFloor = feature.properties.floor;
                buildingInfo.room.center = feature.properties.centroid;
                buildingInfo.isOutdoor = false;
                buildingInfo.isBuilding = true;
                break;

        }
    });

    if (buildingInfo.type === undefined) {
        features.forEach(function (feature) {
            switch (feature.layer.id) {
                case "park":
                    buildingInfo.type = "park";
                    buildingInfo.isOutdoor = true;
                    buildingInfo.isBuilding = false;
                    break;
            }
        });
    }


    for (var i = 0; i < features.length; i++) {
        if (features[i].layer.id === "building") {
            buildingInfo.currentFloor = currentFloor;
            buildingInfo.isOutdoor = false;
            buildingInfo.isBuilding = true;
            break;
        } else {
            buildingInfo.currentFloor = '1';
            buildingInfo.isOutdoor = true;
            buildingInfo.isBuilding = false;

        }
    }

    //Check whether the user click in building level or not.
    buildingInfo.zoomLevel = map.getZoom();
    if (buildingInfo.zoomLevel >= ZOOM_LEVEL_BUILDING_ROOM_BOUNDARY) {
        buildingInfo.clickRoom = true;
    } else {
        buildingInfo.clickRoom = false;
    }

    if (Debug_control === true) {
        console.log(buildingInfo);
    }

    buildingInfo.lngLat = e.lngLat;

    return buildingInfo;
}


//Function to get building floor info array.
function getBuildingFloorInfoArray(buildingInfo) {

    var floorNameArray = [];
    if (buildingInfo.isOutdoor === false && buildingInfo.isBuilding === true) {
        if (buildingInfo.nBasementFloor != null && buildingInfo.nBasementFloor !== undefined) {
            for (var i = parseInt(buildingInfo.nBasementFloor); i > 0; i--) {
                floorNameArray.push("B" + i.toString());
            }
        }

        if (buildingInfo.nFloor != null && buildingInfo.nFloor !== undefined) {
            for (var i = 1; i <= parseInt(buildingInfo.nFloor); i++) {
                floorNameArray.push(i.toString());
            }
        }

    }

    return floorNameArray;

}


function setFloorButton(floorNameArray) {

    $("#floorButton").empty();

    for (var i = floorNameArray.length - 1; i >= 0; i--) {
        const button = $('<button/>').text(floorNameArray[i]).addClass("btn btn-secondary");
        button.click(floorToLayer(floorNameArray[i]), function (event) {
            changeLayers(event.data);
        });
        $("#floorButton").append(button);
    }

}

function setFllorButtonByLatLng(lat, lng) {
    const latLngToPoint = latLngToPixel(lat, lng);
    let clickPoint = {point: latLngToPoint};
    let buildingInfo = getClickBuildingInfo(clickPoint);
    let floorArray = getBuildingFloorInfoArray(buildingInfo);
    setFloorButton(floorArray);

}

