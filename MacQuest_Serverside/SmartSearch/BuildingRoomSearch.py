import logging
import os

from whoosh.qparser import QueryParser
from whoosh.query import Every, Term

from SmartSearch.building_index import getIx


def searchData(string):
    ix = getIx()
    qp = QueryParser("index", schema=ix.schema)
    q = qp.parse(string)
    with ix.searcher() as s:
        results = s.search(q, limit=10)
        ans = []
        uniquekey = []
        if len(results) > 0:
            for r in results:
                #print(r)
                if str(r['outid']) + str(r['rid']) not in uniquekey:
                    uniquekey.append(str(r['outid']) + str(r['rid']))
                    ans.append({'label': r['label'],
                                'short_name': str(r['short_name']),
                                'full_name': str(r['full_name']),
                                'desc': str(r['desc']),
                                'rid': r['rid'],
                                'lat': r['lat'],
                                'lon': r['lon'],
                                'floor': r['floor'],
                                'outid': r['outid'],
                                'rname': r['rname']})
        else:
            suggest = spell_check(string)
            if len(suggest) == 0:
                return ans
            qsb = Term("index", suggest[0])
            for w in suggest:
                qsb = qsb | Term('index', w)
            results = s.search(qsb, limit=10)
            for r in results:
                if str(r['outid']) + str(r['rid']) not in uniquekey:
                    uniquekey.append(str(r['outid']) + str(r['rid']))
                    ans.append({'label': r['label'],
                                'short_name': str(r['short_name']),
                                'full_name': str(r['full_name']),
                                'desc': str(r['desc']),
                                'rid': r['rid'],
                                'lat': r['lat'],
                                'lon': r['lon'],
                                'floor': r['floor'],
                                'outid': r['outid'],
                                'rname': r['rname']})
        return ans


def spell_check(string):
    ix = getIx();
    with ix.searcher() as searcher:
        corrector1 = searcher.corrector("index");
        corrector2 = searcher.corrector("short_name");
        results1 = corrector1.suggest(string, limit=100, maxdist=2)
        results2 = corrector2.suggest(string, limit=100, maxdist=2)
        if len(results1 + results2) == 0:
            return [string]
        return [val for val in results1 if val in results2]
