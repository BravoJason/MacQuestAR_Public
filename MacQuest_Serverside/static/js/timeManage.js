let timeDatePicker = (function () {
    let initDate = function (startDateTimeId, endDateTimeId) {

        startDateTimeId = "#" + startDateTimeId;
        endDateTimeId = "#" + endDateTimeId;
        jQuery(startDateTimeId).datetimepicker({
            format: 'Y-m-d H:i',
            step: 5,
            onShow: function (ct) {
                this.setOptions({
                    maxDate: jQuery(endDateTimeId).val() ? jQuery(endDateTimeId).val() : false
                })
            },
            timepicker: true
        });
        jQuery(endDateTimeId).datetimepicker({
            format: 'Y-m-d H:i',
            step: 5,
            onShow: function (ct) {
                this.setOptions({
                    minDate: jQuery(startDateTimeId).val() ? jQuery(startDateTimeId).val() : false
                })
            },
            timepicker: true
        });
    };
    return {
        initDate: initDate
    };

})();


let heatmap_timeDatePicker = (function () {
    let initDate = function (startDateTimeId, endDateTimeId) {

        startDateTimeId = "#" + startDateTimeId;
        endDateTimeId = "#" + endDateTimeId;
        jQuery(startDateTimeId).datetimepicker({
            format: 'Y-m-d H:i',
            step: 5,
            onShow: function (ct) {
                this.setOptions({
                    maxDate: jQuery(endDateTimeId).val() ? jQuery(endDateTimeId).val() : false
                })
            },
            timepicker: true
        });
        jQuery(endDateTimeId).datetimepicker({
            format: 'Y-m-d H:i',
            step: 5,
            onShow: function (ct) {
                this.setOptions({
                    minDate: jQuery(startDateTimeId).val() ? jQuery(startDateTimeId).val() : false
                })
            },
            timepicker: true
        });
    };
    return {
        initDate: initDate
    };

})();

function setStartTimeMaxDateTime(start_time_id, end_time_id){
    let start_time_id_ = "#" + start_time_id;
    let end_time_id_ = "#" + end_time_id;
    jQuery(start_time_id_).attr( "max", jQuery(end_time_id_).val());
}

function setEndTimeMinDateTime(start_time_id, end_time_id){
    let start_time_id_ = "#" + start_time_id;
    let end_time_id_ = "#" + end_time_id;
    jQuery(end_time_id_).attr( "min",jQuery(start_time_id_).val());
}

function setElementTime(input_time_id, time){
    let input_time_id_ = "#" + input_time_id;
    jQuery(input_time_id_).val(time);
}

function getElementTimeValue(element_time_id){
    let element_id_ = "#" + element_time_id;
    return jQuery(element_id_).val();
}
