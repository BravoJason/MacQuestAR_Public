"""
Created by Jason
2018/08/19
Purpose: URL settings.
"""

from django.conf.urls import url
from django.urls import include
from rest_framework import routers

from EventAPI import apiView, views

router = routers.DefaultRouter()
router.register(r'parentevent', apiView.ParentEventView)
router.register(r'subevent', apiView.SubEventView)
router.register(r'userlocation', apiView.UserLocationView)

urlpatterns = [
    url(r'^api/', include(router.urls)),
    url(r'^heatmapLocation$', views.getHeatmapGeoJsonByParam, name="userLocationGeojson")
]
