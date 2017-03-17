
/* **************************************************************************************
	Some common needed funtions
   ************************************************************************************ */
   
   	function isNull(inputId)
	{
		if($('#'+inputId).val()=="")
			return true;
		else
			return false;
	}
	
	function hasMinCharacters(inputId,numOfMinChars)
	{
		if($('#'+inputId).val().length >= numOfMinChars)
			return true;
		else
			return false;
	}
	
	/*
	isUrlValid(inputId)
	{
		var url = $("#"+inputId).val();
		var pattern = new RegExp('^(https?:\/\/)?'+ // protocol
					'((([a-z\d]([a-z\d-]*[a-z\d])*)\.)+[a-z]{2,}|'+ // domain name
					'((\d{1,3}\.){3}\d{1,3}))'+ // OR ip (v4) address
					'(\:\d+)?(\/[-a-z\d%_.~+]*)*'+ // port and path
					'(\?[;&a-z\d%_.~+=-]*)?'+ // query string
					'(\#[-a-z\d_]*)?$','i'); // fragment locater
			
		if(!pattern.test(url)) 
			return false;
		else 
			return true;
	}
	*/
	
/* **************************************************************************************
	Validating Add Instance Form
   ************************************************************************************ */
   
	function validateAddSectionForm()
	{
		if(isNull("name"))
		{
			$("#alertMsg").html("Section name is empty");
			$("#name").focus();
			return false;
		}
        /*
		else if(!hasMinCharacters("name",3))
		{
			$("#alertMsg").html("Section name should be at least of 3 characters");
			$("#title").focus();
			return false;
		}
		*/

		else if(isNull("userGroupUid"))
		{
			$("#alertMsg").html("User Group is empty");
			$("#userGroupUid").focus();
			return false;
		}
		else
		{
			$("#addSectionModal").modal("hide");
			return true;
		}
	}
	
/* **************************************************************************************
	Validating Edit Section Form
   ************************************************************************************ */
   
	function validateEditSectionForm()
	{
		if(isNull("eName"))
		{
			$("#editAlertMsg").html("Section name is empty");
			$("#eName").focus();
			return false;
		}
        /*
		else if(!hasMinCharacters("eName",3))
		{
			$("#editAlertMsg").html("Section name should be at least of 3 characters");
			$("#eName").focus();
			return false;
		}
		*/

        else if(isNull("eUserGroupUid"))
        {
            $("#editAlertMsg").html("User Group is empty");
            $("#eUserGroupUid").focus();
            return false;
        }

		else
		{
			$("#editSectionModal").modal("hide");
			return true;
		}
	}



/* **************************************************************************************
 Validating configuration Form
 ************************************************************************************ */

function validateConfigurationForm()
{
    if(isNull("userGroup"))
    {
        alert("Please select user group");
        $("#userGroup").focus();
        return false;
    }


    else if(isNull("reportLogo"))
    {
        alert("Please select report logo");
        $("#reportLogo").focus();
        return false;
    }
    else if(isNull("dataSetStatusReport"))
    {
        alert("Please select data set Status Report");
        $("#dataSetStatusReport").focus();
        return false;
    }

    else
    {
        return true;
    }
}