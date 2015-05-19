$(document).ready(function(){



	$(".js-gallery").each(function(){
		var $image = $(this).find(".js-gallery-image");
		var $carousel = $(this).find(".js-gallery-carousel")

		console.log($image,$carousel)


		$image.owlCarousel({
			singleItem : true,
            lazyLoad : true,
			// slideSpeed : 1000,
			// navigation: true,
			 pagination:true,
			//afterAction : syncPosition,
			//responsiveRefreshRate : 200,
			navigation:true,
			navigationText : ["<span class='glyphicon glyphicon-chevron-left'></span>", "<span class='glyphicon glyphicon-chevron-right'></span>"],
			afterAction : function(){
				syncPosition($image,$carousel,this.currentItem)
			},
		});


		$carousel.owlCarousel({
			navigation:true,
			navigationText : ["<span class='glyphicon glyphicon-chevron-left'></span>", "<span class='glyphicon glyphicon-chevron-right'></span>"],
			pagination:false,
			items:2,
			itemsDesktop : [5000,7], 
			itemsDesktopSmall : [1200,5], 
			itemsTablet: [768,4], 
			itemsMobile : [480,3], 
			afterAction : function(){
				console.log("action")
			},
		});


		$carousel.on("click","a.item",function(e){
			e.preventDefault();
	
			$image.trigger("owl.goTo",$(this).parent(".owl-item").data("owlItem"));
		})




		// $image.find(".item img").elevateZoom({ 
		// 	zoomType : "inner", 
		// 	cursor: "crosshair",
		// 	scrollZoom:false,
		// 	responsive:true,
		// }); 



	})







})


function syncPosition($image,$carousel,currentItem){
	$carousel.trigger("owl.goTo",currentItem);
}