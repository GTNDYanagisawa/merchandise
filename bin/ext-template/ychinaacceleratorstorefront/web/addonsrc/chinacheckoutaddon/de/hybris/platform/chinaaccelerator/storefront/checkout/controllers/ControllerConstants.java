/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.chinaaccelerator.storefront.checkout.controllers;



public interface ControllerConstants
{
	final String ADDON_PREFIX = "addon:/chinacheckoutaddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{

			interface MultiStepCheckout
			{
				String AddEditDeliveryAddressPage = ADDON_PREFIX + "pages/checkout/multi/addEditDeliveryAddressPage";
				String ChooseDeliveryMethodPage = ADDON_PREFIX + "pages/checkout/multi/chooseDeliveryMethodPage";
				String ChoosePickupLocationPage = ADDON_PREFIX + "pages/checkout/multi/choosePickupLocationPage";
				String AddPaymentMethodPage = ADDON_PREFIX + "pages/checkout/multi/addPaymentMethodPage";
				String CheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/checkoutSummaryPage";
				String HostedOrderPageErrorPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPageErrorPage";
				String HostedOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPostPage";
				String SilentOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/silentOrderPostPage";

				// CHINAACC_START
				String Step2Page = ADDON_PREFIX + "pages/checkout/multi/step2Page";
				//				String CheckoutLoginPage = "pages/checkout/checkoutLoginPage";
				//				String CheckoutLoginPage = "pages/checkout/multi/checkoutLoginPage";

				// CHINAACC_END
			}

			//			interface Checkout
			//			{
			//				String CheckoutRegisterPage = "pages/checkout/checkoutRegisterPage";
			//				String CheckoutConfirmationPage = "pages/checkout/checkoutConfirmationPage";
			//				String CheckoutSubmittedPage = "pages/checkout/submittedPage";
			//				String CheckoutOrderSucceed = "pages/checkout/ordersucceedPage";
			//				String CheckoutorderFailed = "pages/checkout/orderfailedPage";
			//				String CheckoutLoginPage = "pages/checkout/checkoutLoginPage";
			//			}



			// CHINAACC_START
			/*
			 * interface Account { String AccountLoginPage = "pages/account/accountLoginPage"; String AccountHomePage =
			 * "pages/account/accountHomePage"; String AccountOrderHistoryPage = "pages/account/accountOrderHistoryPage";
			 * String AccountOrderPage = "pages/account/accountOrderPage"; String AccountProfilePage =
			 * "pages/account/accountProfilePage"; String AccountProfileEditPage = "pages/account/accountProfileEditPage";
			 * String AccountProfileEmailEditPage = "pages/account/accountProfileEmailEditPage"; String
			 * AccountChangePasswordPage = "pages/account/accountChangePasswordPage"; String AccountAddressBookPage =
			 * "pages/account/accountAddressBookPage"; String AccountEditAddressPage =
			 * "pages/account/accountEditAddressPage"; String AccountPaymentInfoPage =
			 * "pages/account/accountPaymentInfoPage"; String AccountRegisterPage = "pages/account/accountRegisterPage"; //
			 * CHINAACC_START String AccountChinaAddressBookPage = "pages/account/accountChinaAddressBookPage"; String
			 * AccountChinaEditAddressPage = "pages/account/accountChinaEditAddressPage"; // CHINAACC_END } // CHINAACC_END
			 */
		}

		interface Fragments
		{

			interface Checkout
			{
				String TermsAndConditionsPopup = ADDON_PREFIX + "fragments/checkout/termsAndConditionsPopup";
				String BillingAddressForm = ADDON_PREFIX + "fragments/checkout/billingAddressForm";
			}

		}
	}

}
