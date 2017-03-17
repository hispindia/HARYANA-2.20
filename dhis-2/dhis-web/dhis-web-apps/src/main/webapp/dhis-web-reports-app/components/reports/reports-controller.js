/**
 * Created by harsh on 22/6/15.
 */

reportsApp.controller('ReportsController',
    function ($rootScope,$scope,$window, $location,reportSettingService,reportsService,
              ReportAppSectionSettingService,organisationUnitGroupService) {

        $scope.addReport = function () {
            $location.path('/add-report');
        };

        $scope.editReport = function (reportUid) {

            //$location.path('/edit-report?reportUid=' + reportUid );

            //$location.path('/edit-report').replace();
            //$scope.$apply();
            //if(!$scope.$$phase) $scope.$apply();

            sessionStorage.setItem('reportUid',reportUid);

            window.location.href = '../dhis-web-reports-app/index.html#/edit-report?reportUid=' + reportUid;
            //console.log($location.path());
        };


        reportSettingService.getAll().then(function(data){
            $scope.mappedReports = data.reports;
        });

        reportsService.getAll().then(function(data){
            $scope.reportListMapping = [];
            angular.forEach(data.reports, function(report){
                $scope.reportListMapping[report.id] = report;
            })
        });

        ReportAppSectionSettingService.getAllReportAppSection().then(function(data){
            $scope.sectionListMapping = [];
            angular.forEach(data.sections, function(section){
                $scope.sectionListMapping[section.uid] = section;
            })
        });

        organisationUnitGroupService.getAll().then(function(data){
            $scope.organisationUnitGroupsMapping = [];
            angular.forEach(data.organisationUnitGroups, function(organisationUnitGroup ){
                $scope.organisationUnitGroupsMapping[organisationUnitGroup .id] = organisationUnitGroup ;
            })
        });

        reportSettingService.getAll().then(function(data){
            $scope.reportSettings = data;
        });



        // delete report
        $scope.deleteReport = function(uid,name)
        {
            if(!confirm("Are you sure that you want to delete report? " + name ))
            {
                //alert("pppp");
                //return false;
                //$window.location.path('/reports');
                //$window.location.href = '../dhis-web-reports-app/index.html#/reports';
                //$window.location.reload();
            }
            else
            {
                angular.forEach($scope.reportSettings.reports, function(report,index){

                if( report.id === uid )
                    {
                        //console.log( " Length -- 1 : " + $scope.reportSettings.reports.length );
                        //console.log( " Index : " + index );
                        //console.log( " Selected Uid : " + uid + " Report Name : " + $scope.reportListMapping[report.id].name  );

                        $scope.reportSettings.reports.splice(index,1);

                        //console.log( " Length -- 2 : " + $scope.reportSettings.reports.length );

                        reportSettingService.save($scope.reportSettings).then(function(reponse){
                            if (reponse != "")
                            {
                                $window.location.reload();
                                //$location.path('/reports');
                            }
                        })
                    }

                })
            }

        };
    });
