var mediaNotHybrisTable, mediaHybrisTable;

$(document).ready(function() {

	$("#tabs").tabs({
		select : function(event, ui) {
			hac.global.toggleActiveSidebar(ui.index + 1);
		}
	});
	loadData();
	

	$('#buttonLDAPCheck').click(function() {
		$('#checkResult').fadeOut();
		var url = $('#buttonLDAPCheck').attr('data-url');
		$.ajax({
			url : url,
			type : 'POST',
			headers : {
				'Accept' : 'application/json'
			},
			data : {
				username : $('#username').val(),
				password : $('#password').val()
			},
			success : function(data) {
				debug.log(data);

				if (data.success)
					hac.global.notify("LDAP Login successful!");
				else
					hac.global.error(data.message);

			},
			error : hac.global.err
		});
	});

	$('#buttonImpex').click(function() {
		$('#impexResult').fadeOut();
		var url = $('#buttonImpex').attr('data-url');
		$.ajax({
			url : url,
			type : 'POST',
			headers : {
				'Accept' : 'application/json'
			},
			data : {
				ldif : $('#ldifFile').val(),
				conf : $('#confFile').val()
			},
			success : function(data) {
				debug.log(data);

				if (data.success) {
					// show data.impex
					$('#impex').html(data.impex);
					$('#impexResult').fadeIn();
				} else
					hac.global.error(data.message);

			},
			error : hac.global.err
		});
	});

	$('#buttonSearch').click(function() {
		$('#searchResult').fadeOut();
		var url = $('#buttonSearch').attr('data-url');
		$.ajax({
			url : url,
			type : 'POST',
			headers : {
				'Accept' : 'application/json'
			},
			data : {
				searchBase : $('#searchBase').val(),
				searchFilter : $('#searchFilter').val(),
				searchSelect : $('#searchSelect').val(),
				searchLimit : $('#searchLimit').val()
			},
			success : function(data) {
				debug.log(data);

				if (data.success) {
					// show data.impex
					$('#search').html(data.result);
					$('#searchResult').fadeIn();
				} else
					hac.global.error(data.message);

			},
			error : hac.global.err
		});
	});	

});

function loadData() {
	var url = $('#tabs-1').attr('data-loadUrl');
	$
			.ajax({
				url : url,
				type : 'GET',
				headers : {
					'Accept' : 'application/json'
				},
				success : function(data) {
					debug.log(data);

					if (data.serverAvailable) {
						// tab 1
						$('#serverAvailable').html(
								data.serverAvailable.toString());

						// tab 2
						$('#confFile').val(data.confFile);
						$('#ldifFile').val(data.ldifFile);

					} else
						hac.global
								.error("LDAP is not configured or not configured properly!");

				},
				error : hac.global.err
			});

}
