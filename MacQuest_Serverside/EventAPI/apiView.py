"""
Created by Jason
2018/08/19
Purpose: RestFramework view.
"""

from rest_framework import viewsets

from EventManage.models import ParentEvent, SubEvent, UserLocation
from EventAPI.serializers import ParentEventSerializer, SubEventSerializer, UserLocationSerializer
from EventManage.parentEventSearch import updateParentEventPeriodByPID
from EventManage.subEventManage import updateAllSubEventIsActiveField


# Parent event view.
class ParentEventView(viewsets.ReadOnlyModelViewSet):
    allParentEvents = ParentEvent.objects.all()
    for event in allParentEvents:
        updateParentEventPeriodByPID(event.id)
    queryset = ParentEvent.objects.filter(isActive=True)
    serializer_class = ParentEventSerializer


# Sub-event view.
class SubEventView(viewsets.ReadOnlyModelViewSet):
    updateAllSubEventIsActiveField()
    queryset = SubEvent.objects.filter(isActive=True)
    serializer_class = SubEventSerializer


# Userlocation view.
class UserLocationView(viewsets.ModelViewSet):
    queryset = UserLocation.objects.all()
    serializer_class = UserLocationSerializer
