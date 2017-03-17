// -----------------------------------------------------------------------------
// Report details form
// -----------------------------------------------------------------------------

function checkStartDate( dtStr )
{	
	if( isDate( dtStr ) )
	{
		var splitDate = dtStr.split("-");
		var temDay = splitDate[2];
		if( parseInt( temDay,10 ) > 1 )
		{
			alert("Please select start day of the month");
			return false;
		}		
		return true;
	}
	else
	{
		return false;
	}
}

function checkEndDate( dtStr )
{
	if( isDate( dtStr ) )
	{
		var splitDate = dtStr.split("-");
		var temDay = splitDate[2];
		if( parseInt( temDay,10 ) < 30 )
		{
			alert("Please select end day of the month");
			return false;
		}		
		return true;
	}
	else
	{
		return false;
	}
}

function showReportDetails(reportId) 
{
	$.post("getReport.action",
		{
			reportId : reportId
		},
		function (data)
		{
			reportRecieved(data);
		},'xml');
}

function reportRecieved(reportElement) 
{	
	byId('idField').innerHTML = reportElement.getElementsByTagName( 'id' )[0].firstChild.nodeValue;

	byId('nameField').innerHTML = reportElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;

	byId('modelField').innerHTML = reportElement.getElementsByTagName( 'model' )[0].firstChild.nodeValue;

	byId('frequencyField').innerHTML = reportElement.getElementsByTagName( 'frequency' )[0].firstChild.nodeValue;

	byId('reportTypeField').innerHTML = reportElement.getElementsByTagName( 'reportType' )[0].firstChild.nodeValue;

	byId('excelTemplateField').innerHTML = reportElement.getElementsByTagName( 'exceltemplate' )[0].firstChild.nodeValue;

	byId('xmlTemplateField').innerHTML = reportElement.getElementsByTagName( 'xmltemplate' )[0].firstChild.nodeValue;
	/*
	var orgGroupName = getElementValue( reportElement, 'orgGroupName' );
	setInnerHTML( 'orgGroupNameField', orgGroupName ? orgGroupName : '[' + Null + ']' );
	
	var dataSetName = getElementValue( reportElement, 'dataSetName' );
	setInnerHTML( 'dataSetNameField', dataSetName ? dataSetName : '[' + Null + ']' );
	*/
	
	//byId('orgGroupNameField').innerHTML = reportElement.getElementsByTagName( 'orgGroupName' )[0].firstChild.nodeValue;
	
	//byId('dataSetNameField').innerHTML = reportElement.getElementsByTagName( 'dataSetName' )[0].firstChild.nodeValue;

	showDetails();
}

// -----------------------------------------------------------------------------
// Delete Report
// -----------------------------------------------------------------------------

function removeReport(reportId, reportName) 
{
	var result = window.confirm(i18n_confirm_delete + '\n\n' + "Report Id ="
			+ reportId + '\n\n' + "Report Name =" + reportName);

	if (result) 
	{
		window.location.href = 'delReport.action?reportId=' + reportId;
	}
}

// ----------------------------------------------------------------------
// Validation for Report Add & Update
// ----------------------------------------------------------------------

function validateAddReport() 
{	
	$.post("validateReport.action",
		{
			name : byId('name').value,
			excelnameValue : byId('excelname').value,
			xmlnameValue : byId('xmlname').value
		},
		function (data)
		{
			addreportValidationCompleted(data);
		},'xml');

	return false;
}

function addreportValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['addReportForm'].submit();
	} 
	else if ( type == 'input' ) 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}

function validateEditReport() 
{	
	$.post("validateReport.action",
		{
			name : byId('name').value,
			reportId : byId('reportId').value,
			excelnameValue : byId('excelname').value,
			xmlnameValue : byId('xmlname').value
		},
		function (data)
		{
			editreportValidationCompleted(data);
		},'xml');

	return false;
}

function editreportValidationCompleted(messageElement) 
{
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if (type == 'success') 
	{
		document.forms['editReportForm'].submit();
	} 
	else if (type == 'input') 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}

function getOUDetails(orgUnitIds)
{
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds
			},
			function (data)
			{
				getOUDetailsRecevied(data);
			},'xml');

    getReports();
}

function getOUDetailsForOuWiseProgressReport(orgUnitIds)
{
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId : orgUnitIds
		},
		function (data)
		{
			getOUDetailsRecevied(data);
		},'xml');
}

function getOUDetailsRecevied(xmlObject)
{
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        var level = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
		
        document.reportForm.ouNameTB.value = orgUnitName;
    }    		
}

// ----------------------------------------------------------------------
// Get Periods
// ----------------------------------------------------------------------

function getPeriods() 
{
	var periodTypeList = document.getElementById( 'periodTypeId' );
	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;
	var availablePeriods = document.getElementById( 'availablePeriods' );

	if ( periodTypeId != "NA" ) 
	{
		$.post("getPeriods.action",
			{
				id : periodTypeId
			},
			function (data)
			{
				getPeriodsReceived(data);
			},'xml');			
	} 
	else 
	{
		document.reportForm.generate.disabled=true;
		clearList(availablePeriods);
		clearList(reportsList);
	}
}

function getPeriodsReceived(xmlObject) 
{
	var availablePeriods = document.getElementById("availablePeriods");

	clearList( availablePeriods );

	var periods = xmlObject.getElementsByTagName("period");
	
    if( periods.length > 0 )
    {
        document.reportForm.generate.disabled=false;
    }

	for ( var i = 0; i < periods.length; i++) 
	{
		var id = periods[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var periodName = periods[i].getElementsByTagName("periodname")[0].firstChild.nodeValue;

		$("#availablePeriods").append("<option value='"+ id +"'>" + periodName + "</option>");
	}
	
	/*
	var ouId = document.getElementById('ouIDTB').value;
	var reportType = document.reportForm.reportTypeNameTB.value;

	if( ouId != null && ouId != "" )
	{
		getReports( ouId, reportType );
	}
	*/
	
}



// ----------------------------------------------------------------------
// Get Reports
// ----------------------------------------------------------------------

function getPBFReports( orgUnitIds, reportTypeName ) 
{
	//alert( reportTypeName );
	document.getElementById("ouNameTB").value = "";

	
	if ( orgUnitIds != null && reportTypeName != "" ) 
	{
		document.generateReportForm.generate.disabled=false;
		
		$.post("getPBFReportList.action",
			{
				orgUnitId : orgUnitIds[0],
				reportType : reportTypeName
			},
			function (data)
			{
				getReportsReceived(data);
			},'xml');
	} 
	else 
	{
		document.generateReportForm.generate.disabled=true;
		
		clearList( selectedReportId );
	}


}

function getReportsReceived( xmlObject ) 
{
	var selectedReportId = document.getElementById("selectedReportId");
	var orgUnitName = document.getElementById("ouNameTB");
	var orgUnitUidValue = document.getElementById("orgUnitUid");
	
	var ouLavel = "";
	
	clearList(selectedReportId);

	var reports = xmlObject.getElementsByTagName("report");
	for ( var i = 0; i < reports.length; i++) 
	{
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		var reportDesignFileName = reports[i].getElementsByTagName("reportDesignFileName")[0].firstChild.nodeValue;
		var reportPeriodTypeName = reports[i].getElementsByTagName("reportPeriodTypeName")[0].firstChild.nodeValue;
		var reportOrgUnitGroupId = reports[i].getElementsByTagName("reportOrgUnitGroupId")[0].firstChild.nodeValue;
		
		var reportValue = reportDesignFileName + ":" + reportPeriodTypeName + ":" + reportOrgUnitGroupId;
		
		var ouName = reports[i].getElementsByTagName("ouName")[0].firstChild.nodeValue;
		var orgUnitUid = reports[i].getElementsByTagName("orgUnitUid")[0].firstChild.nodeValue;
		
		ouLavel = reports[i].getElementsByTagName("ouLavel")[0].firstChild.nodeValue;
		
		orgUnitName.value = ouName;
		orgUnitUidValue.value = orgUnitUid;
		
		$("#selectedReportId").append("<option value='"+ reportValue +"'>" + name + "</option>");
		
		//$("#reportList").append("<option value='"+ id +"'>" + name + "</option>");
	}
	
	document.generateReportForm.generate.disabled=false;
	
	getReportPeriods()
	
	//alert( ouLavel );
}



//----------------------------------------------------------------------
//Get Periods
//----------------------------------------------------------------------

function getReportPeriods() 
{
	var selectedList = document.getElementById( 'selectedPeriodId' );
	
	var selectedReportValue = $('#selectedReportId').val();
    
	if( selectedReportValue != null )
	{
		var reportPeriodTypeName = selectedReportValue.split(":")[1];
		
		if( document.getElementById("reportPeriodTypeName").value == reportPeriodTypeName )
		{
			return;
		}
		document.getElementById("reportPeriodTypeName").value = reportPeriodTypeName;
		
		clearList( selectedList );
		
		//alert( reportPeriodTypeName );
		
		if ( reportPeriodTypeName != "" ) 
		{
			addOptionToList( selectedList, '-1', '[ Select ]' );
			
			$.getJSON( "getPBFPeriods.action", {
				"reportPeriodTypeName":reportPeriodTypeName},
				function( json ) {
					
					for ( i in json.periods ) {
			    		addOptionToList( selectedList, json.periods[i].externalId, json.periods[i].name );
			    	}
					
				} );
			
			enable('prevButton');
			enable('nextButton');
			
		} 
		else 
		{
			document.reportForm.generate.disabled=true;
		}
	}
	

}


/*

function getPBFPeriodsReceived(xmlObject) 
{
	var selectedPeriodId = document.getElementById("selectedPeriodId");

	clearList( selectedPeriodId );

	var periods = xmlObject.getElementsByTagName("period");
	
	if( periods.length > 0 )
	{
	     document.reportForm.generate.disabled=false;
	}

	for ( var i = 0; i < periods.length; i++) 
	{
		var id = periods[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var periodName = periods[i].getElementsByTagName("periodname")[0].firstChild.nodeValue;

		$("#selectedPeriodId").append("<option value='"+ id +"'>" + periodName + "</option>");
	}
	
	
	var ouId = document.getElementById('ouIDTB').value;
	var reportType = document.reportForm.reportTypeNameTB.value;

	if( ouId != null && ouId != "" )
	{
		getReports( ouId, reportType );
	}
	
}

*/

//next and pre periods

function getPBFAvailablePeriodsTemp( selectedPeriodsId, year )
{	
	var selectedList = document.getElementById( selectedPeriodsId );
	
	var selectedReportValue = $('#selectedReportId').val();
	
	if( selectedReportValue != null )
	{
		var reportPeriodTypeName = selectedReportValue.split(":")[1];
		
		clearList( selectedList );
		
		addOptionToList( selectedList, '-1', '[ Select ]' );
		
		$.getJSON( "getAvailableNextPrePeriods.action", {
			"year": year,
			"periodType":reportPeriodTypeName},
			function( json ) {
				
				for ( i in json.periods ) {
		    		addOptionToList( selectedList, json.periods[i].externalId, json.periods[i].name );
		    	}
				
			} );
	
	}
}
