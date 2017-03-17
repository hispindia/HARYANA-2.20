var tmpLookupId;
var tmpSource;

// -----------------------------------------------------------------------------
// DataSet details form
// -----------------------------------------------------------------------------

function showlookupDetails(context) {

	jQuery.getJSON('getLookup.action', {
		id : context.id
	}, function(json) {
		setInnerHTML('nameField', json.lookup.name);
		setInnerHTML('codeField', json.lookup.code);
		setInnerHTML('descriptionField', json.lookup.description);
		setInnerHTML('typeField', json.lookup.type);
		setInnerHTML('valueField', json.lookup.value);

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Delete DataSet
// -----------------------------------------------------------------------------

function removeLookup(context) {

	removeItem(context.id, context.name, i18n_confirm_delete,
			'delLookup.action');
}

// ----------------------------------------------------------------------
// DataEntryForm
// ----------------------------------------------------------------------

function editLookupForm(context) {
	location.href = 'editLookupForm.action?lookupId=' + context.id;
}