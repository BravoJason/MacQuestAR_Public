//Function to move the map center with floor number.
function flyMapCenter(Lng, Lat, floorNumber, zoomLevel) {
    console.log(Lng);
    console.log(Lat);
    Lng = parseFloat(Lng);
    Lat = parseFloat(Lat);
    console.log(Lng);
    console.log(Lat);
    changeLayers(floorNumber);
    map.jumpTo({
        center: [
            Lng,
            Lat
        ],
        zoom: zoomLevel

    });


}