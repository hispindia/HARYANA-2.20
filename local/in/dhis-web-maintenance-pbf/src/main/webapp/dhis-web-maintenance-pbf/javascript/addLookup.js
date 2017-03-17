jQuery(document).ready(function() {
	validation2('addLookupForm', function(form) {
		form.submit();
	},
	function(){
		
		isSubmit = true;
	
	});

	checkValueIsExist("name", "validateLookup.action");
	
});
