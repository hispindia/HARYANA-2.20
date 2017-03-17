
// ----------------------------------------------------------------------
// Edit Quality Score Payment
// ---------------------------- ------------------------------------------

function editQualityScorePaymentForm(context) 
{
	location.href = 'editQualityScorePaymentForm.action?id=' + context.id;
}

//-----------------------------------------------------------------------------
//	Quality Score Payment details
//-----------------------------------------------------------------------------

function showQualityScorePaymentDetails(context) 
{

	jQuery.getJSON('getQualityScorePayment.action', {
		id : context.id
	}, function(json) {
		setInnerHTML('stateRangeField', json.qualityscorepayment.startRange);
		setInnerHTML('endRangeField', json.qualityscorepayment.endRange);
		setInnerHTML('additionalPaymentField', json.qualityscorepayment.addQtyPayment);
		
		showDetails();
	});
}


//-----------------------------------------------------------------------------
//	Delete Quality Score Payment
//-----------------------------------------------------------------------------

function removeQualityScorePayment(context) {

	removeItem( context.id, context.name, i18n_confirm_delete,
			'delQualityScorePayment.action');
}
