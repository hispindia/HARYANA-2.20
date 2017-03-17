/**
 * Created by harsh on 22/6/15.
 */

reportsApp.controller('LeftBarMenuController',
    function ($rootScope,$scope, $location,
              UserGroupService,ResourceService,ReportConfigurationService,userService)

    {
        $scope.accessAuthority = false;

        $scope.tempAccessAuthority = false;

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

                $scope.configurationUserGroupUid = $scope.ReportAppConfigurationSettings.parameters['user_group_for_report'].value;
                //$scope.reportLogoUid = $scope.ReportAppConfigurationSettings.parameters['report_logo'].value;

                //console.log("configurationUser Group Uid --" + $scope.configurationUserGroupUid);
                //console.log("Report Logo  Uid --" + $scope.reportLogoUid);

                userService.getUsersByUserGroup($scope.configurationUserGroupUid).then(function (responseData){
                    $scope.users = responseData.userGroups[0].users;

                    //console.log("Users List  --" + $scope.users);

                    userService.getCurrentUser().then(function (responseUser){
                        $scope.currentUser = responseUser;
                        //$scope.currentUserName = responseUser.userCredentials.username;
                        $scope.currentUserName = responseUser.userCredentials.code;// for 2.20
                        console.log("Current User Uid  --" + $scope.currentUser.id + "  Current User Name  --" + $scope.currentUserName);

                        $scope.tempAccessAuthority = userService.isUserInUserGroup($scope.currentUser.id,$scope.users);

                        if( $scope.tempAccessAuthority || $scope.currentUserName === 'admin')
                        {
                            $scope.accessAuthority = true;
                        }

                        //console.log("accessAuthority --" + $scope.accessAuthority);
                    });

                    });

            }
            else
            {
                userService.getCurrentUser().then(function (responseUser){
                    $scope.currentUser = responseUser;
                    //$scope.currentUserName = responseUser.userCredentials.username;
                    $scope.currentUserName = responseUser.userCredentials.code;// for 2.20
                    console.log("Current User Uid  --" + $scope.currentUser.id + "  Current User Name  --" + $scope.currentUserName);

                    if(  $scope.currentUserName === 'admin')
                    {
                        $scope.accessAuthority = true;
                    }

                    //console.log("accessAuthority --" + $scope.accessAuthority);
                });
            }
        });




        //if( $scope.accessAuthority )
        //{
            $scope.showReportConfigurationPage = function () {
                $location.path('/report-configuration');

            };
            $scope.showProgramManagementScreen = function () {
                $location.path('/program-management');

            };

            $scope.showTemplateManagementScreen = function () {
                $location.path('/template-management');

            };
            $scope.showReportsScreen = function () {
                $location.path('/reports');

            };
        //}

        $scope.showReportGenerationScreen = function () {
            $location.path('/generate-report');

        };

        $scope.showDataStatusScreen = function () {
            $location.path('/data-status');

        };

    });