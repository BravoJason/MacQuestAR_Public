"""
Created by Jason
2018/08/23
Purpose: Define event schema.
"""
from datetime import datetime, timedelta
import logging

from whoosh import index
from whoosh.fields import Schema, NGRAMWORDS, TEXT, NUMERIC, DATETIME, BOOLEAN

from MacQuest_Serverside import settings

from EventManage.models import ParentEvent, SubEvent
from MacQuest_Serverside.settings import WHOOSH_INDEX_PARENTEVENT_NAME, WHOOSH_INDEX_SUBEVENTNAME_NAME

logging.basicConfig(format='%(levelname)s:%(message)s', level=logging.DEBUG)

# Parent event schema.
parentEventSchema = Schema(name=NGRAMWORDS(minsize=1,
                                           maxsize=100,
                                           stored=True,
                                           field_boost=1.0,
                                           tokenizer=None,
                                           queryor=True,
                                           sortable=False),
                           pid=NUMERIC(stored=True, unique=True),
                           desc=TEXT(stored=True),
                           authentication=TEXT(stored=True),
                           private_password=TEXT(stored=True),
                           start_time=DATETIME(stored=True),
                           end_time=DATETIME(stored=True),
                           owner=TEXT(stored=True),
                           owner_id=NUMERIC(stored=True),
                           isActive=BOOLEAN(stored=True)
                           )

# Sub event schema.
subEventSchema = Schema(name=NGRAMWORDS(minsize=1,
                                        maxsize=100,
                                        stored=True,
                                        field_boost=1.0,
                                        tokenizer=None,
                                        queryor=True,
                                        sortable=False),
                        isActive=BOOLEAN(stored=True),
                        location_building=TEXT(stored=True),
                        location_lat=NUMERIC(numtype=float, stored=True),
                        location_lng=NUMERIC(numtype=float, stored=True),
                        floor=NUMERIC(numtype=int, signed=True, stored=True),
                        parentEventID=NUMERIC(stored=True),
                        desc_has_url=BOOLEAN(stored=True),
                        desc_url=TEXT(stored=True),
                        desc_show_in_app=TEXT(stored=True),
                        desc_show_in_website=TEXT(stored=True),
                        action_title=TEXT(stored=True),
                        action_has_action=BOOLEAN(stored=True),
                        action_desc=TEXT(stored=True),
                        action_has_url=BOOLEAN(stored=True),
                        action_url=TEXT(stored=True),
                        start_time=DATETIME(stored=True),
                        end_time=DATETIME(stored=True),
                        subEventID=NUMERIC(stored=True, unique=True)
                        )


def getIx_ParentEvent(name):
    if index.exists_in(settings.WHOOSH_INDEX_EVENT, indexname=name):
        ix = index.open_dir(settings.WHOOSH_INDEX_EVENT, indexname=name)
        logging.info("index direct already exists")
    else:
        ix = index.create_in(settings.WHOOSH_INDEX_EVENT, parentEventSchema, indexname=name)
        logging.info('index directory does NOT exit, create a new one')
    return ix


def getIx_SubEvent(name):
    if index.exists_in(settings.WHOOSH_INDEX_EVENT, indexname=name):
        ix = index.open_dir(settings.WHOOSH_INDEX_EVENT, indexname=name)
        logging.info("index direct already exists")
    else:
        ix = index.create_in(settings.WHOOSH_INDEX_EVENT, subEventSchema, indexname=name)
        logging.info('index directory does NOT exit, create a new one')
    return ix


def index_parentEvent(writer):
    parentEvents = ParentEvent.objects.all()
    for event in parentEvents:
        start_time = ""
        end_time = ""
        if event.start_time is None:
            start_time = datetime.utcnow()
        else:
            start_time = event.start_time

        if event.end_time is None:
            end_time = datetime.utcnow() + timedelta(days=1)
        else:
            end_time = event.end_time

        writer.update_document(name=event.name,
                               pid=event.id,
                               desc=event.description,
                               authentication=event.authentication,
                               private_password=event.private_password,
                               start_time=start_time,
                               end_time=end_time,
                               owner=event.owner.username,
                               owner_id=event.owner_id,
                               isActive=event.isActive
                               )


def index_subEvent(writer):
    subEvent = SubEvent.objects.all()
    for event in subEvent:
        writer.update_document(name=event.name,
                               isActive=event.isActive,
                               location_building=event.location_building,
                               location_lat=event.location_lat,
                               location_lng=event.location_lng,
                               floor=event.floor,
                               parentEventID=event.parentEventID.id,
                               desc_has_url=event.desc_has_url,
                               desc_url=event.desc_url,
                               desc_show_in_app=event.desc_show_in_app,
                               desc_show_in_website=event.desc_show_in_website,
                               action_title=event.action_title,
                               action_has_action=event.action_has_action,
                               action_desc=event.action_desc,
                               action_has_url=event.action_has_url,
                               action_url=event.action_url,
                               start_time=event.start_time,
                               end_time=event.end_time,
                               subEventID=event.id
                               )


def buildSchema_parentEvent():
    ix = getIx_ParentEvent(WHOOSH_INDEX_PARENTEVENT_NAME)
    writer = ix.writer()
    index_parentEvent(writer)
    writer.commit()


def buildSchema_subEvent():
    ix = getIx_SubEvent(WHOOSH_INDEX_SUBEVENTNAME_NAME)
    writer = ix.writer()
    index_subEvent(writer)
    writer.commit()


def spell_check(string):
    ix = getIx_ParentEvent(WHOOSH_INDEX_PARENTEVENT_NAME)
    with ix.searcher() as searcher:
        corrector1 = searcher.corrector("name")
        results1 = corrector1.suggest(string, limit=100, maxdist=2)
        if len(results1) == 0:
            return [string]
        return [val for val in results1]
