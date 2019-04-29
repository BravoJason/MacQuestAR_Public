"""
Created by Jason
2018/08/19
Purpose: Heatmap user location handle file.
"""

from EventManage.models import UserLocation
from EventManage.subEventManage import convertSubEventTimeToObj


# Pick up param from requrest.
def pickParamForHeatMapFromPostRequest(request):
    status = True
    param = {}
    if "start_time" in request.GET:
        param["start_time"] = convertSubEventTimeToObj(request.GET["start_time"])
    else:
        status = False

    if "end_time" in request.GET:
        param["end_time"] = convertSubEventTimeToObj(request.GET["end_time"])
    else:
        status = False

    if "pid" in request.GET:
        param["pid"] = request.GET["pid"]
    else:
        status = False

    return param, status


# Search user location based on the parameters(Start time, end time).
def searchUserLocationForHeatMap(param):
    locations = UserLocation.objects.all().filter(JoinedParentEventID=param["pid"],
                                                  time__range=[param["start_time"], param["end_time"]])
    ans = []
    for location in locations:
        ans.append({
            "id": location.id,
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [
                    location.Location_Lng,
                    location.Location_lat

                ]
            }
        })
    return ans


# Delete user location by parent event ID.
def deleteUserLocationByPID(pid):
    UserLocation.objects.all().filter(JoinedParentEventID=pid).delete()
