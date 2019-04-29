function showSubEventQRCode(sid) {
    $("#qrcode").empty();
    setQRCode(sid);
    $("#qrcode").attr("title", "Subevent QRCode");
    $("#qrcode").dialog({
        modal: true,
        buttons: {
            Ok: function () {
                $(this).dialog("close");
            }
        }
    });
}

function genreateSubEventShowQRCodeButton(sid){
    let sid_ = sid.toString()
    return  "<button type='button' class='btn btn-outline-primary'  onclick='showSubEventQRCode(" + sid_ + ")'>QR Code</button>"
}

function setQRCode(sid) {
    let qrCodeURL = getSubEventURL(sid);
    $("#qrcode").qrcode(qrCodeURL)
}

function getSubEventURL(sid) {
    return g_Subevent_URL + sid.toString() + "/";
}