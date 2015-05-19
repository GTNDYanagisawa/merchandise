$(function() {
	$('#loggers').dataTable({
		"iDisplayLength" : 50
	});
	
	$('.loggerLevels').live('change', function() {
		var url = $('#loggers').attr('data-changeLoggerLevelUrl');
		var loggerName = $(this).attr('data-loggerName');
		var levelName = $(this).val();
		debug.log("loggerName -> " + loggerName);
		debug.log("levelName -> " + levelName);
        var token = $("meta[name='_csrf']").attr("content");

        $.ajax({
			url : url,
			type : 'POST',
			data: 'loggerName=' + loggerName + "&levelName=" + levelName,
			headers : {
				'Accept' : 'application/json',
                'X-CSRF-TOKEN' : token
			},
			success : function(data) {
				if (data.changingResult) {
					hac.global.notify("Log level for logger " + data.loggerName + " changed to " + data.levelName);
					$.each($('.loggerLevels'), function(index, select) {
						var currentLoggerName = $(select).attr('data-loggerName');
						var currentLevel = data.loggers[currentLoggerName];
						$(select).val(currentLevel);
					});
				} else {
					hac.global.notify("Failed to change level for logger " + data.loggerName);
				}
			},
			error : hac.global.err
		});
	});
})