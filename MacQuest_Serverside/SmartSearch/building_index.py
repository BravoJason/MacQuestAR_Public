# Define the schema
import logging
import os

from whoosh import index
from whoosh.fields import Schema, TEXT, ID, NGRAMWORDS, NUMERIC
from MacQuestData.models import roomsList
from MacQuest_Serverside import settings

logging.basicConfig(format='%(levelname)s:%(message)s', level=logging.DEBUG)

# Define Schema
schema = Schema(index=NGRAMWORDS(minsize=1,
                                 maxsize=100,
                                 stored=True,
                                 field_boost=1.0,
                                 tokenizer=None,
                                 queryor=True,
                                 sortable=False),
                label=TEXT(stored=True),
                desc=TEXT(stored=True),
                outid=NUMERIC(stored=True),
                floor=NUMERIC(stored=True),
                rid=NUMERIC(stored=True),
                lat=NUMERIC(stored=True),
                lon=NUMERIC(stored=True),
                short_name=TEXT(stored=True, spelling=True),
                full_name=TEXT(stored=True, spelling=True),
                rname=TEXT(stored=True))  # room name


def build_schema():
    ix = getIx()
    writer = ix.writer()
    index_building_fullname(writer)
    index_building_shortname(writer)
    index_building_room(writer)
    index_room_building(writer)
    writer.commit()
    logging.info("Build finished.")


def getIx():
    global schema
    if index.exists_in(settings.WHOOSH_INDEX_ROOM_BUILDING):
        ix = index.open_dir(settings.WHOOSH_INDEX_ROOM_BUILDING)
        logging.info("index direct already exists")
    else:
        ix = index.create_in(settings.WHOOSH_INDEX_ROOM_BUILDING, schema)
        logging.info('index directory does NOT exit, create a new one')
    return ix


def getLat(string):
    tempLat = string.split(',')
    lat = tempLat[1]
    lat = lat.split(")")
    lat = lat[0]
    lat = float(lat)
    return lat


def getLng(string):
    tempLng = string.split(',')
    lng = tempLng[0]
    lng = lng.split("(")
    lng = lng[1]
    lng = float(lng)
    return lng


def index_building_fullname(writer):
    rooms = roomsList.objects.all()
    for room in rooms:
        writer.update_document(
            index=room.buildingName,
            label=room.shortName,
            desc=room.buildingName,
            outid=room.outid,
            rid=-1,
            floor=100,
            lat=getLat(room.centroid),
            lon=getLng(room.centroid),
            full_name=room.buildingName,
            short_name=room.shortName,
            rname='na'
        )


def index_building_shortname(writer):
    rooms = roomsList.objects.all()
    for room in rooms:
        writer.update_document(
            index=room.shortName,
            label=room.shortName,
            desc=room.buildingName,
            outid=room.outid,
            rid=-1,
            floor=100,
            lat=getLat(room.centroid),
            lon=getLng(room.centroid),
            full_name=room.buildingName,
            short_name=room.shortName,
            rname='na'
        )


def index_building_room(writer):
    rooms = roomsList.objects.all()
    for room in rooms:
        writer.update_document(
            index=str(room.shortName) + str(room.name),
            label=str(room.shortName) + " " + str(room.name),
            desc=room.name + " at " + room.buildingName,
            outid=room.outid,
            rid=room.rid,
            floor=room.floor,
            lat=getLat(room.centroid),
            lon=getLng(room.centroid),
            full_name=room.buildingName,
            short_name=room.shortName,
            rname=room.name
        )


def index_room_building(writer):
    rooms = roomsList.objects.all()
    for room in rooms:
        writer.update_document(
            index=str(room.name) + str(room.shortName),
            label=str(room.shortName) + " " + str(room.name),
            desc=room.name + " at " + room.buildingName,
            outid=room.outid,
            rid=room.rid,
            floor=room.floor,
            lat=getLat(room.centroid),
            lon=getLng(room.centroid),
            full_name=room.buildingName,
            short_name=room.shortName,
            rname=room.name
        )
