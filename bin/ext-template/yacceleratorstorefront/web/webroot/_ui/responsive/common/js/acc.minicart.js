ACC.minicart = {

	bindMiniCart: function(){

		$(document).on("click",".js-mini-cart-link", function(e){
			e.preventDefault();
			var url = $(this).data("miniCartUrl");	
			var cartName = ($(this).find(".js-mini-cart-count").html() != 0) ? $(this).data("miniCartName"):$(this).data("miniCartEmptyName");
	
			$.colorbox({
				href: url,
				maxWidth:"100%",
				opacity:0.7,
				width:"320px",
				close:'<span class="glyphicon glyphicon-remove"></span>',
				title:'<div class="headline"><span class="headline-text">' + cartName + '</span></div>',
				onComplete: function(){
					$.colorbox.resize()
				}
			});
		})

		$(document).on("click",".js-mini-cart-close-button", function(e){
			e.preventDefault();
			$.colorbox.close();
		})
	},

	updateMiniCartDisplay: function(){
		var miniCartRefreshUrl = $(".js-mini-cart-link").data("miniCartRefreshUrl");
		$.get(miniCartRefreshUrl,function(data){
			var data = $.parseJSON(data);
			$(".js-mini-cart-link .js-mini-cart-count").html(data.miniCartCount)
			$(".js-mini-cart-link .js-mini-cart-price").html(data.miniCartPrice)
		})
	}

};


$(function(){
	with(ACC.minicart){
		bindMiniCart();
	}
});

