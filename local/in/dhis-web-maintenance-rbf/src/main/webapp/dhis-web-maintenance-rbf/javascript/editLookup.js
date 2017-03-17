jQuery(document).ready(function() {
	validation2('editLookupForm', function(form) {
		form.submit();
	}, 
	function(){
		isSubmit = true;
	
	});

	checkValueIsExist("name", "validateLookup.action",{lookupId:getFieldValue('lookupId')});
});
