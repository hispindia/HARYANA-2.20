'use strict';

/* Filters */

var reportsAppFilters = angular.module('reportsAppFilters', [])

.filter('filterOnSection', function () {
    return function(reportSettings,sectionUID){
        if (reportSettings == undefined || sectionUID == undefined || sectionUID == "")
        return;

        var tempReportSettings = [];

            angular.forEach(reportSettings,function(report,index){
                if (report.section == sectionUID){
                    tempReportSettings.push(report);
                }
            })


        return tempReportSettings;
    }

})

