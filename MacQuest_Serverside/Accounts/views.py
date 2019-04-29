"""
Created by Jason
2018/08/18
Purpose: View URL settings.
"""

# Create your views here.
from django.shortcuts import redirect, render
from django.contrib import messages

from Accounts.forms import RegisterForm
from Accounts.signals import sendEmail_signal

template_name = "Accounts/register.html"


def register(request):
    if request.method == "POST":
        form = RegisterForm(request.POST)

        if form.is_valid():
            sendEmail_signal.send(sender="registerForm", username=form.cleaned_data['username'],
                                  userType=form.cleaned_data['role'],
                                  userFirstName=form.cleaned_data['first_name'],
                                  userLastName=form.cleaned_data['last_name'])
            form.is_active = False
            form.save()
            messages.success(request, "Register successful. Wait for permission from admin. ")

            return redirect('/')
    else:
        form = RegisterForm()

    return render(request, template_name, {"registerForm": form})
