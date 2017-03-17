/**
 * Created by harsh on 22/6/15.
 */

reportsApp.controller('ReportGenerationController',
    function ($rootScope,$scope, $location,reportSettingService,reportsService,
              ReportAppSectionSettingService,periodService,$window,organisationUnitGroupService,
              userService,ReportConfigurationService)
    {

        var clearAllValues = function(){
            $scope.currentReport={
                "id":"",
                "section":"",
                "orgUnit":"",
                "periodType":"",
                "startDate":"",
                "endDate":"",
                "period": {"month" : "",
                            "year" : ""}
            }
        }

        $scope.REPORT_APP_CONFIGURATION_KEY = "reportApp-configuration-json";
        $scope.ReportAppConfigurationSettings = {};
        $scope.ReportAppConfigurationSettings.parameters=[];

        ReportConfigurationService.getAllReportConfiguration($scope.REPORT_APP_CONFIGURATION_KEY).then(function(conf){
            for (var count in conf.parameters){
                $scope.ReportAppConfigurationSettings.parameters[conf.parameters[count].key] = conf.parameters[count];
            }
        });

        ReportConfigurationService.getAllReportConfiguration().then(function (data) {
            if(data != "") {
                $scope.configurationParameters = data;
                $scope.reportLogoUid = $scope.ReportAppConfigurationSettings.parameters['report_logo'].value;
                //console.log("Report Logo  Uid --" + $scope.reportLogoUid);
            }
        });




        $scope.reportSettingsBackup=[];
        clearAllValues();

        var updateReportSetting = function(){
            clearAllValues()
            reportSettingService.getAll().then(function(data){
                $scope.reportSettings = data.reports;

                $scope.reportSettingMapping = [];
                angular.forEach($scope.reportSettings, function(report){
                    report.name = $scope.reportListMapping[report.id].name;
                    report.userGroupAccesses = $scope.reportListMapping[report.id].userGroupAccesses;

                    //console.log( " 1 Report Name-- " + report.name + "  Report userGroupAccesses-- " + report.userGroupAccesses );


                    $scope.reportSettingMapping[report.id] = report;
                    angular.copy($scope.reportSettings,$scope.reportSettingsBackup);

                })

                organisationUnitGroupService.getOuGroupsByOu($scope.selectedOrgUnit).then(function(data){
                    //make a map for quick comparison
                    $scope.organisationUnitGroupMap = [];
                    angular.forEach(data.organisationUnitGroups,function(ouGroup){
                        $scope.organisationUnitGroupMap[ouGroup.id] = ouGroup;
                    })

                         for (var count=0; count < $scope.reportSettings.length;count++){
                            if ($scope.organisationUnitGroupMap[$scope.reportSettings[count].orgUnitGroup] == undefined || !isReportInUserGroup($scope.reportSettings[count].userGroupAccesses)){
                                $scope.reportSettings.splice(count,1);
                                count--;
                            }
                        }
                        //console.log( " 2 Report Name-- " + report.name + " Report OrgUnitGroup-- " + report.orgUnitGroup + " Report userGroupAccesses-- " + report.userGroupAccesses);


                    })
                    $scope.updatePeriods();

                })

        }
        $scope.updatePeriods = function(){
            if (true){
                var currentDate = new Date();
                $scope.monthList = periodService.getMonthList();
                $scope.yearList = periodService.getYearListBetweenTwoYears(1900,currentDate.getFullYear());

            }else{
                $scope.periodList = periodService.getLast12Months();
            }
        }
        //initialize
        updateReportSetting();

            $scope.listenToOuChange = function(){
            $scope.selectedOrgUnit = selection.getSelected();
            $scope.currentReport.orgUnit = $scope.selectedOrgUnit;
            updateReportSetting();

        };

        userService.getCurrentUser().then(function(user){
            $scope.currentUser = user;
            $scope.userGroupMap = [];
            angular.forEach($scope.currentUser.userGroups,function(userGroup,index){

                $scope.userGroupMap[userGroup.id] = userGroup;
            })
        });
        selection.setListenerFunction($scope.listenToOuChange);


        ReportAppSectionSettingService.getAllReportAppSection().then(function(data) {
            $scope.sectionList = data.sections;
        })
          // get DHIS reports
            reportsService.getAllWithDetails().then(function(data){
            $scope.reportList = data.reports;
                $scope.reportListMapping = [];
                angular.forEach($scope.reportList,function(report){
                    $scope.reportListMapping[report.id] = report;
                })

                updateReportSetting();
        });

        var fetchReportSettings = function(){

            reportSettingService.getAll().then(function(data){
                $scope.reportSettings = data.reports;
                $scope.reportSettingMapping = [];
                angular.forEach($scope.reportSettings, function(report){
                        report.name = $scope.reportListMapping[report.id].name;
                        report.userGroupAccesses = $scope.reportListMapping[report.id].userGroupAccesses;

                    $scope.reportSettingMapping[report.id] = report;
                })
            });

        };

        var isReportInUserGroup = function(userGroupAccesses){
          var returnVariable = false;
          angular.forEach(userGroupAccesses,function(userGroupAccess){
              //console.log( " 3 userGroupAccess Name-- " + userGroupAccess + " -- " + $scope.userGroupMap[userGroupAccess.userGroup.id] );
                if ($scope.userGroupMap[userGroupAccess.userGroup.id] != undefined)
                    returnVariable = true;
          });
            return returnVariable;
        };





        $scope.generateReport = function(){
		//alert( $scope.currentReport.period.year );
        //alert( $scope.currentReport.section );
        //alert( $scope.currentReport.id );
        //alert( $scope.currentReport.section );
            var isValidated = "true";
            if( $scope.currentReport.section === "" || $scope.currentReport.section === undefined )
            {
                alert( "Please select report section");
                isValidated = "false";
                return;
            }
            else if( $scope.currentReport.id === "" || $scope.currentReport.id === undefined )
            {
                alert( "Please select report");
                isValidated = "false";
                return;
            }

            else if( $scope.currentReport.id != undefined )
            {
                var date = new Date();
                var currentMonthFirstDay = new Date(date.getFullYear(), date.getMonth(), 1);
                var currentMonthLastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);

                //alert( "First day- " + currentMonthFirstDay + " Last Day --" + currentMonthLastDay );

                if( $scope.reportSettingMapping[$scope.currentReport.id].periodType =='monthly')
                {

                    if( $scope.currentReport.period.month === "" || $scope.currentReport.period.month === undefined )
                    {
                        alert( "Please select month");
                        isValidated = "false";
                        return;
                    }
                    else if( $scope.currentReport.period.year === "" || $scope.currentReport.period.year === undefined )
                    {
                        alert( "Please select year");
                        isValidated = "false";
                        return;
                    }

                    else if( $scope.currentReport.period.month != undefined && $scope.currentReport.period.year != undefined  )
                    {
                        var selYear = $scope.currentReport.period.year;
                        var selMonth = $scope.currentReport.period.month;
                        var selectedPeriod = new Date( selYear + "-" + selMonth + "-01" );
                        var lastDayOfSelPeriod = new Date(selectedPeriod.getFullYear(), selectedPeriod.getMonth() + 1, 0);

                        //alert( "Last day of Sel period " + lastDayOfSelPeriod  );

                        if( lastDayOfSelPeriod > currentMonthLastDay )
                        {
                            alert( "You can not select future period");
                            isValidated = "false";
                            return;
                        }
                    }
                }
                else if( $scope.reportSettingMapping[$scope.currentReport.id].periodType =='start-end' )
                {
                    var selStartDate = new Date( $scope.currentReport.startDate );
                    var selEndDate = new Date( $scope.currentReport.endDate );
                    var startDateYear = $scope.currentReport.startDate.split("-")[0];
                    var endDateYear = $scope.currentReport.endDate.split("-")[0];

                    var diffInYear = endDateYear - startDateYear;

                    if( $scope.currentReport.startDate === "" || $scope.currentReport.startDate === undefined )
                    {
                        alert( "Please select start date");
                        isValidated = "false";
                        return;
                    }
                    else if( $scope.currentReport.endDate === "" || $scope.currentReport.endDate === undefined )
                    {
                        alert( "Please select end date");
                        isValidated = "false";
                        return;
                    }

                    else if( selStartDate > selEndDate )
                    {
                        alert( "Start date should not be greater then end date");
                        isValidated = "false";
                        return;
                    }
                    else if( diffInYear > 5 )
                    {
                        alert( "Date difference should not greater then 5 year");
                        isValidated = "false";
                        return;
                    }
                }

            }

            if( isValidated === "true")
            {
                //alert(isValidated);
                if( $scope.currentReport.period.year == "" )
                {
                    $scope.currentReport.period.year = ($scope.currentReport.startDate).split("-")[0];
                    $scope.currentReport.period.month = ($scope.currentReport.startDate).split("-")[1];
                }
                $window.location.href = "../dhis-web-reporting/generateHtmlReport.action?uid="+$scope.currentReport.id+"&pe="+$scope.currentReport.period.year+""+$scope.currentReport.period.month+"&ou="+ selection.getSelected()+"&sd="+$scope.currentReport.startDate+"&ed="+$scope.currentReport.endDate;
            }

        }

    });
