"""
Created by Jason
2018/08/19
Purpose: View settings.
"""

import json

from django.http import JsonResponse

# Create your views here.
from EventAPI.heatmapData import pickParamForHeatMapFromPostRequest, searchUserLocationForHeatMap


def getHeatmapGeoJsonByParam(request):
    param, status = pickParamForHeatMapFromPostRequest(request)
    if status is True:
        features = searchUserLocationForHeatMap(param)
        if features is None:
            ans = {"type": "FeatureCollection", "features": []}
        else:
            ans = {"type": "FeatureCollection", "features": features}

    else:
        ans = {"type": "FeatureCollection", "features": []}
    return JsonResponse(ans, safe=False)
