ACC.global = {

	bindToggleOffcanvas: function(){
		$(document).on("click",".js-toggle-sm-navigation",function(){
			ACC.global.toggleClassState($("main"),"offcanvas");
		});
	},

	bindToggleXsSearch: function(){
		$(document).on("click",".js-toggle-xs-search",function(){
			ACC.global.toggleClassState($(".site-search"),"active");
		});
	},

	bindToggleHeaderLinks: function(){
		$(document).on("click",".js-toggle-header-links",function(){
			var $e = $(".md-secondary-navigation");
			ACC.global.toggleClassState($e,"active")? $e.slideDown(300): $e.slideUp(300);
		})
	},

	toggleClassState: function($e,c){
		$e.hasClass(c)? $e.removeClass(c): $e.addClass(c);
		return $e.hasClass(c);
	},

	bindHoverIntentMainNavigation: function(){
		$("nav.main-navigation > ul > li").hoverIntent(function(){
			$(this).addClass("md-show-sub")
		},function(){
			$(this).removeClass("md-show-sub")
		})
	}


};


$(function(){

	with(ACC.global){
		bindToggleOffcanvas();
		bindToggleXsSearch();
		bindToggleHeaderLinks();
		bindHoverIntentMainNavigation();
	}
	
});