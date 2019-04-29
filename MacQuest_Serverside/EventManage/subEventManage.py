import datetime

from EventManage.models import SubEvent, ParentEvent
from EventManage.parentEventSearch import updateParentEventPeriodByPID


def convertSubEventTimeToObj(str):
    str = str.strip()
    return datetime.datetime.strptime(str, "%Y-%m-%dT%H:%M")


def preHandleSubEventInfo(param):
    databaseObj = {}
    # Set start_time
    start_time = convertSubEventTimeToObj(param["sub_event_start_time"])
    databaseObj["start_time"] = start_time
    # Set end time
    end_time = convertSubEventTimeToObj(param["sub_event_end_time"])
    databaseObj["end_time"] = end_time
    # Set is active.
    if datetime.datetime.now() <= end_time:
        databaseObj["isActive"] = True
    else:
        databaseObj["isActive"] = False

    # Set sub event name.
    name = param["sub_event_name"]
    databaseObj["name"] = name

    # Set building.
    building = param["sub_event_building"]
    databaseObj["location_building"] = building

    # Set lat
    location_lat = param["sub_event_lat"]
    databaseObj["location_lat"] = location_lat

    # Set lng
    location_lng = param["sub_event_lng"]
    databaseObj["location_lng"] = location_lng

    # Set floor
    floor = param["sub_event_floor"]
    databaseObj["floor"] = floor

    # Set room
    room = param["sub_event_room"]
    databaseObj["room"] = room

    # Set pid
    parentEventID = param["sub_event_pid"]
    parentEvent = ParentEvent.objects.get(id=parentEventID)
    databaseObj["parentEvent"] = parentEvent

    # Set description URL
    desc_url = param["sub_event_desc_url"]
    databaseObj["desc_url"] = desc_url

    if len(desc_url) > 0:
        databaseObj["desc_has_url"] = True
    else:
        databaseObj["desc_has_url"] = False

    # Set desc_show_in_app
    desc_show_in_app = param["sub_event_desc_app"]
    databaseObj["desc_show_in_app"] = desc_show_in_app

    # Set desc_show_in_website
    desc_show_in_website = param["sub_event_desc"]
    databaseObj["desc_show_in_website"] = desc_show_in_website

    # Set action_title
    action_title = param["sub_event_action_title"]

    if len(action_title) <= 0:
        action_title = "Could you do this action?"
    databaseObj["action_title"] = action_title

    # Set action_desc
    action_desc = param["sub_event_action_desc"]
    databaseObj["action_desc"] = action_desc

    # Set action_url
    action_url = param["sub_event_action_url"]
    databaseObj["action_url"] = action_url

    # Set action_has_action
    if len(action_desc) > 0:
        action_has_action = True
    else:
        action_has_action = False

    databaseObj["action_has_action"] = action_has_action

    # Set action_has_url
    if len(action_url) > 0 and action_has_action is True:
        action_has_url = True
    else:
        action_has_url = False
    databaseObj["action_has_url"] = action_has_url

    # Set sub event ID
    if "sid" in param:
        databaseObj["id"] = param["sid"]

    return databaseObj


def saveSubEventDataIntoDatabase(data):
    subEvent = SubEvent(**data)
    subEvent.save()
    return subEvent


def pickSubEventParamterFromPostArray(PostArray):
    paramterDict = {}
    if "pid" in PostArray:
        paramterDict["parentEvent_id"] = PostArray["pid"]
    if "sid" in PostArray:
        paramterDict["id"] = PostArray["sid"]

    return paramterDict


def deleteSubEvent(sid, owner):
    event = SubEvent.objects.get(id=sid)
    status = False
    if event.parentEvent.owner_id == owner.id:
        event.delete()
        status = True
    return status


def updateSubEvent(sid, owner, param):
    status = False
    param.pop("id")
    event = SubEvent.objects.get(id=sid)
    if event.parentEvent.owner_id == owner.id:
        updateParentEventPeriodByPID(event.parentEvent.id)
        SubEvent.objects.filter(id=sid).update(**param)
        status = True
    return status


def handleTimeString(str):
    str = str.strip()
    if len(str) <= 18:
        return str + ":00"
    return str;


def updateAllSubEventIsActiveField():
    allSubEvents = SubEvent.objects.all()
    for event in allSubEvents:
        if event.end_time > datetime.datetime.now():
            event.isActive = True
        else:
            event.isActive = False
        event.save()
