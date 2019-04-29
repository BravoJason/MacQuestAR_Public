"""
Created by Jason
2018/08/18
Purpose: Email signals.
"""
from django.contrib.auth.models import Group
from django.db.models.signals import post_save, post_init
from django.dispatch import receiver, Signal

from Accounts import models as Accounts_Models
from Accounts.emailTools import SendRegisterEmailinThread
from Accounts.models import User
from Accounts import groups as Accounts_Groups

# User defined signal.
from MacQuest_Serverside import settings

sendEmail_signal = Signal(providing_args=['username', 'userType', 'userFirstName', 'userLastName'])


# Receiver function to compare the is_active value.
@receiver(post_save, sender=User)
def post_active_callback(sender, created, instance, **kwargs):
    if not created and instance.__original_is_active != instance.is_active and instance.is_active is True:
        print("User is actived.")
        ctx = {
            'username': instance.username,
            'userType': instance.role,
            'url': settings.LOGIN_URL
        }
        send_email = SendRegisterEmailinThread(settings.EMAIL_USER_ACTIVATE_SUBJECT, settings.SENDING_EMAIL,
                                               [instance.email], ctx,
                                               settings.EMAIL_USER_ACTIVATE_TEMPLATE_PATH_TEXT,
                                               settings.EMAIL_USER_ACTIVATE_TEMPLATE_PATH_HTML)
        send_email.start()


@receiver(post_save, sender=User)
def post_user_permission_callback(sender, created, instance, **kwargs):
    # Chech the user role.
    if instance.role == Accounts_Models.type_planner:
        # It is event planner
        event_planner_group = Group.objects.get(name=Accounts_Groups.group_event_planner_name)
        # Remove the user from other groups.
        instance.groups.clear()
        # Add the user into event planner group.
        event_planner_group.user_set.add(instance)
    elif instance.role == Accounts_Models.type_viewer:
        event_viewer_group = Group.objects.get(name=Accounts_Groups.group_event_viewer_name)
        instance.groups.clear()
        event_viewer_group.user_set.add(instance)


# Receiver function to get the original is_active value.
@receiver(post_init, sender=User)
def init_active_callback(sender, instance, **kwargs):
    instance.__original_is_active = instance.is_active


@receiver(sendEmail_signal)
def sendRegisterNotification(sender, **kwargs):
    print("sendRegisterNotification")
    '''sendAdminRegisterEmail(kwargs['username'], kwargs['userType'],
                           kwargs['userFirstName'], kwargs['userLastName'])
                           '''
    ctx = {
        'username': kwargs['username'],
        'userType': kwargs['userType'],
        'userFirstName': kwargs['userFirstName'],
        'userLastName': kwargs['userLastName'],
    }
    # def __init__(self, subject, send_from, to_list, ctx, email_template_text, email_template_html,
    # fail_silently=True):
    send_email = SendRegisterEmailinThread(settings.EMAIL_REGISTER_SUBJECT, settings.SENDING_EMAIL,
                                           [settings.ADMIN_EMAIL], ctx, settings.EMAIL_REGISTER_TEMPLATE_PATH_TEXT,
                                           settings.EMAIL_REGISTER_TEMPLATE_PATH_HTML)
    send_email.start()
