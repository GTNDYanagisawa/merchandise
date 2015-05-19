ACC.checkoutsteps = {
		
		
		
		permeateLinks: function() {
		
			$(document).on("click",".js-checkout-step",function(e){
				e.preventDefault();
				window.location=$(this).closest("a").attr("href")
			})		
		}


}

$(document).ready(function ()
		{
			with (ACC.checkoutsteps)
			{
				permeateLinks();
			}
		});