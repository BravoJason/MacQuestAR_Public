import json

from django.contrib import messages
from django.contrib.auth.decorators import login_required
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse, HttpResponseBadRequest
from django.shortcuts import render
# Create your views here.
from django.urls import reverse

from EventManage import datetimeConvertor
from EventManage.decorators import group_required, ajax_required
from EventManage.eventIndex import buildSchema_parentEvent, buildSchema_subEvent
from EventManage.forms import addEventForm, addSubEventForm
from EventManage.parentEventManage import addParentEventIntoDataBase, deleteParentEvent, getParentEventObjByID, \
    pickParamFromEditedParentEvent, updateParentEvent, getPrivateParentEventPermissionInfo, getViewerPermissionInfo, \
    applyPermission, removeUserFromWaitingList, removeUserFromViewerList, accpetViewerPermission, \
    cancelViewerPermission, convertStrToInt
from EventManage.parentEventSearch import pickParentEventParameterFromPostArray, getParentEventByParam, \
    updateParentEventPeriodByPID, getPIDbySID
from EventManage.subEventManage import preHandleSubEventInfo, saveSubEventDataIntoDatabase, \
    pickSubEventParamterFromPostArray, deleteSubEvent, updateSubEvent, handleTimeString
from EventManage.subEventSearch import getSubEventByParam

'''
def searchView(request):
    if request.is_ajax() == True and 'q' in request.POST:
        kw = request.POST['q']
    else:
        kw = ""
    if 'timestamp' in request.POST:
        timestamp = int(request.POST['timestamp'])
    else:
        timestamp = 0
    if len(kw) != 0:
        ans = getParentEventByName(kw)
        searchAns = {"timestamp": timestamp, "ans": ans}
        searchAns = json.dumps(searchAns, default=datetimeConvertor.datetimeConvertor)
        logging.info("ans: " + str(searchAns))
    else:
        searchAns = {"timestamp": timestamp, "ans": []}

    return JsonResponse(searchAns, safe=False)

def searchActiveParentEvent(request):
    if request.is_ajax() == True and 'q' in request.POST:
        kw = request.POST['q']
    else:
        kw = ""
    if 'timestamp' in request.POST:
        timestamp = int(request.POST['timestamp'])
    else:
        timestamp = 0
    if len(kw) != 0:
        ans = getParentEventByName(kw)
        ans = filterParentEventActive(True, ans)
        searchAns = {"timestamp": timestamp, "ans": ans}
        searchAns = json.dumps(searchAns, default=datetimeConvertor.datetimeConvertor)
        logging.info("ans: " + str(searchAns))
    else:
        searchAns = {"timestamp": timestamp, "ans": []}

    return JsonResponse(searchAns, safe=False)


@ajax_required
# Function to search parent event.
def searchParentEvent(request):
    if request.is_ajax() == True and 'q' in request.POST:
        kw = request.POST['q']
    else:
        kw = ""
    if 'timestamp' in request.POST:
        timestamp = int(request.POST['timestamp'])
    else:
        timestamp = 0

    # Get is_active keyword form ajax request.
    is_active_kw = ""
    if 'is_active' in request.POST:
        is_active_kw = request.POST['is_active']

    if is_active_kw == "true":
        is_active = True
    else:
        is_active = False

    if len(kw) != 0:
        ans = getParentEventByName(kw)
        ans = filterParentEventActive(is_active, ans)
        searchAns = {"timestamp": timestamp, "ans": ans}
        logging.info("ans: " + str(searchAns))
    else:
        searchAns = {"timestamp": timestamp, "ans": []}
    searchAns = json.dumps(searchAns, default=datetimeConvertor.datetimeConvertor)

    return JsonResponse(searchAns, safe=False)
'''


@ajax_required
def searchParentEventByParam(request):
    param_ = pickParentEventParameterFromPostArray(request.POST)
    ans = getParentEventByParam(param_, request.user)
    ans = {"ans": ans}
    searchAns = json.dumps(ans, default=datetimeConvertor.datetimeConvertor)

    return JsonResponse(searchAns, safe=False)


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
def addParentEventView(request):
    if request.method == "POST":
        form = addEventForm(request.POST)

        if form.is_valid():
            addParentEventIntoDataBase(form, request)
            messages.success(request, "Add the parent event successful.")

            return HttpResponseRedirect(reverse('index'))
    else:
        form = addEventForm()
    return render(request, 'EventManage/addparentevent.html', {'form': form})


def buildSchemaView(request):
    buildSchema_parentEvent()
    buildSchema_subEvent()
    return HttpResponse("Build finished")


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
@ajax_required
def addSubEvent(request):
    post_data = request.POST.copy()
    '''
    start_time = handleTimeString(post_data["sub_event_start_time"])
    end_time = handleTimeString(post_data["sub_event_end_time"])
    post_data.update({"sub_event_start_time": start_time})
    post_data.update({"sub_event_end_time": end_time})
    '''
    f = addSubEventForm(post_data)
    if f.is_valid():
        dataObj = preHandleSubEventInfo(f.data)
        subEventObject = saveSubEventDataIntoDatabase(dataObj)
        # subEventDocumentUpdate(subEventObject)
        updateParentEventPeriodByPID(subEventObject.parentEvent.id)

        print("Successful")
        result = {"status": "Successful", "subEventID": subEventObject.id, "pid": subEventObject.parentEvent.id}
        result_json = json.dumps(result, default=datetimeConvertor.datetimeConvertor)
        return JsonResponse(result_json, safe=False)
    else:
        result = {"status": "Fail", "errors": f.errors}
        result_json = json.dumps(result, default=datetimeConvertor.datetimeConvertor)
        return JsonResponse(result_json, safe=False)


@ajax_required
def searchSubEventByParam(request):
    param_ = pickSubEventParamterFromPostArray(request.POST)
    subEventAns = getSubEventByParam(param_)
    ans = {"ans": subEventAns}
    searchAns = json.dumps(ans, default=datetimeConvertor.datetimeConvertor)

    return JsonResponse(searchAns, safe=False)


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
@ajax_required
def deleteParentEventView(request):
    pid = convertStrToInt(request.POST["pid"])
    owner = request.user
    status = deleteParentEvent(pid, owner)

    if status is True:
        ans = {"title": "Delete parent event.", "info": "Delete successful."}
    else:
        ans = {"title": "Delete parent event.", "info": "Delete failed."}
    ans = json.dumps(ans);
    return JsonResponse(ans, safe=False)


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
@ajax_required
def deleteSubEventView(request):
    sid = convertStrToInt(request.POST["sid"])
    owner = request.user
    pid = getPIDbySID(sid)
    status = deleteSubEvent(sid, owner)
    updateParentEventPeriodByPID(pid)

    if status is True:
        ans = {"title": "Delete subevent.", "info": "Delete successful."}
    else:
        ans = {"title": "Delete subevent.", "info": "Delete failed."}
    ans = json.dumps(ans)
    return JsonResponse(ans, safe=False)


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
def editParentEventView(request):
    if request.method == "GET":
        if "pid" in request.GET:
            id = request.GET['pid']
            parentEventObj = getParentEventObjByID(id)
            owner = request.user
            if owner.id != parentEventObj.owner.id:
                messages.success(request, "You can not edit this event.")
                return HttpResponseRedirect(reverse('index'))
            else:
                ans = parentEventObj.__dict__
                return render(request, "EventManage/editparentevent.html", {"parentEvent": ans})
        else:
            return HttpResponseRedirect(reverse("index"))
    if request.method == "POST":
        param = pickParamFromEditedParentEvent(request.POST)
        updateParentEvent(param)
        messages.success(request, "Edit the parent event successful.")
        return HttpResponseRedirect(reverse('index'))


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
@ajax_required
def editSubEvent(request):
    '''
    try:
        param_ = preHandleSubEventInfo(request.POST)
    except ValueError:
        ans = {"title": "Edit subevent.", "info": "Submit value error."}
        result_json = json.dumps(ans, default=datetimeConvertor.datetimeConvertor)
        return HttpResponseBadRequest(result_json)
    else:
        status = updateSubEvent(param_["id"], request.user, param_)
        if status:
            ans = {"title": "Edit subevent.", "info": "Edit successful."}
        else:
            ans = {"title": "Edit subevent.", "info": "Edit Failed."}

        result_json = json.dumps(ans, default=datetimeConvertor.datetimeConvertor)
        return JsonResponse(result_json, safe=False)
'''
    post_data = request.POST.copy()
    '''
    start_time = handleTimeString(post_data["sub_event_start_time"])
    end_time = handleTimeString(post_data["sub_event_end_time"])
    post_data.update({"sub_event_start_time": start_time})
    post_data.update({"sub_event_end_time": end_time})
    '''
    f = addSubEventForm(post_data)

    if f.is_valid():
        param_ = preHandleSubEventInfo(f.data)
        status = updateSubEvent(param_["id"], request.user, param_)

        print("Successful")
        if status:
            ans = {"title": "Edit subevent.", "info": "Edit successful."}
            result_json = json.dumps(ans, default=datetimeConvertor.datetimeConvertor)
            return JsonResponse(result_json, safe=False)
        else:
            ans = {"title": "Edit subevent.", "info": "Edit Failed."}
            result_json = json.dumps(ans, default=datetimeConvertor.datetimeConvertor)
            return HttpResponseBadRequest(result_json)


    else:
        result = {"title": "Edit subevent.", "info": "Edit Failed.", "errors": f.errors}
        result_json = json.dumps(result, default=datetimeConvertor.datetimeConvertor)
        return HttpResponseBadRequest(result_json)


@login_required(login_url="/accounts/login/")
def plannerParentEventAuth(request):
    return render(request, 'EventManage/plannerauth.html', {})


@login_required(login_url="/accounts/login/")
def viewerParentEventAuth(request):
    return render(request, "EventManage/viewerauth.html", {})


# Check user event permission info.
@login_required(login_url="/accounts/login/")
@group_required("event_planner")
@ajax_required
def getPlannerPermissionRequest(request):
    owner_id = request.user.id
    eventPermissionList = getPrivateParentEventPermissionInfo(owner_id)
    ans = {"list": eventPermissionList}
    result_json = json.dumps(ans, default=datetimeConvertor.datetimeConvertor)
    return JsonResponse(result_json, safe=False)


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
@ajax_required
def acceptPermission(request):
    event_id = convertStrToInt(request.POST["event_id"])
    user_id = convertStrToInt(request.POST["user_id"])
    owner = request.user
    accpetViewerPermission(user_id, event_id, owner)
    results = {"title": "Accept permission", "msg": "Accept viewer permission successful."}
    result_json = json.dumps(results, default=datetimeConvertor.datetimeConvertor)
    return JsonResponse(result_json, safe=False)


@login_required(login_url="/accounts/login/")
@group_required("event_planner")
@ajax_required
def cancelPermission(request):
    event_id = convertStrToInt(request.POST["event_id"])
    user_id = convertStrToInt(request.POST["user_id"])
    owner = request.user
    cancelViewerPermission(user_id, event_id, owner)
    results = {"title": "Cancel permission", "msg": "Cancel viewer permission successful."}
    result_json = json.dumps(results, default=datetimeConvertor.datetimeConvertor)
    return JsonResponse(result_json, safe=False)


@login_required(login_url="/accounts/login/")
@ajax_required
def getViewerAuthPermissionInfo(request):
    owner_id = request.user.id
    ans = getViewerPermissionInfo(owner_id)
    results = {"list": ans}
    result_json = json.dumps(results, default=datetimeConvertor.datetimeConvertor)
    return JsonResponse(result_json, safe=False)


@login_required(login_url="/accounts/login/")
@ajax_required
def applyPrivatePermission(request):
    pid = convertStrToInt(request.POST["pid"])
    user = request.user
    applyPermission(user, pid)
    results = {"title": "Apply permission", "msg": "Apply permission successful."}
    result_json = json.dumps(results, default=datetimeConvertor.datetimeConvertor)
    return JsonResponse(result_json, safe=False)


@login_required(login_url="/accounts/login/")
@ajax_required
def cancelWaitingListPermission(request):
    pid = convertStrToInt(request.POST["pid"])
    user = request.user
    removeUserFromWaitingList(user, pid)
    results = {"title": "Cancel permission", "msg": "Cancel apply permission successful."}
    result_json = json.dumps(results, default=datetimeConvertor.datetimeConvertor)
    return JsonResponse(result_json, safe=False)


@login_required(login_url="/accounts/login/")
@ajax_required
def canceViewerListPermission(request):
    pid = convertStrToInt(request.POST["pid"])
    user = request.user
    removeUserFromViewerList(user, pid)
    results = {"title": "Cancel permission", "msg": "Cancel viewer permission successful."}
    result_json = json.dumps(results, default=datetimeConvertor.datetimeConvertor)
    return JsonResponse(result_json, safe=False)


