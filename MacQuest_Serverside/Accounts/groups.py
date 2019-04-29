"""
Created by Jason
2018/08/18
Purpose: Group model.
"""

from django.contrib.auth.models import Group, Permission
from django.contrib.contenttypes.models import ContentType

from Accounts.models import User

# Get or create permissions.

ct = ContentType.objects.get_for_model(User)

permission_create_event, created = Permission.objects.get_or_create(codename='create_event',
                                                                    name='Can create an event',
                                                                    content_type=ct)
permission_delete_event, created = Permission.objects.get_or_create(codename='delete_event',
                                                                    name='Can delete event',
                                                                    content_type=ct)

permission_change_event, created = Permission.objects.get_or_create(codename='change_event',
                                                                    name='Can change event',
                                                                    content_type=ct)
permission_view_event, created = Permission.objects.get_or_create(codename='view_event',
                                                                  name='Can view event',
                                                                  content_type=ct)

# Event planner group create
group_event_planner_name = "event_planner"
group_event_planner, created = Group.objects.get_or_create(name=group_event_planner_name)

group_event_planner.permissions.add(permission_create_event)
group_event_planner.permissions.add(permission_delete_event)
group_event_planner.permissions.add(permission_change_event)
group_event_planner.permissions.add(permission_view_event)

# Event viewer group create
group_event_viewer_name = "event_viewer"
group_event_viewer, created = Group.objects.get_or_create(name=group_event_viewer_name)

group_event_viewer.permissions.add(permission_view_event)
