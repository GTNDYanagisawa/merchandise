var updateIntervalId;

$(document).ready(function() {
    var token = $("meta[name='_csrf']").attr("content");
    updateSessionInfo();
	setInterval("updateSessionInfo()", 2000)

	$('#closeSessions').click(function() {
		initiateClose(token, false);
	});

	$('#closeSessionsAndHttp').click(function() {
		initiateClose(token, true);
	});

});

function initiateClose(token, shutDownHttpSession) {
	shutDownHttpSession = shutDownHttpSession || false;

	var url = $('#sessionsContainter').attr('data-closeSessionsUrl');
	$.ajax({
		url : url,
		type : 'POST',
		data : 'shutDownHttpSession=' + shutDownHttpSession,
		headers : {
			'Accept' : 'application/json',
            'X-CSRF-TOKEN' : token
		},
		success : function(data) {
		debug.log(data);
		updateData(data);

		if (data['closedSessions']) {
			var msg = data.closedSessions + " sessions were closed.";

			if (shutDownHttpSession) {
				msg += " As you included http, you will be redirected to the login screen.";
				hac.global.notify(msg, 1000, function() {
					location.href = hac.contextPath;
				});
			} else {
				hac.global.notify(msg);
			}
		}

		},
		error : hac.global.err
	});
}

function updateData(data) {
	$('#sessionCheckInterval').html(data.sessionCheckInterval + ' sec');
	$('#sessionTimeout').html(data.sessionTimeout + ' sec');
	$('#sessionsInstantiated').html(data.sessionsInstantiated);
	$('#sessionsExpired').html(data.sessionsExpired);

}

function updateSessionInfo() {
    var token = $("meta[name='_csrf']").attr("content");

    var url = $('#sessionsContainter').attr('data-updateSessionUrl');
	$.ajax({
		url : url,
		type : 'GET',
		headers : {
			'Accept' : 'application/json',
            'X-CSRF-TOKEN' : token
		},
		success : function(data) {
			debug.log(data);
			updateData(data);
		},
		error : hac.global.err
	});
}