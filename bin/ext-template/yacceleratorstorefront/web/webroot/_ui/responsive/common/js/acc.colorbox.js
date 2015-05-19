ACC.colorbox = {
	config: {
		maxWidth:"100%",
		opacity:0.7,
		width:"auto",
		close:'<span class="glyphicon glyphicon-remove"></span>',
		title:'<div class="headline"><span class="headline-text">{title}</span></div>'
	},

	open: function(title,config){
		var config = $.extend(ACC.colorbox.config,config);
		config.title = config.title.replace(/{title}/g,title);
		return $.colorbox(config);
	},

	resize: function(){
		$.colorbox.resize();
	}
};


