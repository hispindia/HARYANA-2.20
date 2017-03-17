
window.onload=function()
{
	jQuery('#addUpdatePartnerDiv').dialog({autoOpen: false});
}

// load partner list
function loadPartnerList()
{
	$( '#partnerListDiv' ).html('');
	
	
	var dataSetId = $( "#dataSetId" ).val();
   	var optionSetId = $( "#optionSetId" ).val();
	var dataElementId = $( "#dataElementId" ).val();
	
	if ( dataSetId == "-1" )
	{
		$( '#partnerListDiv' ).html('');
		showWarningMessage( "Please Select DataSet" );
		return false;
	}	
	
	else if ( optionSetId == "-1" )
	{
		$( '#partnerListDiv' ).html('');
		showWarningMessage( "Please Select Partner" );
		return false;
	}
	
	else if ( dataElementId == "-1" )
	{
		$( '#partnerListDiv' ).html('');
		showWarningMessage( "Please Select DataElement" );
		return false;
	}
	
	else
	{
	    document.getElementById('overlayPartner').style.visibility = 'visible';
		jQuery('#partnerListDiv').load('getpartnerDateList.action',
			{
				dataSetId:dataSetId,
				optionSetId:optionSetId,
				dataElementId:dataElementId
			}, function()
			{
				showById('partnerListDiv');
				document.getElementById('overlayPartner').style.visibility = 'hidden';
			});
	}
}

// show add partner form
function showAddParentForm()
{
  //  jQuery('#addNewPeriod').dialog('destroy').remove();

	var dataSetId = $( "#dataSetId" ).val();
   	var optionSetId = $( "#optionSetId" ).val();
	var dataElementId = $( "#dataElementId" ).val();

	if ( dataSetId == "-1" )
    {
        showWarningMessage( "Please Select DataSet" );

        return false;
    }

    else if( optionSetId == "-1" )
    {
        showWarningMessage( "Please Select Partner" );
        return false;
    }

    else if( dataElementId == "-1" )
    {
        showWarningMessage( "Please Select DataElement" );
        return false;
    }
	else
	{ 		
		jQuery('#addUpdatePartnerDiv').dialog('destroy').remove();
		jQuery('<div id="addUpdatePartnerDiv">' ).load( 'showAddPartnerForm.action?dataSetId='+ dataSetId + "&optionSetId=" + optionSetId + "&dataElementId=" + dataElementId).dialog({
			title: 'Add Partner Information',
	        maximize: true,
	        closable: true,
	        modal:true,
	        overlay:{background:'#000000', opacity:0.1},
	        width: 600,
	        height: 500
		});
	}
    
}


//Validation of Partner Add
function validateAddPartner()
{

	var url = "validatePartner.action?";
		url += '&dataSetId=' + $("#dataSetId").val();
		url += '&optionSetId=' + $("#optionSetId").val();
		url += '&dataElementId=' + $("#dataElementId").val();
		
	//alert( " In Validation :" + url );
	$.postJSON( url, {}, function( json )
	{
		if ( json.response == "input" )
		{
			//setHeaderDelayMessage( json.message );
			//showWarningMessage( json.message )
			setHeaderDelayMessage( json.message );
		}
		else if ( json.response == "success" )
		{
			addPartner();
		}
	});
}


function addPartner()
{	
	//document.forms['addPartnerForm'].submit();
	//loadPartnerList();
	
	/*
	$('#addUpdatePartnerDiv').on('change', function () {
	    $('#addUpdatePartnerDiv').parent('form').submit();
	});
	*/
	
	
	//alert("inside add partner");
	lockScreen();
	$.ajax({
      type: "POST",
      url: 'addPartnerForm.action',
      data: getParamsForDiv('addUpdatePartnerDiv'),
      success: function(json) {
    	var type = json.response;
		//alert("after  add partner");
		jQuery('#addUpdatePartnerDiv').dialog('destroy').remove();
		
		unLockScreen();
		
		loadPartnerList();
		
      }
     });
     
	
     
}

//show update partner form
function showUpdatePartnerForm( dataSetId, dataElementId, optionSetId, partnerStartDate , partnerEndDate )
{
  //  jQuery('#addNewPeriod').dialog('destroy').remove();

	var dataSetId = $( "#dataSetId" ).val();
   	var optionSetId = $( "#optionSetId" ).val();
	var dataElementId = $( "#dataElementId" ).val();

	if ( dataSetId == "-1" )
    {
        showWarningMessage( "Please Select DataSet" );

        return false;
    }

    else if( optionSetId == "-1" )
    {
        showWarningMessage( "Please Select Partner" );
        return false;
    }

    else if( dataElementId == "-1" )
    {
        showWarningMessage( "Please Select DataElement" );
        return false;
    }
	else
	{ 		
		jQuery('#addUpdatePartnerDiv').dialog('destroy').remove();
		jQuery('<div id="addUpdatePartnerDiv">' ).load( 'showUpdatePartnerForm.action?dataSetId='+ dataSetId + "&optionSetId=" + optionSetId + "&dataElementId=" + dataElementId + "&startDate=" + partnerStartDate + "&endDate=" + partnerEndDate ).dialog({
	        title: 'Update Partner Information',
	        maximize: true,
	        closable: true,
	        modal:true,
	        overlay:{background:'#000000', opacity:0.1},
	        width: 600,
	        height: 500
		});
	}
    
}


//Validation of Partner Update
function validateUpdatePartner()
{

	var url = "validatePartner.action?";
		url += '&dataSetId=' + $("#dataSetId").val();
		url += '&optionSetId=' + $("#optionSetId").val();
		url += '&dataElementId=' + $("#dataElementId").val();
		
	//alert( " In Validation :" + url );
	$.postJSON( url, {}, function( json )
	{
		if ( json.response == "input" )
		{
			//setHeaderDelayMessage( json.message );
			//showWarningMessage( json.message )
			setHeaderDelayMessage( json.message );
		}
		else if ( json.response == "success" )
		{
			updatePartner();
		}
	});
}


// update partner form
function updatePartner()
{
	lockScreen();
	
	$.ajax({
      type: "POST",
      url: 'updatePartnerForm.action',
      data: getParamsForDiv('addUpdatePartnerDiv'),
      success: function(json) {
		var type = json.response;
		
		jQuery('#addUpdatePartnerDiv').dialog('destroy').remove();
		unLockScreen();
		loadPartnerList();
		
      }
     });
}

// close the window
function closePopUpWindow()
{
	jQuery('#addUpdatePartnerDiv').dialog('destroy').remove();
}

function getParamsForDiv( partnerDiv )
{
	var params = '';
	
	//alert("Inside  PARAM");
	
	var dataSetId = getFieldValue('dataSetId');
	params += '&dataSetId=' + dataSetId;
	
	var optionSetId = getFieldValue('optionSetId');
	params += '&optionSetId=' + optionSetId;
	
	var dataElementId = getFieldValue('dataElementId');
	params += '&dataElementId=' + dataElementId;
	
	var startDate = getFieldValue('startDate');
	params += '&startDate=' + startDate;
	
	var endDate = getFieldValue('endDate');
	params += '&endDate=' + endDate;
	
	//alert( "1 " + params )
	
	//params += "&" + getParamStringPartner( 'selectedOrganisationUnit', 'selectedOrganisationUnit' )
	
	//alert( "2 " + params );
	
	/*
	jQuery("#" + partnerDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}			
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				params += elementId + "="+ value + "&";
			}
			
		});
	*/
	
	//getParamStringPartner( elementId, param )
	
	//selectedUnits
	
	//params += "&" + getParamStringPartner( 'selectedUnits', 'selectedUnits' )
	
	//alert( "Final PARAM : " + params );
	
	return params;
}


function getParamStringPartner( elementId, param )
{
    var result = "";
	var list = jQuery( "#" + elementId ).children();
	
	list.each( function( i, item ){
		result += param + "=" + item.value;
		result += (i < list.length-1) ? "&" : "";
	});
	
	return result;
}



// Remove/Delete partnerDetails
function removePartner( dataSetId, dataElementId, optionSetId, startDate, endDate )
{
	var result = window.confirm( i18n_confirm_delete );
	
	if ( result )
	{
		lockScreen();
		$.ajax({
		    type: "POST",
		    url: 'removePartner.action?dataSetId='+ dataSetId + "&dataElementId=" + dataElementId + "&optionSetId=" + optionSetId + "&startDate=" + startDate + "&endDate=" + endDate,
		    	success: function( json ) 
		    	{
		    		unLockScreen();
		    		loadPartnerList();
				}
			});
	}
}


// Load DataElement and Period List
function loadDataElementAndPeriod()
{
	var dataSetId = $( '#dataSetId' ).val();
	
	if ( dataSetId == "-1" )
	{
		showWarningMessage( "Please Select DataSet" );
		
		//document.getElementById( "dataSetId" ).disabled = true;
		
		//document.getElementById( "selectedPeriodId" ).disabled = true;
		//document.getElementById( "prevButton" ).disabled = true;
		//document.getElementById( "nextButton" ).disabled = true;
		
		return false;
	}
	
	else
	{
		//enable('dataSetId');
		
		$.post("getDataElementsAndPeriodList.action",
				{
					dataSetId:dataSetId
				},
				function(data)
				{
					
					populateDataElementAndPeriodList( data );
					//loadDataSets();				
				},'xml');
	}
	
}


function populateDataElementAndPeriodList( data )
{
	var dataElementId = document.getElementById("dataElementId");
	clearList( dataElementId );
	
	//var periodId = document.getElementById("periodId");
	//clearList( periodId );
	
	var dataElementList = data.getElementsByTagName("dataelement");
	
	dataElementId.options[0] = new Option( "Select", "-1" , false, false);
	
	for ( var i = 0; i < dataElementList.length; i++ )
	{
		var id = dataElementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = dataElementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		dataElementId.add(option, null);
	} 
	
	loadPartnerList();
	
	
	/*var periodList = data.getElementsByTagName("period");
	
	periodId.options[0] = new Option( "Select", "-1" , false, false);
	
	for ( var i = 0; i <  periodList.length; i++ )
	{
		var id = periodList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = periodList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		periodId.add(option, null);
	} */
	
}



/**
 * Clock screen by mask *
 */
/*
function lockScreen()
{
	jQuery.blockUI({ message: i18n_waiting , css: { 
		border: 'none', 
		padding: '15px', 
		backgroundColor: '#000', 
		'-webkit-border-radius': '10px', 
		'-moz-border-radius': '10px', 
		opacity: .5, 
		color: '#fff'			
	} }); 
}
*/

/**
 * unClock screen *
 */
/*
function unLockScreen()
{
	jQuery.unblockUI();
}
*/

