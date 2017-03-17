/**
 * Created by Mithilesh .
 *
 */

reportsApp.controller('ConfigurationController',
    function($rootScope,$scope, $location,$route,$window,
             $modal,
             UserGroupService,
             ResourceService,
             ReportAppSectionSettingService,
             ReportConfigurationService,
             GenerateUidService,reportsService,
             $timeout)
    {

        UserGroupService.getAllUserGroup().then(function(data){
            $scope.userGroups = data.userGroups;
        });

        ResourceService.getAllResource().then( function(data){
            $scope.documents = data.documents;
        })

        $scope.showSuccess = false;
        ReportConfigurationService.getAllReportConfiguration().then(function (data) {
            if (data != "") {
                $scope.configurationParameters = data;

            } else {
                //do default properties
                $scope.configurationParameters = {
                    "parameters": [
                        {
                            "key" : "user_group_for_report",
                            "valueType" : "userGroup",
                            "value" : ""
                        },
                        {
                            "key" : "report_logo",
                            "valueType" : "reportLogo",
                            "value" : ""
                        },
                        {
                            "key" : "ds_status_report",
                            "valueType" : "dataSetStatusReport",
                            "value" : ""
                        }
                    ]
                };
            }

            UserGroupService.getAllUserGroup().then(function(data){
                $scope.userGroups = data.userGroups;
            });

            ResourceService.getAllResource().then( function(data){
                $scope.documents = data.documents;
            })
            reportsService.getAllWithDetails().then(function(data){
                $scope.reportList = data.reports;
            });
        });

        // Save Report Configuration
        $scope.saveReportConfigurationAndGo = function () {
            if(validateConfigurationForm())
            {
                $scope.saveConfiguration();
                $scope.showSuccess = true;
                $timeout(function () {
                    $location.path('/');
                }, 400);

            }

        };

        $scope.saveConfiguration = function () {

            ReportConfigurationService.saveReportConfiguration($scope.configurationParameters).then(function (response) {

            });
        };

        //delete/reset Configuration
        $scope.resetConfiguration = function () {
            ReportConfigurationService.deleteReportConfiguration().then(function (response) {
                $scope.showSuccess = true;
                $timeout(function () {
                    $route.reload();
                }, 200);
            });
        };

        $scope.cancel = function(){
            $location.path('/index.html');

        };

    });
