"""
Created by Jason
2018/08/18
Purpose: Accounts URL settings.
"""
from django.conf.urls import url
from django.urls import include, path, reverse_lazy

from Accounts import views
from django.contrib.auth import views as auth_views

app_name = 'Accounts'

urlpatterns = [
    url(r"^register/", views.register, name='register'),
    path('login/', auth_views.LoginView.as_view(template_name="Accounts/login.html"), name='login'),
    path('logout/', auth_views.LogoutView.as_view(), name='logout'),

    path('password_change/', auth_views.PasswordChangeView.as_view(template_name="Accounts/password_change_form.html",
                                                                   success_url=reverse_lazy(
                                                                       "Accounts:password_change_done")),
         name='password_change'),
    path('password_change/done/', auth_views.PasswordChangeDoneView.as_view(
        template_name="Accounts/password_change_done.html"), name='password_change_done'),

    path('password_reset/', auth_views.PasswordResetView.as_view(
        template_name="Accounts/password_reset_form.html",
        success_url=reverse_lazy("Accounts:password_reset_done"),
        email_template_name="Accounts/password_reset_email.html"),
         name='password_reset'),

    path('password_reset/done/', auth_views.PasswordResetDoneView.as_view(
        template_name="Accounts/password_reset_done.html"
    ), name='password_reset_done'),

    path('reset/<uidb64>/<token>/', auth_views.PasswordResetConfirmView.as_view(
        template_name="Accounts/password_reset_confirm.html",
        success_url=reverse_lazy("Accounts:password_reset_complete")), name='password_reset_confirm'),

    path('reset/done/', auth_views.PasswordResetCompleteView.as_view(
        template_name="Accounts/password_reset_complete.html"), name='password_reset_complete'),

]
