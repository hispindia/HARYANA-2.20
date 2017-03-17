

//next and pre periods
function getAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	var availableList = document.getElementById( availablePeriodsId );
	var selectedList = document.getElementById( selectedPeriodsId );
	var periodType = availablePeriodsId;
	
	clearList( selectedList );
	
	addOptionToList( selectedList, '-1', '[ Select ]' );
	
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"year": year,
		"periodType":periodType},
		function( json ) {
			
			for ( i in json.periods ) {
	    		addOptionToList( selectedList, json.periods[i].externalId, json.periods[i].name );
	    	}
			
		} );
}