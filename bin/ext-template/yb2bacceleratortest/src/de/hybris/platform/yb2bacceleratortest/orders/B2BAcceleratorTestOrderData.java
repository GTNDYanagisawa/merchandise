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
package de.hybris.platform.yb2bacceleratortest.orders;

import static de.hybris.platform.b2b.services.B2BWorkflowIntegrationService.DECISIONCODES.APPROVE;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.customer.B2BCustomerAccountService;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.yb2bacceleratortest.constants.YB2BAcceleratorTestConstants;
import de.hybris.platform.yb2bacceleratortest.services.AccountManagerApproveScheduleService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Places test orders data.
 */
public class B2BAcceleratorTestOrderData
{
	private static final Logger LOG = Logger.getLogger(B2BAcceleratorTestOrderData.class);

	private PlatformTransactionManager transactionManager;
	private CMSAdminSiteService cmsAdminSiteService;
	private UserService userService;
	private ImpersonationService impersonationService;
	private CustomerAccountService customerAccountService;
	private CronJobService cronJobService;
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;
	private B2BCustomerAccountService b2bCustomerAccountService;
	private CartService cartService;
	private CalculationService calculationService;
	private CommerceCheckoutService commerceCheckoutService;
	private AddressReversePopulator addressReversePopulator;
	private BaseStoreSelectorStrategy baseStoreSelectorStrategy;
	private ModelService modelService;
	private CommonI18NService i18nService;
	private AccountManagerApproveScheduleService accountManagerApproveScheduleService;

	private CartFacade cartFacade;
	private B2BOrderFacade orderFacade;
	private B2BCheckoutFacade b2bCheckoutFacade;


	protected final static String CURRENCY_ISO_CODE = "USD";
	protected final static String PRODUCT_A = "3755207";// IXO Power Screwdriver
	protected final static String PRODUCT_B = "2116266";// KA270K Sander
	protected final static String PRODUCT_C = "4567181"; // GWS 24-230 LVI Angle Grinders

	private static final long DELAY_IN_MILLIS = 10000;


	/**
	 * Main method Create 12 Orders for the users. Orders have unique identifiable PONumbers. If this method is called
	 * more than once, the PONumbers will match and new orders will not be placed.
	 */
	public void createSampleOrders()
	{
		LOG.info("Creating Sample Orders");


		placeOrderWithInCustomerPermissions(YB2BAcceleratorTestConstants.PAY_USER_UID, "ORDER_SENT_NOTIFICATION_SENT paid", false);

		placeOrderRequiringApproval(YB2BAcceleratorTestConstants.HISTORICAL_USER_UID, "PENDING_APPROVAL");
		placeOrderForQuoteNegotiation(YB2BAcceleratorTestConstants.HISTORICAL_USER_UID, "PENDING_QUOTE");
		placeOrderAndApproveByB2BApprover(YB2BAcceleratorTestConstants.HISTORICAL_USER_UID, "ORDER_SENT_NOTIFICATION_SENT Approved");
		placeOrderWithInCustomerPermissions(YB2BAcceleratorTestConstants.HISTORICAL_USER_UID,
				"ORDER_SENT_NOTIFICATION_SENT costcenter", true);

		placeReplenishmentOrderInCancelledState(YB2BAcceleratorTestConstants.REPENISHMENT_USER_UID, "REPLENISHMENT_ENDED");
		placeReplenishmentOrderInActiveState(YB2BAcceleratorTestConstants.REPENISHMENT_USER_UID, "REPLENISHMENT_ONGOING");
		placeReplenishmentOrderScheduleForFuture(YB2BAcceleratorTestConstants.REPENISHMENT_USER_UID, "REPLENISHMENT_FUTURE");

		placeOrderQuoteRequestWithMerchantResponse(YB2BAcceleratorTestConstants.QUOTE_USER_UID, "APPROVED_QUOTE");
		placeOrderQuoteRequestWithMerchantResponse(YB2BAcceleratorTestConstants.QUOTE_USER_UID, "REJECTED_QUOTE");
		placeOrderExceedingCreditLimit(YB2BAcceleratorTestConstants.QUOTE_USER_UID, "PENDING_MERCHANT_APPROVAL");
		placeOrderExceedingCreditAndRejected(YB2BAcceleratorTestConstants.QUOTE_USER_UID, "REJECTED_BY_MERCHANT");

		LOG.info("Finished Creating Sample Orders");
	}

	public void createPaymentInfos()
	{
		createPaymentInfo(YB2BAcceleratorTestConstants.PAY_USER_UID, "USD", createVisaCardInfo(), createUsBillingInfo());
		createPaymentInfo(YB2BAcceleratorTestConstants.PAY_USER_UID, "USD", createMasterCardInfo(), createGermanyBillingInfo());
	}

	/**
	 * Create scheduled order to commence 20 years hence and cancel
	 */
	protected String placeReplenishmentOrderInCancelledState(final String userID, final String purchaseOrderNumber)
	{
		String code = null;
		try
		{
			final TriggerData triggerData = new TriggerData();
			triggerData.setActivationTime(DateUtils.addYears(new Date(), 20));// Twenty Years From Now
			triggerData.setDaysOfWeek(Collections.singletonList(DayOfWeek.MONDAY));
			triggerData.setWeekInterval(Integer.valueOf(2));
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_A, Long.valueOf(1));
			code = scheduleOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.STANDARD_COSTCENTER, null, triggerData).getJobCode();
			if (code != null)
			{
				this.orderFacade.cancelReplenishment(code, userID);
			}

		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return code;
	}

	/**
	 * Create schedule order to begin in the past so it will be picked up immediately as it's overdue. Run an additional
	 * time manually.
	 */
	protected String placeReplenishmentOrderInActiveState(final String userID, final String purchaseOrderNumber)
	{
		String code = null;
		try
		{
			final TriggerData triggerData = new TriggerData();
			triggerData.setActivationTime(DateUtils.addDays(new Date(), -1));
			triggerData.setDaysOfWeek(Collections.singletonList(DayOfWeek.SUNDAY));
			triggerData.setWeekInterval(Integer.valueOf(26));
			triggerData.setDay(Integer.valueOf(-1));

			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_A, Long.valueOf(1));
			code = scheduleOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.STANDARD_COSTCENTER, null, triggerData).getJobCode();
			if (code != null)
			{
				runJobSynchronously(code, userID);
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return code;
	}

	protected String placeReplenishmentOrderScheduleForFuture(final String userID, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));
		String code = null;
		try
		{
			final TriggerData triggerData = new TriggerData();
			triggerData.setActivationTime(DateUtils.addYears(new Date(), 20));// Twenty Years From Now
			triggerData.setDaysOfWeek(Collections.singletonList(DayOfWeek.MONDAY));
			triggerData.setWeekInterval(Integer.valueOf(2));
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_A, Long.valueOf(1));
			code = scheduleOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.STANDARD_COSTCENTER, null, triggerData).getJobCode();
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return code;
	}

	protected String placeOrderWithInCustomerPermissions(final String userID, final String purchaseOrderNumber,
			final boolean useCostCenter)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));
		String orderCode = null;
		try
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_A, Long.valueOf(1));
			products.put(PRODUCT_B, Long.valueOf(1));
			final OrderData orderData = placeOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					useCostCenter ? YB2BAcceleratorTestConstants.STANDARD_COSTCENTER : null, null);
			orderCode = orderData == null ? null : orderData.getCode();
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return orderCode;
	}

	protected String placeOrderRequiringApproval(final String userID, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));
		String orderCode = null;
		try
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_B, Long.valueOf(13));

			final OrderData orderData = placeOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.STANDARD_COSTCENTER, null);

			orderCode = orderData == null ? null : orderData.getCode();
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		return orderCode;
	}

	protected String placeOrderAndApproveByB2BApprover(final String userID, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));

		final TransactionTemplate template = new TransactionTemplate(getTransactionManager());
		template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
		return template.execute(new TransactionCallback<String>()
		{
			@Override
			public String doInTransaction(final TransactionStatus status)
			{
				String orderCode = null;
				final Map<String, Long> products = new HashMap<String, Long>();
				products.put(PRODUCT_C, Long.valueOf(5));
				final OrderData orderData = placeOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
						YB2BAcceleratorTestConstants.STANDARD_COSTCENTER, null);

				orderCode = orderData == null ? null : orderData.getCode();

				if (orderCode != null)
				{
					b2bApproverApproveThisOrder(orderCode);
				}
				return orderCode;
			}
		});
	}

	protected String placeOrderForQuoteNegotiation(final String userID, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));
		String orderCode = null;
		try
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_C, Long.valueOf(1));

			final OrderData orderData = placeOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.STANDARD_COSTCENTER, "Please approve request");

			orderCode = orderData == null ? null : orderData.getCode();
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return orderCode;
	}

	protected String placeOrderQuoteRequestWithMerchantResponse(final String userID, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));
		String orderCode = null;
		try
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_C, Long.valueOf(2));

			final OrderData orderData = placeOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.STANDARD_COSTCENTER, "Please approve request");

			orderCode = orderData == null ? null : orderData.getCode();

			if (orderCode != null)
			{
				sendToAccountManagerToRejectIfPONumContainsReject(orderCode, userID);
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return orderCode;
	}


	protected String placeOrderExceedingCreditAndRejected(final String userID, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));
		String orderCode = null;
		try
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_C, Long.valueOf(3));

			final OrderData orderData = placeOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.LOWLIMIT_COSTCENTER, null);

			orderCode = orderData == null ? null : orderData.getCode();

			if (orderCode != null)
			{
				sendToAccountManagerToRejectIfPONumContainsReject(orderCode, userID);
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return orderCode;
	}

	protected String placeOrderExceedingCreditLimit(final String userID, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("******************Starting Attempt to setup and create order for PONumber: %s for " + "%s ",
				purchaseOrderNumber, userID));
		String orderCode = null;
		try
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put(PRODUCT_B, Long.valueOf(3));

			final OrderData orderData = placeOrder(userID, products, createUsAddressData(), purchaseOrderNumber,
					YB2BAcceleratorTestConstants.LOWLIMIT_COSTCENTER, null);

			orderCode = orderData == null ? null : orderData.getCode();
		}
		catch (final Exception e)
		{
			LOG.error(e);
		}
		return orderCode;
	}

	protected void createPaymentInfo(final String customerUid, final String currencyIso, final CardInfo cardInfo,
			final BillingInfo billingInfo)
	{
		// Lookup the site
		final CMSSiteModel cmsSite = getCmsAdminSiteService().getSiteForId(YB2BAcceleratorTestConstants.POWERTOOLS_SITE);
		// Lookup the customer
		final B2BCustomerModel customer = getUserService().getUserForUID(customerUid.toLowerCase(), B2BCustomerModel.class);

		// Impersonate site and customer
		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(cmsSite);
		ctx.setUser(customer);
		ctx.setLanguage(i18nService.getLanguage("en"));
		ctx.setCurrency(i18nService.getCurrency(currencyIso));
		getImpersonationService().executeInContext(ctx, new ImpersonationService.Executor<Object, ImpersonationService.Nothing>()
		{
			@Override
			public Object execute() throws ImpersonationService.Nothing
			{
				// Check if the card info already exists
				final List<CreditCardPaymentInfoModel> storedCards = getCustomerAccountService().getCreditCardPaymentInfos(customer,
						true);
				if (!containsCardInfo(storedCards, cardInfo))
				{
					LOG.info("Creating stored card subscription for [" + customerUid + "] card type [" + cardInfo.getCardType() + "]");

					// Create payment subscription
					final String customerTitleCode = (customer == null || customer.getTitle() == null) ? null : customer.getTitle()
							.getCode();
					final CreditCardPaymentInfoModel creditCardPaymentInfoModel = getCustomerAccountService()
							.createPaymentSubscription(customer, cardInfo, billingInfo, customerTitleCode, getPaymentProvider(), true);

					// Make this the default payment option
					getCustomerAccountService().setDefaultPaymentInfo(customer, creditCardPaymentInfoModel);
				}

				return null;
			}
		});
	}


	protected OrderData placeOrder(final String customerUid, final Map<String, Long> products, final AddressData deliveryAddress,
			final String purchaseOrderNumber, final String costCenterCode, final String quotePetition)
	{
		// Impersonate site and customer
		final B2BCustomerModel customer = getUserService().getUserForUID(customerUid.toLowerCase(), B2BCustomerModel.class);

		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(getCmsAdminSiteService().getSiteForId(YB2BAcceleratorTestConstants.POWERTOOLS_SITE));
		ctx.setUser(customer);
		ctx.setCurrency(i18nService.getCurrency(CURRENCY_ISO_CODE));

		return (OrderData) getImpersonationService().executeInContext(ctx,
				new ImpersonationService.Executor<Object, ImpersonationService.Nothing>()
				{
					@Override
					public Object execute() throws ImpersonationService.Nothing
					{

						OrderData orderData = null;

						// Check if the order already exists by PurchaseOrderNumber
						if (prepareANewSessionCartForCheckout(products, deliveryAddress, purchaseOrderNumber, costCenterCode,
								quotePetition, customer))
						{
							try
							{
								orderData = getB2bCheckoutFacade().placeOrder();

								if (orderData == null)
								{
									LOG.error("Failed to placeOrder");
								}
								else
								{
									LOG.info("Created order [" + orderData.getCode() + "]");
									// Sleep for 10s to allow the fulfilment processes to run for this order
									// Only have to worry about this here if we are running initialize from ant
									// as the process will exit immediately that we finish initialising.
									// Also to allow approval process to locate approver
									try
									{
										Thread.sleep(DELAY_IN_MILLIS);
									}
									catch (final InterruptedException e)
									{
										LOG.error(e.getMessage(), e);
									}
								}
							}
							catch (final InvalidCartException e)
							{
								LOG.error(e.getMessage(), e);
							}
						}
						return orderData;
					}
				});
	}


	protected ScheduledCartData scheduleOrder(final String customerUid, final Map<String, Long> products,
			final AddressData deliveryAddress, final String purchaseOrderNumber, final String costCenterCode,
			final String quotePetition, final TriggerData triggerData)
	{
		// Impersonate site and customer
		final B2BCustomerModel customer = getUserService().getUserForUID(customerUid.toLowerCase(), B2BCustomerModel.class);

		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(getCmsAdminSiteService().getSiteForId(YB2BAcceleratorTestConstants.POWERTOOLS_SITE));
		ctx.setUser(customer);
		ctx.setCurrency(i18nService.getCurrency(CURRENCY_ISO_CODE));

		return (ScheduledCartData) getImpersonationService().executeInContext(ctx,
				new ImpersonationService.Executor<Object, ImpersonationService.Nothing>()
				{
					@Override
					public Object execute() throws ImpersonationService.Nothing
					{

						ScheduledCartData scheduledCartData = null;
						// Check if the order already exists by PurchaseOrderNumber

						if (prepareANewSessionCartForCheckout(products, deliveryAddress, purchaseOrderNumber, costCenterCode,
								quotePetition, customer))
						{
							scheduledCartData = getB2bCheckoutFacade().scheduleOrder(triggerData);

							if (scheduledCartData == null)
							{
								LOG.error("Failed to placeOrder");
							}
							else
							{
								LOG.info("Scheduled Order order job [" + scheduledCartData.getJobCode() + "]");
								// Sleep for 10s to allow the fulfilment processes to run for this order
								// Only have to worry about this here if we are running initialize from ant
								// as the process will exit immediately that we finish initialising.
								// Also to allow approval process to locate approver
								try
								{
									Thread.sleep(10000);
								}
								catch (final InterruptedException e)
								{
									LOG.error(e.getMessage(), e);
								}
							}
						}
						return scheduledCartData;
					}
				});
	}


	protected boolean prepareANewSessionCartForCheckout(final Map<String, Long> products, final AddressData deliveryAddress,
			final String purchaseOrderNumber, final String costCenterCode, final String quotePetition,
			final B2BCustomerModel customer)
	{
		LOG.info(String.format("Creating order for [%s] for site [%s]", customer.getUid(),
				YB2BAcceleratorTestConstants.POWERTOOLS_SITE));

		final List<OrderModel> orderList = getCustomerAccountService().getOrderList(customer,
				getBaseStoreSelectorStrategy().getCurrentBaseStore(), null);

		if (containsOrder(orderList, purchaseOrderNumber))
		{
			LOG.info(String.format("Not creating order because purchaseOrderNumber %s for [%s] for site [%s] already exists. ",
					purchaseOrderNumber, customer.getUid(), YB2BAcceleratorTestConstants.POWERTOOLS_SITE));

			return false;
		}
		else
		{
			// Remove any existing cart
			getCartService().removeSessionCart();

			// Populate cart
			for (final Map.Entry<String, Long> productEntry : products.entrySet())
			{
				try
				{
					getCartFacade().addToCart(productEntry.getKey(), productEntry.getValue().longValue());

				}
				catch (final CommerceCartModificationException e)
				{
					LOG.error(e.getMessage(), e);
				}
			}

			// Begin checkout
			// Add an address to the address-book, set as the delivery address
			final AddressModel addressModel = getModelService().create(AddressModel.class);
			getAddressReversePopulator().populate(deliveryAddress, addressModel);
			getCustomerAccountService().saveAddressEntry(customer, addressModel);

			final CartModel sessionCart = getCartService().getSessionCart();
			try
			{
				getCalculationService().calculate(sessionCart);
			}
			catch (final CalculationException e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
			if (costCenterCode == null)
			{
				// Set payment info
				getB2bCheckoutFacade().setPaymentTypeSelectedForCheckout(CheckoutPaymentType.CARD.getCode());
				getB2bCheckoutFacade().setPaymentInfoIfAvailable();
				getB2bCheckoutFacade().authorizePayment("123");

			}
			else
			{
				getB2bCheckoutFacade().setPaymentTypeSelectedForCheckout(CheckoutPaymentType.ACCOUNT.getCode());
				getB2bCheckoutFacade().setCostCenterForCart(costCenterCode, getB2bCheckoutFacade().getCheckoutCart().getCode());
				// if quotePetition is null a regular order is placed
				// otherwise a quote negotiation process is started
				if (quotePetition != null)
				{
					getB2bCheckoutFacade().setQuoteRequestDescription(quotePetition);
				}
			}

			if (!getCommerceCheckoutService().setDeliveryAddress(sessionCart, addressModel))
			{
				LOG.error("Failed to set delivery address on cart");
			}

			if (sessionCart.getDeliveryAddress() == null)
			{
				LOG.error("Failed to set delivery address");
			}

			getB2bCheckoutFacade().setDeliveryModeIfAvailable();
			getB2bCheckoutFacade().setPurchaseOrderNumber(purchaseOrderNumber);
			return true;
		}
	}

	protected void runJobSynchronously(final String code, final String uid)
	{
		final B2BCustomerModel user = getUserService().getUserForUID(uid.toLowerCase(), B2BCustomerModel.class);
		final CartToOrderCronJobModel job = b2bCustomerAccountService.getCartToOrderCronJobForCode(code, user);

		cronJobService.performCronJob(job, true);
	}

	public void sendToAccountManagerToRejectIfPONumContainsReject(final String orderCode, final String customerID)
	{
		accountManagerApproveScheduleService.scheduleAccountManagerJobToApproveReject(orderCode, customerID);

	}

	protected B2BOrderApprovalData b2bApproverApproveThisOrder(final String orderCode)
	{
		final CMSSiteModel cmsSite = getCmsAdminSiteService().getSiteForId(YB2BAcceleratorTestConstants.POWERTOOLS_SITE);
		final B2BCustomerModel customer = getUserService().getUserForUID(YB2BAcceleratorTestConstants.APPROVER_UID.toLowerCase(),
				B2BCustomerModel.class);

		// Impersonate site and customer
		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(cmsSite);
		ctx.setUser(customer);
		ctx.setCurrency(i18nService.getCurrency("USD"));

		return (B2BOrderApprovalData) getImpersonationService().executeInContext(ctx,
				new ImpersonationService.Executor<Object, ImpersonationService.Nothing>()
				{
					@Override
					public Object execute() throws ImpersonationService.Nothing
					{
						return adjustOrderApproveDecision(orderCode, APPROVE.toString(), "QA Approver approvers");
					}
				});
	}


	protected B2BOrderApprovalData adjustOrderApproveDecision(final String orderCode, final String decision, final String comment)
	{
		final List<B2BOrderApprovalData> approvals = getOrderFacade().getOrdersForApproval();
		LOG.info(getUserService().getCurrentUser().getUid() + " has approvals " + CollectionUtils.isNotEmpty(approvals));


		for (final B2BOrderApprovalData approval : approvals)
		{
			final B2BOrderApprovalData data = getOrderFacade().getOrderApprovalDetailsForCode(approval.getWorkflowActionModelCode());
			if (!data.getAllDecisions().contains(decision.toUpperCase()))
			{
				LOG.info("Workflow [" + data.getWorkflowActionModelCode() + "] does not contain decision " + decision);
				continue;
			}
			if (approval.getB2bOrderData().getCode().equals(orderCode))
			{
				final B2BOrderApprovalData b2bOrderApprovalData = new B2BOrderApprovalData();
				b2bOrderApprovalData.setSelectedDecision(decision);
				b2bOrderApprovalData.setApprovalComments(comment);
				b2bOrderApprovalData.setWorkflowActionModelCode(approval.getWorkflowActionModelCode());
				getOrderFacade().setOrderApprovalDecision(b2bOrderApprovalData);
				return b2bOrderApprovalData;
			}
		}
		return null;
	}

	protected boolean containsCardInfo(final List<CreditCardPaymentInfoModel> storedCards, final CardInfo cardInfo)
	{
		if (storedCards != null && !storedCards.isEmpty() && cardInfo != null)
		{
			for (final CreditCardPaymentInfoModel storedCard : storedCards)
			{
				if (matchesCardInfo(storedCard, cardInfo))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected boolean matchesCardInfo(final CreditCardPaymentInfoModel storedCard, final CardInfo cardInfo)
	{
		return (storedCard.getType().equals(cardInfo.getCardType()) && StringUtils.equals(storedCard.getCcOwner(),
				cardInfo.getCardHolderFullName()));
	}

	protected String getPaymentProvider()
	{
		return "Mockup";
	}

	protected CardInfo createVisaCardInfo()
	{
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName("John Doe");
		cardInfo.setCardNumber("4111111111111111");
		cardInfo.setCardType(CreditCardType.VISA);
		cardInfo.setExpirationMonth(Integer.valueOf(12));
		cardInfo.setExpirationYear(Integer.valueOf(2020));
		return cardInfo;
	}

	protected CardInfo createMasterCardInfo()
	{
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardHolderFullName("John Doe");
		cardInfo.setCardNumber("5555555555554444");
		cardInfo.setCardType(CreditCardType.MASTERCARD_EUROCARD);
		cardInfo.setExpirationMonth(Integer.valueOf(11));
		cardInfo.setExpirationYear(Integer.valueOf(2022));
		return cardInfo;
	}

	protected BillingInfo createUsBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setFirstName("John");
		billingInfo.setLastName("Doe");
		billingInfo.setStreet1("Holborn Tower");
		billingInfo.setStreet2("137 High Holborn");
		billingInfo.setCity("New London");
		billingInfo.setPostalCode("06320");
		billingInfo.setCountry("US");
		billingInfo.setPhoneNumber("860 555 1212");
		return billingInfo;
	}

	protected BillingInfo createGermanyBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setFirstName("John");
		billingInfo.setLastName("Doe");
		billingInfo.setStreet1("Nymphenburger Str. 86");
		billingInfo.setStreet2("Some Line 2 data");
		billingInfo.setCity("Munchen");
		billingInfo.setPostalCode("80636");
		billingInfo.setCountry("DE");
		billingInfo.setPhoneNumber("+49 (0)89 / 890 650");
		return billingInfo;
	}

	protected AddressData createUsAddressData()
	{
		final AddressData data = new AddressData();
		data.setTitle("Mr.");
		data.setTitleCode("mr");
		data.setFirstName("John");
		data.setLastName("Doe");

		data.setCompanyName("hybris");
		data.setLine1("137 High Holborn");
		data.setLine2("");
		data.setTown("New London");
		data.setPostalCode("63020");

		final CountryData countryData = new CountryData();
		countryData.setIsocode("US");
		countryData.setName("US");
		data.setCountry(countryData);

		data.setPhone("860 555 1212 ");
		data.setEmail("sales@hybris.local");
		data.setShippingAddress(true);
		data.setBillingAddress(true);

		return data;
	}

	protected boolean containsOrder(final List<OrderModel> orderList, final String purchaseOrderNumber)
	{
		LOG.debug(String.format("Searching for purchaseOrder:%s among list of %s", purchaseOrderNumber,
				CollectionUtils.isEmpty(orderList) ? "0" : String.valueOf(orderList.size())));

		if (orderList != null && !orderList.isEmpty() && purchaseOrderNumber != null)
		{
			for (final OrderModel order : orderList)
			{
				if (purchaseOrderNumber.equals(order.getPurchaseOrderNumber()))
				{
					return true;
				}
			}
		}
		return false;
	}

	public CommonI18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final CommonI18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	protected BaseStoreSelectorStrategy getBaseStoreSelectorStrategy()
	{
		return baseStoreSelectorStrategy;
	}

	@Required
	public void setBaseStoreSelectorStrategy(final BaseStoreSelectorStrategy baseStoreSelectorStrategy)
	{
		this.baseStoreSelectorStrategy = baseStoreSelectorStrategy;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected B2BOrderFacade getOrderFacade()
	{
		return orderFacade;
	}

	@Required
	public void setOrderFacade(final B2BOrderFacade orderFacade)
	{
		this.orderFacade = orderFacade;
	}

	protected B2BCheckoutFacade getB2bCheckoutFacade()
	{
		return b2bCheckoutFacade;
	}

	@Required
	public void setB2bCheckoutFacade(final B2BCheckoutFacade b2bCheckoutFacade)
	{
		this.b2bCheckoutFacade = b2bCheckoutFacade;
	}

	protected B2BWorkflowIntegrationService getB2bWorkflowIntegrationService()
	{
		return b2bWorkflowIntegrationService;
	}

	@Required
	public void setB2bWorkflowIntegrationService(final B2BWorkflowIntegrationService b2bWorkflowIntegrationService)
	{
		this.b2bWorkflowIntegrationService = b2bWorkflowIntegrationService;
	}

	protected AddressReversePopulator getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final AddressReversePopulator addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	protected B2BCustomerAccountService getB2bCustomerAccountService()
	{
		return b2bCustomerAccountService;
	}

	@Required
	public void setB2bCustomerAccountService(final B2BCustomerAccountService b2bCustomerAccountService)
	{
		this.b2bCustomerAccountService = b2bCustomerAccountService;
	}

	protected CronJobService getCronJobService()
	{
		return cronJobService;
	}

	@Required
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	public AccountManagerApproveScheduleService getAccountManagerApproveScheduleService()
	{
		return accountManagerApproveScheduleService;
	}

	public void setAccountManagerApproveScheduleService(
			final AccountManagerApproveScheduleService accountManagerApproveScheduleService)
	{
		this.accountManagerApproveScheduleService = accountManagerApproveScheduleService;
	}

	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	public PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Required
	public void setTransactionManager(final PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

}
