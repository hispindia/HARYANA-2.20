/**
 * Created by harsh on 22/6/15.
 */


reportsApp.controller('ProgramsController',
    function ($rootScope,$scope, $location,$route,$window,
              $modal,
              UserGroupService,reportSettingService,reportsService,
              ReportAppSectionSettingService,
              GenerateUidService,
              $timeout)
    {

        ReportAppSectionSettingService.getAllReportAppSection().then(function(data){
            $scope.allSections = data.sections;
        });

        $scope.currentSection = {
            uid: "",
            name: "",
            userGroupUid: ""
        };

        $scope.editCurrentSection = {
            uid: "",
            name: "",
            userGroupUid: ""
        };

        $scope.alertMsg = "All fields are required";
        $scope.editAlertMsg = "All fields are required";

        ReportAppSectionSettingService.getAllReportAppSection().then(function(data){
            $scope.sectionSettings = data;
        });

        // creating map for user group uid to name
        UserGroupService.getAllUserGroup().then(function(data){
            $scope.userGroupsMap = [];
            angular.forEach(data.userGroups, function(userGroup){
                $scope.userGroupsMap[userGroup.id] = userGroup;
            })
        });

        // method for open add popup
        $scope.addSectionForm = function () {
            //alert("add section");
            $('#addSectionModal').modal('show');

            UserGroupService.getAllUserGroup().then(function(data){
                    $scope.userGroups = data.userGroups;
                }
            )
        };

        reportSettingService.getAll().then(function(data){
            $scope.reportSettings = data;
        });

        reportsService.getAll().then(function(data){
            $scope.reportListMapping = [];
            angular.forEach(data.reports, function(report){
                $scope.reportListMapping[report.id] = report;
            })
        });


        //add Section action
        $scope.addSection = function() {
            if(validateAddSectionForm())
            {
                GenerateUidService.getUid().then(function(data){
                    $scope.currentSection.uid = data.codes[0];

                    if ($scope.sectionSettings == ""){
                        // create new json
                        $scope.sectionSettings={ "sections": [{
                            "uid": $scope.currentSection.uid,
                            "name": $scope.currentSection.name,
                            "userGroupUid": $scope.currentSection.userGroupUid
                        }]
                        }
                    }else{
                        //push to existing json

                        $scope.sectionSettings.sections.push($scope.currentSection);
                    }

                    ReportAppSectionSettingService.saveSection($scope.sectionSettings).then(function (reponse) {
                        if (reponse != "")
                        {
                            //window.location.path.('/program-management');
                            //$location.path('/program-management');
                            //$route.reload();
                            $window.location.reload();
                            //console.log( " After save Uid : " + $scope.sectionData.uid + " After save Name : " + $scope.sectionData.name + " After Save User Group : " + $scope.sectionData.userGroupUid )
                        }

                    });

                })
            }
        };


    // Code start for Update Section

        // method for open add popup
        $scope.editSection = function ( uid ) {
            //alert("Edit section--" + uid );
            $('#editSectionModal').modal('show');

            ReportAppSectionSettingService.getAllReportAppSection().then(function(data){
                $scope.sectionSettings = data;

                angular.forEach($scope.sectionSettings.sections, function(section){

                    if( section.uid === uid )
                    {
                        //console.log( " Section Uid -- " + section.uid +  " Section Name -- " + section.name + " Section User Group -- " + section.userGroupUid );
                        $scope.editCurrentSection.uid = section.uid;
                        $scope.editCurrentSection.name = section.name;
                        $scope.editCurrentSection.userGroupUid = section.userGroupUid;
                    }
                });

            });

            UserGroupService.getAllUserGroup().then(function(data){
                    $scope.userGroups = data.userGroups;
                }
            )
        };

        //update Section action
        $scope.updateSection = function() {
            if(validateEditSectionForm())
            {

                angular.forEach($scope.sectionSettings.sections, function(section){

                    if( section.uid === $scope.editCurrentSection.uid )
                    {
                        console.log( " Section Uid -- " + section.uid +  " Section Name -- " + $scope.editCurrentSection.name + " Section User Group -- " + $scope.editCurrentSection.userGroupUid );

                        section.name = $scope.editCurrentSection.name;
                        section.userGroupUid = $scope.editCurrentSection.userGroupUid;

                    }
                });

                ReportAppSectionSettingService.saveSection($scope.sectionSettings).then(function (reponse) {
                    if (reponse != "")
                    {
                        //window.location.path.('/program-management');
                        //$location.path('/program-management');
                        //$route.reload();
                        $window.location.reload();
                        //console.log( " After save Uid : " + $scope.sectionData.uid + " After save Name : " + $scope.sectionData.name + " After Save User Group : " + $scope.sectionData.userGroupUid )
                    }

                });
            }
        };


        /*
        $scope.editSection = function (uid) {

            var modalEditSection = $modal.open({
                templateUrl: 'components/programs/editSection.html',
                controller: 'editSectionController',
                windowClass: 'container-1-5',
                resolve: {
                    updateSectionUid: function () {
                        return uid;
                    }
                }
            });

            modalEditSection.result.then(function (response) {
                $timeout(function () {
                    $scope.back();
                }, 500)
            }, function () {
                console.log('Modal dismissed at: ' + new Date());

            });

        };
        */

    // Code for Delete Section


        $scope.deleteSection = function(uid,name)
        {
            if(!confirm("Are you sure that you want to delete section? " + name ))
            {
                //alert("pppp");
                //$location.path('/program-management');
                //$window.location.reload();
            }
            else
            {
                angular.forEach($scope.sectionSettings.sections, function(section,index){

                    if( section.uid === uid)
                    {
                        console.log( " Length -- 1 : " + $scope.sectionSettings.sections.length );
                        console.log( " Index : " + index );
                        console.log( " Selected Uid : " + uid + " Section Name : " + section.name + " After Save User Group : " + $scope.userGroupsMap[section.userGroupUid].name );
                        $scope.sectionSettings.sections.splice(index,1);

                        console.log( " Length -- 2 : " + $scope.sectionSettings.sections.length );


                        ReportAppSectionSettingService.saveSection($scope.sectionSettings).then(function (reponse) {
                            if (reponse != "")
                            {
                                $window.location.reload();
                                //$location.path('/program-management');
                            }

                        });


                    }

                })
            }

        };
    });