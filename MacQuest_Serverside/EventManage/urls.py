from django.conf.urls import url

from EventManage import views

app_name = 'EventManage'

urlpatterns = [
    url(r"^addevent$", views.addParentEventView, name="addEvent"),
    url(r"^buildSchema$", views.buildSchemaView, name="buildSchema"),
    url(r"^searchparenteventbyparam$", views.searchParentEventByParam, name="searchParentEventByParam"),
    url(r"^addsubevent$", views.addSubEvent, name="addSubEvent"),
    url(r"^searchsubeventbyparam$", views.searchSubEventByParam, name="searchSubEventByParam"),
    url(r"^deleteParentEvent$", views.deleteParentEventView, name="deleteParentEvent"),
    url(r"^deleteSubEvent$", views.deleteSubEventView, name="deleteSubEvent"),
    url(r"^editParentEvent", views.editParentEventView, name="editParentEvent"),
    url(r"^editSubEvent", views.editSubEvent, name="editSubEvent"),
    url(r"^plannerEventAuth", views.plannerParentEventAuth, name="plannerAuth"),
    url(r"^viewerEventAuth", views.viewerParentEventAuth, name="viewAuth"),
    url(r"^viewPermissionRequest$", views.getPlannerPermissionRequest, name="ajaxGetPermission"),
    url(r"^acceptPermission$", views.acceptPermission, name="accpetPermission"),
    url(r"^cancelPermission$", views.cancelPermission, name="cancelPermission"),
    url(r"^getViewerPermission$", views.getViewerAuthPermissionInfo, name="ajaxGetViewerPermissionInfo"),
    url(r"^applyPrivatePermission$", views.applyPrivatePermission, name="ajaxApplyPrivatePermission"),
    url(r"^cancelPrivateEventPermissionApply$", views.cancelWaitingListPermission,
        name="ajaxCancelPrivatePermissionApply"),
    url(r"^cancelViewerPermission$", views.canceViewerListPermission,
        name="ajaxCancelViewerPermission"),


]
