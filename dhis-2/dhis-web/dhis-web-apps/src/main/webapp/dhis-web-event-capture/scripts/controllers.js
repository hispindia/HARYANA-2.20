/* global angular */

'use strict';

/* Controllers */
var eventCaptureControllers = angular.module('eventCaptureControllers', [])

    //Controller for settings page
    .controller('MainController',
        function($rootScope,
                 $scope,
                 $modal,
                 $timeout,
                 $translate,
                 $anchorScroll,
                 orderByFilter,
                 SessionStorageService,
                 Paginator,
                 OptionSetService,
                 MetaDataFactory,
                 ProgramFactory,
                 DHIS2EventFactory,
                 DHIS2EventService,
                 ContextMenuSelectedItem,
                 DateUtils,

                 CalendarService,
                 GridColumnService,
                 CustomFormService,
                 ECStorageService,
                 CurrentSelection,
                 ModalService,
                 DialogService,
                 AuthorityService,
                 TrackerRulesExecutionService,
                 TrackerRulesFactory) {
            //selected org unit
            $scope.selectedOrgUnit = '';
            $scope.treeLoaded = false;
            $scope.selectedSection = {id: 'ALL'};
            $rootScope.ruleeffects = {};
            $scope.hiddenFields = {};

            $scope.calendarSetting = CalendarService.getSetting();

            //Paging
            $scope.pager = {pageSize: 50, page: 1, toolBarDisplay: 5};

            //Editing
            $scope.eventRegistration = false;
            $scope.editGridColumns = false;
            $scope.editingEventInFull = false;
            $scope.editingEventInGrid = false;
            $scope.updateSuccess = false;
            $scope.currentGridColumnId = '';
            $scope.dhis2Events = [];
            $scope.currentEvent = {};
            $scope.currentEventOriginialValue = {};
            $scope.displayCustomForm = false;
            $scope.currentElement = {id: '', update: false};
            $scope.optionSets = [];
            $scope.proceedSelection = true;
            $scope.formUnsaved = false;

            //notes
            $scope.note = {};
            $scope.today = DateUtils.getToday();

            var userProfile = SessionStorageService.get('USER_PROFILE');
            var storedBy = userProfile && userProfile.username ? userProfile.username : '';

            $scope.noteExists = false;

            //watch for selection of org unit from tree
            $scope.$watch('selectedOrgUnit', function() {

                if(angular.isObject($scope.selectedOrgUnit)){

                    $scope.pleaseSelectLabel = $translate.instant('please_select');
                    $scope.registeringUnitLabel = $translate.instant('registering_unit');
                    $scope.eventCaptureLabel = $translate.instant('event_capture');
                    $scope.programLabel = $translate.instant('program');
                    $scope.searchLabel = $translate.instant('search');
                    $scope.yesLabel = $translate.instant('yes');
                    $scope.noLabel = $translate.instant('no');

                    SessionStorageService.set('SELECTED_OU', $scope.selectedOrgUnit);

                    $scope.userAuthority = AuthorityService.getUserAuthorities(SessionStorageService.get('USER_ROLES'));

                    //get ouLevels
                    ECStorageService.currentStore.open().done(function(){
                        ECStorageService.currentStore.getAll('ouLevels').done(function(response){
                            var ouLevels = angular.isObject(response) ? orderByFilter(response, '-level').reverse() : [];
                            CurrentSelection.setOuLevels(orderByFilter(ouLevels, '-level').reverse());
                        });
                    });

                    if($scope.optionSets.length < 1){
                        $scope.optionSets = [];
                        MetaDataFactory.getAll('optionSets').then(function(optionSets){
                            angular.forEach(optionSets, function(optionSet){
                                $scope.optionSets[optionSet.id] = optionSet;
                            });
                            $scope.loadPrograms();
                        });
                    }
                    else{
                        $scope.loadPrograms();
                    }
                }
            });

            //load programs associated with the selected org unit.
            $scope.loadPrograms = function() {

                $scope.resetOu = false;
                $scope.selectedProgramStage = null;
                $scope.allProgramRules = [];
                $scope.dhis2Events = [];
                $scope.currentEvent = {};
                $scope.currentEventOriginialValue = {};

                $scope.eventRegistration = false;
                $scope.editGridColumns = false;
                $scope.editingEventInFull = false;
                $scope.editingEventInGrid = false;
                $scope.updateSuccess = false;
                $scope.currentGridColumnId = '';
                $scope.displayCustomForm = false;

                if (angular.isObject($scope.selectedOrgUnit)) {

                    ProgramFactory.getProgramsByOu($scope.selectedOrgUnit, $scope.selectedProgram).then(function(response){
                        $scope.programs = response.programs;
                        $scope.selectedProgram = response.selectedProgram;
                        $scope.getProgramDetails();
                    });
                }
            };

            $scope.getProgramDetails = function(){

                $scope.selectedProgramStage = null;
                $scope.eventFetched = false;

                //Filtering
                $scope.reverse = false;
                $scope.sortHeader = {};
                $scope.filterText = {};

                if( $scope.userAuthority && $scope.userAuthority.canAddOrUpdateEvent &&
                    $scope.selectedProgram &&
                    $scope.selectedProgram.programStages &&
                    $scope.selectedProgram.programStages[0] &&
                    $scope.selectedProgram.programStages[0].id){

                    //because this is single event, take the first program stage
                    MetaDataFactory.get('programStages', $scope.selectedProgram.programStages[0].id).then(function (programStage){

                        $scope.selectedProgramStage = programStage;

                        angular.forEach($scope.selectedProgramStage.programStageSections, function(section){
                            section.open = true;
                        });

                        $scope.prStDes = [];
                        $scope.eventGridColumns = [];
                        $scope.filterTypes = {};
                        $scope.newDhis2Event = {};

                        $scope.eventGridColumns.push({name: 'form_id', id: 'uid', type: 'string', compulsory: false, showFilter: false, show: false});
                        $scope.filterTypes['uid'] = 'string';

                        $scope.eventGridColumns.push({name: $scope.selectedProgramStage.reportDateDescription ? $scope.selectedProgramStage.reportDateDescription : 'incident_date', id: 'eventDate', type: 'date', compulsory: false, showFilter: false, show: true});
                        $scope.filterTypes['eventDate'] = 'date';
                        $scope.filterText['eventDate']= {};

                        angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
                            $scope.prStDes[prStDe.dataElement.id] = prStDe;
                            $scope.newDhis2Event[prStDe.dataElement.id] = '';

                            //generate grid headers using program stage data elements
                            //create a template for new event
                            //for date type dataelements, filtering is based on start and end dates
                            $scope.eventGridColumns.push({name: prStDe.dataElement.formName ? prStDe.dataElement.formName : prStDe.dataElement.name,
                                id: prStDe.dataElement.id,
                                type: prStDe.dataElement.type,
                                compulsory: prStDe.compulsory,
                                showFilter: false,
                                show: prStDe.displayInReports});

                            $scope.filterTypes[prStDe.dataElement.id] = prStDe.dataElement.type;

                            if(prStDe.dataElement.type === 'date' || prStDe.dataElement.type === 'int' ){
                                $scope.filterText[prStDe.dataElement.id]= {};
                            }
                        });

                        $scope.customForm = CustomFormService.getForProgramStage($scope.selectedProgramStage, $scope.prStDes);

                        if($scope.selectedProgramStage.captureCoordinates){
                            $scope.newDhis2Event.coordinate = {};
                        }
                        $scope.newDhis2Event.eventDate = '';

                        TrackerRulesFactory.getRules($scope.selectedProgram.id).then(function(rules){
                            $scope.allProgramRules = rules;
                            $scope.loadEvents();
                        });
                    });
                }
            };

            //get events for the selected program (and org unit)
            $scope.loadEvents = function(){

                $scope.noteExists = false;
                $scope.dhis2Events = [];
                $scope.eventLength = 0;

                $scope.eventFetched = false;

                if( $scope.selectedProgram &&
                    $scope.selectedProgram.programStages &&
                    $scope.selectedProgram.programStages[0] &&
                    $scope.selectedProgram.programStages[0].id){

                    //Load events for the selected program stage and orgunit
                    DHIS2EventFactory.getByStage($scope.selectedOrgUnit.id, $scope.selectedProgramStage.id, $scope.pager, true ).then(function(data){

                        if(data.events){
                            $scope.eventLength = data.events.length;
                        }

                        $scope.dhis2Events = data.events;

                        if( data.pager ){
                            $scope.pager = data.pager;
                            $scope.pager.toolBarDisplay = 5;

                            Paginator.setPage($scope.pager.page);
                            Paginator.setPageCount($scope.pager.pageCount);
                            Paginator.setPageSize($scope.pager.pageSize);
                            Paginator.setItemCount($scope.pager.total);
                        }

                        //process event list for easier tabular sorting
                        if( angular.isObject( $scope.dhis2Events ) ) {

                            for(var i=0; i < $scope.dhis2Events.length; i++){

                                if($scope.dhis2Events[i].notes && !$scope.noteExists){
                                    $scope.noteExists = true;
                                }

                                //check if event is empty
                                if(!angular.isUndefined($scope.dhis2Events[i].dataValues)){

                                    angular.forEach($scope.dhis2Events[i].dataValues, function(dataValue){

                                        //converting event.datavalues[i].datavalue.dataelement = value to
                                        //event[dataElement] = value for easier grid display.
                                        if($scope.prStDes[dataValue.dataElement]){

                                            var val = dataValue.value;
                                            if(angular.isObject($scope.prStDes[dataValue.dataElement].dataElement)){

                                                //converting int string value to number for proper sorting.
                                                if($scope.prStDes[dataValue.dataElement].dataElement.type === 'int'){
                                                    if( dhis2.validation.isNumber(val)  ){
                                                        //val = new Number(val);
                                                        val = parseFloat(val, 10);
                                                    }
                                                }
                                                if($scope.prStDes[dataValue.dataElement].dataElement.optionSetValue){
                                                    if($scope.prStDes[dataValue.dataElement].dataElement.optionSet &&
                                                        $scope.prStDes[dataValue.dataElement].dataElement.optionSet.id &&
                                                        $scope.optionSets[$scope.prStDes[dataValue.dataElement].dataElement.optionSet.id] &&
                                                        $scope.optionSets[$scope.prStDes[dataValue.dataElement].dataElement.optionSet.id].options ){
                                                        val = OptionSetService.getName($scope.optionSets[$scope.prStDes[dataValue.dataElement].dataElement.optionSet.id].options, val);
                                                    }
                                                }
                                                if($scope.prStDes[dataValue.dataElement].dataElement.type === 'date'){
                                                    val = DateUtils.formatFromApiToUser(val);
                                                }

                                                if($scope.prStDes[dataValue.dataElement].dataElement.type === 'bool'){
                                                    val = val === 'true' ? $scope.yesLabel : $scope.noLabel;
                                                }

                                                if( $scope.prStDes[dataValue.dataElement].dataElement.type === 'trueOnly'){
                                                    if(val === 'true'){
                                                        val = true;
                                                    }
                                                    else{
                                                        val = false;
                                                    }
                                                }
                                            }
                                            $scope.dhis2Events[i][dataValue.dataElement] = val;
                                        }

                                    });
                  
                                    
                                    var tomorrow =new Date($scope.dhis2Events[i].eventDate);

                                    tomorrow.setDate( tomorrow.getDate());
                                    
                                    
                                    var now = new Date();
                                 
                                    now=tomorrow.toLocaleDateString('af-ZA');
                                    $scope.dhis2Events[i]['uid'] = $scope.dhis2Events[i].event;
                                    $scope.dhis2Events[i].eventDate = now;

                                    $scope.dhis2Events[i]['eventDate'] = now;
                                    delete $scope.dhis2Events[i].dataValues;
                                }
                            }

                            if($scope.noteExists && !GridColumnService.columnExists($scope.eventGridColumns, 'comment')){
                                $scope.eventGridColumns.push({name: 'comment', id: 'comment', type: 'string', compulsory: false, showFilter: false, show: true});
                            }

                            if(!$scope.sortHeader.id){
                                $scope.sortEventGrid({name: $scope.selectedProgramStage.reportDateDescription ? $scope.selectedProgramStage.reportDateDescription : 'incident_date', id: 'eventDate', type: 'date', compulsory: false, showFilter: false, show: true});
                            }
                        }

                        $scope.eventFetched = true;
                    });
                }
            };

            $scope.jumpToPage = function(){

                if($scope.pager && $scope.pager.page && $scope.pager.pageCount && $scope.pager.page > $scope.pager.pageCount){
                    $scope.pager.page = $scope.pager.pageCount;
                }
                $scope.loadEvents();
            };

            $scope.resetPageSize = function(){
                $scope.pager.page = 1;
                $scope.loadEvents();
            };

            $scope.getPage = function(page){
                $scope.pager.page = page;
                $scope.loadEvents();
            };

            $scope.sortEventGrid = function(gridHeader){
                if ($scope.sortHeader && $scope.sortHeader.id === gridHeader.id){
                    $scope.reverse = !$scope.reverse;
                    return;
                }
                $scope.sortHeader = gridHeader;
                if($scope.sortHeader.type === 'date'){
                    $scope.reverse = true;
                }
                else{
                    $scope.reverse = false;
                }
            };

            $scope.d2Sort = function(dhis2Event){
                if($scope.sortHeader && $scope.sortHeader.type === 'date'){
                    var d = dhis2Event[$scope.sortHeader.id];
                    return DateUtils.getDate(d);
                }
                return dhis2Event[$scope.sortHeader.id];
            };

            $scope.showHideColumns = function(){

                $scope.hiddenGridColumns = 0;

                angular.forEach($scope.eventGridColumns, function(eventGridColumn){
                    if(!eventGridColumn.show){
                        $scope.hiddenGridColumns++;
                    }
                });

                var modalInstance = $modal.open({
                    templateUrl: 'views/column-modal.html',
                    controller: 'ColumnDisplayController',
                    resolve: {
                        gridColumns: function () {
                            return $scope.eventGridColumns;
                        },
                        hiddenGridColumns: function(){
                            return $scope.hiddenGridColumns;
                        }
                    }
                });

                modalInstance.result.then(function (gridColumns) {
                    $scope.eventGridColumns = gridColumns;
                }, function () {
                });
            };

            $scope.searchInGrid = function(gridColumn){

                $scope.currentFilter = gridColumn;

                for(var i=0; i<$scope.eventGridColumns.length; i++){

                    //toggle the selected grid column's filter
                    if($scope.eventGridColumns[i].id === gridColumn.id){
                        $scope.eventGridColumns[i].showFilter = !$scope.eventGridColumns[i].showFilter;
                    }
                    else{
                        $scope.eventGridColumns[i].showFilter = false;
                    }
                }
            };

            $scope.removeStartFilterText = function(gridColumnId){
                $scope.filterText[gridColumnId].start = undefined;
            };

            $scope.removeEndFilterText = function(gridColumnId){
                $scope.filterText[gridColumnId].end = undefined;
            };

            $scope.cancel = function(){

                if($scope.formIsChanged()){
                    var modalOptions = {
                        closeButtonText: 'cancel',
                        actionButtonText: 'proceed',
                        headerText: 'warning',
                        bodyText: 'unsaved_data_exists_proceed'
                    };

                    ModalService.showModal({}, modalOptions).then(function(result){
                        for(var i=0; i<$scope.dhis2Events.length; i++){
                            if($scope.dhis2Events[i].event === $scope.currentEvent.event){
                                $scope.dhis2Events[i] = $scope.currentEventOriginialValue;
                                break;
                            }
                        }
                        $scope.showEventList();
                    });
                }
                else{
                    $scope.showEventList();
                }
            };

            $scope.showEventList = function(dhis2Event){
                ContextMenuSelectedItem.setSelectedItem(dhis2Event);
                $scope.eventRegistration = false;
                $scope.editingEventInFull = false;
                $scope.editingEventInGrid = false;
                $scope.currentElement.updated = false;
                $scope.currentEvent = {};
                $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);
            };

            $scope.showEventRegistration = function(){
                $scope.displayCustomForm = $scope.customForm ? true:false;
                $scope.currentEvent = {};
                $scope.eventRegistration = !$scope.eventRegistration;
                $scope.currentEvent = angular.copy($scope.newDhis2Event);
                $scope.outerForm.submitted = false;
                $scope.note = {};

                if($scope.selectedProgramStage.preGenerateUID){
                    $scope.eventUID = dhis2.util.uid();
                    $scope.currentEvent['uid'] = $scope.eventUID;
                }
                $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);

                if($scope.eventRegistration){
                    $scope.executeRules();
                }
            };

            $scope.showEditEventInGrid = function(){
                $scope.currentEvent = ContextMenuSelectedItem.getSelectedItem();
                $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);
                $scope.editingEventInGrid = !$scope.editingEventInGrid;

                $scope.outerForm.$valid = true;
            };

            $scope.showEditEventInFull = function(){
                $scope.note = {};
                $scope.displayCustomForm = $scope.customForm ? true:false;

                $scope.currentEvent = ContextMenuSelectedItem.getSelectedItem();
                $scope.editingEventInFull = !$scope.editingEventInFull;
                $scope.eventRegistration = false;

                angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
                    if(!$scope.currentEvent.hasOwnProperty(prStDe.dataElement.id)){
                        $scope.currentEvent[prStDe.dataElement.id] = '';
                    }
                });
                $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);

                if($scope.editingEventInFull){
                    //Blank out rule effects, as there is no rules in effect before the first
                    //time the rules is run on a new page.
                    $rootScope.ruleeffects[$scope.currentEvent.event] = {};
                    $scope.executeRules();
                }
            };

            $scope.switchDataEntryForm = function(){
                $scope.displayCustomForm = !$scope.displayCustomForm;
            };

            $scope.addEvent = function(addingAnotherEvent){

                //check for form validity
                $scope.outerForm.submitted = true;
                if( $scope.outerForm.$invalid ){
                    $scope.selectedSection.id = 'ALL';
                    angular.forEach($scope.selectedProgramStage.programStageSections, function(section){
                        section.open = true;
                    });
                    return false;
                }

                //the form is valid, get the values
                //but there could be a case where all dataelements are non-mandatory and
                //the event form comes empty, in this case enforce at least one value
                var valueExists = false;
                var dataValues = [];
                for(var dataElement in $scope.prStDes){
                    var val = $scope.currentEvent[dataElement];

                    if($scope.prStDes[dataElement].dataElement.type === 'trueOnly'){
                        valueExists = true;
                        if(!val){
                            val = "";
                        }
                    }
                    if(val && $scope.prStDes[dataElement].dataElement.optionSetValue){
                        valueExists = true;
                        if($scope.prStDes[dataElement].dataElement.optionSet){
                            val = OptionSetService.getCode($scope.optionSets[$scope.prStDes[dataElement].dataElement.optionSet.id].options,val);
                        }
                    }
                    if(val && $scope.prStDes[dataElement].dataElement.type === 'date'){
                        valueExists = true;
                        val = DateUtils.formatFromUserToApi(val);
                    }
                    dataValues.push({dataElement: dataElement, value: val});
                }

                if(!valueExists){
                    var dialogOptions = {
                        headerText: 'empty_form',
                        bodyText: 'please_fill_at_least_one_dataelement'
                    };

                    DialogService.showDialog({}, dialogOptions);
                    return false;
                }

                if(addingAnotherEvent){
                    $scope.disableSaveAndAddNew = true;
                }

                var newEvent = angular.copy($scope.currentEvent);

                //prepare the event to be created
                var dhis2Event = {
                    program: $scope.selectedProgram.id,
                    programStage: $scope.selectedProgramStage.id,
                    orgUnit: $scope.selectedOrgUnit.id,
                    status: 'ACTIVE',
                    eventDate: DateUtils.formatFromUserToApi(newEvent.eventDate),
                    dataValues: dataValues
                };

                if($scope.selectedProgramStage.preGenerateUID && !angular.isUndefined(newEvent['uid'])){
                    dhis2Event.event = newEvent['uid'];
                }

                if(!angular.isUndefined($scope.note.value) && $scope.note.value != ''){
                    dhis2Event.notes = [{value: $scope.note.value}];

                    newEvent.notes = [{value: $scope.note.value, storedDate: $scope.today, storedBy: storedBy}];

                    $scope.noteExists = true;
                }

                if($scope.selectedProgramStage.captureCoordinates){
                    dhis2Event.coordinate = {latitude: $scope.currentEvent.coordinate.latitude ? $scope.currentEvent.coordinate.latitude : '',
                        longitude: $scope.currentEvent.coordinate.longitude ? $scope.currentEvent.coordinate.longitude : ''};
                }

                //send the new event to server
                DHIS2EventFactory.create(dhis2Event).then(function(data) {
                    if (data.response.importSummaries[0].status === 'ERROR') {
                        var dialogOptions = {
                            headerText: 'event_registration_error',
                            bodyText: data.message
                        };

                        DialogService.showDialog({}, dialogOptions);
                    }
                    else {

                        //add the new event to the grid
                        newEvent.event = data.response.importSummaries[0].reference;
                        if( !$scope.dhis2Events ){
                            $scope.dhis2Events = [];
                        }
                        newEvent['uid'] = newEvent.event;
                        newEvent['eventDate'] = newEvent.eventDate;
                        $scope.dhis2Events.splice(0,0,newEvent);

                        $scope.eventLength++;

                        $scope.eventRegistration = false;
                        $scope.editingEventInFull = false;
                        $scope.editingEventInGrid = false;

                        //reset form
                        $scope.currentEvent = {};
                        $scope.currentEvent = angular.copy($scope.newDhis2Event);
                        $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);

                        $scope.note = {};
                        $scope.outerForm.submitted = false;
                        $scope.outerForm.$setPristine();
                        $scope.disableSaveAndAddNew = false;

                        //this is to hide typeAheadPopUps - shouldn't be an issue in
                        //the first place.
                        $timeout(function() {
                            angular.element('#hideTypeAheadPopUp').trigger('click');
                        }, 10);

                        //decide whether to stay in the current screen or not.
                        if(addingAnotherEvent){
                            $scope.showEventRegistration();
                            $anchorScroll();
                        }
                    }
                });
            };

            $scope.updateEvent = function(){

                //check for form validity
                $scope.outerForm.submitted = true;
                if( $scope.outerForm.$invalid ){
                    $scope.selectedSection.id = 'ALL';
                    angular.forEach($scope.selectedProgramStage.programStageSections, function(section){
                        section.open = true;
                    });
                    return false;
                }

                //the form is valid, get the values
                var dataValues = [];
                for(var dataElement in $scope.prStDes){
                    var val = $scope.currentEvent[dataElement];

                    if($scope.prStDes[dataElement].dataElement.type === 'trueOnly'){
                        if(!val){
                            val = "";
                        }
                    }
                    if(val && $scope.prStDes[dataElement].dataElement.optionSetValue){
                        if($scope.prStDes[dataElement].dataElement.optionSet){
                            val = OptionSetService.getCode($scope.optionSets[$scope.prStDes[dataElement].dataElement.optionSet.id].options,val);
                        }
                    }
                    if(val && $scope.prStDes[dataElement].dataElement.type === 'date'){
                        val = DateUtils.formatFromUserToApi(val);
                    }
                    dataValues.push({dataElement: dataElement, value: val});
                }

                var updatedEvent = {
                    program: $scope.currentEvent.program,
                    programStage: $scope.currentEvent.programStage,
                    orgUnit: $scope.currentEvent.orgUnit,
                    status: 'ACTIVE',
                    eventDate: DateUtils.formatFromUserToApi($scope.currentEvent.eventDate),
                    event: $scope.currentEvent.event,
                    dataValues: dataValues
                };

                if($scope.selectedProgramStage.captureCoordinates){
                    updatedEvent.coordinate = {latitude: $scope.currentEvent.coordinate.latitude ? $scope.currentEvent.coordinate.latitude : '',
                        longitude: $scope.currentEvent.coordinate.longitude ? $scope.currentEvent.coordinate.longitude : ''};
                }

                if(!angular.isUndefined($scope.note.value) && $scope.note.value != ''){

                    updatedEvent.notes = [{value: $scope.note.value}];

                    if($scope.currentEvent.notes){
                        $scope.currentEvent.notes.splice(0,0,{value: $scope.note.value, storedDate: $scope.today, storedBy: storedBy});
                    }
                    else{
                        $scope.currentEvent.notes = [{value: $scope.note.value, storedDate: $scope.today, storedBy: storedBy}];
                    }

                    $scope.noteExists = true;
                }

                DHIS2EventFactory.update(updatedEvent).then(function(data){

                    //reflect the change in the gird
                    $scope.resetEventValue($scope.currentEvent);
                    $scope.outerForm.submitted = false;
                    $scope.editingEventInFull = false;
                    $scope.currentEvent = {};
                    $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);
                });
            };

            $scope.updateEventDate = function () {
                $scope.updateSuccess = false;

                $scope.currentElement = {id: 'eventDate'};

                var rawDate = angular.copy($scope.currentEvent.eventDate);
                var convertedDate = DateUtils.format($scope.currentEvent.eventDate);

                if (!rawDate || !convertedDate || rawDate !== convertedDate) {
                    $scope.invalidDate = true;
                    $scope.currentEvent.eventDate = $scope.currentEventOriginialValue.eventDate;
                    $scope.resetEventValue($scope.currentEvent);
                    $scope.currentElement.updated = false;
                    return false;
                }

                //get new and old values
                var newValue = $scope.currentEvent.eventDate;
                var oldValue = $scope.currentEventOriginialValue.eventDate;

                if ($scope.currentEvent.eventDate === '') {
                    $scope.currentEvent.eventDate = oldValue;
                    $scope.resetEventValue($scope.currentEvent);
                    $scope.currentElement.updated = false;
                    return false;
                }

                if(newValue !== oldValue){
                    var e = {event: $scope.currentEvent.event,
                        orgUnit: $scope.currentEvent.orgUnit,
                        eventDate: DateUtils.formatFromUserToApi($scope.currentEvent.eventDate)
                    };

                    var updatedFullValueEvent = DHIS2EventService.reconstructEvent($scope.currentEvent, $scope.selectedProgramStage.programStageDataElements);

                    DHIS2EventFactory.updateForEventDate(e, updatedFullValueEvent).then(function () {
                        //reflect the new value in the grid
                        $scope.resetEventValue($scope.currentEvent);

                        //update original value
                        $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);

                        $scope.currentElement.updated = true;
                        $scope.updateSuccess = true;
                    });
                }
            };

            $scope.updateEventDataValue = function(currentEvent, dataElement){
                $scope.updateSuccess = false;

                //get current element
                $scope.currentElement = {id: dataElement};

                //get new and old values
                var newValue = $scope.currentEvent[dataElement];
                var oldValue = $scope.currentEventOriginialValue[dataElement];

                //check for form validity
                if( $scope.isFormInvalid() ){
                    $scope.currentElement.updated = false;
                    //reset value back to original
                    $scope.currentEvent[dataElement] = oldValue;
                    $scope.resetEventValue($scope.currentEvent);
                    return;
                }

                if( $scope.prStDes[dataElement].compulsory && !newValue ) {
                    $scope.currentElement.updated = false;
                    //reset value back to original
                    $scope.currentEvent[dataElement] = oldValue;
                    $scope.resetEventValue($scope.currentEvent);
                    return;
                }

                if( newValue != oldValue ){

                    if($scope.prStDes[dataElement].dataElement.optionSetValue){
                        if($scope.prStDes[dataElement].dataElement.optionSet){
                            newValue = OptionSetService.getCode($scope.optionSets[$scope.prStDes[dataElement].dataElement.optionSet.id].options, newValue);
                        }
                    }
                    if($scope.prStDes[dataElement].dataElement.type === 'date'){
                        newValue = DateUtils.formatFromUserToApi(newValue);
                    }

                    if($scope.prStDes[dataElement].dataElement.type === 'trueOnly'){
                        if(!newValue){
                            newValue = "";
                        }
                    }

                    var updatedSingleValueEvent = {event: $scope.currentEvent.event, dataValues: [{value: newValue, dataElement: dataElement}]};
                    var updatedFullValueEvent = DHIS2EventService.reconstructEvent($scope.currentEvent, $scope.selectedProgramStage.programStageDataElements);

                    DHIS2EventFactory.updateForSingleValue(updatedSingleValueEvent, updatedFullValueEvent).then(function(data){

                        //reflect the new value in the grid
                        $scope.resetEventValue($scope.currentEvent);

                        //update original value
                        $scope.currentEventOriginialValue = angular.copy($scope.currentEvent);

                        $scope.currentElement.updated = true;
                        $scope.updateSuccess = true;
                    });
                }
            };

            $scope.resetEventValue = function(currentEvent){
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === currentEvent.event ){
                        $scope.dhis2Events[i] = currentEvent;
                        continueLoop = false;
                    }
                }
            };

            $scope.removeEvent = function(){

                var dhis2Event = ContextMenuSelectedItem.getSelectedItem();

                var modalOptions = {
                    closeButtonText: 'cancel',
                    actionButtonText: 'remove',
                    headerText: 'remove',
                    bodyText: 'are_you_sure_to_remove'
                };

                ModalService.showModal({}, modalOptions).then(function(result){

                    DHIS2EventFactory.delete(dhis2Event).then(function(data){

                        var continueLoop = true, index = -1;
                        for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                            if($scope.dhis2Events[i].event === dhis2Event.event ){
                                $scope.dhis2Events[i] = dhis2Event;
                                continueLoop = false;
                                index = i;
                            }
                        }
                        $scope.dhis2Events.splice(index,1);
                        $scope.currentEvent = {};
                    });
                });
            };

            $scope.showNotes = function(dhis2Event){

                var modalInstance = $modal.open({
                    templateUrl: 'views/notes.html',
                    controller: 'NotesController',
                    resolve: {
                        dhis2Event: function () {
                            return dhis2Event;
                        }
                    }
                });

                modalInstance.result.then(function (){
                });
            };

            $scope.getHelpContent = function(){
            };

            $scope.showMap = function(event){
                var modalInstance = $modal.open({
                    templateUrl: '../dhis-web-commons/angular-forms/map.html',
                    controller: 'MapController',
                    windowClass: 'modal-full-window',
                    resolve: {
                        location: function () {
                            return {lat: event.coordinate.latitude, lng: event.coordinate.longitude};
                        }
                    }
                });

                modalInstance.result.then(function (location) {
                    if(angular.isObject(location)){
                        event.coordinate.latitude = location.lat;
                        event.coordinate.longitude = location.lng;
                    }
                }, function () {
                });
            };

            $scope.formIsChanged = function(){
                var isChanged = false;
                var emptyForm = $scope.formIsEmpty();
                for(var i=0; i<$scope.selectedProgramStage.programStageDataElements.length && !isChanged; i++){
                    var deId = $scope.selectedProgramStage.programStageDataElements[i].dataElement.id;
                    if($scope.currentEventOriginialValue[deId] !== $scope.currentEvent[deId]){
                        if($scope.currentEvent[deId] || $scope.currentEventOriginialValue[deId] !== "" && !emptyForm){
                            isChanged = true;
                        }
                    }
                }
                if(!isChanged){
                    if($scope.currentEvent.eventDate !== $scope.currentEventOriginialValue.eventDate){
                        isChanged = true;
                    }
                }

                return isChanged;
            };

            $scope.isFormInvalid = function(){

                if($scope.outerForm.submitted){
                    return $scope.outerForm.$invalid;
                }

                if(!$scope.outerForm.$dirty){
                    return false;
                }

                var formIsInvalid = false;
                for(var k in $scope.outerForm.$error){
                    if(angular.isObject($scope.outerForm.$error[k])){

                        for(var i=0; i<$scope.outerForm.$error[k].length && !formIsInvalid; i++){
                            if($scope.outerForm.$error[k][i].$dirty && $scope.outerForm.$error[k][i].$invalid){
                                formIsInvalid = true;
                            }
                        }
                    }

                    if(formIsInvalid){
                        break;
                    }
                }

                return formIsInvalid;
            };

            $scope.formIsEmpty = function(){
                for(var dataElement in $scope.prStDes){
                    if($scope.currentEvent[dataElement]){
                        return false;
                    }
                }
                return true;
            };

            //watch for event editing
            $scope.$watchCollection('[editingEventInFull, eventRegistration]', function() {
                if($scope.editingEventInFull || $scope.eventRegistration){
                    //Disable ou selection while in editing mode
                    $( "#orgUnitTree" ).addClass( "disable-clicks" );
                }
                else{
                    //enable ou selection if not in editing mode
                    $( "#orgUnitTree" ).removeClass( "disable-clicks" );
                }
            });

            $scope.interacted = function(field) {
                var status = false;
                if(field){
                    status = $scope.outerForm.submitted || field.$dirty;
                }
                return status;
            };

            //Infinite Scroll
            $scope.infiniteScroll = {};
            $scope.infiniteScroll.optionsToAdd = 20;
            $scope.infiniteScroll.currentOptions = 20;

            $scope.resetInfScroll = function() {
                $scope.infiniteScroll.currentOptions = $scope.infiniteScroll.optionsToAdd;
            };

            $scope.addMoreOptions = function(){
                $scope.infiniteScroll.currentOptions += $scope.infiniteScroll.optionsToAdd;
            };

            //listen for rule effect changes
            $scope.$on('ruleeffectsupdated', function(event, args) {
                //Blank hidden rule effects, they will be rebuilt by this function.
                $scope.hiddenFields = [];

                if($rootScope.ruleeffects[args.event]) {
                    //Establish which event was affected:
                    var affectedEvent = $scope.currentEvent;
                    //In most cases the updated effects apply to the current event. In case the affected event is not the current event, fetch the correct event to affect:
                    if(args.event !== affectedEvent.event) {
                        angular.forEach($scope.currentStageEvents, function(searchedEvent) {
                            if(searchedEvent.event === args.event) {
                                affectedEvent = searchedEvent;
                            }
                        });
                    }

                    angular.forEach($rootScope.ruleeffects[args.event], function(effect) {
                        if( effect.dataElement && effect.ineffect ) {
                            //in the data entry controller we only care about the "hidefield" actions
                            if(effect.action === "HIDEFIELD") {
                                if(effect.dataElement) {
                                    if(effect.ineffect && affectedEvent[effect.dataElement.id]) {
                                        //If a field is going to be hidden, but contains a value, we need to take action;
                                        if(effect.content) {
                                            //TODO: Alerts is going to be replaced with a proper display mecanism.
                                            alert(effect.content);
                                        }
                                        else {
                                            //TODO: Alerts is going to be replaced with a proper display mecanism.
                                            alert($scope.prStDes[effect.dataElement.id].dataElement.formName + "Was blanked out and hidden by your last action");
                                        }

                                        //Blank out the value:
                                        affectedEvent[effect.dataElement.id] = "";
                                    }

                                    $scope.hiddenFields[effect.dataElement.id] = effect.ineffect;
                                }
                                else {
                                    $log.warn("ProgramRuleAction " + effect.id + " is of type HIDEFIELD, bot does not have a dataelement defined");
                                }
                            }
                        }
                    });
                }
            });

            $scope.executeRules = function() {
                $scope.currentEvent.event = !$scope.currentEvent.event ? 'SINGLE_EVENT' : $scope.currentEvent.event;
                $scope.eventsByStage = [];
                $scope.eventsByStage[$scope.selectedProgramStage.id] = [$scope.currentEvent];
                var evs = {all: [$scope.currentEvent], byStage: $scope.eventsByStage};

                var flag = {debug: true, verbose: false};

                //TrackerRulesExecutionService.executeRules($scope.selectedProgram.id,$scope.currentEvent,$scope.eventsByStage,$scope.prStDes,null,false);
                TrackerRulesExecutionService.executeRules($scope.allProgramRules, $scope.currentEvent, evs, $scope.prStDes, $scope.selectedTei, $scope.selectedEnrollment, flag);
            };


            $scope.formatNumberResult = function(val){
                return dhis2.validation.isNumber(val) ? val : '';
            };

            //check if field is hidden
            $scope.isHidden = function(id) {
                //In case the field contains a value, we cant hide it.
                //If we hid a field with a value, it would falsely seem the user was aware that the value was entered in the UI.
                if($scope.currentEvent[id]) {
                    return false;
                }
                else {
                    return $scope.hiddenFields[id];
                }
            };

            $scope.saveDatavalue = function(){
                $scope.executeRules();
            };
            /*$scope.getInputNotifcationClass = function(id, custom, event){
             var style = "";
             if($scope.currentElement.id && $scope.currentElement.id === id){
             style = $scope.currentElement.updated ? 'update-success' : 'update-error';
             }
             return style + ' form-control';
             };*/

            $scope.getInputNotifcationClass = function(id, custom){
                return '; ';
            };
        });