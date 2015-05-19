/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.yb2bacceleratorstorefront.controllers;

import de.hybris.platform.acceleratorcms.model.components.CategoryFeatureComponentModel;
import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarComponentModel;
import de.hybris.platform.acceleratorcms.model.components.ProductFeatureComponentModel;
import de.hybris.platform.acceleratorcms.model.components.ProductReferencesComponentModel;
import de.hybris.platform.acceleratorcms.model.components.PurchasedCategorySuggestionComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;


/**
 * Class with constants for controllers.
 */
public interface ControllerConstants
{
	/**
	 * Class with action name constants
	 */
	interface Actions
	{
		interface Cms
		{
			String _Prefix = "/view/";
			String _Suffix = "Controller";

			/**
			 * Default CMS component controller
			 */
			String DefaultCMSComponent = _Prefix + "DefaultCMSComponentController";

			/**
			 * CMS components that have specific handlers
			 */
			String PurchasedCategorySuggestionComponent = _Prefix + PurchasedCategorySuggestionComponentModel._TYPECODE + _Suffix;
			String ProductReferencesComponent = _Prefix + ProductReferencesComponentModel._TYPECODE + _Suffix;
			String ProductCarouselComponent = _Prefix + ProductCarouselComponentModel._TYPECODE + _Suffix;
			String MiniCartComponent = _Prefix + MiniCartComponentModel._TYPECODE + _Suffix;
			String ProductFeatureComponent = _Prefix + ProductFeatureComponentModel._TYPECODE + _Suffix;
			String CategoryFeatureComponent = _Prefix + CategoryFeatureComponentModel._TYPECODE + _Suffix;
			String NavigationBarComponent = _Prefix + NavigationBarComponentModel._TYPECODE + _Suffix;
			String CMSLinkComponent = _Prefix + CMSLinkComponentModel._TYPECODE + _Suffix;
		}
	}

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Cms
		{
			String ComponentPrefix = "cms/";
		}

		interface Pages
		{
			interface Account
			{
				String AccountLoginPage = "pages/account/accountLoginPage";
				String AccountHomePage = "pages/account/accountHomePage";
				String AccountOrderHistoryPage = "pages/account/accountOrderHistoryPage";
				String AccountOrderPage = "pages/account/accountOrderPage";
				String AccountProfilePage = "pages/account/accountProfilePage";
				String AccountProfileEditPage = "pages/account/accountProfileEditPage";
				String AccountProfileEmailEditPage = "pages/account/accountProfileEmailEditPage";
				String AccountChangePasswordPage = "pages/account/accountChangePasswordPage";
				String AccountAddressBookPage = "pages/account/accountAddressBookPage";
				String AccountEditAddressPage = "pages/account/accountEditAddressPage";
				String AccountPaymentInfoPage = "pages/account/accountPaymentInfoPage";
				String AccountMyQuotesPage = "pages/account/accountMyQuotesPage";
				String AccountReplenishmentSchedule = "pages/account/accountReplenishmentSchedule";
				String AccountReplenishmentScheduleDetails = "pages/account/accountReplenishmentScheduleDetails";
				String AccountOrderApprovalDashboardPage = "pages/account/accountOrderApprovalDashboardPage";
				String AccountOrderApprovalDetailsPage = "pages/account/accountOrderApprovalDetailsPage";
				String AccountQuoteDetailPage = "pages/account/accountQuoteDetailPage";
				String AccountCancelActionConfirmationPage = "pages/account/accountCancelActionConfirmationPage";
			}

			interface Checkout
			{
				String CheckoutLoginPage = "pages/checkout/checkoutLoginPage";
				String CheckoutConfirmationPage = "pages/checkout/checkoutConfirmationPage";
				String QuoteCheckoutConfirmationPage = "pages/checkout/quoteCheckoutConfirmationPage";
				String CheckoutReplenishmentConfirmationPage = "pages/checkout/checkoutReplenishmentConfirmationPage";
			}

			interface SingleStepCheckout
			{
				String CheckoutSummaryPage = "pages/checkout/single/checkoutSummaryPage";
			}

			interface MultiStepCheckout
			{
				String CheckoutSampleLandingPage = "pages/checkout/multi/checkoutSampleLandingPage";
			}

			interface Password
			{
				String PasswordResetChangePage = "pages/password/passwordResetChangePage";
			}

			interface Error
			{
				String ErrorNotFoundPage = "pages/error/errorNotFoundPage";
			}

			interface Cart
			{
				String CartPage = "pages/cart/cartPage";
			}

			interface StoreFinder
			{
				String StoreFinderSearchPage = "pages/storeFinder/storeFinderSearchPage";
				String StoreFinderDetailsPage = "pages/storeFinder/storeFinderDetailsPage";
			}

			interface Misc
			{
				String MiscRobotsPage = "pages/misc/miscRobotsPage";
			}

			interface MyCompany
			{
				String MyCompanyLoginPage = "pages/company/myCompanyLoginPage";
				String MyCompanyManageUnitsPage = "pages/company/myCompanyManageUnitsPage";
				String MyCompanyManageUnitEditPage = "pages/company/myCompanyManageUnitEditPage";
				String MyCompanyManageUnitDetailsPage = "pages/company/myCompanyManageUnitDetailsPage";
				String MyCompanyManageUnitCreatePage = "pages/company/myCompanyManageUnitCreatePage";
				String MyCompanyManageBudgetsPage = "pages/company/myCompanyManageBudgetsPage";
				String MyCompanyManageBudgetsViewPage = "pages/company/myCompanyManageBudgetsViewPage";
				String MyCompanyManageBudgetsEditPage = "pages/company/myCompanyManageBudgetsEditPage";
				String MyCompanyManageBudgetsAddPage = "pages/company/myCompanyManageBudgetsAddPage";
				String MyCompanyManageCostCentersPage = "pages/company/myCompanyManageCostCentersPage";
				String MyCompanyCostCenterViewPage = "pages/company/myCompanyCostCenterViewPage";
				String MyCompanyCostCenterEditPage = "pages/company/myCompanyCostCenterEditPage";
				String MyCompanyAddCostCenterPage = "pages/company/myCompanyAddCostCenterPage";
				String MyCompanyManagePermissionsPage = "pages/company/myCompanyManagePermissionsPage";
				String MyCompanyManageUnitUserListPage = "pages/company/myCompanyManageUnitUserListPage";
				String MyCompanyManageUnitApproverListPage = "pages/company/myCompanyManageUnitApproversListPage";
				String MyCompanyManageUserDetailPage = "pages/company/myCompanyManageUserDetailPage";
				String MyCompanyManageUserAddEditFormPage = "pages/company/myCompanyManageUserAddEditFormPage";
				String MyCompanyManageUsersPage = "pages/company/myCompanyManageUsersPage";
				String MyCompanyManageUserDisbaleConfirmPage = "pages/company/myCompanyManageUserDisableConfirmPage";
				String MyCompanyManageUnitDisablePage = "pages/company/myCompanyManageUnitDisablePage";
				String MyCompanySelectBudgetPage = "pages/company/myCompanySelectBudgetsPage";
				String MyCompanyCostCenterDisableConfirm = "pages/company/myCompanyDisableCostCenterConfirmPage";
				String MyCompanyManageUnitAddAddressPage = "pages/company/myCompanyManageUnitAddAddressPage";
				String MyCompanyManageUserPermissionsPage = "pages/company/myCompanyManageUserPermissionsPage";
				String MyCompanyManageUserResetPasswordPage = "pages/company/myCompanyManageUserPassword";
				String MyCompanyBudgetDisableConfirm = "pages/company/myCompanyDisableBudgetConfirmPage";
				String MyCompanyManageUserGroupsPage = "pages/company/myCompanyManageUserGroupsPage";
				String MyCompanyManageUsergroupViewPage = "pages/company/myCompanyManageUsergroupViewPage";
				String MyCompanyManageUsergroupEditPage = "pages/company/myCompanyManageUsergroupEditPage";
				String MyCompanyManageUsergroupCreatePage = "pages/company/myCompanyManageUsergroupCreatePage";
				String MyCompanyManageUsergroupDisableConfirmationPage = "pages/company/myCompanyManageUsergroupDisableConfirmationPage";
				String MyCompanyManagePermissionDisablePage = "pages/company/myCompanyManagePermissionDisablePage";
				String MyCompanyManagePermissionsViewPage = "pages/company/myCompanyManagePermissionsViewPage";
				String MyCompanyManagePermissionsEditPage = "pages/company/myCompanyManagePermissionsEditPage";
				String MyCompanyManagePermissionTypeSelectPage = "pages/company/myCompanyManagePermissionTypeSelectPage";
				String MyCompanyManagePermissionAddPage = "pages/company/myCompanyManagePermissionAddPage";
				String MyCompanyManageUserCustomersPage = "pages/company/myCompanyManageUserCustomersPage";
				String MyCompanyManageUserGroupPermissionsPage = "pages/company/myCompanyManageUserGroupPermissionsPage";
				String MyCompanyManageUserGroupMembersPage = "pages/company/myCompanyManageUserGroupMembersPage";
				String MyCompanyRemoveDisableConfirmationPage = "pages/company/myCompanyRemoveDisableConfirmationPage";
				String MyCompanyManageUserB2BUserGroupsPage = "pages/company/myCompanyManageUserB2BUserGroupsPage";
				String MyCompanyManageUsergroupRemoveConfirmationPage = "pages/company/myCompanyManageUsergroupRemoveConfirmationPage";
			}

			interface Product
			{
				String OrderForm = "pages/product/productOrderFormPage";
			}
		}

		interface Fragments
		{
			interface Cart
			{
				String AddToCartPopup = "fragments/cart/addToCartPopup";
				String MiniCartPanel = "fragments/cart/miniCartPanel";
				String MiniCartErrorPanel = "fragments/cart/miniCartErrorPanel";
				String CartPopup = "fragments/cart/cartPopup";
				String ExpandGridInCart = "fragments/cart/expandGridInCart";
			}

			interface Checkout
			{
				String TermsAndConditionsPopup = "fragments/checkout/termsAndConditionsPopup";
			}

			interface SingleStepCheckout
			{
				String DeliveryAddressFormPopup = "fragments/checkout/single/deliveryAddressFormPopup";
				String PaymentDetailsFormPopup = "fragments/checkout/single/paymentDetailsFormPopup";
			}

			interface Password
			{
				String PasswordResetRequestPopup = "fragments/password/passwordResetRequestPopup";
				String ForgotPasswordValidationMessage = "fragments/password/forgotPasswordValidationMessage";
			}

			interface Product
			{
				String FutureStockPopup = "fragments/product/futureStockPopup";
				String QuickViewPopup = "fragments/product/quickViewPopup";
				String ZoomImagesPopup = "fragments/product/zoomImagesPopup";
				String ReviewsTab = "fragments/product/reviewsTab";
				String ProductLister = "fragments/product/productLister";
			}
		}
	}
}
