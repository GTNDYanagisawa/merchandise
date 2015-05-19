





//-----------------------------------  DOCUMENT READY  -----------------------------------
$(document).ready(function() {

	var divs = $('.acc_summary .results.grid .col');
	for(var i = 0; i < divs.length; i+=3) {
		divs.slice(i, i+3).wrapAll("<div class='row'></div>");
	}
	//var h = $('.acc_summary .results.grid .row').height();
	var h = $('.acc_summary .results.grid .col .due_date').outerHeight();
		
	
	
	
});

//-----------------------------------  DOCUMENT PRINT  -----------------------------------
function printPage()
{
	window.print();
}


	
				
