/**
 *
 */

reportsApp.controller('DataStatusResultController',
    function ($rootScope,$scope, $location,reportSettingService,reportsService,
              ReportAppSectionSettingService,periodService,$window,organisationUnitGroupService,
              userService,ReportConfigurationService,DataSetService,$timeout,OrganisationUnitService)
    {


        $scope.selOrgUnit = sessionStorage.getItem('selOrgUnit');
        $scope.selDataSetUid = sessionStorage.getItem('selDataSetUid');
        //$scope.dataSetPeriodType = sessionStorage.getItem('dataSetPeriodType');
        //$scope.dataSetSource = sessionStorage.getItem('dataSetSource');
        $scope.selStartPeriod = sessionStorage.getItem('selStartPeriod');
        $scope.selEndPeriod = sessionStorage.getItem('selEndPeriod');
        $scope.selStartDate = sessionStorage.getItem('selectedStartPeriod');
        $scope.selEndDate = sessionStorage.getItem('lastDayOfSelEndPeriod');

        DataSetService.getDataSetPeriodTypeAndSource( $scope.selDataSetUid ).then(function(data){
            $scope.dataSetPeriodType = data.dataSets[0].periodType;
            $scope.dataSetName = data.dataSets[0].name;
            $scope.dataSetSource = data.dataSets[0].organisationUnits;
            console.log( "dataSetPeriodType is -- " + $scope.dataSetPeriodType );
            console.log( "dataSetSource length  is -- " + $scope.dataSetSource.length );

            $scope.filteredOrgUnitList=[];
            OrganisationUnitService.getAllChildrenOfSelectedOrgUnit( $scope.selOrgUnit ).then(function(orgUnitList){
                    $scope.allChildrenList = orgUnitList.organisationUnits;
                    angular.forEach( $scope.allChildrenList, function(child){
                        angular.forEach( $scope.dataSetSource, function(dataSetSource){
                            if( child.id === dataSetSource.id )
                            {
                                $scope.filteredOrgUnitList.push( dataSetSource );
                            }
                        });
                    });
                    console.log( "filtered OrgUnit List length  is -- " + $scope.filteredOrgUnitList.length );
                }
            );

            $scope.periodList = periodService.getPeriodListBetweenTwoDates($scope.dataSetPeriodType, $scope.selStartPeriod, $scope.selEndPeriod );
            console.log( "period List length  is -- " + $scope.periodList.length );

            $scope.periodListMap = [];
            angular.forEach( $scope.periodList, function(period){
                $scope.periodName = periodService.getPeriodName($scope.dataSetPeriodType, period );
                $scope.periodListMap[period] = $scope.periodName;
            });

            /*
            periodService.getPeriodListBetweenTwoDates( $scope.dataSetPeriodType, $scope.selStartPeriod, $scope.selEndPeriod ).then(function(periods){
                    $scope.periodList = periods;
                }
            );
            */

        });

        /*
        console.log( "orgUnit Uid is -- " + $scope.selOrgUnit );
        console.log( "dataSet uid  is -- " + $scope.selDataSetUid );
        console.log( "dataSetPeriodType is -- " + $scope.dataSetPeriodType );
        console.log( "dataSetSource length  is -- " + $scope.dataSetSource.length );
        console.log( "selStartPeriod is -- " + $scope.selStartPeriod );
        console.log( "selEndPeriod  is -- " + $scope.selEndPeriod );
        console.log( "selStart Date is -- " + $scope.selStartDate );
        console.log( "selEnd Date  is -- " + $scope.selEndDate );
        */

        $scope.back = function(){
            $location.path('/data-status');

        };


        $scope.back = function( ){
            $location.path('/data-status');

        };





    });
