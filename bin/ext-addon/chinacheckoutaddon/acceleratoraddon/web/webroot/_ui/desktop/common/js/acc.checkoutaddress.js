
ACC.checkoutaddress = {

	spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),
	addressID: '',

	showAddressBook: function ()
	{
		$(document).on("click", "#viewAddressBook", function ()
		{
			var data = $("#savedAddressListHolder").html();
			$.colorbox({

				height: false,
				html: data,
				onComplete: function ()
				{

					$(this).colorbox.resize();
				}
			});

		})
	},

	showRemoveAddressConfirmation: function ()
	{
		$(document).on("click", ".removeAddressButton", function ()
		{
			var addressId = $(this).data("addressId");
			var c_txt = $("#confirm").val();
			if(!window.confirm(c_txt)){
				return;
			}
			window.location.href="remove-address/"+addressId;
		})
	}
}

// Address Verification
$(document).ready(function ()
{
	with (ACC.checkoutaddress)
	{

		showAddressBook();
		showRemoveAddressConfirmation();
	}
});


