"""
Created by Jason
2018/08/23
Purpose: Parent event search file.
"""
from datetime import datetime

from Accounts.models import User
from EventManage.models import ParentEvent, SubEvent, event_auth_public


# Function to filter all parent event by active field.
def filterParentEventActive(isActive, parentEvents):
    ans = []
    for event in parentEvents:
        if event['isActive'] == isActive:
            ans.append(event)
    return ans


# Get parent event by given parameters.
def getParentEventByParam(param, viewer):
    if "owner" in param:
        owner = User.objects.get(username=param["owner"])
        param["owner_id"] = owner.id
        param.pop("owner")

    # Update parent event active status.
    updateParentEventIsActive(param)

    events = ParentEvent.objects.filter(**param)

    ans = []

    # Get parent events information.
    for event in events:
        if checkUserIsInViewerTable(event, viewer) or event.authentication == event_auth_public:
            ans.append({"name": event.name,
                        "pid": event.id,
                        "description": event.description,
                        "authentication": event.authentication,
                        "start_time": event.start_time,
                        "end_time": event.end_time,
                        "owner": event.owner.username,
                        "owner_id": event.owner_id,
                        "isActive": event.isActive})

    return ans


# Check user is in the viewer table or not.
def checkUserIsInViewerTable(event, viewer):
    if event.authentication == event_auth_public:
        return True
    if viewer.id is not None:
        result = event.viewer.filter(id=viewer.id)
        if result.count() > 0:
            return True
        else:
            return False
    else:
        return False


# Update parent event active status.
def updateParentEventIsActive(param):
    events = ParentEvent.objects.filter(**param)

    for event in events:
        updateParentEventPeriodByPID(event.id)


# Pick up parameters from the post request from the User.
def pickParentEventParameterFromPostArray(PostArray):
    paramterDict = {}
    if "name" in PostArray:
        paramterDict["name"] = PostArray["name"]
    if "pid" in PostArray:
        paramterDict["id"] = PostArray["pid"]
    if "desc" in PostArray:
        paramterDict["desc"] = PostArray["desc"]
    if "authentication" in PostArray:
        paramterDict["authentication"] = PostArray["authentication"]
    if "private_password" in PostArray:
        paramterDict["private_password"] = PostArray["private_password"]
    if "start_time" in PostArray:
        paramterDict["start_time"] = PostArray["start_time"]
    if "end_time" in PostArray:
        paramterDict["end_time"] = PostArray["end_time"]
    if "owner" in PostArray:
        owner = User.objects.all().filter(username=PostArray["owner"])
        for owner_ in owner:
            paramterDict["owner_id"] = owner_.id
    if "owner_id" in PostArray:
        paramterDict["owner_id"] = PostArray["owner_id"]
    if "isActive" in PostArray:
        if PostArray["isActive"] == "true" or PostArray["isActive"] == "True":
            paramterDict["isActive"] = True
        else:
            paramterDict["isActive"] = False
    return paramterDict


def updateParentEventPeriodByPID(pid):
    parentEvent = ParentEvent.objects.get(id=pid)
    subEvent = SubEvent.objects.all().filter(parentEvent_id=parentEvent.id)
    start_time = datetime.min
    end_time = datetime.min

    for event in subEvent:
        # Get earliest start time.
        if start_time == datetime.min:
            start_time = event.start_time
        else:
            if start_time > event.start_time:
                start_time = event.start_time

        # Get latest end time.
        if end_time == datetime.min:
            end_time = event.end_time
        else:
            if end_time < event.end_time:
                end_time = event.end_time

    parentEvent.start_time = start_time
    parentEvent.end_time = end_time

    if datetime.now() > parentEvent.end_time:
        parentEvent.isActive = False
    else:
        parentEvent.isActive = True

    parentEvent.save()


def updateParentEventPeriodBySID(sid):
    pid = getPIDbySID(sid)
    updateParentEventPeriodByPID(pid)


def getPIDbySID(sid):
    pid = SubEvent.objects.get(id=sid).parentEvent.id
    return pid
