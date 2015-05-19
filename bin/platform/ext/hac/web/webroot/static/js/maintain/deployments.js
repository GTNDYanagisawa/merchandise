$(function() {

	
	$("#tabs").tabs({
		select : function(event, ui) {
			toggleActiveSidebar(ui.index + 1);
		}
	});

	$('#typesWith').dataTable();
	$('#typesWithout').dataTable();
	$('#deplWithout').dataTable();
	
	hac.global.notify('First free deployment typecode is: ' + $('#content').attr('data-firstFree'));

});

function toggleActiveSidebar(num) {
	// fadeout
	$("div[id^=sidebar]").each(function() {
		$(this).fadeOut('fast');
	});

	setTimeout("$('#sidebar" + num + "').fadeIn('fast');", 500);
}


