from django.db import models

# Create your models here.

from django.db import models


class buildingList(models.Model):
    outid = models.TextField();
    showname = models.TextField();
    st_astext = models.TextField();

    def __str__(self):
        return self.showname;


class roomsList(models.Model):
    rid = models.TextField(null=True)
    outid = models.TextField(null=True)
    name = models.TextField(null=True)
    utility = models.TextField(null=True)
    floor = models.TextField(null=True)
    location = models.TextField(null=True)
    nearsetStaircase = models.TextField(null=True)
    nearestElevator = models.TextField(null=True)
    geometry = models.TextField(null=True);
    buildingName = models.TextField(null=True)
    centroid = models.TextField(null=True)
    shortName = models.TextField(null=True)
    buildingCenter = models.TextField(null=True)

    def __str__(self):
        return self.name;
