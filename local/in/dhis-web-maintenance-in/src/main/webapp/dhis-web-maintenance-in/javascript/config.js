
function clearFolder( folderId )
{
	/*
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( clearFolderRecieved );
	  
	var requestString = "clearFolder.action";
	var params = 'selectedButton=' + folderId;	
	
	request.sendAsPost( params );
	request.send( requestString );
	*/
	$.post("clearFolder.action",
			{
				selectedButton : folderId
			},
			function (data)
			{
				clearFolderRecieved(data);
			},'xml');
	
}

function clearFolderRecieved( messageElement )
{
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	
	//var message = messageElement.firstChild.nodeValue;
	//alert( messageElement );
	//alert( messageElement.firstChild.nodeValue );
    document.getElementById( 'message' ).innerHTML = messageElement.firstChild.nodeValue;
    document.getElementById( 'message' ).style.display = 'block';
}


function downloadFolder( folderId )
{
	window.location.href="clearFolder.action?selectedButton="+folderId;
}