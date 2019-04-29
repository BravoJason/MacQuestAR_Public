//Function to set parent event list.
function setActivePublicParentEventList(data) {
    let dataObj = JSON.parse(data);
    dataObj = dataObj.ans;

    let htmlCode = '';
    for (let i = 0; i < dataObj.length; i++) {

        let eventActiveClassName = getEventActiveClassName(dataObj[i]);
        let parentBlockID = generateParentEventBlockID(dataObj[i].pid);
        let parentBlockHTMLCode = generateParentBodyCode(dataObj[i]);


        htmlCode +=
            `<div class="card ${eventActiveClassName} parent_event_card">
            <div class="card-header parent_event_card_header">
            <a class="collapsed card-link" data-toggle="collapse" href="#${parentBlockID}" >
            ${dataObj[i].name}
            </a>
            </div>
            <div id="${parentBlockID}" class="collapse parent_event_class" data-parent="#parent-event-list">
            <div class="card-body">
            ${parentBlockHTMLCode}
            </div>
            <script>
            if (addEventListenerIDIntoDict("${parentBlockID}", "hidden.bs.collapse") === true){
                $("#${parentBlockID}").on("hidden.bs.collapse", function(event) {
                cleanCurrentViewParentEventID()
                removeAllParentMarker(${dataObj[i].pid})                              
                event.stopPropagation()
            })
            }
            if (addEventListenerIDIntoDict("${parentBlockID}", "show.bs.collapse") === true){
                $("#${parentBlockID}").on("show.bs.collapse", function(event) {
                searchSubEvent(${dataObj[i].pid})
                event.stopPropagation()
                
            })
            }

            </script>
            </div>
            </div>`
    }
    insertCodeIntoElement("parent-event-list", htmlCode);

    let scrollID = getCurrentViewParentEventID();
    if (scrollID !== -1) {
        let parentEventID = generateParentEventBlockID(scrollID)
        $(`#${parentEventID}`).collapse("show");
    }

}


function addEventListenerIDIntoDict(elementID, type, namespace, event) {

    let events = $._data($(`#${elementID}`)[0], 'events');

    if (typeof events === 'undefined') {
        return true;
    } else {
        if (events.hasOwnProperty(type)) {
            return false
        } else {
            return true;
        }
    }
}

function getCurrentViewParentEventID() {


    return g_current_view_parent_event_id;

}

function getDateObj(date) {
    let d = new Date(date);
    let dateObj = {};
    dateObj.year = d.getFullYear();
    dateObj.month = d.getMonth() + 1;
    dateObj.date = d.getDate();
    dateObj.hours = d.getHours();
    dateObj.minutes = d.getMinutes();
    dateObj.seconds = d.getSeconds();
    dateObj.dateObj = d;
    dateObj.str = dateObj.year + "-" + dateObj.month + "-" + dateObj.date + " " + dateObj.hours + ":" + dateObj.minutes
        + ":" + dateObj.seconds;
    return dateObj;
}

function generateParentEventBlockID(pid) {
    return 'parent-event-id-' + pid;
}

function generateParentBodyCode(parentEvent) {
    let start_date = parentEvent.start_time;
    let end_date = parentEvent.end_time;
    let owner = parentEvent.owner;
    let addSubEventButtonCode = generateAddSubEventButton(owner, parentEvent.pid);
    let deleteParentEvent = generateDeleteParentEventButton(owner, parentEvent.pid);
    let editParentEvent = generateParentEventEditButtonCode(parentEvent.pid, owner);
    let addSubCardID = generateAddSubEventCardID(parentEvent.pid);
    let heatMapButtonCode = generateHeatMapButtonCode(parentEvent.pid, start_date, end_date);
    let subEventContainerID = generateSubEventListContainerID(parentEvent.pid);
    let startTimeID = generateParentEventStartTimeID(parentEvent.pid);
    let endTimeID = generateParentEventEndTimeID(parentEvent.pid);
    let htmlCode = `
        <div class="parent_event_container">
        <p>Description:</p>
        <textarea disabled>${parentEvent.description}</textarea>
        <p>Authentication:</p>
        <input value="${parentEvent.authentication}" disabled>
        <p>Start time:</p>
        <input type="datetime-local" id="${startTimeID}" value="${start_date}"  disabled>
        <p>End time:</p>
        <input type="datetime-local" id="${endTimeID}" value="${end_date}" disabled>
        <div>
        ${addSubEventButtonCode}
        ${heatMapButtonCode}
        ${editParentEvent}
        ${deleteParentEvent}
        </div>
        <div id="${addSubCardID}"></div>
        </div>
        <div id="${subEventContainerID}"></div>`;

    return htmlCode
}


function generateParentEventStartTimeID(pid) {
    return `parent-event-${pid}-start-time`
}

function generateParentEventEndTimeID(pid) {
    return `parent-event-${pid}-end-time`
}

function generateAddSubEventBlockID(parentEventID) {
    return "new-sub-event-" + parentEventID;
}

function generateAddSubEventCardID(parentEventID) {
    return generateAddSubEventBlockID(parentEventID) + "-block";
}

function generateSubEventListContainerID(parentEventID) {
    return `parentEvent-${parentEventID}-subEvent-Container`;
}


function generateAddSubEventBody(parentEventID) {
    let subBlockID = generateAddSubEventBlockID(parentEventID);
    let subBlockCardID = generateAddSubEventCardID(parentEventID);
    //<form method="post" class="form-inline" >
    let htmlCode =
        `<div class="card">
            <div class="card-header">
                <a class="card-link collapsed" data-toggle="collapse" href="#${subBlockID}">
                    New subevent
                </a>
            </div>
            <div id="${subBlockID}" class="collapse show" data-parent="#${subBlockCardID}">
                <div class="card-body" >
                    
                    
                    <p id="sub_event_name_error" class="add_event_error"></p>
                    <p>Subevent name:</p>
                    <input type="text" class="form-control" id='sub_event_name'
                               autocomplete="off" name="sub_event_name" placeholder="Event name">
                               
                    <p id="sub_event_location_error" class="add_event_error"></p>
                    <p>Location:</p>
                    <input type="text" class="form-control" id="sub_event_location"
                               autocomplete="off" name="sub_event_location" placeholder="Please click on the map." disabled>
                    
                    <p id="sub_event_desc_error" class="add_event_error"></p>
                    <p>Description:</p>
                    <textarea rows="4" class="form-control" id="sub_event_desc" autocomplete="off" name="sub_event_desc"
                    placeholder="Event description"></textarea>
                    
                    <p id="sub_event_desc_url_error" class="add_event_error"></p>
                    <p>Description URL:</p>
                    <input type="text" class="form-control" id="sub_event_desc_url"
                               autocomplete="off" name="sub_event_desc_url" placeholder="Event description URL">
                    
                    <p id="sub_event_desc_app_error" class="add_event_error"></p>
                    <textarea rows="4" class="form-control" id="sub_event_desc_app" autocomplete="off" name="sub_event_desc_app"
                    placeholder="Event description" hidden></textarea>
                    
                    <p id="sub_event_action_title_error" class="add_event_error"></p>
                    <p>Action Title:</p>
                    <input type="text" class="form-control" id="sub_event_action_title" autocomplete="off" 
                    name="sub_event_action_title" placeholder="Action title">
                    
                    <p id="sub_event_action_desc_error" class="add_event_error"></p>
                    <p>Action description:</p>
                    <textarea rows="4" class="form-control" id="sub_event_action_desc" autocomplete="off" 
                    name="sub_event_action_desc" placeholder="Action description"></textarea>
                    
                    <p id="sub_event_action_url_error" class="add_event_error"></p>
                    <p>Action URL:</p>
                    <input type="text" class="form-control" id="sub_event_action_url" autocomplete="off" 
                    name="sub_event_action_url" placeholder="Action description URL">
                    
                    <p id="sub_event_start_time_error" class="add_event_error"></p>
                    <p>Start time:</p>
                    <input id="sub_event_start_time" type="datetime-local" class="form-control" name="sub_event_start_time"   placeholder="YYYY-MM-DD HH:MM">
                    
                    <p id="sub_event_end_time_error" class="add_event_error"></p>
                    <p>End time:</p>
                    <input id="sub_event_end_time" type="datetime-local" name="sub_event_end_time" class="form-control"   placeholder="YYYY-MM-DD HH:MM">
                    
                   <input id="sub_event_lat" type="hidden">
                   <input id="sub_event_lng" type="hidden">
                   <input id="sub_event_floor" type="hidden">
                   <input id="sub_event_pid" type="hidden" value="${parentEventID}">
                   <input id="sub_event_building" type="hidden">
                   <input id="sub_event_room" type="hidden">
                    
                    <button type="button" class="btn btn-outline-primary" id="sub_event_create" onclick="submitSubEvent()">Create</button>
                    <button type="button" class="btn btn-outline-primary" id="sub_event_cancel" onclick="removeAddSubEventBody(${parentEventID})">Cancel</button>
                    
                 
                    
                </div>
            </div>
        </div>
        <script id="sub_event_script" name="sub_event_script">
        $("#SearchOption").collapse('hide');
        Status_add_sub_event = true;
        if(addEventListenerIDIntoDict("sub_event_start_time", "onchange") === true){
            $("#sub_event_start_time").change(function(e){
                setEndTimeMinDateTime("sub_event_start_time", "sub_event_end_time");
            })
        }
        
         if(addEventListenerIDIntoDict("sub_event_end_time", "onchange") === true){
            $("#sub_event_end_time").change(function(e){
                setStartTimeMaxDateTime("sub_event_start_time", "sub_event_end_time");
            })
        }
        
        
        </script>`;
    //timeDatePicker.initDate("sub_event_start_time", "sub_event_end_time");


    return htmlCode
}

function addSubEvent(parentEventID) {
    parentEventID = parentEventID.toString();
    let addSubBlockID = generateAddSubEventCardID(parentEventID);
    let htmlCode = generateAddSubEventBody(parentEventID);

    insertCodeIntoElement(addSubBlockID, htmlCode)

}


function insertCodeIntoElement(elementID, htmlCode) {
    $("#" + elementID).htmlPolyfill(htmlCode);
}


function getEventActiveClassName(dataObj) {
    let className = "";
    if (dataObj.isActive === false) {
        className = "event-inactive"
    } else {
        className = "event-active"
    }

    return className
}

function submitSubEvent() {
    let param = {
        "sub_event_name": $("#sub_event_name").val(),
        "sub_event_location": $("#sub_event_location").val(),
        "sub_event_desc": $("#sub_event_desc").val(),
        "sub_event_desc_url": $("#sub_event_desc_url").val(),
        "sub_event_desc_app": $("#sub_event_desc_app").val(),
        "sub_event_action_title": $("#sub_event_action_title").val(),
        "sub_event_action_desc": $("#sub_event_action_desc").val(),
        "sub_event_action_url": $("#sub_event_action_url").val(),
        "sub_event_start_time": $("#sub_event_start_time").val(),
        "sub_event_end_time": $("#sub_event_end_time").val(),
        "sub_event_lat": $("#sub_event_lat").val(),
        "sub_event_lng": $("#sub_event_lng").val(),
        "sub_event_floor": $("#sub_event_floor").val(),
        "sub_event_pid": $("#sub_event_pid").val(),
        "sub_event_building": $("#sub_event_building").val(),
        "sub_event_room": $("#sub_event_room").val(),
    };
    uploadSubEvent(param)
}

function submitEditSubEvent(sid) {
    let param = {};
    param["sub_event_name"] = getEditEventValue("sub_event_name", sid);

    param["sub_event_location"] = getEditEventValue("sub_event_location", sid);

    param["sub_event_desc"] = getEditEventValue("sub_event_desc", sid);

    param["sub_event_desc_url"] = getEditEventValue("sub_event_desc_url", sid);

    param["sub_event_desc_app"] = getEditEventValue("sub_event_desc_app", sid);

    param["sub_event_action_title"] = getEditEventValue("sub_event_action_title", sid);

    param["sub_event_action_desc"] = getEditEventValue("sub_event_action_desc", sid);

    param["sub_event_action_url"] = getEditEventValue("sub_event_action_url", sid);

    param["sub_event_start_time"] = getEditEventValue("sub_event_start_time", sid);

    param["sub_event_end_time"] = getEditEventValue("sub_event_end_time", sid);

    param["sub_event_lat"] = getEditEventValue("sub_event_lat", sid);

    param["sub_event_lng"] = getEditEventValue("sub_event_lng", sid);

    param["sub_event_floor"] = getEditEventValue("sub_event_floor", sid);

    param["sub_event_pid"] = getEditEventValue("sub_event_pid", sid);

    param["sub_event_building"] = getEditEventValue("sub_event_building", sid);

    param["sub_event_room"] = getEditEventValue("sub_event_room", sid);

    param["sid"] = sid;

    uploadEditSubEvent(sid, param)
}

function cancelEditSubEvent() {
    searchParentEventByParam(g_CurrentParentEventSearchParam);
}


function setAddSubEventLocation(location) {
    if (Status_add_sub_event === true || Status_edit_sub_event === true) {
        //Set lat lng
        $('#sub_event_lat').val(location.lngLat.lat);
        $('#sub_event_lng').val(location.lngLat.lng);

        g_location_lat = location.lngLat.lat;
        g_location_lng = location.lngLat.lng;
        if (location.zoomLevel >= ZOOM_LEVEL_BUILDING_ROOM_BOUNDARY) {
            if (location.isRoom === true) {
                //It is a room
                //Set floor.
                $("#sub_event_floor").val(location.currentFloor);

                g_location_floor = location.currentFloor;

                console.log("It is a room");
                let building = location.buildingShortName;
                let room = location.room.roomNumber;
                $("#sub_event_location").val(`${building} ${room}`);
                $("#sub_event_building").val(`${building}`)
                $("#sub_event_room").val(`${room}`)

                g_location_building = location.buildingShortName;
                g_location_room = location.room.roomNumber;

            } else if (location.isBuilding === true) {
                //It is a building.
                //Set floor.
                $("#sub_event_floor").val(location.currentFloor);

                g_location_floor = location.currentFloor;
                console.log("It is a building");
                let building = location.buildingShortName;
                $("#sub_event_location").val(`${building}`);
                $("#sub_event_building").val(`${building}`);


                g_location_building = location.buildingShortName;
                g_location_room = "";
            } else {
                //It is outdoor.
                console.log("It is outdoor.");
                //Set floor number.
                $("#sub_event_location").val("Outdoor");
                //Set floor.
                $("#sub_event_floor").val("1");

                $("#sub_event_building").val(`Outdoor`);

                g_location_building = "Outdoor";
                g_location_floor = "1";
                g_location_room = "";

            }
        } else {
            if (location.isBuilding === true) {
                //It is a building.
                console.log("It is a building");
                let building = location.buildingShortName;
                $("#sub_event_location").val(`${building}`);
                $("#sub_event_building").val(`${building}`);
                g_location_building = location.buildingShortName;
                //Set floor number.
                $("#sub_event_floor").val("1");
                g_location_floor = "1";
                g_location_room = "";
            } else if (location.isOutdoor === true) {
                //It is outdoor.
                console.log("It is outdoor.");
                $("#sub_event_location").val("Outdoor");
                $("#sub_event_floor").val("1");
                $("#sub_event_building").val(`Outdoor`);
                g_location_building = "Outdoor";
                g_location_floor = "1";
                g_location_room = "";
            }
        }
        if (typeof g_location_room === "undefined") {
            g_location_room = "";
        }
        setEditEventLocationInfo(g_location_lat, g_location_lng, g_location_floor, g_location_building, g_location_room);
    }

}

function setEditEventLocationInfo(lat, lng, floor, building, room) {
    g_location_building = building;
    g_location_floor = floor;
    g_location_room = room;
    g_location_lat = lat;
    g_location_lng = lng;
    if (g_edit_sub_event_id !== -1) {
        let building_obj = getEditElementObject("sub_event_building", g_edit_sub_event_id);
        building_obj.val(g_location_building);
        let floor_obj = getEditElementObject("sub_event_floor", g_edit_sub_event_id);
        floor_obj.val(g_location_floor);
        let room_obj = getEditElementObject("sub_event_room", g_edit_sub_event_id);
        room_obj.val(g_location_room);
        let lat_obj = getEditElementObject("sub_event_lat", g_edit_sub_event_id);
        lat_obj.val(g_location_lat);
        let lng_obj = getEditElementObject("sub_event_lng", g_edit_sub_event_id);
        lng_obj.val(g_location_lng);
        let location_obj = getEditElementObject("sub_event_location", g_edit_sub_event_id);
        location_obj.val(`${g_location_building} ${g_location_room}`);
    }
}

function getEditElementObject(id, sid) {
    let element_id = generateEditElementID(id, sid);
    return $(`#${element_id}`);

}

function handleAddSubEventAjaxReturn(data) {

    let dataObj = JSON.parse(data);
    if (dataObj.status === "Successful") {
        alert("Add subevent successful.");

        removeAddSubEventBody(dataObj.pid);
        Status_add_sub_event = false;
        searchParentEventByID({'pid': dataObj.pid});
        searchSubEventByPID(dataObj.pid);
    } else {
        displayAddSubEventError(data);
    }


}

function displayAddSubEventError(data) {
    let dataObj = JSON.parse(data);

    if (dataObj.status === "Fail") {
        if (dataObj.errors.hasOwnProperty("sub_event_name")) {
            $("#sub_event_name_error").html(dataObj.errors.sub_event_name)
        } else {
            $("#sub_event_name_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_location")) {
            $("#sub_event_location_error").html(dataObj.errors.sub_event_location)
        } else {
            $("#sub_event_location_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_desc")) {
            $("#sub_event_desc_error").html(dataObj.errors.sub_event_desc)
        } else {
            $("#sub_event_desc_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_desc_url")) {
            $("#sub_event_desc_url_error").html(dataObj.errors.sub_event_desc_url)
        } else {
            $("#sub_event_desc_url_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_desc_app")) {
            $("#sub_event_desc_app_error").html(dataObj.errors.sub_event_desc_app)
        } else {
            $("#sub_event_desc_app_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_action_desc")) {
            $("#sub_event_action_desc_error").html(dataObj.errors.sub_event_action_desc)
        } else {
            $("#sub_event_action_desc_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_action_url")) {
            $("#sub_event_action_url_error").html(dataObj.errors.sub_event_action_url)
        } else {
            $("#sub_event_action_url_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_start_time")) {
            $("#sub_event_start_time_error").html(dataObj.errors.sub_event_start_time)
        } else {
            $("#sub_event_start_time_error").empty();
        }

        if (dataObj.errors.hasOwnProperty("sub_event_end_time")) {
            $("#sub_event_end_time_error").html(dataObj.errors.sub_event_end_time)
        } else {
            $("#sub_event_end_time_error").empty();
        }


    }

}

function removeAddSubEventBody(pid) {
    let blockID = generateAddSubEventCardID(pid);
    let blockIDSelector = `#${blockID}`;
    $(blockIDSelector).empty();
}


function searchSubEvent(pid) {
    let param = {'pid': pid};
    searchSubEventByParam(param, pid);

}

function generateSubEventBlockID(sid) {
    return `sub-event-${sid}`
}

function generateSubEventBlockCardID(sid) {
    return `sub-event-${sid}-block`
}


function generateSubEventCode(JsonObj, pid) {
    let Obj = JSON.parse(JsonObj);
    let htmlCode = generateSubEventCardCode(Obj.ans);
    let subEventContainerID = generateSubEventListContainerID(pid);
    insertCodeIntoElement(subEventContainerID, htmlCode);
    scrollToParentEvent(pid);
}

function generateSubEventCardCode(subEventObj) {
    let htmlCode = ``;
    for (let i = 0; i < subEventObj.length; i++) {
        let subEvent = subEventObj[i];
        let subBlockID = generateSubEventBlockID(subEvent.subEventID);
        let subBlockCardID = generateSubEventBlockCardID(subEvent.subEventID);
        let deleteButtonCode = subEventDeleteButtonCode(subEvent.subEventID, subEvent.owner, subEvent.pid);
        let editButtonCode = generateSubEventEditButton(subEvent.subEventID, subEvent.owner);
        let showQRCodeButtonCode = genreateSubEventShowQRCodeButton(subEvent.subEventID);
        htmlCode +=
            `
                    <div id="${subBlockCardID}">
                        <div class="card">
                            <div class="card-header sub_event_card_header">
                                <a class="card-link collapsed" data-toggle="collapse" href="#${subBlockID}"">
                                    ${subEvent.name}
                                </a>
                            </div>
                            <div id="${subBlockID}" class="collapse sub_event_class" data-parent="#${subBlockCardID}">
                                <div class="card-body">
                                    <p id="sub_event_name_error" class="add_event_error"></p>
                                    <p>Subevent name:</p>
                                    <input type="text" class="form-control" id='sub_event_name-${subEvent.subEventID}'
                                        autocomplete="off" name="sub_event_name" placeholder="Event name" value="${subEvent.name}" disabled>

                                    <p id="sub_event_location_error" class="add_event_error"></p>
                                    <p>Location:</p>
                                    <input type="text" class="form-control" id="sub_event_location-${subEvent.subEventID}"
                                        autocomplete="off" name="sub_event_location" placeholder="Please click on the map." value="${subEvent.location_building} ${subEvent.room}" disabled>

                                    <p id="sub_event_desc_error" class="add_event_error"></p>
                                    <p>Description:</p>
                                    <textarea rows="4" class="form-control" id="sub_event_desc-${subEvent.subEventID}" autocomplete="off" name="sub_event_desc"
                                        placeholder="Event description"  disabled>${subEvent.desc_show_in_website}</textarea>
                                        
                                                        
                                    <p id="sub_event_desc_url_error" class="add_event_error"></p>
                                    <p>Description URL:</p>
                                    <input type="text" class="form-control" id="sub_event_desc_url-${subEvent.subEventID}"
                                        autocomplete="off" name="sub_event_desc_url" placeholder="Event description URL" value="${subEvent.desc_url}" disabled>

                                    <p id="sub_event_desc_app_error" class="add_event_error"></p>
                                    <textarea rows="4" class="form-control" id="sub_event_desc_app-${subEvent.subEventID}" autocomplete="off" name="sub_event_desc_app"
                                        placeholder="Event description" disabled hidden>${subEvent.desc_show_in_app}</textarea>

                                    <p id="sub_event_action_title_error" class="add_event_error"></p>
                                    <p>Action Title:</p>
                                    <input type="text" class="form-control" id="sub_event_action_title-${subEvent.subEventID}" autocomplete="off"
                                        name="sub_event_action_title" placeholder="Event action title" value="${subEvent.action_title}" disabled>

                                    <p id="sub_event_action_desc_error" class="add_event_error"></p>
                                    <p>Action description:</p>
                                    <textarea rows="4" class="form-control" id="sub_event_action_desc-${subEvent.subEventID}" autocomplete="off"
                                        name="sub_event_action_desc" placeholder="Action description" disabled>${subEvent.action_desc}</textarea>

                                    <p id="sub_event_action_url_error" class="add_event_error"></p>
                                    <p>Action URL:</p>
                                    <input type="text" class="form-control" id="sub_event_action_url-${subEvent.subEventID}" autocomplete="off"
                                        name="sub_event_action_url" placeholder="Action description URL" value="${subEvent.action_url}" disabled>

                                    <p id="sub_event_start_time_error" class="add_event_error"></p>
                                    <p>Start time:</p>
                                    <input id="sub_event_start_time-${subEvent.subEventID}" class="form-control" type="datetime-local"
                                         name="sub_event_start_time-${subEvent.subEventID}" value="${subEvent.start_time}"  disabled>
                                        
                                    <p id="sub_event_end_time_error" class="add_event_error"></p>
                                    <p>End time:</p>
                                    <input id="sub_event_end_time-${subEvent.subEventID}" name="sub_event_end_time-${subEvent.subEventID}" class="form-control" type="datetime-local" placeholder="YYYY-MM-DD HH:MM"
                                    value="${subEvent.end_time}" disabled>
                                    
                                    <input id="sub_event_lat-${subEvent.subEventID}" type="hidden" value="${subEvent.location_lat}">
                                    <input id="sub_event_lng-${subEvent.subEventID}" type="hidden" value="${subEvent.location_lng}">
                                    <input id="sub_event_floor-${subEvent.subEventID}" type="hidden" value="${subEvent.floor}">
                                    <input id="sub_event_building-${subEvent.subEventID}" type="hidden" value="${subEvent.location_building}">
                                    <input id="sub_event_room-${subEvent.subEventID}" type="hidden" value="${subEvent.room}">
                                    <input id="sub_event_pid-${subEvent.subEventID}" type="hidden" value="${subEvent.pid}">
                                    
                                    ${editButtonCode}
                                    ${showQRCodeButtonCode}
                                    ${deleteButtonCode}
                                </div>
                            </div>
                        </div>
                        <script>
                        addSubMarker(${subEvent.location_lat}, ${subEvent.location_lng}, ${subEvent.pid}, ${subEvent.subEventID}, "${subEvent.name}", ${subEvent.floor})

                        
                        if(addEventListenerIDIntoDict("${subBlockCardID}", "show.bs.collapse"))
                        {
                            $("#${subBlockCardID}").on('show.bs.collapse', function(event){
                            changeLayers(${subEvent.floor})
                            setCurrentViewSubEventID(${subEvent.subEventID});
                            scrollToSubEvent(${subEvent.pid}, ${subEvent.subEventID});
                            event.stopPropagation();
                            });
                        }
                        if(addEventListenerIDIntoDict("${subBlockCardID}", "hidden.bs.collapse")){
                            $("#${subBlockCardID}").on('hidden.bs.collapse', function (event){
                            closeSubEventPopup(${subEvent.pid}, ${subEvent.subEventID});
                            cleanCurrentViewSubEventID();
                            event.stopPropagation();
                        })
                        }

                        </script>
                        <script id="sub_event_script-${subEvent.subEventID}" name="sub_event_script-${subEvent.subEventID}"></script>
                    </div>
                    `;
    }
    return htmlCode;
}


function comfirmDeleteParentEvent(pid) {
    var r = confirm("Would you want delete this event?");

    if (r) {
        deleteParentEvent(pid)
    }

}

function comfirmDeleteSubEvent(sid, pid) {
    var r = confirm("Would you want delete this event?");

    if (r) {
        deleteSubEvent(sid, pid)
    }

}

function subEventDeleteButtonCode(sid, owner, pid) {
    let htmlCode = "";
    if (isEventOwner(owner) === true) {
        htmlCode +=
            `<button type="button" class="btn btn-outline-primary" id="sub_event_delete" onclick="comfirmDeleteSubEvent(${sid}, ${pid})">Delete</button>`
    }

    return htmlCode;
}

function generateParentEventEditButtonCode(pid, owner) {
    let htmlCode = "";
    if (isEventOwner(owner) === true) {
        htmlCode += `<button type="button" class="btn btn-outline-primary" id="parent_event_edit" onclick="editParentEvent(${pid})">Edit</button>`
    }
    return htmlCode
}


function scrollToSubEvent(pid, sid) {
    if (pid === -1 || sid === -1) {
        return;
    }
    hideSearchBar();
    let subeventBlock_id = generateSubEventBlockID(sid);
    let parenteventBlock_id = generateParentEventBlockID(pid);
    if (addEventListenerIDIntoDict(subeventBlock_id, "shown.bs.collapse") === true) {
        $("#" + subeventBlock_id).on('shown.bs.collapse', function (event) {
            showSubEventPopup(pid, sid)
            let parentEventIndex = getParentEventCardIndexInParentEventList(parenteventBlock_id);
            let subEventIndex = getSubEventCardIndexInSubEventContainer(parenteventBlock_id, subeventBlock_id);
            let container_size = getParentEventContainerSize(pid);
            let parentEventCardSize = getParentEventCardSize();
            let subEventCardSize = getSubEventCardSize();
            let card_body_padding_top = parseInt($(`.card-body`).css('padding-top').replace("px", ""));
            let scrollPosition_subEvent = parentEventCardSize * parentEventIndex + parentEventCardSize + card_body_padding_top + container_size + subEventIndex * subEventCardSize;
            $('#parent-event-list').animate({
                scrollTop: scrollPosition_subEvent
            }, 0);
            setCurrentViewParentEventID(pid);
            setCurrentViewSubEventID(sid);
            collapseSiblingSubEvent(pid, sid);
            showEventMap();
            event.stopPropagation();

        });
    }


}

function hideSearchBar() {
    $("#SearchOption").collapse('hide');
}

function scrollToParentEvent(pid) {
    if (pid === -1) {
        return;
    }
    hideSearchBar();
    let id = generateParentEventBlockID(pid);

    if (addEventListenerIDIntoDict(id, "shown.bs.collapse") === true) {
        $(`#${id}`).on('shown.bs.collapse', function (event) {
            let parentEventIndex = getParentEventCardIndexInParentEventList(id);
            let cardSize = getParentEventCardSize();
            let scrollPosition = cardSize * parentEventIndex;
            showEventMap();
            $('#parent-event-list').animate({
                scrollTop: scrollPosition
            }, 0);
            setCurrentViewParentEventID(pid);

            event.stopPropagation();

        });
    }
}

function setCurrentViewParentEventID(pid) {
    g_current_view_parent_event_id = pid;
}

function setCurrentViewSubEventID(sid) {
    g_current_view_sub_event_id = sid;
}

function cleanCurrentViewSubEventID() {
    g_current_view_sub_event_id = -1;
}

function cleanCurrentViewParentEventID() {
    g_current_view_parent_event_id = -1;
}

function getEditSubEventWidgetIDByWidgetName(name, sid) {
    return `${name}-${sid}`
}

function setEditSubEventStatusToEditable(sid) {
    Status_edit_sub_event = true;
    g_edit_sub_event_id = sid;
}

function setEditSubEventStatusToDisediable() {
    Status_edit_sub_event = false;
    g_edit_sub_event_id = -1;
}

function editSubEvent(sid) {
    setEditSubEventStatusToEditable(sid);
    let cardBlockID = generateSubEventBlockID(sid);
    let cardID = generateSubEventBlockCardID(sid);
    let subEventName = $(`#${cardID}`).find("a").val();

    g_location_lat = getEditEventValue("sub_event_lat", sid);
    g_location_lng = getEditEventValue("sub_event_lng", sid);
    g_location_room = getEditEventValue("sub_event_room", sid);
    g_location_floor = getEditEventValue("sub_event_floor", sid);
    g_location_building = getEditEventValue("sub_event_building", sid);

    let inputs = $(`#${cardBlockID}`).find("input");
    let locationInputID = generateEditElementID("sub_event_location", sid);

    for (let i = 0; i < inputs.length; i++) {
        if (inputs[i].id !== locationInputID && inputs[i].id.length != 0) {
            $(`#${inputs[i].id}`).removeAttr("disabled");
        }
    }
    let textAreas = $(`#${cardBlockID}`).find("textarea");
    for (let i = 0; i < textAreas.length; i++) {
        $(`#${textAreas[i].id}`).removeAttr("disabled");
    }

    let script_id = getEditSubEventWidgetIDByWidgetName("sub_event_script", sid);
    let start_time_id = getEditSubEventWidgetIDByWidgetName("sub_event_start_time", sid);
    let end_time_id = getEditSubEventWidgetIDByWidgetName("sub_event_end_time", sid);

    // Set start time end time callback function.
    //$(`#${script_id}`).html(timeDatePicker.initDate(start_time_id, end_time_id));

    let buttons = $(`#${cardBlockID}`).find("button");
    for (let i = 0; i < buttons.length; i++) {
        buttons[i].remove();
    }

    let $submitButtion = $(`<button type="button" class="btn btn-outline-primary" id="sub_event_submit_button" onclick="submitEditSubEvent(${sid})">Save</button>`);
    let $cancelButton = $(`<button type="button" class="btn btn-outline-primary" id="sub_event_cancel_button" onclick="cancelEditSubEvent()">Cancel</button>`);
    $(`#${cardBlockID}`).children(".card-body").append($submitButtion);
    $(`#${cardBlockID}`).children(".card-body").append($cancelButton);


}


function getEditEventValue(elevemtId, sid) {
    let id = generateEditElementID(elevemtId, sid);
    let value = $(`#${id}`).val();
    return value;

}


function generateEditElementID(elementID, sid) {
    return `${elementID}-${sid}`;
}


function getParentEventCardIndexInParentEventList(pid) {
    let parentEventList = $(
        `#parent-event-list`
    ).find(
        `div.parent_event_class`
    );
    let counter = 0;
    for (counter = 0; counter < parentEventList.length; counter++) {
        if (parentEventList[counter].id === pid) {
            break;
        }

    }
    return counter;
}

function getSubEventCardIndexInSubEventContainer(pid, sid) {
    let subEventList = $(
        `#${pid}`
    ).find(
        `.sub_event_class`
    )
    let counter = 0;
    for (counter = 0; counter < subEventList.length; counter++) {
        if (subEventList[counter].id === sid) {
            break;
        }
    }

    return counter;

}

function getParentEventCardSize() {
    let element = $(
        `.parent_event_card_header`
    );
    let height = element.outerHeight();
    return height;
}

function getSubEventCardSize() {
    let element = $(
        `.sub_event_card_header`
    );
    let height = element.outerHeight();
    return height;

}

function getParentEventContainerSize(pid) {
    let parentEventBlockID = generateParentEventBlockID(pid);
    let block = $(
        `#${parentEventBlockID}`
    ).find(".parent_event_container");
    let height = block.outerHeight();
    return height
}

function collapseSiblingSubEvent(pid, sid) {
    let parenteventContainerID = generateSubEventListContainerID(pid);
    let show_subEventBlocks = $(`#${parenteventContainerID}`).find(".show");
    let subeventBlockID = generateSubEventBlockID(sid);
    for (let i = 0; i < show_subEventBlocks.length; i++) {
        let subevent = show_subEventBlocks[i];
        if (subevent.id !== subeventBlockID) {
            $(`#${subevent.id}`).collapse("hide");
        }
    }


}

function resizeElementByID(ID){
    $("#" + ID.toString()).resize();
}


















