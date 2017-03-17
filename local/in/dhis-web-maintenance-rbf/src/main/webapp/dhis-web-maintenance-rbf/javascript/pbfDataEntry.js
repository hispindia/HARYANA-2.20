
	var COLOR_GREEN = '#b9ffb9';
	var COLOR_YELLOW = '#fffe8c';
	var COLOR_RED = '#ff8a8a';
	var COLOR_ORANGE = '#ff6600';
	var COLOR_WHITE = '#ffffff';
	var COLOR_GREY = '#cccccc';
	var LocaleColor = 'black';
	
function orgUnitHasBeenSelected( orgUnitIds )
{    
	$( '#dataEntryFormDiv' ).html( '' );
	
	//document.getElementById('selectedOrgunitID').value = orgUnitIds;
	
	//alert( orgUnitIds );
	
	if( orgUnitIds != null && orgUnitIds != "" )
	{
		var dataSetId = $( '#dataSetId' ).val();
		var periodId = $( '#selectedPeriodId' ).val();
		 $.getJSON( 'getOrganisationUnit.action', {orgUnitId:orgUnitIds[0]}
	        , function( json ) 
	        {
	            var type = json.response;
	            setFieldValue('orgUnitName', json.message );
	            setFieldValue('selectedOrgunitName', json.message );	            
	            if( type == "success" )
	            {
					enable('dataSetId');
					
					var options = '';
		            $.each(json.dataSets, function(i, obj){
		                options += '<option value="' + obj.id + '"'+ '>' + obj.name + '</option>';
		            });
		            $("select#dataSetId").html(options);
		            
		            $("select#dataSetId option[value="+dataSetId+"]").attr('selected', 'selected');
		            $("select#selectedPeriodId option[value="+periodId+"]").attr('selected', 'selected');
		            loadPeriods();		            
					setFieldValue('selectedOrgunitID',orgUnitIds[0])
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );	                
	            }
	            else if( type == "input" )
	            {
	                disable('dataSetId');
	                disable('selectedPeriodId');
	                disable('prevButton');
	                disable('nextButton');
	                
	                setFieldValue('orgUnitName', json.message );
	                setFieldValue('selectedOrgunitName', json.message );
	            }
	        } );		
	}
}

selection.setListenerFunction( orgUnitHasBeenSelected );


function loadDataEntryForm()
{
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	$( '#dataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );
	
	/*
	var dataSetLockStatus = $( '#dataSetLockStatus' ).val();
	
	alert( dataSetLockStatus );
	
	if ( dataSetLockStatus == "true" )
	{
        $( '#dataEntryFormDiv input').attr( 'disabled', 'disabled' );
		setHeaderDelayMessage( i18n_dataset_is_locked );
	}
	else
	{
        $( '#dataEntryFormDiv input' ).removeAttr( 'disabled' );
		
	}
	*/
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" && dataSetId == "-1" )
	{
		$( '#dataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    jQuery('#loaderDiv').show();
	    
	    //alert("uuuuuu");
	    
	    var url = "loadDataEntryForm.action" + "?orgUnitId=" + orgUnitId + "&dataSetId=" + dataSetId + "&selectedPeriodId=" + selectedPeriodId;
	    
		$( "#dataEntryFormDiv" ).load( url, function()
			    {
					var lockStatue  = document.getElementById("dataSetLockStatus").value;
					
					//alert( lockStatue );
					
					if ( lockStatue == "true" )
					{
						document.getElementById( "utilizationRate" ).disabled = true;
						$( '#dataEntryFormDiv input').attr( 'disabled', 'disabled' );
						setHeaderDelayMessage( i18n_dataset_is_locked );
					}
					else
					{
				        $( '#dataEntryFormDiv input' ).removeAttr( 'disabled' );
				        $( '#utilizationRate' ).removeAttr( 'disabled' );
						
					}
					
					jQuery('#loaderDiv').hide();
					showById('dataEntryFormDiv');

			    } );
	    
	    /*
		jQuery('#dataEntryFormDiv').load('loadDataEntryForm.action',
			{
				orgUnitId:orgUnitId,
				dataSetId:dataSetId,
				selectedPeriodId:selectedPeriodId
			}, function()
			{
				
				var lockStatue  = document.getElementById("dataSetLockStatus").value;
				
				alert( lockStatue );
				
				if ( lockStatue == "true" )
				{
			        $( '#dataEntryFormDiv input').attr( 'disabled', 'disabled' );
					setHeaderDelayMessage( i18n_dataset_is_locked );
				}
				else
				{
			        $( '#dataEntryFormDiv input' ).removeAttr( 'disabled' );
					
				}
				
				
				showById('dataEntryFormDiv');
				jQuery('#loaderDiv').hide();
			});
		hideLoader();
		*/
		
	}

}

function saveValue( dataElementId,optionComboId )
{
	var period = document.getElementById("selectedPeriodId").value;
	var valueId = "dataelement"+dataElementId+":"+optionComboId;
	
	var fieldId = "#"+valueId;
	var defaultValue = document.getElementById(valueId).defaultValue;
	var value = document.getElementById(valueId).value;
	
	if(defaultValue != value)
	{
	var dataValue = {
        'dataElementId' : dataElementId,
        'optionComboId' : optionComboId,
        'organisationUnitId' : $("#selectedOrgunitID").val(),
        'periodIso' : period,
        'value' : value
    };
    jQuery.ajax( {
            url: 'saveValue.action',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
	}
	
	function handleSuccess( json )
	{
	    var code = json.c;
	    if ( code == '0' || code == 0) // Value successfully saved on server
	    {
	    	 markValue( fieldId, COLOR_GREEN );
	    }
	    else if ( code == 2 )
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_dataset_is_locked );
	    }
	    else // Server error during save
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
	    }            
	}

	function handleError( jqXHR, textStatus, errorThrown )
	{       
	    markValue( fieldId, COLOR_GREEN );
	}

	function markValue( fieldId, color )
	{
	    document.getElementById(valueId).style.backgroundColor = color;	   
	}
}


function savePBFDataValue( dataElementId, valueType )
{
	var period = document.getElementById("selectedPeriodId").value;
	var dataSetId = $( '#dataSetId' ).val();
	var valueId = "";
	
	var qtyReportedFieldId = "pbfdv_qty_reported_"+dataElementId;
	
	var qtyValidatedFieldId = "pbfdv_qty_validated_"+dataElementId;
	
	var qtyRreported = document.getElementById( qtyReportedFieldId ).value;
	
	var qtyValidated = document.getElementById( qtyValidatedFieldId ).value;
	
	if( valueType == 1 )
	{
		valueId = "pbfdv_qty_reported_"+dataElementId;
	}
	
	else if( valueType == 2 )
	{
		//alert( qtyRreported + ":" + qtyValidated )
		
		if(  parseInt(qtyValidated)  > parseInt(qtyRreported)   )
		{
			alert( "Quantity Validated should less or equal to Quantity Reported" );
			document.getElementById( qtyValidatedFieldId ).value = "";
			return;
		}
		
		else
		{
			valueId = "pbfdv_qty_validated_"+dataElementId;
		
			saveDataInDataValue( dataElementId );
		}
		
		//valueId = "pbfdv_qty_validated_"+dataElementId;
		
		//saveDataInDataValue( dataElementId );
		
	}
	
	else
	{
		valueId = "pbfdv_qty_external_verification_"+dataElementId;
	}
	
	var tariffValueId = "pbfdv_tariff_amt_"+dataElementId;
	var tariffAmt = document.getElementById( tariffValueId ).value;
	
	var fieldId = "#"+valueId;
	var defaultValue = document.getElementById(valueId).defaultValue;
	var value = document.getElementById( valueId ).value;
	
	//if(defaultValue != value)
	//{
		var dataValue = {
				'dataElementId' : dataElementId,
				'valueType' : valueType,
				'dataSetId' : dataSetId,
				'organisationUnitId' : $("#selectedOrgunitID").val(),
				'periodIso' : period,
				'tariffAmt' : tariffAmt,
				'value' : value
    };
    jQuery.ajax( {
            url: 'saveValue.action',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
	//}
	
	function handleSuccess( json )
	{
	    var code = json.c;

	    //alert(code)
	    if ( code == '0' || code == 0) // Value successfully saved on server
	    {
	    	 markValue( fieldId, COLOR_GREEN );
	    }
	    else if ( code == 2 )
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_dataset_is_locked );
	    }
	    else // Server error during save
	    {
	        markValue( fieldId, COLOR_RED );
	        window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
	    }
	}

	function handleError( jqXHR, textStatus, errorThrown )
	{       
	    markValue( fieldId, COLOR_GREEN );
	}

	function markValue( fieldId, color )
	{
	    document.getElementById(valueId).style.backgroundColor = color;	   
	}
}

// save qty validated in dataValue table
function saveDataInDataValue( dataElementId )
{	
	//alert ( " Inside Save qty validated in dataValue table " );
	
	var period = document.getElementById("selectedPeriodId").value;
	var valueId  = "pbfdv_qty_validated_"+dataElementId;

	var fieldId = "#"+valueId;
	var defaultValue = document.getElementById(valueId).defaultValue;
	var value = document.getElementById(valueId).value;

	if( defaultValue != value )
	{
       var dataValue = {
            'dataElementId' : dataElementId,        
            'organisationUnitId' : $("#selectedOrgunitID").val(),
            'periodIso' : period,
            'value' : value
        };
    
       jQuery.ajax( {
            url: 'saveValueInDataValue.action',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
	}

	function handleSuccess( json )
	{
       var code = json.c;
       if ( code == '0' || code == 0) // Value successfully saved on server
       {
    	  markValue( fieldId, COLOR_GREEN );
       }
       else if ( code == 2 )
       {
           markValue( fieldId, COLOR_RED );
           window.alert( i18n_saving_value_failed_dataset_is_locked );
       }
       else // Server error during save
       {
           markValue( fieldId, COLOR_RED );
           window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
       }
	}

	function handleError( jqXHR, textStatus, errorThrown )
	{       
       markValue( fieldId, COLOR_RED );
	}

	function markValue( fieldId, color )
	{
       document.getElementById(valueId).style.backgroundColor = color;	   
	}	
}



function saveTotalValueInDataValue()
{	
	//alert ( " Inside Save Total in dataValue table " );
	var period = document.getElementById("selectedPeriodId").value;
	
	var totalDeId  = document.getElementById("totalDataElementId").value;
	
	//alert( totalDeId );
	
	var totalDeFieldId = "#"+totalDeId;
	
	var defaultValue = document.getElementById("all-total").defaultValue;
	var value = document.getElementById("all-total").value;
	//alert( value );
	
	if( defaultValue != value )
	{
       var dataValue = {
            'dataElementId' : totalDeId,        
            'organisationUnitId' : $("#selectedOrgunitID").val(),
            'periodIso' : period,
            'value' : value
        };
    
       jQuery.ajax( {
            url: 'saveValueInDataValue.action',
            data: dataValue,
            dataType: 'json',
            success: handleSuccess,
            error: handleError
        } );
	}

	function handleSuccess( json )
	{
       var code = json.c;
       if ( code == '0' || code == 0) // Value successfully saved on server
       {
    	  markValue( totalDeFieldId, COLOR_GREEN );
    	  
       }
       else if ( code == 2 )
       {
           markValue( totalDeFieldId, COLOR_RED );
           window.alert( i18n_saving_value_failed_dataset_is_locked );
       }
       else // Server error during save
       {
           markValue( totalDeFieldId, COLOR_RED );
           window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
       }
	}

	function handleError( jqXHR, textStatus, errorThrown )
	{       
       markValue( totalDeFieldId, COLOR_RED );
	}

	function markValue( totalDeFieldId, color )
	{
       document.getElementById("all-total").style.backgroundColor = color;	   
	}	
}


// load periods
function loadPeriods()
{
	$( '#dataEntryFormDiv' ).html( '' );
	
	hideById('urDataElementLavel');
	hideById('urDataElementText');
	
    var orgUnitId = $( '#selectedOrgunitID' ).val();

    var dataSetId = $( '#dataSetId' ).val();
	
	
	if ( dataSetId == "-1" )
	{
		showWarningMessage( i18n_select_dataset );
		
		document.getElementById( "selectedPeriodId" ).disabled = true;
		document.getElementById( "prevButton" ).disabled = true;
		document.getElementById( "nextButton" ).disabled = true;
		return false;
	}
	
	else
	{
		
		enable('selectedPeriodId');
		
		enable('prevButton');
		enable('nextButton');
				
		var url = 'loadPeriods.action?dataSetId=' + dataSetId;
		
		var list = document.getElementById( 'selectedPeriodId' );
			
		clearList( list );
		
		addOptionToList( list, '-1', '[ Select ]' );
		
	    $.getJSON( url, function( json ) {
	    	for ( i in json.periods ) {
	    		addOptionToList( list, json.periods[i].isoDate, json.periods[i].name );
	    	}
	    } );
	    
	}	
}


//next and pre periods
function getAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	$( '#dataEntryFormDiv' ).html( '' );
	
	var dataSetId = $( '#dataSetId' ).val();
	
	var availableList = document.getElementById( availablePeriodsId );
	var selectedList = document.getElementById( selectedPeriodsId );
	
	clearList( selectedList );
	
	addOptionToList( selectedList, '-1', '[ Select ]' );
	
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"dataSetId": dataSetId ,
		"year": year },
		function( json ) {
			
			for ( i in json.periods ) {
	    		addOptionToList( selectedList, json.periods[i].isoDate, json.periods[i].name );
	    	}
			
		} );
}

















/*
function getUtilizationRateTariffValue()
{
    var utilizationRate = $('#utilizationRate').val();
	//alert( utilizationRate );
	
	var tempUtilizationRateDataElements = document.getElementById("utilizationRateDataElementLB");
   	
	if( utilizationRate != "" || utilizationRate != " " || utilizationRate != '' || utilizationRate != ' ' || utilizationRate.length != 0 )
	{
		for ( i=0; i < tempUtilizationRateDataElements.length; i++ )
        {                    
			var utilizationRateTariffMapValue1 = utilizationRateTariffMap[tempUtilizationRateDataElements.options[i].value];
			
			//alert( utilizationRateTariffMapValue1.split("#").length + " :  " + utilizationRateTariffMapValue1 );
			
			for( j=0; j < utilizationRateTariffMapValue1.split("#").length; j++ )
			{
		        var utilizationRateTariffMapValue = utilizationRateTariffMapValue1.split("#")[j];
		        
                var startRange = parseFloat( utilizationRateTariffMapValue.split(":")[0] );           
                var endRange = parseFloat( utilizationRateTariffMapValue.split(":")[1] );
                var tariffValue = parseFloat( utilizationRateTariffMapValue.split(":")[2] );    				
			
			    //alert( startRange + "--" + endRange + "--" +  tariffValue + "--" + utilizationRate );
			
                if( parseFloat( utilizationRate) >= parseFloat( startRange ) && parseFloat( utilizationRate ) < parseFloat( endRange ) )
                {	
					//alert( "2 Alert" + " : " + startRange + "--" + endRange + "--" +  tariffValue + "--" +  utilizationRate );
					var dataElementId = tempUtilizationRateDataElements.options[i].value;
					//alert( '#pbfdv_tariff_amt_'+dataElementId );
                    $('#pbfdv_tariff_amt_'+dataElementId).val( tariffValue );
					
					//alert( $('#pbfdv_tariff_amt_'+dataElementId).val( parseFloat( tariffValue ) ) );
                }
            }
        }
	}
	            
}

*/

















