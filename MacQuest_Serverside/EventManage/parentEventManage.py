"""
Created by Jason
2018/08/23
Purpose: Parent event manage file.
"""

import datetime
import json
from json import JSONDecodeError

from Accounts.models import User
from EventAPI.heatmapData import deleteUserLocationByPID
from EventManage.models import ParentEvent, event_auth_private


# Add  parent event.
def addParentEventIntoDataBase(form, request):
    parentEventData = form.save(commit=False)
    parentEventData.owner = request.user
    parentEventData.start_time = datetime.datetime.min
    parentEventData.end_time = datetime.datetime.min
    parentEventData.save()
    parentEventData.viewer.add(request.user)
    updateViewerList(parentEventData, request.POST["email-list"])


# Save predefined viewers into database.
def updateViewerList(parentObj, emailListStr):
    if parentObj.authentication == event_auth_private and emailListStr != "":
        try:
            emailList = json.loads(emailListStr)
            emailList = list(set(emailList))
            if len(emailList) > 0:
                for email in emailList:
                    viewerUser = User.objects.all().filter(email=email.strip().lower())
                    for user in viewerUser:
                        parentObj.viewer.add(user)
        except JSONDecodeError:
            print("emailListError")


# Delete parent event function.
def deleteParentEvent(pid, owner):
    event = ParentEvent.objects.get(id=pid)

    status = False

    if event.owner_id == owner.id:
        event.delete()
        deleteUserLocationByPID(pid)

        status = True

    return status


# Get parent event object by event id.
def getParentEventObjByID(id):
    event = ParentEvent.objects.get(id=id)
    return event


# Get parent event parameters from post array.
def pickParamFromEditedParentEvent(postArray):
    param = {}
    if "id" in postArray:
        param["id"] = postArray["id"]
    if "name" in postArray:
        param["name"] = postArray["name"]
    if "description" in postArray:
        param["description"] = postArray["description"]
    if "authentication" in postArray:
        param["authentication"] = postArray["authentication"]
    if "private_password" in postArray:
        if len(postArray["private_password"]) > 0:
            param["private_password"] = postArray["private_password"]
    if "start_time" in postArray:
        param["start_time"] = postArray["start_time"]
    if "end_time" in postArray:
        param["end_time"] = postArray["end_time"]
    return param


# Update parent event information in database.
def updateParentEvent(param):
    id = param["id"]
    param.pop("id")
    ParentEvent.objects.filter(id=id).update(**param)


# Get parent event waiting list.
def getWaitingList(event: ParentEvent, owner_id):
    ans = []
    waitingList = event.waitingList.all()
    for user in waitingList:
        if user.id != owner_id:
            ans.append({
                "id": user.id,
                "lastName": user.last_name,
                "firstName": user.first_name
            })
    return ans


# Get event viewer list.
def getViewerList(event, owner_id):
    ans = []
    viewerList = event.viewer.all()
    for user in viewerList:
        if user.id != owner_id:
            ans.append({
                "id": user.id,
                "lastName": user.last_name,
                "firstName": user.first_name
            })
    return ans


# Get event waiting list and viewer list.
def getPrivateParentEventPermissionInfo(userID):
    ans = []
    events = ParentEvent.objects.all().filter(owner_id=userID, authentication=event_auth_private)
    for event in events:
        waitingList = getWaitingList(event, userID)
        viewerList = getViewerList(event, userID)
        ans.append({
            "eventName": event.name,
            "eventID": event.id,
            "viewerList": viewerList,
            "waitingList": waitingList
        })
    return ans


# Get the info which the user is already in the viewer list.
def getUserAllCanViewPrivateEvent(userID):
    ans = []
    allEvents = ParentEvent.objects.all().filter(authentication=event_auth_private)
    for event in allEvents:
        if event.owner.id != userID:
            if event.viewer.all().filter(id=userID).exists():
                ans.append({
                    "eventName": event.name,
                    "id": event.id,
                    "ownerInfo": {"ownerID": event.owner.id,
                                  "ownerLastName": event.owner.last_name,
                                  "ownerFirstName": event.owner.first_name}
                })
    return ans


# Get the info which the user is already in the waiting list.
def getUserAllWaitingPrivateEvent(userID):
    ans = []
    allEvents = ParentEvent.objects.all().filter(authentication=event_auth_private)
    for event in allEvents:
        if event.waitingList.all().filter(id=userID).exists():
            ans.append({
                "eventName": event.name,
                "id": event.id,
                "ownerInfo": {"ownerID": event.owner.id,
                              "ownerLastName": event.owner.last_name,
                              "ownerFirstName": event.owner.first_name}
            })
    return ans


# Get the info which the user isn't neither in the waiting list nor in the viewer list.
def getUserCanApplyPermitionPrivateEvent(userID):
    ans = []
    allEvents = ParentEvent.objects.all().filter(authentication=event_auth_private)
    for event in allEvents:
        if (not event.waitingList.all().filter(id=userID).exists()) and (
                not event.viewer.all().filter(id=userID).exists()):
            ans.append({
                "eventName": event.name,
                "id": event.id,
                "ownerInfo": {"ownerID": event.owner.id,
                              "ownerLastName": event.owner.last_name,
                              "ownerFirstName": event.owner.first_name}
            })
    return ans


# Get viewer permission info.
def getViewerPermissionInfo(userID):
    viewerPermissionInfo = getUserAllCanViewPrivateEvent(userID)
    waitingPermissionInfo = getUserAllWaitingPrivateEvent(userID)
    appliablePermissionInfo = getUserCanApplyPermitionPrivateEvent(userID)
    ans = {"viewerInfo": viewerPermissionInfo, "waitingInfo": waitingPermissionInfo,
           "appliableInfo": appliablePermissionInfo}
    return ans


# Apply private event permission.
def applyPermission(user, pid):
    events = ParentEvent.objects.all().filter(id=pid, authentication=event_auth_private)
    for event in events:
        if (not event.waitingList.all().filter(id=user.id).exists()) and (
                not event.viewer.all().filter(id=user.id).exists()):
            event.waitingList.add(user)


# Remove user from event waiting list.
def removeUserFromWaitingList(user, pid):
    events = ParentEvent.objects.all().filter(id=pid, authentication=event_auth_private)
    for event in events:
        if event.waitingList.all().filter(id=user.id).exists():
            event.waitingList.remove(user)


# Remove user from event waiting list.
def removeUserFromViewerList(user, pid):
    events = ParentEvent.objects.all().filter(id=pid, authentication=event_auth_private)
    for event in events:
        if event.viewer.all().filter(id=user.id).exists():
            event.waitingList.remove(user)


# Planner accept viewer permission.
def accpetViewerPermission(viewerID, pid, owner):
    events = ParentEvent.objects.all().filter(id=pid, owner_id=owner.id)
    viewer = User.objects.all().filter(id=viewerID)
    for event in events:
        if event.waitingList.all().filter(id=viewerID).exists():
            for viewer_ in viewer:
                event.viewer.add(viewer_)
                event.waitingList.remove(viewer_)


# Planner cancel viewer permission.
def cancelViewerPermission(viewerID, pid, owner):
    events = ParentEvent.objects.all().filter(id=pid, owner_id=owner.id)
    viewer = User.objects.all().filter(id=viewerID)
    for event in events:
        for viewer_ in viewer:
            if event.viewer.all().filter(id=viewerID).exists():
                event.viewer.remove(viewer_)


# Convert string to number
def convertStrToInt(param):
    if type(param) is type("a"):
        return int(param)
    else:
        return param
