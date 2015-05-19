
var host; 

$(document).ready(function() {

	$.ajax({
		url : $('#dashboards').attr('data-url'),
		type : 'GET',
		headers : {
			'Accept' : 'application/json'
		},
		success : function(data) {
			debug.log(data);
			
			$('#waiting').remove();
			
			if (!data.error)
			{
				host = data.host;
				for (name in data.dashboards)
				{
					var board = data.dashboards[name];
					$('#dashboards').append(htmlForDashboard(name, board.href, board.jnlp, board.desc));
				}
			}
			else
				hac.global.error("Unable to connect to the dynaTrace server...");
		},
		error : hac.global.err
	});
	
});

function htmlForDashboard(name, href, jnlp, desc)
{
	var html = '';
	html += '<div class="dashboard">' 
	html += '<h4><a class="dark" href="'+jnlp+'">' + name + '</a>'; 
	html += '</h4>';
	if (desc && desc.length > 0)
	{	
		html += '<img style="float:left; margin-right: 10px;" src="'+localIcon(name)+'"/>';
		html += '<p class="desc">'+desc+'</p>';
		
	}
	html += '</div>'
		
	return html;
}

function localIcon(name)
{
	name = name.replace(/ /g, "_");
	return $('#dashboards').attr('data-base') + name + ".png";
}