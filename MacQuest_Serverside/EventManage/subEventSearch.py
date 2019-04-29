from whoosh.writing import AsyncWriter

from EventManage import eventIndex
from EventManage.models import SubEvent
from MacQuest_Serverside.settings import WHOOSH_INDEX_SUBEVENTNAME_NAME


def subEventDocumentUpdate(event):
    ix = eventIndex.getIx_SubEvent(WHOOSH_INDEX_SUBEVENTNAME_NAME)
    writer = AsyncWriter(ix)

    writer.add_document(name=event.name,
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
    writer.commit()


'''
def getSubEventByParam(param):
    ix = eventIndex.getIx_SubEvent(WHOOSH_INDEX_SUBEVENTNAME_NAME)
    with ix.searcher() as searcher:
        results = searcher.documents(**param)
        ans = []
        for r in results:
            ans.append({'name': r['name'],
                        'isActive': r['isActive'],
                        'location_building': r['location_building'],
                        'location_lat': r['location_lat'],
                        'location_lng': r['location_lng'],
                        'floor': r['floor'],
                        'parentEventID': r['parentEventID'],
                        'desc_has_url': r['desc_has_url'],
                        'desc_url': r['desc_url'],
                        'desc_show_in_app': r['desc_show_in_app'],
                        'desc_show_in_website': r['desc_show_in_website'],
                        'action_title': r['action_title'],
                        'action_has_action': r['action_has_action'],
                        'action_desc': r['action_desc'],
                        'action_has_url': r['action_has_url'],
                        'action_url': r['action_url'],
                        'start_time': r['start_time'],
                        'end_time': r['end_time'],
                        'subEventID': r['subEventID'],
                        }
                       )
    return ans
'''


def getSubEventByParam(param):
    subEvents = SubEvent.objects.filter(**param)

    ans = []
    for event in subEvents:
        ans.append({
            'name': event.name,
            'isActive': event.isActive,
            'location_building': event.location_building,
            'location_lat': event.location_lat,
            'location_lng': event.location_lng,
            'room': event.room,
            'floor': event.floor,
            'parentEventID': event.parentEvent_id,
            'desc_has_url': event.desc_has_url,
            'desc_url': event.desc_url,
            'desc_show_in_app': event.desc_show_in_app,
            'desc_show_in_website': event.desc_show_in_website,
            'action_title': event.action_title,
            'action_has_action': event.action_has_action,
            'action_desc': event.action_desc,
            'action_has_url': event.action_has_url,
            'action_url': event.action_url,
            'start_time': event.start_time,
            'end_time': event.end_time,
            'subEventID': event.id,
            'owner': event.parentEvent.owner.username,
            'pid': event.parentEvent.id
        }
        )
    return ans
