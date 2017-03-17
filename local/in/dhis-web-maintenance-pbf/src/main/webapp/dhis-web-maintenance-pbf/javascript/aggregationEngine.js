
//-----------------------------------------------------------------------------
//Add Aggregation Query Form
//----------------------------------------------------------------------------- 
function showAddAggregationForm() 
{
	window.location.href = 'addAggregationQueryForm.action';
}


//-----------------------------------------------------------------------------
//Update Aggregation Query Form
//-----------------------------------------------------------------------------

function showUpdateAggregationForm( context ) 
{
	location.href = 'showUpdateAggregationForm.action?id=' + context.id;
}

//-----------------------------------------------------------------------------
//Remove Aggregation
//-----------------------------------------------------------------------------

function removeAggregation( context ) 
{
	removeItem( context.id, context.name, i18n_confirm_delete_aggregation_query, 'removeAggregation.action');
}


//-----------------------------------------------------------------------------
//View Aggregation Query details
//-----------------------------------------------------------------------------

function showAggregationDetails( context ) 
{
	jQuery.getJSON('getAggregation.action', { id: context.id }, function( json ) 
	{
		setInnerHTML('nameField', json.caseAggregation.name);
		setInnerHTML('aggregationDataElementField', json.caseAggregation.aggregationDataElement);
		setInnerHTML('operatorField', json.caseAggregation.operator);
		//setInnerHTML('optionComboField', json.caseAggregation.optionCombo);
		//setInnerHTML('aggregationExpressionField', json.caseAggregation.aggregationExpression);
		//setInnerHTML('deSumField', json.caseAggregation.deSum);
		showDetails();
	});
}