"""
Created by Jason
2018/08/23
Purpose: Formating date string.
"""

import datetime


# Convert date object to string in specific format.
def datetimeConvertor(o):
    if type(o) is datetime.date or type(o) is datetime.datetime:
        return o.strftime("%Y-%m-%dT%H:%M")
