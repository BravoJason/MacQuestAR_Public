import json
import logging

from django.http import JsonResponse, HttpResponse
from django.shortcuts import render

# Create your views here.
from django.template.context_processors import csrf

from SmartSearch.BuildingRoomSearch import searchData
from SmartSearch.building_index import build_schema

logging.basicConfig(format='%(levelname)s:%(message)s', level=logging.DEBUG)


def buildingSearchView(request):
    if request.is_ajax() == True and 'q' in request.POST:
        kw = request.POST['q']
    else:
        kw = ""
    if 'timestamp' in request.POST:
        timestamp = int(request.POST['timestamp'])
    else:
        timestamp = 0
    if len(kw) != 0:
        ans = searchData(kw)
        searchAns = {"timestamp": timestamp, "ans": ans};
        searchAns = json.dumps(searchAns);
        logging.info("ans: " + str(searchAns))
    else:
        searchAns = {"timestamp": timestamp, "ans": []};

    return JsonResponse(searchAns, safe=False)


def createDataIndex(request):
    build_schema()
    return HttpResponse("Finished.")


def test(request):
    print(searchData("ETB"));
    return HttpResponse(searchData("ETB"));
