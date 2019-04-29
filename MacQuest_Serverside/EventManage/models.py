"""
Created by Jason
2018/08/23
Purpose:  Model file for parent event and sub event.
"""

from django.db import models
from Accounts import models as Accounts_Models
from django.utils import timezone

event_auth_public = "Public"
event_auth_private = "Private"

Authentication_Choice = (
    (event_auth_public, "Public"),
    (event_auth_private, "Private"),
)

# Setting definitions.
LENGTH_DESCRIPTION = 100
LENGTH_WEB_DESCRIPTION = 255
LENGTH_NAME = 100
LENGTH_PASSWORD = 100
LENGTH_DEFAULT = 100
LENGTH_DESCRIPTION_WEBSITE = 999
LENGTH_DESCRIPTION_CLIENT = 100


# Parent event model.
class ParentEvent(models.Model):
    # Basic properties
    name = models.CharField(max_length=LENGTH_NAME, blank=False, unique=True)
    description = models.TextField(max_length=LENGTH_WEB_DESCRIPTION, blank=False)

    authentication = models.CharField(max_length=8, choices=Authentication_Choice, default=event_auth_public)
    private_password = models.CharField(max_length=LENGTH_PASSWORD, blank=True)

    start_time = models.DateTimeField(blank=True, null=True)
    end_time = models.DateTimeField(blank=True, null=True)

    owner = models.ForeignKey(Accounts_Models.User, on_delete=models.CASCADE)

    isActive = models.BooleanField(default=False)

    viewer = models.ManyToManyField(Accounts_Models.User, related_name="viewer")
    waitingList = models.ManyToManyField(Accounts_Models.User, related_name="waitingList")

    @property
    def owner_full_name(self):
        return self.owner.full_name


# Sub event model.
class SubEvent(models.Model):
    # Basic properties
    name = models.CharField(max_length=LENGTH_NAME, blank=False)
    isActive = models.BooleanField(default=True)

    # Location properties
    location_building = models.CharField(max_length=LENGTH_DEFAULT, blank=False)
    location_lat = models.FloatField()
    location_lng = models.FloatField()
    floor = models.IntegerField()
    room = models.CharField(max_length=LENGTH_DEFAULT, blank=True)

    # Parent event settings
    parentEvent = models.ForeignKey(ParentEvent, on_delete=models.CASCADE)

    # Description settings.
    desc_has_url = models.BooleanField(default=False)
    desc_url = models.URLField()
    desc_show_in_app = models.CharField(max_length=LENGTH_DESCRIPTION_CLIENT, blank=False)
    desc_show_in_website = models.CharField(max_length=LENGTH_DESCRIPTION_WEBSITE, blank=False)

    # Action settings.
    action_title = models.CharField(max_length=LENGTH_NAME, blank=True)
    action_has_action = models.BooleanField(default=False)
    action_desc = models.CharField(max_length=LENGTH_DESCRIPTION_CLIENT, blank=True)
    action_has_url = models.BooleanField(default=False)
    action_url = models.URLField()

    # Time settings
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()


# User location model.
class UserLocation(models.Model):
    DeviceID = models.UUIDField()
    JoinedParentEventID = models.IntegerField(blank=True, null=True)
    Location_lat = models.FloatField(blank=True, null=True)
    Location_Lng = models.FloatField(blank=True, null=True)
    time = models.DateTimeField(default=timezone.now)
