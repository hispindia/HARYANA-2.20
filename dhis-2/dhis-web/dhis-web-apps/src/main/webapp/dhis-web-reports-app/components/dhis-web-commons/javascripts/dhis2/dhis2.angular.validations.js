d2Directives.directive('d2NumberValidator', function() {
    
    return {
        require: 'ngModel',
        restrict: 'A',
        link: function (scope, element, attrs, ngModel) {
            
            function setValidity(numberType, isRequired){
                if(numberType === 'number'){
                    ngModel.$validators.number = function(value) { 
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isNumber(value);
                    };
                }
                else if(numberType === 'posInt'){
                    ngModel.$validators.posInt = function(value) { 
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isPositiveInt(value);
                    };
                }
                else if(numberType === 'negInt'){
                    ngModel.$validators.negInt = function(value) {
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isNegativeInt(value);
                    };
                }
                else if(numberType === 'zeroPositiveInt'){
                    ngModel.$validators.zeroPositiveInt = function(value) { 
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isZeroOrPositiveInt(value);
                    };
                }
                else if(numberType === 'int'){
                    ngModel.$validators.int = function(value) {
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isInt(value);
                    };
                }
            }

            var numberType = attrs.numberType;
            var isRequired = attrs.ngRequired === 'true';            
            setValidity(numberType, isRequired);
        }
    };
})

.directive("d2DateValidator", function(DateUtils, CalendarService, $parse) {
    return {
        restrict: "A",
         
        require: "ngModel",
         
        link: function(scope, element, attrs, ngModel) {
            ngModel.$validators.dateValidator = function(value) {
                var convertedDate = DateUtils.format(angular.copy(value));
                var isValid = value === convertedDate;
                return isValid;
            };
            
            ngModel.$validators.futureDateValidator = function(value) {
                var maxDate = $parse(attrs.maxDate)(scope);
                var convertedDate = DateUtils.format(angular.copy(value));
                var isValid = value === convertedDate;
                var calendarSetting = CalendarService.getSetting();
                if(isValid){
                    isValid = maxDate === 0 ? !moment(convertedDate, calendarSetting.momentFormat).isAfter(DateUtils.getToday()) : isValid;
                }
                return isValid;
            };
        }
    };
})

.directive("d2CoordinateValidator", function() {
    return {
        restrict: "A",
         
        require: "ngModel",
         
        link: function(scope, element, attrs, ngModel) {
            ngModel.$validators.latitudeValidator = function(value) { 
                var isRequired = attrs.ngRequired === 'true';
                
                if(!value){
                    return !isRequired;
                }
                var isNumber = dhis2.validation.isNumber(value);
                if(!isNumber){
                    return isNumber;
                }
                return value >= -90 && value <= 90;
            };
            
            ngModel.$validators.longitudeValidator = function(value) { 
                var isRequired = attrs.ngRequired === 'true';
                
                if(!value){
                    return !isRequired;
                }
                var isNumber = dhis2.validation.isNumber(value);
                if(!isNumber){
                    return isNumber;
                }
                return value >= -180 && value <= 180;
            };
        }
    };
});