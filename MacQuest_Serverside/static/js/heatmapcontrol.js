function generateHeatMapButtonCode(pid, start_time, end_time) {
    return `<button type="button" class="btn btn-outline-primary heatmap_button_class" id="heatmap_button" onclick="toggleHeatMap(${pid}, '${start_time}', '${end_time}')">Heatmap</button>`;
}

let g_isHeatMapShow = false;

function toggleHeatMap(pid, start_time, end_time) {
    if (g_isHeatMapShow === false) {
        showHeatMap(pid, start_time, end_time);
        g_isHeatMapShow = true;
    } else {
        showEventMap();
        g_isHeatMapShow = false;
    }

}

function showHeatMap(pid, start_time, end_time) {
    $("#map").prop("hidden", true);
    $("#roomBuildingSearch-filter-ctrl").prop("hidden", true);
    $("#heatmap-time-block").prop("hidden", false);
    $("#heatmap").prop("hidden", false);
    $("#map-container").prop("hidden", true);
    $(".heatmap_button_class").html("Event map");
    setElementTime("heatmap-start-time", start_time);
    setElementTime("heatmap-end-time", end_time);
    heatmap.resize();
    g_currentPID = pid;
    setHeatMapData(pid);
    g_isHeatMapShow = true;
}

function searchHeatMapUserLocation() {
    setHeatMapData(g_currentPID);
}

function setHeatMapData(pid) {
    let start_time = getElementTimeValue("heatmap-start-time");
    let end_time = getElementTimeValue("heatmap-end-time");
    let HeatMap_URL = generateHeatMapParamURL(pid, start_time, end_time);
    $.getJSON(HeatMap_URL, function (data) {
        let geojson = {
            "type":"FeatureCollection",
            "features":data.features
        };
            heatmap.getSource("userlocation").setData(data);
        }
    )

}

function generateHeatMapParamURL(pid, start_time, end_time) {
    return `${g_HeatMap_URL}?pid=${pid}&start_time=${start_time}&end_time=${end_time}`;
}


function showEventMap() {
    $("#heatmap").prop("hidden", true);
    $("#map-container").prop("hidden", false);
    $("#map").prop("hidden", false);
    $("#roomBuildingSearch-filter-ctrl").prop("hidden", false);
    $("#heatmap-time-block").prop("hidden", true);
    $(".heatmap_button_class").html("Heatmap");
    map.resize();
    g_isHeatMapShow = false;
}