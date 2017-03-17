/**
 * Created by harsh on 22/6/15.
 */


var reportsAppServices = angular.module('reportsAppServices',[])


    .service('reportsService', function(){

})

// service for generate uid in application
//http://localhost:8090/dhis/api/system/id.json?n=10
//http://localhost:8090/dhis/api/system/id.json
.service('GenerateUidService',  function ($http){
    return {
        getUid: function () {
            var promise = $http.get('../api/system/id.json&paging=false').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})



// service for get all userGroup name and uid
 //http://127.0.0.1:8090/dhis/api/userGroups.json?fields=[id,name]&paging=false
.service('UserGroupService',  function ($http){
        return {
            getAllUserGroup: function () {
                var promise = $http.get('../api/userGroups.json?fields=[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            }
        };
})


// service for get all Resource name and uid
 //http://hospdev.hispindia.org/haryana_217/api/documents.json?fields=[id,name]&paging=false
.service('ResourceService',  function ($http){
    return {
        getAllResource: function () {
            var promise = $http.get('../api/documents.json?fields=[id,name]&paging=false').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})

// ../api/systemSettings/reportApp-configuration-json
//save delete get report Configuration parameters
.service('ReportConfigurationService',  function ($http){
    return {
        getAllReportConfiguration: function () {
            var promise = $http.get('../api/systemSettings/reportApp-configuration-json').then(function (response) {
                return response.data ;
            });
            return promise;
        },

        saveReportConfiguration: function (configuration) {
            var reportConfigurationJson = JSON.stringify(configuration);
            var promise = $http.post('../api/systemSettings/reportApp-configuration-json?value=' + reportConfigurationJson, '', {headers: {'Content-Type': 'text/plain;charset=utf-8'}}).then(function (response) {
                return response.data;
            });
            return promise;
        },
        deleteReportConfiguration: function(){
            var promise = $http.delete('../api/systemSettings/reportApp-configuration-json').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})


// save report app section and get all section
.service('ReportAppSectionSettingService',  function ($http){
    return {
        saveSection: function (sectionData) {
            var sectionDataJson = JSON.stringify(sectionData);
            var promise = $http.post('../api/systemSettings/reportApp-section-json?value=' + sectionDataJson, '', {headers: {'Content-Type': 'text/plain;charset=utf-8'}}).then(function (response) {
                return response.data ;
            });
            return promise;
        },
        getAllReportAppSection: function () {
            var promise = $http.get('../api/systemSettings/reportApp-section-json').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})

// save report app section and get all section
.service('reportSettingService',  function ($http){
    return {
        save: function (reportData) {
            var reportDataJson = JSON.stringify(reportData);
            var promise = $http.post('../api/systemSettings/reportApp-reports-json?value=' + reportDataJson, '', {headers: {'Content-Type': 'text/plain;charset=utf-8'}}).then(function (response) {
                return response.data ;
            });
            return promise;
        },
        getAll: function () {
            var promise = $http.get('../api/systemSettings/reportApp-reports-json').then(function (response) {
                return response.data ;
            });
            return promise;
        }
    };
})

    .service('reportsService',  function ($http){
        return {
            getAll: function () {
                var promise = $http.get('../api/reports.json?fields=[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            },
            getAllWithDetails : function(){
                var promise = $http.get('../api/reports.json?fields=[id,name,userGroupAccesses]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            }
        };
    })


    .service('organisationUnitGroupService',  function ($http){
        return {
            getAll: function () {
                var promise = $http.get('../api/organisationUnitGroups?fields=[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            },
            getOuGroupsByOu: function(ouUid){
                var promise = $http.get('../api/organisationUnits/'+ouUid+'.json?fields=id,name,organisationUnitGroups[id,name]').then(function (response) {
                    return response.data ;
                });
                return promise;
            }
        };
    })

    //http://127.0.0.1:8090/dhis/api/dataSets.json?fields=id,name&paging=false
    //http://127.0.0.1:8090/dhis/api/dataSets.json?filter=id:eq:qNNFDcWTNc6&&fields=id,name,periodType,organisationUnits[id,name]&paging=false
    .service('DataSetService',  function ($http){
        return {
            getAllDataSet: function () {
                var promise = $http.get('../api/dataSets.json?fields=id,name&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            },
            getDataSetPeriodTypeAndSource: function( dataSetUid ){
                if( dataSetUid != "" ){
                    var promise = $http.get( '../api/dataSets.json?filter=id:eq:' + dataSetUid + '&fields=id,name,periodType,organisationUnits[id,name]&paging=false' ).then(function(response){
                        return response.data ;
                    });
                }
                return promise;
            }
        };
    })


    //http://127.0.0.1:8090/dhis/api/organisationUnitLevels.json?paging=false
    //http://127.0.0.1:8090/dhis/api/organisationUnits.json?filter=id:eq:axtsxUvRhSr&fields=id,name
    //http://127.0.0.1:8090/dhis/api/organisationUnits.json?filter=id:eq:axtsxUvRhSr&fields=id,name&paging=false
    //http://127.0.0.1:8090/dhis/api/organisationUnits.json?filter=id:eq:axtsxUvRhSr&fields=id,name,level&paging=false
    //http://127.0.0.1:8090/dhis/api/organisationUnits/axtsxUvRhSr?fields=name,id
    //http://127.0.0.1:8090/dhis/api/organisationUnits.json?filter=id:eq:axtsxUvRhSr&fields=id,name,children[id,name,children[id,name]]&paging=false
    //http://127.0.0.1:8090/dhis/api/organisationUnits/axtsxUvRhSr.json?includeDescendants=true&fields=id,name&paging=false
    //http://127.0.0.1:8090/dhis/api/organisationUnits/axtsxUvRhSr.json?fields=id,name&includeDescendants=true&paging=false
    .service('OrganisationUnitService',  function ($http){
        return {
            getOrgUnitNameAndLevelByUid: function ( orgUnitUid ) {
                if( orgUnitUid != "" ) {
                    var promise = $http.get('../api/organisationUnits.json?filter=id:eq:' + orgUnitUid + '&fields=id,name,level&paging=false').then(function (response) {
                        return response.data;
                    });
                }
                return promise;
            },

            getOrganisationUnitLevelLength: function(){
                var promise = $http.get( '../api/organisationUnitLevels.json?paging=false' ).then(function(response){
                    return response.data ;
                });
                return promise;
            },

            getAllChildren: function( orgUnitUid ){
                if( orgUnitUid != "" ){
                    var promise = $http.get( '../api/organisationUnits.json?filter=id:eq:' + orgUnitUid + '&fields=id,name,children[id,name,children[id,name]]&paging=false' ).then(function(response){
                        return response.data ;
                    });
                }
                return promise;
            },

            getAllChildrenOfSelectedOrgUnit: function( orgUnitUid ){
                if( orgUnitUid != "" ){
                    var promise = $http.get( '../api/organisationUnits/'+orgUnitUid+'.json?includeDescendants=true&fields=id,name&paging=false' ).then(function(response){
                        return response.data ;
                    });
                }
                return promise;
            }
        };
    })

    //http://127.0.0.1:8090/dhis/api/userGroups.json?filter=id:eq:eg0hX1B7aRb&fields=id,name,users[id,name]&paging=false// for 2.17
    //http://127.0.0.1:8090/dhis/api/currentUser.json?fields=id,name,userGroups[id,name],userCredentials&paging=false// for 2.20
    //http://127.0.0.1:8090/dhis/api/currentUser.json?fields=*&paging=false
    .service('userService',  function ($http){
        return {
            getCurrentUser: function () {
                var promise = $http.get('../api/currentUser.json?fields=id,name,userCredentials,userGroups[id,name]&paging=false').then(function (response) {
                    return response.data ;
                });
                return promise;
            },

            getUsersByUserGroup: function(userGroupUid){
                if(  userGroupUid != "" ){
                    var promise = $http.get( '../api/userGroups.json?filter=id:eq:' + userGroupUid + '&fields=id,name,users[id,name]&paging=false' ).then(function(response){
                        return response.data ;
                    });
                }
                return promise;
            },
            isUserInUserGroup: function(currentUserUid,users){
                var returnVariable = false;
                angular.forEach(users,function(user){
                    if(user.id === currentUserUid){
                        returnVariable = true;
                    }

                })
                return returnVariable;
            }
        };
    })


    .service('periodService',  function ($http){
        return {
            getLast12Months: function () {

                var list = [
                            {
                                "id": "201501",
                                "name": "Jan 2015"
                            }
                            ];
                return list;
            },
            getMonthList : function() {

                var list = [
                    {
                        "name": "Jan",
                        "id": "01"
                    },
                    {
                        "name": "Feb",
                        "id": "02"
                    },
                    {
                        "name": "Mar",
                        "id": "03"
                    },
                    {
                        "name": "Apr",
                        "id": "04"
                    },
                    {
                        "name": "May",
                        "id": "05"
                    },
                    {
                        "name": "Jun",
                        "id": "06"
                    },
                    {
                        "name": "Jul",
                        "id": "07"
                    },
                    {
                        "name": "Aug",
                        "id": "08"
                    },
                    {
                        "name": "Sep",
                        "id": "09"
                    },
                    {
                        "name": "Oct",
                        "id": "10"
                    },
                    {
                        "name": "Nov",
                        "id": "11"
                    },
                    {
                        "name": "Dec",
                        "id": "12"
                    }
                ]
                return list;
            },
            getYearListBetweenTwoYears: function(startYear,EndYear){
                var list=[];

                for (EndYear;EndYear > startYear;EndYear--){
                    list.push(EndYear);
                }

                return list;
            },

            getPeriodListBetweenTwoDates: function(periodType,startPeriod,endPeriod){
                var periodList=[];
                //var periodsStr = "";
                if( periodType === "Monthly")
                {
                    var startYear = startPeriod.substring(0,4);
                    var startMonth = startPeriod.substring(4,6);

                    var endYear = endPeriod.substring(0,4);
                    var endMonth = endPeriod.substring(4,6);

                    var firstDayOfStartPeriod = new Date( startYear + "-" + startMonth + "-01" );

                    var selectedEndPeriod = new Date( endYear + "-" + endMonth + "-01" );
                    var lastDayOfSelEndPeriod = new Date(selectedEndPeriod.getFullYear(), selectedEndPeriod.getMonth() + 1, 0);

                    while(firstDayOfStartPeriod <= lastDayOfSelEndPeriod)
                    {
                        var month = firstDayOfStartPeriod.getMonth() + 1;
                        month = ( month  ) > 9 ?  month : "0"+ month;

                        var period = firstDayOfStartPeriod.getFullYear() + "" + month;
                        //periodsStr = ( periodsStr == "" ) ? period : ( periodsStr + ";" + period );

                        periodList.push( period );

                        firstDayOfStartPeriod.setMonth( firstDayOfStartPeriod.getMonth() + 1 );
                    }
                }

                else if( periodType === "Quarterly")
                {
                    var startYear = startPeriod.substring(0,4);
                    var startMonth = startPeriod.substring(4,6);

                    var endYear = endPeriod.substring(0,4);
                    var endMonth = endPeriod.substring(4,6);

                    var firstDayOfStartPeriod = new Date( startYear + "-" + startMonth + "-01" );

                    var selectedEndPeriod = new Date( endYear + "-" + endMonth + "-01" );
                    var lastDayOfSelEndPeriod = new Date(selectedEndPeriod.getFullYear(), selectedEndPeriod.getMonth() + 1, 0);

                    var startQuater = Math.floor((firstDayOfStartPeriod.getMonth() + 3) / 3);

                    var endQuater = Math.floor((lastDayOfSelEndPeriod.getMonth() + 3) / 3);
                    //alert( "Start Quater - " + startQuater );
                    //alert( "End Quater - " + endQuater );

                    while (startYear <= endYear)
                    {
                        if (startYear == endYear)
                        {
                            for (var i = startQuater; i <= endQuater; i++)
                            {
                                periodList.push(startYear + "Q" + i);
                                // periodList = periodList.concat(';' + startPeriodYear + "Q" + i );
                            }
                        }
                        else
                        {
                            for (var i = startQuater; i <= 4; i++)
                            {
                                periodList.push(startYear + "Q" + i);
                                //periodList = periodList.concat(';' + startPeriodYear + "Q" + i );
                            }
                        }
                        //startPeriodQuater++;
                        startYear++;
                        startQuater = 1;
                        //console.log(periodList);
                    }
                }

                else if( periodType === "Yearly")
                {
                    var startYear = startPeriod.substring(0,4);

                    var endYear = endPeriod.substring(0,4);
                    for(startYear; startYear <= endYear; startYear++ )
                    {
                        periodList.push( startYear );
                    }

                }

                return periodList;
            },
            getPeriodName: function(periodType,period){
                var periodName="";

                if( periodType === "Monthly")
                {
                    var year = period.substring(0,4);
                    var month = period.substring(4,6);
                    var strMonth="";

                    if(month=="01" || month=="1") 	    strMonth="January";
                    else if(month=="02" || month=="2")	strMonth="February";
                    else if(month=="03" || month=="3")	strMonth="March";
                    else if(month=="04" || month=="4")	strMonth="April";
                    else if(month=="05" || month=="5")	strMonth="May";
                    else if(month=="06" || month=="6")	strMonth="June";
                    else if(month=="07" || month=="7")	strMonth="July";
                    else if(month=="08" || month=="8")	strMonth="August";
                    else if(month=="09" || month=="9")	strMonth="September";
                    else if(month=="10")				strMonth="October";
                    else if(month=="11") 				strMonth="November";
                    else if(month=="12")				strMonth="December";

                    periodName =  strMonth + " " +year;
                }

                else if( periodType === "Quarterly")
                {
                    var year = period.substring(0,4);
                    var quater = period.substring(4,6);
                    var quaterName = "";

                    if(quater== "Q1" ) 	    quaterName = "January - March";
                    else if(quater=="Q2" )	quaterName = "April - June";
                    else if(quater=="Q3" )	quaterName = "July - September";
                    else if(quater=="Q4" )	quaterName = "October - December";

                    periodName =  quaterName + " " +year;
                }

                else if( periodType === "Yearly")
                {
                    //var year = period.substring(0,4);

                    periodName =  period;
                }
                return periodName;
            }

        };
    })
