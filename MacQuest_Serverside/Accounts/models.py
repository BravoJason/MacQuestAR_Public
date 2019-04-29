"""
Created by Jason
2018/08/18
Purpose: Account model.
"""

from django.contrib.auth.models import AbstractUser

from django.db import models
from django.utils.translation import gettext_lazy as _

type_planner = "Planner"
type_viewer = "Viewer"


# User model.
class User(AbstractUser):
    role_type = ((type_planner, u'Planner'), (type_viewer, u'Viewer'))
    role = models.CharField(max_length=100, blank=False, choices=role_type, default=type_planner)
    email = models.EmailField(unique=True)
    is_active = models.BooleanField(
        _('active'),
        default=False,
        help_text=_(
            'Designates whether this user should be treated as active. '
            'Unselect this instead of deleting accounts.'
        ),
    )

    class Meta(AbstractUser.Meta):
        swappable = "AUTH_USER_MODEL"

    @property
    def full_name(self):
        return "{0} {1}".format(self.first_name, self.last_name)
