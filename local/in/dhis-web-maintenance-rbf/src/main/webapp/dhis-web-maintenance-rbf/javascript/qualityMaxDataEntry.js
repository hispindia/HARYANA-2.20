window.onload=function(){
	jQuery('#qualityMaxScoreDiv').dialog({autoOpen: false});	

}


	var COLOR_GREEN = '#b9ffb9';
	var COLOR_YELLOW = '#fffe8c';
	var COLOR_RED = '#ff8a8a';
	var COLOR_ORANGE = '#ff6600';
	var COLOR_WHITE = '#ffffff';
	var COLOR_GREY = '#cccccc';
	var LocaleColor = 'black';
	var countryTags;
	
function orgUnitHasBeenSelected( orgUnitIds , orgUnitNames )
{
	$( '#dataEntryFormDiv' ).html( '' );
		
	if( orgUnitIds != null && orgUnitIds != "" )
	{
		setFieldValue('selectedOrgunitID',orgUnitIds[0]);
		setFieldValue('selectedOrgunitName', orgUnitNames[0] );
		
		orgUnitGroupChange();
	}
}

selection.setListenerFunction( orgUnitHasBeenSelected );




function orgUnitGroupChange()
{
	$( '#dataEntryFormDiv' ).html( '' );
		
	var dataSetId = $( '#dataSetId' ).val();
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	var orgUnitGroupId = $( '#orgUnitGroupId' ).val();

	//alert( orgUnitId + " : " + orgUnitGroupId );
	
	if( orgUnitId != "" && orgUnitGroupId != "-1" )
	{
		 $.getJSON( 'getOrganisationUnitForMax.action', { orgUnitId:orgUnitId, orgUnitGroupId:orgUnitGroupId }
	        , function( json ) 
	        {
	            var type = json.response;
	            setFieldValue('orgUnitName', json.message );
	            setFieldValue('selectedOrgunitName', json.message );
	            setFieldValue('selectedOrgunitID', orgUnitId )
	            
	            if( type == "success" )
	            {
					enable('dataSetId');
					enable('startDate');
					enable('endDate');
					enable('orgUnitGroupId');
					
					var options = '';
					options += '<option value="-1">Please Select</option>';
		            $.each(json.dataSets, function(i, obj){		            	
		                options += '<option value="' + obj.id + '"'+ '>' + obj.name + '</option>';
		            });
		            $("select#dataSetId").html(options);
		            
		            $("select#dataSetId option[value="+dataSetId+"]").attr('selected', 'selected');
		            	            
	                if( $( '#dataSetId' ).val() != "-1" ) loadDataEntryForm();
	            }
	            else if( type == "input" )
	            {
	                disable('dataSetId');
	                disable('selectedPeriodId');
	                disable('startDate');
	                disable('endDate');
	                //disable('orgUnitGroupId');
	            }
	        } );		
	}
		
}


function loadDataEntryForm()
{
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	var orgUnitGroupId = $( '#orgUnitGroupId' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	
	$( '#dataEntryFormDiv' ).html('');
	
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var date1 = Date.parse( startDate );
	var date2 = Date.parse( endDate );
	if (date1 > date2) {
	    alert ("Please enter correct date");
	    return false;
	}	
	if(startDate != "" && endDate != "")
	{
		 var dataValue = {
     			'orgUnitId' : orgUnitId,
     			'orgUnitGroupId' : orgUnitGroupId,
     			'startDate' : startDate,
				'endDate' : endDate,
				'dataSetId': dataSetId
 			};
				jQuery.ajax( {
             url: 'validateMaxData.action', 
			 data: dataValue,
             dataType: 'json',
             success: handleSuccess,
             error: handleError
         } );
         
		
	}
	
	function handleSuccess( json )
	{		
		if(json.message == "true")	
		{
			alert("Max quality score exist between this range");						
		}
		else
		{
			jQuery('#loaderDiv').show();	    
			jQuery('#dataEntryFormDiv').load('loadQualityMaxForm.action',
				{
					orgUnitId:orgUnitId,
					orgUnitGroupId : orgUnitGroupId,
					dataSetId:dataSetId,
					startDate:startDate,
					endDate:endDate
				}, function()
				{
					showById('dataEntryFormDiv');
					jQuery('#loaderDiv').hide();				
				});
			hideLoader();
		}
	}
	function handleError( json )
	{	
		alert("Error!");
	}
	
}


function saveQualityDataValue( dataElementId )
{
	var dataSetId = $( '#dataSetId' ).val();
	var valueId = "value_"+dataElementId;
	
	var fieldId = "#"+valueId;
	
	var defaultValue = document.getElementById(valueId).defaultValue;
	var value = document.getElementById( valueId ).value;
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	if(startDate == "" && endDate == "")
	{
		alert("Please select start date and end Date");
		return false;
	}
	
	if( defaultValue != value )
	{
		var dataValue = {
				'dataElementId' : dataElementId,
				'dataSetId' : dataSetId,
				'organisationUnitId' : $("#selectedOrgunitID").val(),
				'orgUnitGroupId' : $("#orgUnitGroupId").val(),
				'value' : value,
				'startDate' : startDate,
				'endDate' : endDate
    };
    jQuery.ajax( {
            url: 'saveQualityValue.action',
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


function loadQualityMaxScore()
{
	
	var dataSetId = $( '#dataSetId' ).val();
	var orgUnitId = $( '#selectedOrgunitID' ).val();
	var orgUnitGroupId = $( '#orgUnitGroupId' ).val();
	
	if ( dataSetId == "-1"  || orgUnitGroupId == "-1"  || orgUnitId == "" ||  orgUnitId.length == 0 )
	{
		alert( "Please Select Organisation unit / Orgainsation Unit Group / Dataset" );
		//return false;
	}
	
	else
	{
		jQuery('#qualityMaxScoreDiv').dialog('destroy').remove();
		jQuery('<div id="qualityMaxScoreDiv">' ).load( 'loadQualityMaxScore.action?dataSetId='+ dataSetId + "&orgUnitId=" + orgUnitId + "&orgUnitGroupId=" + orgUnitGroupId ).dialog({
			title: " Quality Max Score ",
			maximize: true,
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 500,
			height: 200
		});
	}
	
}


function applyStartDateEndDate( startDateEndDate )
{
	var sDateEDate = startDateEndDate.split(":");
	var startDate = sDateEDate[0];
	
	var enddate = sDateEDate[1];
	
	document.getElementById("startDate").value = startDate;
	document.getElementById("endDate").value = enddate;
	
	jQuery('#qualityMaxScoreDiv').dialog('destroy').remove();
	
	loadDataEntryForm();
}



























