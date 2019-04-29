from django.conf.urls import url

from SmartSearch.views import buildingSearchView, createDataIndex, test

urlpatterns = [
    url(r'^$', buildingSearchView, name='buildingSearch'),
    url(r'createBuildingIndex', createDataIndex, name="createBuildingIndex"),
    url(r'test', test, name='testVIew'),
]