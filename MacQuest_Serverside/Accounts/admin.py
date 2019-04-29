"""
Created by Jason
2018/08/18
Purpose: Admin page model.
"""

from django.contrib import admin

# Register your models here.
from Accounts.models import User


# Register your models here.
class UserAdmin(admin.ModelAdmin):
    fields = ('username', 'password', 'email', 'first_name', 'last_name', 'role', 'is_active')
    search_fields = ['username', 'email', 'first_name', 'last_name']
    list_display = ('username', 'first_name', 'last_name', 'role', 'is_active', 'is_staff')


admin.site.register(User, UserAdmin)
