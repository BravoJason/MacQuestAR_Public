let lastUploadTimeStamp = 0;


function updateBuildingList(dataObj) {
    dataObj = JSON.parse(dataObj);

    if (dataObj['timestamp'] < lastUploadTimeStamp) {
        return;
    }
    else {
        lastUploadTimeStamp = dataObj['timestamp'];
    }


    if (dataObj['ans'].length !== 0) {
        if (stringIsEmpty($("#roomBuildingSearchBar").val()) === true) {
            initRoomDropdownList();
        } else {
            emptyRoomDropdownList();
            addRoomsObjectIntoDropdownList(dataObj['ans']);
        }


    }
    else {
        noResultRoomDropdownList();
    }
}

function noResultRoomDropdownList() {
    emptyRoomDropdownList();
    const li = $("<li></li>").text("No result.");
    $('#roomDropdownList').append(li)

}

function emptyRoomDropdownList() {
    $('#roomDropdownList').empty();
}


function initRoomDropdownList() {
    emptyRoomDropdownList();
    const li = $("<li></li>").text("Please input building and room number.");
    $('#roomDropdownList').append(li);
}

function addRoomsObjectIntoDropdownList(dataObj) {
    dataObj.forEach(function (roomObj) {
        let buildingRoomNumber = roomObj['label'];
        let floor = roomObj['floor'];
        let desc = roomObj['desc'];

        let text = "<b>" + buildingRoomNumber + "</b>" + "<br/>" + desc;
        let li = $("<li></li>").addClass("dropdown-item").html(text);

        if (floor.toString() !== "100") {
            li.click(function () {
                //Room
                setFllorButtonByLatLng(roomObj['lat'], roomObj['lon']);
                changeLayers(floor);
                flyMapCenter(roomObj['lon'], roomObj['lat'], floor, ZOOM_LEVEL_ROOM);
                cleanHighlightBuilding();
                cleanHighlightRoom(currentFloor);
                highlightRoomWithDelaytime(roomObj['lat'], roomObj['lon'], floor, 2000);

            })
        } else {
            li.click(function () {
                //Building
                setFllorButtonByLatLng(roomObj['lat'], roomObj['lon']);
                flyMapCenter(roomObj['lon'], roomObj['lat'], 1, ZOOM_LEVEL_BUILDING);
                cleanHighlightBuilding();
                cleanHighlightRoom(currentFloor);
                //highlightBuildingByLatLng(roomObj['lat'], roomObj['lon'])
                highlightBuildingWithDelaytime(roomObj['lat'], roomObj['lon'], 2000);
            })
        }

        $('#roomDropdownList').append(li);

    })
}