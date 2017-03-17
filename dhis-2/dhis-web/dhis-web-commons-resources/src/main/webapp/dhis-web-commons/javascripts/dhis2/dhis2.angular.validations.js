d2Directives.directive('d2NumberValidator', function() {
    
    return {
        require: 'ngModel',
        restrict: 'A',
        link: function (scope, element, attrs, ngModel) {
            
            function setValidity(numberType, isRequired){
                if(numberType === 'number'){
                    ngModel.$validators.number = function(value) {
                    	value = value === 0 ? value.toString(): value; 
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isNumber(value);
                    };
                }
                else if(numberType === 'posInt'){
                    ngModel.$validators.posInt = function(value) {
                    	value = value === 0 ? value.toString(): value; 
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isPositiveInt(value);
                    };
                }
                else if(numberType === 'negInt'){
                    ngModel.$validators.negInt = function(value) {
                    	value = value === 0 ? value.toString(): value;
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isNegativeInt(value);
                    };
                }
                else if(numberType === 'zeroPositiveInt'){
                    ngModel.$validators.zeroPositiveInt = function(value) {
                    	value = value === 0 ? value.toString(): value; 
                        return value === 'null' || !value ? !isRequired : dhis2.validation.isZeroOrPositiveInt(value);
                    };
                }
                else if(numberType === 'int'){
                    ngModel.$validators.int = function(value) {
                    	value = value === 0 ? value.toString(): value;
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
        	
            var isRequired = attrs.ngRequired === 'true';
        	
            ngModel.$validators.dateValidator = function(value) {
                if(!value){
                    return !isRequired;
                }                
                var convertedDate = DateUtils.format(angular.copy(value));
                var isValid = value === convertedDate;
                return isValid;
            };
            
            ngModel.$validators.futureDateValidator = function(value) {
                if(!value){
                    return !isRequired;
                }
                var maxDate = $parse(attrs.maxDate)(scope);
                var convertedDate = DateUtils.format(angular.copy(value));
                var isValid = value === convertedDate;                
                if(isValid){
                    isValid = maxDate === 0 ? !moment(convertedDate).isAfter(DateUtils.getToday()) : isValid;
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
            
            var isRequired = attrs.ngRequired === 'true';
            
            if(attrs.name === 'latitude'){
                ngModel.$validators.latitudeValidator = function(value) {
                    if(!value){
                        return !isRequired;
                    }
                    var isNumber = dhis2.validation.isNumber(value);
                    if(!isNumber){
                        return isNumber;
                    }
                    return value >= -90 && value <= 90;
                };
            }
            
            if(attrs.name === 'longitude'){
                ngModel.$validators.longitudeValidator = function(value) {
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
        }
    };
})

.directive("d2OptionValidator", function($translate) {
    return {
        restrict: "A",         
        require: "ngModel",         
        link: function(scope, element, attrs, ngModel) {
        	
            var isRequired = attrs.ngRequired === 'true';
            
            ngModel.$validators.optionValidator = function(value) {               
                
                var res = !value ? !isRequired : true;
                
                if(!res){
                    alert($translate.instant('option_required'));
                }
                
                return res;
            };
        }
    };
});