ACC.product = {

	enableAddToCartButton: function ()
	{
		$('.js-add-to-cart').removeAttr("disabled");
	},
	
	enableVariantSelectors: function ()
	{
		$('.variant-select').removeAttr("disabled");
	},
	
	bindToAddToCartForm: function ()
	{
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},

	bindToAddToCartStorePickUpForm: function ()
	{
		var addToCartStorePickUpForm = $('#colorbox #add_to_cart_storepickup_form');
		addToCartStorePickUpForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},


	enableStorePickupButton: function ()
	{
		$('.js-pickup-in-store-button').removeAttr("disabled");
	},

	displayAddToCartPopup: function (cartResult, statusText, xhr, formElement)
	{
		$('#addToCartLayer').remove();

		if (typeof ACC.minicart.updateMiniCartDisplay == 'function')
		{
			ACC.minicart.updateMiniCartDisplay();
		}
		var titleHeader = $('#addToCartTitle > .add-to-cart-header').clone();
		$.colorbox({
			href: "",
			inline:true,
			maxWidth:"100%",
			opacity:0.7,
			width:"320px",
			close:'<span class="glyphicon glyphicon-remove"></span>',
			title: titleHeader,
			onComplete: function(){
				$('#cboxLoadedContent').append(cartResult.addToCartLayer)
				$.colorbox.resize();
			}
		});


		var productCode = $('[name=productCodePost]', formElement).val();
		var quantityField = $('[name=qty]', formElement).val();

		var quantity = 1;
		if (quantityField != undefined)
		{
			quantity = quantityField;
		}

		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);

	}

};

$(document).ready(function ()
{
	with(ACC.product)
	{
		bindToAddToCartForm();
		//bindToAddToCartStorePickUpForm();
		enableStorePickupButton();
		enableAddToCartButton();
		enableVariantSelectors();
	}
});

