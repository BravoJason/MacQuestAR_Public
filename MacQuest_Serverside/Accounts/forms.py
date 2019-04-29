"""
Created by Jason
2018/08/18
Purpose: Register form model.
"""

from django.contrib.auth.forms import UserCreationForm

from Accounts.models import User


# Register user form module.
class RegisterForm(UserCreationForm):
    class Meta(UserCreationForm.Meta):
        model = User
        fields = ("username", "first_name", "last_name", "email", "role")
