from django.shortcuts import render

# Create your views here.

template = "index.html"


def indexView(request):
    return render(request, template, {})
