/* Pagination service */
/* global angular, dhis2, moment */

var d2Services = angular.module('d2Services', ['ngResource'])

/* Factory for loading translation strings */
.factory('i18nLoader', function ($q, $http, SessionStorageService) {

    var getTranslationStrings = function (locale) {
        var defaultUrl = 'i18n/i18n_app.properties';
        var url = '';
        if (locale === 'en' || !locale) {
            url = defaultUrl;
        }
        else {
            url = 'i18n/i18n_app_' + locale + '.properties';
        }

        var tx = {locale: locale};

        var promise = $http.get(url).then(function (response) {
            tx = {locale: locale, keys: dhis2.util.parseJavaProperties(response.data)};
            return tx;
        }, function () {

            setHeaderDelayMessage('No translation file is found for the selected locale. Using default translation (English).');

            var p = $http.get(defaultUrl).then(function (response) {
                tx = {locale: locale, keys: dhis2.util.parseJavaProperties(response.data)};
                return tx;
            });
            return p;
        });
        return promise;
    };

    var getLocale = function () {
        var locale = 'en';

        var promise = $http.get('../api/me/profile.json').then(function (response) {
            SessionStorageService.set('USER_PROFILE', response.data);
            if (response.data && response.data.settings && response.data.settings.keyUiLocale) {
                locale = response.data.settings.keyUiLocale;
            }
            return locale;
        }, function () {
            return locale;
        });

        return promise;
    };
    return function () {
        var deferred = $q.defer(), translations;
        var userProfile = SessionStorageService.get('USER_PROFILE');
        if (userProfile && userProfile.settings && userProfile.settings.keyUiLocale) {
            getTranslationStrings(userProfile.settings.keyUiLocale).then(function (response) {
                translations = response.keys;
                deferred.resolve(translations);
            });
            return deferred.promise;
        }
        else {
            getLocale().then(function (locale) {
                getTranslationStrings(locale).then(function (response) {
                    translations = response.keys;
                    deferred.resolve(translations);
                });
            });
            return deferred.promise;
        }
    };
})

.service('AuthorityService', function () {
    var getAuthorities = function (roles) {
        var authority = {};
        if (roles && roles.userCredentials && roles.userCredentials.userRoles) {
            angular.forEach(roles.userCredentials.userRoles, function (role) {
                angular.forEach(role.authorities, function (auth) {
                    authority[auth] = true;
                });
            });
        }
        return authority;
    };

    return {
        getUserAuthorities: function (roles) {
            var auth = getAuthorities(roles);
            var authority = {};
            authority.canDeleteEvent = auth['F_TRACKED_ENTITY_DATAVALUE_DELETE'] || auth['ALL'] ? true : false;
            authority.canAddOrUpdateEvent = auth['F_TRACKED_ENTITY_DATAVALUE_ADD'] || auth['ALL'] ? true : false;
            authority.canSearchTei = auth['F_TRACKED_ENTITY_INSTANCE_SEARCH'] || auth['ALL'] ? true : false;
            authority.canDeleteTei = auth['F_TRACKED_ENTITY_INSTANCE_DELETE'] || auth['ALL'] ? true : false;
            authority.canRegisterTei = auth['F_TRACKED_ENTITY_INSTANCE_ADD'] || auth['ALL'] ? true : false;
            authority.canEnrollTei = auth['F_PROGRAM_ENROLLMENT'] || auth['ALL'] ? true : false;
            authority.canUnEnrollTei = auth['F_PROGRAM_UNENROLLMENT'] || auth['ALL'] ? true : false;
            authority.canAdministerDashboard = auth['F_PROGRAM_DASHBOARD_CONFIG_ADMIN'] || auth['ALL'] ? true : false;
            return authority;
        }
    };
})

/* Factory for loading external data */
.factory('ExternalDataFactory', function ($http) {

    return {
        get: function (fileName) {
            var promise = $http.get(fileName).then(function (response) {
                return response.data;
            });
            return promise;
        }
    };
})

/* service for wrapping sessionStorage '*/
.service('SessionStorageService', function ($window) {
    return {
        get: function (key) {
            return JSON.parse($window.sessionStorage.getItem(key));
        },
        set: function (key, obj) {
            $window.sessionStorage.setItem(key, JSON.stringify(obj));
        },
        clearAll: function () {
            for (var key in $window.sessionStorage) {
                $window.sessionStorage.removeItem(key);
            }
        }
    };
})

/* service for getting calendar setting */
.service('CalendarService', function (storage, $rootScope) {

    return {
        getSetting: function () {

            var dhis2CalendarFormat = {keyDateFormat: 'yyyy-MM-dd', keyCalendar: 'gregorian', momentFormat: 'YYYY-MM-DD'};
            var storedFormat = storage.get('CALENDAR_SETTING');
            if (angular.isObject(storedFormat) && storedFormat.keyDateFormat && storedFormat.keyCalendar) {
                if (storedFormat.keyCalendar === 'iso8601') {
                    storedFormat.keyCalendar = 'gregorian';
                }

                if (storedFormat.keyDateFormat === 'dd-MM-yyyy') {
                    dhis2CalendarFormat.momentFormat = 'DD-MM-YYYY';
                }

                dhis2CalendarFormat.keyCalendar = storedFormat.keyCalendar;
                dhis2CalendarFormat.keyDateFormat = storedFormat.keyDateFormat;
            }
            $rootScope.dhis2CalendarFormat = dhis2CalendarFormat;
            return dhis2CalendarFormat;
        }
    };
})

/* service for dealing with dates */
.service('DateUtils', function ($filter, CalendarService) {

    return {
        getDate: function (dateValue) {
            if (!dateValue) {
                return;
            }
            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, calendarSetting.momentFormat)._d;
            return Date.parse(dateValue);
        },
        format: function (dateValue) {
            if (!dateValue) {
                return;
            }

            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, calendarSetting.momentFormat)._d;
            dateValue = $filter('date')(dateValue, calendarSetting.keyDateFormat);
            return dateValue;
        },
        formatToHrsMins: function (dateValue) {
            var calendarSetting = CalendarService.getSetting();
            var dateFormat = 'YYYY-MM-DD @ hh:mm A';
            if (calendarSetting.keyDateFormat === 'dd-MM-yyyy') {
                dateFormat = 'DD-MM-YYYY @ hh:mm A';
            }
            return moment(dateValue).format(dateFormat);
        },
        getToday: function () {
            var calendarSetting = CalendarService.getSetting();
            var tdy = $.calendars.instance(calendarSetting.keyCalendar).newDate();
            var today = moment(tdy._year + '-' + tdy._month + '-' + tdy._day, 'YYYY-MM-DD')._d;
            today = Date.parse(today);
            today = $filter('date')(today, calendarSetting.keyDateFormat);
            return today;
        },
        formatFromUserToApi: function (dateValue) {
            if (!dateValue) {
                return;
            }
            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, calendarSetting.momentFormat)._d;
            dateValue = Date.parse(dateValue);
            dateValue = $filter('date')(dateValue, 'yyyy-MM-dd');
            return dateValue;
        },
        formatFromApiToUser: function (dateValue) {
            if (!dateValue) {
                return;
            }
            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, 'YYYY-MM-DD')._d;
            return $filter('date')(dateValue, calendarSetting.keyDateFormat);
        }
    };
})

/* service for dealing with custom form */
.service('CustomFormService', function ($translate) {

    return {
        getForProgramStage: function (programStage, programStageDataElements) {

            var htmlCode = programStage.dataEntryForm ? programStage.dataEntryForm.htmlCode : null;

            if (htmlCode) {

                var inputRegex = /<input.*?\/>/g,
                        match,
                        inputFields = [],
                        hasEventDate = false;

                while (match = inputRegex.exec(htmlCode)) {
                    inputFields.push(match[0]);
                }

                for (var i = 0; i < inputFields.length; i++) {
                    var inputField = inputFields[i];
                    var inputElement = $.parseHTML(inputField);
                    var attributes = {};

                    $(inputElement[0].attributes).each(function () {
                        attributes[this.nodeName] = this.value;
                    });

                    var fieldId = '', newInputField;
                    if (attributes.hasOwnProperty('id')) {

                        if (attributes['id'] === 'executionDate') {
                            fieldId = 'eventDate';
                            hasEventDate = true;

                            //name needs to be unique so that it can be used for validation in angularjs
                            if (attributes.hasOwnProperty('name')) {
                                attributes['name'] = fieldId;
                            }

                            newInputField = '<input type="text" ' +
                                    this.getAttributesAsString(attributes) +
                                    ' ng-model="currentEvent.' + fieldId + '"' +
                                    ' input-field-id="' + fieldId + '"' +
                                    ' d2-date ' +
                                    ' d2-date-validator ' +
                                    ' max-date="' + 0 + '"' +
                                    ' placeholder="{{dhis2CalendarFormat.keyDateFormat}}" ' +
                                    ' ng-class="getInputNotifcationClass(prStDes.' + fieldId + '.dataElement.id,true)"' +
                                    ' blur-or-change="saveDatavalue(prStDes.' + fieldId + ')"' +
                                    ' ng-required="{{true}}">';
                        }
                        else {
                            fieldId = attributes['id'].substring(4, attributes['id'].length - 1).split("-")[1];

                            //name needs to be unique so that it can be used for validation in angularjs
                            if (attributes.hasOwnProperty('name')) {
                                attributes['name'] = fieldId;
                            }

                            var prStDe = programStageDataElements[fieldId];

                            var commonInputFieldProperty = this.getAttributesAsString(attributes) +
                                    ' ng-model="currentEvent.' + fieldId + '" ' +
                                    ' input-field-id="' + fieldId + '"' +
                                    ' ng-class="{{getInputNotifcationClass(prStDes.' + fieldId + '.dataElement.id, true)}}" ' +
                                    ' ng-disabled="isHidden(prStDes.' + fieldId + '.dataElement.id) || selectedEnrollment.status===\'CANCELLED\' || selectedEnrollment.status===\'COMPLETED\' || currentEvent[uid]==\'uid\' || currentEvent.editingNotAllowed"' +
                                    ' ng-required="{{prStDes.' + fieldId + '.compulsory}}" ';

                            if (prStDe && prStDe.dataElement && prStDe.dataElement.type) {
                                //check if dataelement has optionset								
                                if (prStDe.dataElement.optionSetValue) {
                                    var optionSetId = prStDe.dataElement.optionSet.id;                 
                                    newInputField = '<ui-select style="width:100%;" theme="select2" ' + commonInputFieldProperty + ' on-select="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')" >' +
                                            '<ui-select-match style="width:100%;" allow-clear="true" placeholder="' + $translate.instant('select_or_search') + '">{{$select.selected.name || $select.selected}}</ui-select-match>' +
                                            '<ui-select-choices ' +
                                            ' repeat="option.name as option in optionSets.' + optionSetId + '.options | filter: $select.search | limitTo:30">' +
                                            '<span ng-bind-html="option.name | highlight: $select.search"></span>' +
                                            '</ui-select-choices>' +
                                            '</ui-select>';
                                }
                                else {
                                    //check data element type and generate corresponding angular input field
                                    if (prStDe.dataElement.type === "int") {
                                        newInputField = '<input type="number" ' +
                                                ' d2-number-validator ' +
                                                ' number-type="' + prStDe.dataElement.numberType + '" ' +
                                                ' ng-blur="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')"' +
                                                commonInputFieldProperty + ' >';
                                    }
                                    else if (prStDe.dataElement.type === "bool") {
                                        newInputField = '<select ' +
                                                ' ng-change="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')" ' +
                                                commonInputFieldProperty + '>' +
                                                '<option value="">{{\'please_select\'| translate}}</option>' +
                                                '<option value="false">{{\'no\'| translate}}</option>' +
                                                '<option value="true">{{\'yes\'| translate}}</option>' +
                                                '</select> ';
                                    }
                                    else if (prStDe.dataElement.type === "date") {
                                        var maxDate = prStDe.allowFutureDate ? '' : 0;
                                        newInputField = '<input type="text" ' +
                                                ' placeholder="{{dhis2CalendarFormat.keyDateFormat}}" ' +
                                                ' d2-date ' +
                                                ' d2-date-validator ' +
                                                ' max-date="' + maxDate + '"' +
                                                ' blur-or-change="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')"' +
                                                commonInputFieldProperty + ' >';
                                    }
                                    else if (prStDe.dataElement.type === "trueOnly") {
                                        newInputField = '<input type="checkbox" ' +
                                                ' ng-change="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')"' +
                                                commonInputFieldProperty + ' >';
                                    }
                                    else if (prStDe.dataElement.type === "string") {
										if(prStDe.dataElement.textType === "longText"){
											newInputField = '<textarea row ="3" ' +                                                
                                                ' ng-blur="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')"' +
                                                commonInputFieldProperty + '></textarea>';
										}
										else{
											newInputField = '<input type="text" ' +
                                                ' ng-blur="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')"' +
                                                commonInputFieldProperty + ' >';
										}
                                    }
                                    else {
                                        newInputField = '<input type="text" ' +
                                                ' ng-blur="saveDatavalue(prStDes.' + fieldId + ', outerForm.' + fieldId + ')"' +
                                                commonInputFieldProperty + ' >';
                                    }
                                }
                            }
                        }
                        newInputField = newInputField + ' <span ng-messages="outerForm.' + fieldId + '.$error" class="required" ng-if="interacted(outerForm.' + fieldId + ')" ng-messages-include="../dhis-web-commons/angular-forms/error-messages.html"></span>';

                        htmlCode = htmlCode.replace(inputField, newInputField);
                    }
                }
                return {htmlCode: htmlCode, hasEventDate: hasEventDate};
            }
            return null;
        },
        getForTrackedEntity: function (trackedEntityForm, target) {
            if (!trackedEntityForm) {
                return null;
            }

            var htmlCode = trackedEntityForm.htmlCode ? trackedEntityForm.htmlCode : null;
            if (htmlCode) {

                var trackedEntityFormAttributes = [];
                angular.forEach(trackedEntityForm.attributes, function (att) {
                    trackedEntityFormAttributes[att.id] = att;
                });


                var inputRegex = /<input.*?\/>/g, match, inputFields = [];
                var hasProgramDate = false;
                while (match = inputRegex.exec(htmlCode)) {
                    inputFields.push(match[0]);
                }

                for (var i = 0; i < inputFields.length; i++) {
                    var inputField = inputFields[i];
                    var inputElement = $.parseHTML(inputField);
                    var attributes = {};

                    $(inputElement[0].attributes).each(function () {
                        attributes[this.nodeName] = this.value;
                    });

                    var attId = '', newInputField, programId;
                    if (attributes.hasOwnProperty('attributeid')) {
                        attId = attributes['attributeid'];

                        var fieldName = attId;
                        var attMaxDate = trackedEntityFormAttributes[attId].allowFutureDate ? '' : 0;

                        var att = trackedEntityFormAttributes[attId];

                        if (att) {

                            var commonInputFieldProperty = ' name="' + fieldName + '"' +
                                    ' element-id="' + i + '"' +
                                    this.getAttributesAsString(attributes) +
                                    ' d2-focus-next-on-enter' +
                                    ' ng-model="selectedTei.' + attId + '" ' +
                                    ' ng-disabled="editingDisabled"' +
                                    ' d2-validation ' +
                                    ' ng-required=" ' + (att.mandatory || att.unique) + '" ';

                            //check if attribute has optionset
                            if (att.optionSetValue) {
                                var optionSetId = att.optionSet.id;
                                newInputField = '<ui-select theme="select2" ' + commonInputFieldProperty + ' >' +
                                        '<ui-select-match allow-clear="true" placeholder="' + $translate.instant('select_or_search') + '">{{$select.selected.name || $select.selected}}</ui-select-match>' +
                                        '<ui-select-choices ' +
                                        'repeat="option.name as option in optionSets.' + optionSetId + '.options | filter: $select.search | limitTo:30">' +
                                        '<span ng-bind-html="option.name | highlight: $select.search"></span>' +
                                        '</ui-select-choices>' +
                                        '</ui-select>';
                            }
                            else {
                                //check attribute type and generate corresponding angular input field
                                if (att.valueType === "number") {
                                    newInputField = '<input type="text" ' +
                                            ' d2-number-validator ' +
                                            ' ng-blur="validationAndSkipLogic(selectedTei,\'' + attId + '\')" ' +
                                            commonInputFieldProperty + ' >';
                                }
                                else if (att.valueType === "bool") {
                                    newInputField = '<select ' +
                                            ' ng-change="validationAndSkipLogic(selectedTei,\'' + attId + '\')" ' +
                                            commonInputFieldProperty + ' > ' +
                                            ' <option value="">{{\'please_select\'| translate}}</option>' +
                                            ' <option value="false">{{\'no\'| translate}}</option>' +
                                            ' <option value="true">{{\'yes\'| transslate}}</option>' +
                                            '</select> ';
                                }
                                else if (att.valueType === "date") {
                                    newInputField = '<input  type="text" ' +
                                            ' placeholder="{{dhis2CalendarFormat.keyDateFormat}}" ' +
                                            ' max-date="' + attMaxDate + '"' + '\'' +
                                            ' d2-date' +
                                            ' blur-or-change="validationAndSkipLogic(selectedTei,\'' + attId + '\')" ' +
                                            commonInputFieldProperty + ' >';
                                }
                                else if (att.valueType === "trueOnly") {
                                    newInputField = '<input type="checkbox" ' +
                                            ' ng-change="validationAndSkipLogic(selectedTei,\'' + attId + '\')" ' +
                                            commonInputFieldProperty + ' >';
                                }
                                else if (att.valueType === "email") {
                                    newInputField = '<input type="email" ' +
                                            ' ng-blur="validationAndSkipLogic(selectedTei,\'' + attId + '\')" ' +
                                            commonInputFieldProperty + ' >';
                                }
                                else {
                                    newInputField = '<input type="text" ' +
                                            ' ng-blur="validationAndSkipLogic(selectedTei,\'' + attId + '\')" ' +
                                            commonInputFieldProperty + ' >';
                                }
                            }
                        }

                    }

                    if (attributes.hasOwnProperty('programid')) {
                        hasProgramDate = true;
                        programId = attributes['programid'];
                        if (programId === 'enrollmentDate') {
                            fieldName = 'dateOfEnrollment';
                            var enMaxDate = trackedEntityForm.selectEnrollmentDatesInFuture ? '' : 0;
                            newInputField = '<input type="text" ' +
                                    ' name="' + fieldName + '"' +
                                    ' element-id="' + i + '"' +
                                    this.getAttributesAsString(attributes) +
                                    ' d2-focus-next-on-enter' +
                                    ' placeholder="{{dhis2CalendarFormat.keyDateFormat}}" ' +
                                    ' ng-model="selectedEnrollment.dateOfEnrollment" ' +
                                    ' ng-disabled="\'' + target + '\' === \'PROFILE\'"' +
                                    ' d2-date' +
                                    ' max-date="' + enMaxDate + '"' +
                                    ' ng-required="true"> ';
                        }
                        if (programId === 'dateOfIncident' && trackedEntityForm.displayIncidentDate) {
                            fieldName = 'dateOfIncident';
                            var inMaxDate = trackedEntityForm.selectIncidentDatesInFuture ? '' : 0;
                            newInputField = '<input type="text" ' +
                                    ' name="' + fieldName + '"' +
                                    ' element-id="' + i + '"' +
                                    this.getAttributesAsString(attributes) +
                                    ' d2-focus-next-on-enter' +
                                    ' placeholder="{{dhis2CalendarFormat.keyDateFormat}}" ' +
                                    ' ng-model="selectedEnrollment.dateOfIncident" ' +
                                    ' ng-disabled="\'' + target + '\' === \'PROFILE\'"' +
                                    ' d2-date ' +
                                    ' max-date="' + inMaxDate + '"> ';
                        }
                    }

                    newInputField = newInputField + ' <span ng-messages="outerForm.' + fieldName + '.$error" class="required" ng-if="interacted(outerForm.' + fieldName + ')" ng-messages-include="../dhis-web-commons/angular-forms/error-messages.html"></span>';

                    htmlCode = htmlCode.replace(inputField, newInputField);
                }
                return {htmlCode: htmlCode, hasProgramDate: hasProgramDate};
            }
            return null;
        },
        getAttributesAsString: function (attributes) {
            if (attributes) {
                var attributesAsString = '';
                for (var prop in attributes) {
                    if (prop !== 'value') {
                        attributesAsString += prop + '="' + attributes[prop] + '" ';
                    }
                }
                return attributesAsString;
            }
            return null;
        }
    };
})

/* Context menu for grid*/
.service('ContextMenuSelectedItem', function () {
    this.selectedItem = '';

    this.setSelectedItem = function (selectedItem) {
        this.selectedItem = selectedItem;
    };

    this.getSelectedItem = function () {
        return this.selectedItem;
    };
})

/* Modal service for user interaction */
.service('ModalService', ['$modal', function ($modal) {

        var modalDefaults = {
            backdrop: true,
            keyboard: true,
            modalFade: true,
            templateUrl: 'views/modal.html'
        };

        var modalOptions = {
            closeButtonText: 'Close',
            actionButtonText: 'OK',
            headerText: 'Proceed?',
            bodyText: 'Perform this action?'
        };

        this.showModal = function (customModalDefaults, customModalOptions) {
            if (!customModalDefaults)
                customModalDefaults = {};
            customModalDefaults.backdrop = 'static';
            return this.show(customModalDefaults, customModalOptions);
        };

        this.show = function (customModalDefaults, customModalOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempModalDefaults = {};
            var tempModalOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempModalOptions, modalOptions, customModalOptions);

            if (!tempModalDefaults.controller) {
                tempModalDefaults.controller = function ($scope, $modalInstance) {
                    $scope.modalOptions = tempModalOptions;
                    $scope.modalOptions.ok = function (result) {
                        $modalInstance.close(result);
                    };
                    $scope.modalOptions.close = function (result) {
                        $modalInstance.dismiss('cancel');
                    };
                };
            }

            return $modal.open(tempModalDefaults).result;
        };

    }])

/* Dialog service for user interaction */
.service('DialogService', ['$modal', function ($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,
            templateUrl: 'views/dialog.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?'
        };

        this.showDialog = function (customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function (customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function ($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function (result) {
                        $modalInstance.close(result);
                    };
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }])

.service('Paginator', function () {
    this.page = 1;
    this.pageSize = 50;
    this.itemCount = 0;
    this.pageCount = 0;
    this.toolBarDisplay = 5;

    this.setPage = function (page) {
        if (page > this.getPageCount()) {
            return;
        }

        this.page = page;
    };

    this.getPage = function () {
        return this.page;
    };

    this.setPageSize = function (pageSize) {
        this.pageSize = pageSize;
    };

    this.getPageSize = function () {
        return this.pageSize;
    };

    this.setItemCount = function (itemCount) {
        this.itemCount = itemCount;
    };

    this.getItemCount = function () {
        return this.itemCount;
    };

    this.setPageCount = function (pageCount) {
        this.pageCount = pageCount;
    };

    this.getPageCount = function () {
        return this.pageCount;
    };

    this.setToolBarDisplay = function (toolBarDisplay) {
        this.toolBarDisplay = toolBarDisplay;
    };

    this.getToolBarDisplay = function () {
        return this.toolBarDisplay;
    };

    this.lowerLimit = function () {
        var pageCountLimitPerPageDiff = this.getPageCount() - this.getToolBarDisplay();

        if (pageCountLimitPerPageDiff < 0) {
            return 0;
        }

        if (this.getPage() > pageCountLimitPerPageDiff + 1) {
            return pageCountLimitPerPageDiff;
        }

        var low = this.getPage() - (Math.ceil(this.getToolBarDisplay() / 2) - 1);

        return Math.max(low, 0);
    };
})

.service('GridColumnService', function () {
    return {
        columnExists: function (cols, id) {
            var colExists = false;
            if (!angular.isObject(cols) || !id || angular.isObject(cols) && !cols.length) {
                return colExists;
            }

            for (var i = 0; i < cols.length && !colExists; i++) {
                if (cols[i].id === id) {
                    colExists = true;
                }
            }
            return colExists;
        }
    };
})

/* service for building variables based on the data in users fields */
.service('VariableService', function(DateUtils,$filter,$log){
    
    var pushVariable = function(variables, variablename, variableValue, variableType, variablefound, variablePrefix) {
        //First clean away single or double quotation marks at the start and end of the variable name.
        variableValue = $filter('trimquotes')(variableValue);

        //Append single quotation marks in case the variable is of text or date type:
        if(variableType === 'string' || variableType === 'date') {
            variableValue = "'" + variableValue + "'";
        }
        else if(variableType === 'bool' || variableType === 'trueOnly') {
            if(eval(variableValue)) {
                variableValue = true;
            }
            else {
                variableValue = false;
            }
        }
        else if(variableType === "int" || variableType === "number") {
            variableValue = Number(variableValue);
        }
        else{
            $log.warn("unknown datatype:" + variableType);
        }

        variables[variablename] = {
                        variableValue:variableValue,
                        variableType:variableType,
                        hasValue:variablefound,
                        variablePrefix:variablePrefix
                    };
        return variables;            
    };
    
    return {        
        processVariables: function(variables, variablename, variableValue, variableType, variablefound, variablePrefix) {            
            return pushVariable(variables, variablename, variableValue, variableType, variablefound, variablePrefix);
        },
        getVariables: function(allProgramRules, executingEvent, evs, allDes, selectedEntity, selectedEnrollment) {
            var variables = {};
            
            var programVariables = allProgramRules.programVariables;            
                            
            programVariables = programVariables.concat(allProgramRules.programIndicators.variables);

            angular.forEach(programVariables, function(programVariable) {
                var dataElementId = programVariable.dataElement;
                if(programVariable.dataElement && programVariable.dataElement.id) {
                    dataElementId = programVariable.dataElement.id;
                }

                var programStageId = programVariable.programStage;
                if(programVariable.programStage && programVariable.programStage.id) {
                    programStageId = programVariable.programStage.id;
                }

                var valueFound = false;
                if(programVariable.programRuleVariableSourceType === "DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE"){
                    if(programStageId) {
                        angular.forEach(evs.byStage[programStageId], function(event) {
                            if(angular.isDefined(event[dataElementId])
                                    && event[dataElementId] !== null ){
                                valueFound = true;
                                variables = pushVariable(variables, programVariable.name, event[dataElementId], allDes[dataElementId].dataElement.type, valueFound, '#');
                            }
                        });
                    } else {
                        $log.warn("Variable id:'" + programVariable.id + "' name:'" + programVariable.name 
                                + "' does not have a programstage defined,"
                                + " despite that the variable has sourcetype DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE" );
                    }

                }
                else if(programVariable.programRuleVariableSourceType === "DATAELEMENT_NEWEST_EVENT_PROGRAM"){
                    angular.forEach(evs.all, function(event) {
                        if(angular.isDefined(event[dataElementId])
                                && event[dataElementId] !== null ){
                            valueFound = true;
                            variables = pushVariable(variables, programVariable.name, event[dataElementId], allDes[dataElementId].dataElement.type, valueFound, '#' );
                         }
                    });
                }
                else if(programVariable.programRuleVariableSourceType === "DATAELEMENT_CURRENT_EVENT"){
                    if(angular.isDefined(executingEvent[dataElementId])
                            && executingEvent[dataElementId] !== null ){
                        valueFound = true;
                        variables = pushVariable(variables, programVariable.name, executingEvent[dataElementId], allDes[dataElementId].dataElement.type, valueFound, '#' );
                    }      
                }
                else if(programVariable.programRuleVariableSourceType === "DATAELEMENT_PREVIOUS_EVENT"){
                    //Only continue checking for a value if there is more than one event.
                    if(evs.all && evs.all.length > 1) {
                        var previousvalue = null;
                        var currentEventPassed = false;
                        for(var i = 0; i < evs.all.length; i++) {
                            //Store the values as we iterate through the stages
                            //If the event[i] is not the current event, it is older(previous). Store the previous value if it exists
                            if(!currentEventPassed && evs.all[i] !== executingEvent && 
                                    angular.isDefined(evs.all[i][dataElementId])) {
                                previousvalue = evs.all[i][dataElementId];
                                valueFound = true;
                            }
                            else if(evs.all[i] === executingEvent) {
                                //We have iterated to the newest event - store the last collected variable value - if any is found:
                                if(valueFound) {
                                    variables = pushVariable(variables, programVariable.name, previousvalue, allDes[dataElementId].dataElement.type, valueFound, '#' );
                                }
                                //Set currentEventPassed, ending the iteration:
                                currentEventPassed = true;
                            }
                        }
                    }
                }
                else if(programVariable.programRuleVariableSourceType === "TEI_ATTRIBUTE"){
                    angular.forEach(selectedEntity.attributes , function(attribute) {
                        if(!valueFound) {
                            if(attribute.attribute === programVariable.trackedEntityAttribute.id) {
                                valueFound = true;
                                variables = pushVariable(variables, programVariable.name, attribute.value, attribute.type, valueFound, 'A' );
                            }
                        }
                    });
                }
                else if(programVariable.programRuleVariableSourceType === "CALCULATED_VALUE"){
                    //We won't assign the calculated variables at this step. The rules execution will calculate and assign the variable.
                }
                else if(programVariable.programRuleVariableSourceType === "NUMBEROFEVENTS_PROGRAMSTAGE"){
                    var numberOfEvents = 0;
                    if( programStageId && evs.byStage[programStageId] ) {
                        numberOfEvents = evs.byStage[programStageId].length;
                    }
                    valueFound = true;
                    variables = pushVariable(variables, programVariable.name, numberOfEvents, 'int', valueFound, '#' );
                }
                else {
                    //Missing handing of ruletype
                    $log.warn("Unknown programRuleVariableSourceType:" + programVariable.programRuleVariableSourceType);
                }


                if(!valueFound){
                    //If there is still no value found, assign default value:
                    if(dataElementId) {
                        var dataElement = allDes[dataElementId];
                        if( dataElement ) {
                            variables = pushVariable(variables, programVariable.name, "", dataElement.dataElement.type, false, '#' );
                        } 
                        else {
                            $log.warn("Variable #{" + programVariable.name + "} is linked to a dataelement that is not part of the program");
                            variables = pushVariable(variables, programVariable.name, "", "string",false, '#' );
                        }
                    }
                    else if (programVariable.trackedEntityAttribute) {
                        //The variable is an attribute, set correct prefix and a blank value
                        variables = pushVariable(variables, programVariable.name, "", "string",false, 'A' );
                    }
                    else {
                        //Fallback for calculated(assigned) values:
                        variables = pushVariable(variables, programVariable.name, "", "string",false, '#' );
                    }
                }
            });

            //add context variables:
            //last parameter "valuefound" is always true for event date
            variables = pushVariable(variables, 'incident_date', executingEvent.eventDate, 'date', true, 'V' );
            variables = pushVariable(variables, 'current_date', DateUtils.getToday(), 'date', true, 'V' );
            if(selectedEnrollment){
                variables = pushVariable(variables, 'enrollment_date', selectedEnrollment.dateOfEnrollment, 'date', true, 'V' );
            }

            //variables = pushVariable(variables, 'value_count', executingEvent.eventDate, 'date', true, 'V' );
            //variables = pushVariable(variables, 'zero_pos_value_count', executingEvent.eventDate, 'date', true, 'V' );

            //Push all constant values:
            angular.forEach(allProgramRules.constants, function(constant){
                variables = pushVariable(variables, constant.id, constant.value, 'int', true, 'C' );
            });

            return variables;
        }
    };
})

/* service for executing tracker rules and broadcasting results */
.service('TrackerRulesExecutionService', function(VariableService, $rootScope, $log, $filter, orderByFilter){
    
    var replaceVariables = function(expression, variablesHash){
        //replaces the variables in an expression with actual variable values.                

        //Check if the expression contains program rule variables at all(any dollar signs):
        if(expression.indexOf('#{') !== -1) {
            //Find every variable name in the expression;
            var variablespresent = expression.match(/#{\w+.?\w*}/g);
            //Replace each matched variable:
            angular.forEach(variablespresent, function(variablepresent) {
                //First strip away any prefix and postfix signs from the variable name:
                variablepresent = variablepresent.replace("#{","").replace("}","");

                if(angular.isDefined(variablesHash[variablepresent]) &&
                        variablesHash[variablepresent].variablePrefix === '#') {
                    //Replace all occurrences of the variable name(hence using regex replacement):
                    expression = expression.replace(new RegExp("#{" + variablepresent + "}", 'g'),
                        variablesHash[variablepresent].variableValue);
                }
                else {
                    $log.warn("Expression " + expression + " conains variable " + variablepresent 
                            + " - but this variable is not defined." );
                }  
            });
        }

        //Check if the expression contains environment  variables
        if(expression.indexOf('V{') !== -1) {
            //Find every variable name in the expression;
            var variablespresent = expression.match(/V{\w+.?\w*}/g);
            //Replace each matched variable:
            angular.forEach(variablespresent, function(variablepresent) {
                //First strip away any prefix and postfix signs from the variable name:
                variablepresent = variablepresent.replace("V{","").replace("}","");

                if(angular.isDefined(variablesHash[variablepresent]) &&
                        variablesHash[variablepresent].variablePrefix === 'V') {
                    //Replace all occurrences of the variable name(hence using regex replacement):
                    expression = expression.replace(new RegExp("V{" + variablepresent + "}", 'g'),
                        variablesHash[variablepresent].variableValue);
                }
                else {
                    $log.warn("Expression " + expression + " conains context variable " + variablepresent 
                            + " - but this variable is not defined." );
                } 
            });
        }
        
        //Check if the expression contains attribute variables:
        if(expression.indexOf('A{') !== -1) {
            //Find every attribute in the expression;
            var variablespresent = expression.match(/A{\w+.?\w*}/g);
            //Replace each matched variable:
            angular.forEach(variablespresent, function(variablepresent) {
                //First strip away any prefix and postfix signs from the variable name:
                variablepresent = variablepresent.replace("A{","").replace("}","");

                if(angular.isDefined(variablesHash[variablepresent]) &&
                        variablesHash[variablepresent].variablePrefix === 'A') {
                    //Replace all occurrences of the variable name(hence using regex replacement):
                    expression = expression.replace(new RegExp("A{" + variablepresent + "}", 'g'),
                        variablesHash[variablepresent].variableValue);
                }
                else {
                    $log.warn("Expression " + expression + " conains attribute " + variablepresent 
                            + " - but this attribute is not defined." );
                } 
            });
        }

        //Check if the expression contains constants
        if(expression.indexOf('C{') !== -1) {
            //Find every constant in the expression;
            var variablespresent = expression.match(/C{\w+.?\w*}/g);
            //Replace each matched variable:
            angular.forEach(variablespresent, function(variablepresent) {
                //First strip away any prefix and postfix signs from the variable name:
                variablepresent = variablepresent.replace("C{","").replace("}","");

                if(angular.isDefined(variablesHash[variablepresent]) &&
                        variablesHash[variablepresent].variablePrefix === 'C') {
                    //Replace all occurrences of the variable name(hence using regex replacement):
                    expression = expression.replace(new RegExp("C{" + variablepresent + "}", 'g'),
                        variablesHash[variablepresent].variableValue);
                }
                else {
                    $log.warn("Expression " + expression + " conains constant " + variablepresent 
                            + " - but this constant is not defined." );
                } 
            });
        }
        
        return expression;            
    };
    
    var runDhisFunctions = function(expression, variablesHash, flag){
        //Called from "runExpression". Only proceed with this logic in case there seems to be dhis function calls: "dhis." is present.
        if(angular.isDefined(expression) && expression.indexOf("dhis.") !== -1){   
            var dhisFunctions = [{name:"dhis.daysbetween",parameters:2},
                                {name:"dhis.yearsbetween",parameters:2},
                                {name:"dhis.floor",parameters:1},
                                {name:"dhis.modulus",parameters:2},
                                {name:"dhis.concatenate"}];

            angular.forEach(dhisFunctions, function(dhisFunction){
                //Replace each * with a regex that matches each parameter, allowing commas only inside single quotation marks.
                var regularExFunctionCall = new RegExp(dhisFunction.name.replace(".","\\.") + "\\([^\\)]*\\)",'g');
                var callsToThisFunction = expression.match(regularExFunctionCall);
                angular.forEach(callsToThisFunction, function(callToThisFunction){
                    //Remove the function name and paranthesis:
                    var justparameters = callToThisFunction.replace(/(^[^\(]+\()|\)$/g,"");
                    //Then split into single parameters:
                    var parameters = justparameters.match(/(('[^']+')|([^,]+))/g);

                    //Show error if no parameters is given and the function requires parameters,
                    //or if the number of parameters is wrong.
                    if(angular.isDefined(dhisFunction.parameters)){
                        //But we are only checking parameters where the dhisFunction actually has a defined set of parameters(concatenate, for example, does not have a fixed number);
                        if((!angular.isDefined(parameters) && dhisFunction.parameters > 0)
                                || parameters.length !== dhisFunction.parameters){
                            $log.warn(dhisFunction.name + " was called with the incorrect number of parameters");
                        }
                    }

                    //In case the function call is nested, the parameter itself contains an expression, run the expression.
                    if(angular.isDefined(parameters)) {
                        for (var i = 0; i < parameters.length; i++) {
                            parameters[i] = runExpression(parameters[i],dhisFunction.name,"parameter:" + i, flag, variablesHash);
                        }
                    }

                    //Special block for dhis.weeksBetween(*,*) - add such a block for all other dhis functions.
                    if(dhisFunction.name === "dhis.daysbetween")
                    {
                        var firstdate = $filter('trimquotes')(parameters[0]);
                        var seconddate = $filter('trimquotes')(parameters[1]);
                        firstdate = moment(firstdate);
                        seconddate = moment(seconddate);
                        //Replace the end evaluation of the dhis function:
                        expression = expression.replace(callToThisFunction, seconddate.diff(firstdate,'days'));
                    }
                    else if(dhisFunction.name === "dhis.yearsbetween")
                    {
                        var firstdate = $filter('trimquotes')(parameters[0]);
                        var seconddate = $filter('trimquotes')(parameters[1]);
                        firstdate = moment(firstdate);
                        seconddate = moment(seconddate);
                        //Replace the end evaluation of the dhis function:
                        expression = expression.replace(callToThisFunction, seconddate.diff(firstdate,'years'));
                    }
                    else if(dhisFunction.name === "dhis.floor")
                    {
                        var floored = Math.floor(parameters[0]);
                        //Replace the end evaluation of the dhis function:
                        expression = expression.replace(callToThisFunction, floored);
                    }
                    else if(dhisFunction.name === "dhis.modulus")
                    {
                        var dividend = Number(parameters[0]);
                        var divisor = Number(parameters[1]);
                        var rest = dividend % divisor;
                        //Replace the end evaluation of the dhis function:
                        expression = expression.replace(callToThisFunction, rest);
                    }
                    else if(dhisFunction.name === "dhis.concatenate")
                    {
                        var returnString = "'";
                        for (var i = 0; i < parameters.length; i++) {
                            returnString += parameters[i];
                        }
                        returnString += "'";
                        expression = expression.replace(callToThisFunction, returnString);
                    }
                });
            });
        }

        return expression;
    };
    
    var runExpression = function(expression, beforereplacement, identifier, flag, variablesHash ){
        //determine if expression is true, and actions should be effectuated
        //If DEBUG mode, use try catch and report errors. If not, omit the heavy try-catch loop.:
        var answer = false;
        if(flag.debug) {
            try{

                var dhisfunctionsevaluated = runDhisFunctions(expression, variablesHash, flag);
                answer = eval(dhisfunctionsevaluated);

                if(flag.verbose)
                {
                    $log.info("Expression with id " + identifier + " was successfully run. Original condition was: " + beforereplacement + " - Evaluation ended up as:" + expression + " - Result of evaluation was:" + answer);
                }
            }
            catch(e)
            {
                $log.warn("Expression with id " + identifier + " could not be run. Original condition was: " + beforereplacement + " - Evaluation ended up as:" + expression + " - error message:" + e);
            }
        }
        else {
            //Just run the expression. This is much faster than the debug route: http://jsperf.com/try-catch-block-loop-performance-comparison
            var dhisfunctionsevaluated = runDhisFunctions(expression, variablesHash, flag);
            answer = eval(dhisfunctionsevaluated);
        }
        if(dhis2.validation.isNumber(answer)){
        	answer = Math.round(answer*100)/100;
        }
        return answer;
    }; 
    
    return {
        executeRules: function(allProgramRules, executingEvent, evs, allDataElements, selectedEntity, selectedEnrollment, flag ) {
            if(allProgramRules) {
                var variablesHash = {};

                //Concatenate rules produced by indicator definitions into the other rules:
                var rules = $filter('filter')(allProgramRules.programRules, {programStageId: null});

                if(executingEvent.programStage){
                    if(!rules) {
                        rules = [];
                    }
                    rules = rules.concat($filter('filter')(allProgramRules.programRules, {programStageId: executingEvent.programStage}));
                }
                if(!rules) {
                        rules = [];
                }
                rules = rules.concat(allProgramRules.programIndicators.rules);

                //Run rules in priority - lowest number first(priority null is last)
                rules = orderByFilter(rules, 'priority');

                variablesHash = VariableService.getVariables(allProgramRules, executingEvent, evs, allDataElements, selectedEntity, selectedEnrollment);

                if(angular.isObject(rules) && angular.isArray(rules)){
                    //The program has rules, and we want to run them.
                    //Prepare repository unless it is already prepared:
                    if(angular.isUndefined( $rootScope.ruleeffects ) ) {
                        $rootScope.ruleeffects = {};
                    }

                    if(angular.isUndefined( $rootScope.ruleeffects[executingEvent.event] )){
                        $rootScope.ruleeffects[executingEvent.event] = {};
                    }

                    var updatedEffectsExits = false;

                    angular.forEach(rules, function(rule) {
                        var ruleEffective = false;

                        var expression = rule.condition;
                        //Go through and populate variables with actual values, but only if there actually is any replacements to be made(one or more "$" is present)
                        if(expression) {
                            if(expression.indexOf('{') !== -1) {
                                expression = replaceVariables(expression, variablesHash);
                            }
                            //run expression:
                            ruleEffective = runExpression(expression, rule.condition, "rule:" + rule.id, flag, variablesHash);
                        } else {
                            $log.warn("Rule id:'" + rule.id + "'' and name:'" + rule.name + "' had no condition specified. Please check rule configuration.");
                        }

                        angular.forEach(rule.programRuleActions, function(action){
                            //In case the effect-hash is not populated, add entries
                            if(angular.isUndefined( $rootScope.ruleeffects[executingEvent.event][action.id] )){
                                $rootScope.ruleeffects[executingEvent.event][action.id] =  {
                                    id:action.id,
                                    location:action.location, 
                                    action:action.programRuleActionType,
                                    dataElement:action.dataElement,
                                    content:action.content,
                                    data:action.data,
                                    ineffect:undefined
                                };
                            }

                            //In case the rule is effective and contains specific data, 
                            //the effect be refreshed from the variables list.
                            //If the rule is not effective we can skip this step
                            if(ruleEffective && action.data)
                            {
                                //Preserve old data for comparison:
                                var oldData = $rootScope.ruleeffects[executingEvent.event][action.id].data;
                                
                                //The key data might be containing a dollar sign denoting that the key data is a variable.
                                //To make a lookup in variables hash, we must make a lookup without the dollar sign in the variable name
                                //The first strategy is to make a direct lookup. In case the "data" expression is more complex, we have to do more replacement and evaluation.

                                var nameWithoutBrackets = action.data.replace('#{','').replace('}','');
                                if(angular.isDefined(variablesHash[nameWithoutBrackets]))
                                {
                                    //The variable exists, and is replaced with its corresponding value
                                    $rootScope.ruleeffects[executingEvent.event][action.id].data =
                                        variablesHash[nameWithoutBrackets].variableValue;
                                }
                                else if(action.data.indexOf('{') !== -1)
                                {
                                    //Since the value couldnt be looked up directly, and contains a dollar sign, the expression was more complex
                                    //Now we will have to make a thorough replacement and separate evaluation to find the correct value:
                                    $rootScope.ruleeffects[executingEvent.event][action.id].data = replaceVariables(action.data, variablesHash);
                                    //In a scenario where the data contains a complex expression, evaluate the expression to compile(calculate) the result:
                                    $rootScope.ruleeffects[executingEvent.event][action.id].data = runExpression($rootScope.ruleeffects[executingEvent.event][action.id].data, action.data, "action:" + action.id, flag, variablesHash);
                                }
                                
                                if(oldData !== $rootScope.ruleeffects[executingEvent.event][action.id].data) {
                                    updatedEffectsExits = true;
                                }
                            }

                            //Update the rule effectiveness if it changed in this evaluation;
                            if($rootScope.ruleeffects[executingEvent.event][action.id].ineffect !== ruleEffective)
                            {
                                //There is a change in the rule outcome, we need to update the effect object.
                                updatedEffectsExits = true;
                                $rootScope.ruleeffects[executingEvent.event][action.id].ineffect = ruleEffective;
                            }

                            //In case the rule is of type "assign variable" and the rule is effective,
                            //the variable data result needs to be applied to the correct variable:
                            if($rootScope.ruleeffects[executingEvent.event][action.id].action === "ASSIGNVARIABLE" && $rootScope.ruleeffects[executingEvent.event][action.id].ineffect){
                                //from earlier evaluation, the data portion of the ruleeffect now contains the value of the variable to be assign.
                                //the content portion of the ruleeffect defines the name for the variable, when dollar is removed:
                                var variabletoassign = $rootScope.ruleeffects[executingEvent.event][action.id].content.replace("#{","").replace("}","");

                                if(!angular.isDefined(variablesHash[variabletoassign])){
                                    $log.warn("Variable " + variabletoassign + " was not defined.");
                                }

                                //Even if the variable is not defined: we assign it:
                                if(variablesHash[variabletoassign] && 
                                        variablesHash[variabletoassign].variableValue !== $rootScope.ruleeffects[executingEvent.event][action.id].data){
                                    //If the variable was actually updated, we assume that there is an updated ruleeffect somewhere:
                                    updatedEffectsExits = true;
                                    //Then we assign the new value:
                                    variablesHash[variabletoassign].variableValue = $rootScope.ruleeffects[executingEvent.event][action.id].data;
                                }
                            }
                        });
                    });

                    //Broadcast rules finished if there was any actual changes to the event.
                    if(updatedEffectsExits){
                        $rootScope.$broadcast("ruleeffectsupdated", { event: executingEvent.event });
                    }
                }

                return true;
            }
        }
    };
});
