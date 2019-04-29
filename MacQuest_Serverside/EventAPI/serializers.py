"""
Created by Jason
2018/08/19
Purpose: Event API serializers.
"""
from rest_framework import serializers

from Accounts.models import User
from EventManage.models import ParentEvent, SubEvent, UserLocation


# User model serializer.
class UserEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ("first_name", "last_name")


# Parent event model serializer.
class ParentEventSerializer(serializers.HyperlinkedModelSerializer):
    owner_full_name = serializers.ReadOnlyField()

    class Meta:
        model = ParentEvent
        fields = (
            'id', 'name', 'description', "owner_full_name", 'authentication', 'private_password', 'start_time',
            'end_time',
            'isActive')


# Subevent model serializer.
class SubEventSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = SubEvent
        fields = (
            'id', 'name', 'parentEvent_id', 'location_building', 'location_lat', 'location_lng', 'floor', 'room',
            'desc_has_url',
            'desc_url',
            'desc_show_in_app', 'desc_show_in_website', 'action_title', 'action_has_action', 'action_desc',
            'action_has_url',
            'action_url', 'start_time', 'end_time')


# Userlocation model serializer.
class UserLocationSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = UserLocation
        fields = (
            "DeviceID", "JoinedParentEventID", "Location_lat", "Location_Lng", "time"
        )
