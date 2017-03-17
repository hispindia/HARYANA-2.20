
window.onload=function(){
		
	jQuery('#addUpdateUtilizationRateDiv').dialog({autoOpen: false});
}


function loadUtilizationRate()
{
	$( '#utilizationRateListDiv' ).html('');
	
	var dataElementId = $( '#dataElementId' ).val();
	
	if ( dataElementId == "-1" )
	{
		$( '#utilizationRateListDiv' ).html('');
		showWarningMessage( "Please Select DataElement" );
		return false;
	}
	
	else
	{
	    document.getElementById('overlayUtilization').style.visibility = 'visible';
		jQuery('#utilizationRateListDiv').load('getUtilizationRateList.action',
			{
				dataElementId:dataElementId
			}, function()
			{
				showById('utilizationRateListDiv');
				document.getElementById('overlayUtilization').style.visibility = 'hidden';
			});
	}
}


function showAddUtilizationRateForm()
{
	var dataElementId = $( '#dataElementId' ).val();
	
	if ( dataElementId == "-1" )
	{
		showWarningMessage( "Please Select DataElement" );
		return false;
	}
	
	else
	{
		//var url = "showAddUtilizationRateForm.action?dataElementId=" + dataElementId;
		//window.location.href = url;
		
		
		jQuery('#addUpdateUtilizationRateDiv').dialog('destroy').remove();
		jQuery('<div id="addUpdateUtilizationRateDiv">' ).load( 'showAddUtilizationRateForm.action?dataElementId='+ dataElementId ).dialog({
			title: 'Add Utilization Rule',
			maximize: true,
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 450,
			height: 300
		});
		
	}
}

function addUtilizationRate()
{
	$.ajax({
      type: "POST",
      url: 'addUtilizationRate.action',
      data: getParamsForDiv('addUpdateUtilizationRateDiv'),
      success: function(json) {
		var type = json.response;
		
		jQuery('#addUpdateUtilizationRateDiv').dialog('destroy').remove();
		loadUtilizationRate();
		
      }
     });
}


function closePopUpWindow()
{
	jQuery('#addUpdateUtilizationRateDiv').dialog('destroy').remove();
}


function showUpdateutilizationRateForm( startRange, endRange, dataElementId )
{
	var dataElementId = $( '#dataElementId' ).val();
	
	if ( dataElementId == "-1" )
	{
		showWarningMessage( "Please Select DataElement" );
		return false;
	}
	
	else
	{
		//var url = "showAddUtilizationRateForm.action?dataElementId=" + dataElementId;
		//window.location.href = url;
		
		
		jQuery('#addUpdateUtilizationRateDiv').dialog('destroy').remove();
		jQuery('<div id="addUpdateUtilizationRateDiv">' ).load( 'showUpdateUtilizationRateForm.action?dataElementId='+ dataElementId + "&startRange=" + startRange + "&endRange=" + endRange ).dialog({
			title: 'Update Utilization Rule',
			maximize: true,
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 450,
			height: 300
		});
		
	}
}


function updateUtilizationRate()
{
	$.ajax({
      type: "POST",
      url: 'updateUtilizationRate.action',
      data: getParamsForDiv('addUpdateUtilizationRateDiv'),
      success: function(json) {
		var type = json.response;
		
		jQuery('#addUpdateUtilizationRateDiv').dialog('destroy').remove();
		loadUtilizationRate();
		
      }
     });
}


function getParamsForDiv( utilizationRateDiv )
{
	var params = '';
	
	jQuery("#" + utilizationRateDiv + " :input").each(function()
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
	
	//alert( params );
	
	return params;
}


function removeutilizationRate( startRange, endRange, dataElementId  )
{
	var result = window.confirm( i18n_confirm_delete );
	
	if ( result )
	{
		$.ajax({
		    type: "POST",
		    url: 'removeUtilizationRate.action?dataElementId='+ dataElementId + "&startRange=" + startRange + "&endRange=" + endRange,
		    	success: function( json ) {
				
		    		loadUtilizationRate();
				}
			});
	}
}




