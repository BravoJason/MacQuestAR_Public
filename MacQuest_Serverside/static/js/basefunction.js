const ZOOM_LEVEL_ROOM = 19
const ZOOM_LEVEL_BUILDING = 16.9;
const ZOOM_LEVEL_BUILDING_ROOM_BOUNDARY = 17;
const Debug_control = true;
let currentFloor = "1";
let previousFllor = "1";
const MAPBOX_TOKEN = "pk.eyJ1IjoiemVyb25lLWppYSIsImEiOiJjamc5eG5nanIyNjUxMndybjR4bW44djFpIn0._bHsaywMOGemkFMrjOnRnA";
const MAPBOX_STYLE = "mapbox://styles/zerone-jia/cjgz9pk67001r2smf459nliw3";

//Function to verify the string is empty or not.
function stringIsEmpty(str) {
    return str === undefined || str.length === 0;
}

//Function to jump page after certain second.
function jumpPage(seconds, url, textAreaObject, pageName) {
    setInterval(refer, 1000); //set timmer
    function refer() {
        if (seconds == 0) {
            location = url; //set the jump url
        }
        textAreaObject.text("" + seconds + " second to jump to " + pageName + "."); // show the timer.
        seconds--; // reduce the time
    }
}

function showMessageDialog(message) {

    $("#message_dlg").attr("title", message.title);
    $("#message_dlg").html(`<p>${message.info}</p>`);

    $("#message_dlg").dialog({
        modal: true,
        buttons: {
            Ok: function () {
                $(this).dialog("close");
            }
        }
    });
}

