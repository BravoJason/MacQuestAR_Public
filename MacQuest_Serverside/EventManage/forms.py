"""
Created by Jason
2018/08/18
Purpose: Form file for managing parent event and sub event.
"""

from django import forms

from EventManage.models import ParentEvent


# Add parent event form.
class addEventForm(forms.ModelForm):
    class Meta:
        model = ParentEvent
        fields = ('name', 'description', 'authentication', 'private_password')
        widgets = {
            'description': forms.Textarea(attrs={'rows': 2, 'class': "from-control"}),
        }


# Add sub event form.
class addSubEventForm(forms.Form):
    sub_event_name = forms.CharField(required=True, error_messages={'required': 'Subevent name can not be empty.'})

    sub_event_location = forms.CharField(required=True, error_messages={'required': 'Subevent location can not be '
                                                                                    'empty.'})
    sub_event_room = forms.CharField(required=False)
    sub_event_desc = forms.CharField(required=True,
                                     error_messages={'required': 'Subevent description can not be empty.'})
    sub_event_desc_url = forms.URLField(required=False,
                                        error_messages={'required': 'The format of the URL is incorrect.'})
    sub_event_desc_app = forms.CharField(required=False, error_messages={'required': 'The event description showing in '
                                                                                     'app cannot be empty.'})
    sub_event_action_title = forms.CharField(required=False, error_messages={'required': "The action title formate is "
                                                                                         "incorrect."})
    sub_event_action_desc = forms.CharField(required=False)
    sub_event_action_url = forms.URLField(required=False,
                                          error_messages={'required': 'The format of the URL is incorrect.'})

    sub_event_start_time = forms.DateTimeField(required=True, input_formats=['%Y-%m-%dT%H:%M'],
                                               error_messages={'required': 'Start time is required. '})

    sub_event_end_time = forms.DateTimeField(required=True, input_formats=['%Y-%m-%dT%H:%M'],
                                             error_messages={'required': 'End time is required. '})

    sub_event_lat = forms.FloatField(required=True, error_messages={"required": "Event location cannot be empty."})
    sub_event_lng = forms.FloatField(required=True, error_messages={"required": "Event location cannot be empty."})
    sub_event_floor = forms.FloatField(required=True, error_messages={"required": "Event floor information cannot be "
                                                                                  "empty."})
    sub_event_pid = forms.IntegerField(required=True, error_messages={"required": "PID is required."})

    sub_event_building = forms.CharField(required=True, error_messages={"required": "Building is required."})


# Edit parent event form.
class EditEventForm(forms.ModelForm):
    class Meta:
        model = ParentEvent
        fields = ('name', 'description', 'authentication', 'private_password')
        widgets = {
            'description': forms.Textarea(attrs={'rows': 2}),
        }
