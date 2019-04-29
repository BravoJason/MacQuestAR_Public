"""
Created by Jason
2018/08/23
Purpose:
"""

from django.contrib.auth.decorators import user_passes_test
from django.http import HttpResponseBadRequest


def group_required(*group_names):
    """
    Requires user membership in at least one of the groups passed in.

    Checks is_active and allows superusers to pass regardless of group
    membership.
    """

    def in_group(u):
        return u.is_active and (u.is_superuser or bool(u.groups.filter(name__in=group_names)))

    return user_passes_test(in_group)


def ajax_required(f):
    """
    AJAX request required decorator
    use it in your views:

    @ajax_required
    def my_view(request):
        ....

    """

    def wrap(request, *args, **kwargs):
        if not request.is_ajax():
            return HttpResponseBadRequest()
        return f(request, *args, **kwargs)

    wrap.__doc__ = f.__doc__
    wrap.__name__ = f.__name__
    return wrap
