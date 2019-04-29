function searchParentEvent() {
    let param={};


    switch ($("#active-parent-event").val()) {
        case "active-event":
            param.isActive = true;
            break;
        case "all-event":
            param={};
            break;

    }

    let eventKW = $("#eventSearchBar").val();
    eventKW = eventKW.trim();
    if(eventKW.length > 0){
        param.name = eventKW;
    }

    //param.authentication = "Public";


    searchParentEventByParam(param)
}











