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
package de.hybris.platform.yacceleratortest.orders;

import de.hybris.platform.basecommerce.strategies.BaseStoreSelectorStrategy;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Create test order data
 */
public class AcceleratorTestOrderData
{
	private static final Logger LOG = Logger.getLogger(AcceleratorTestOrderData.class);

	private CMSAdminSiteService cmsAdminSiteService;
	private UserService userService;
	private ImpersonationService impersonationService;
	private CustomerAccountService customerAccountService;
	private CartFacade cartFacade;
	private CartService cartService;
	private CheckoutFacade checkoutFacade;
	private CommerceCheckoutService commerceCheckoutService;
	private AddressReversePopulator addressReversePopulator;
	private BaseStoreSelectorStrategy baseStoreSelectorStrategy;
	private ModelService modelService;
	private CommonI18NService i18nService;

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
	public void setImpersonationService(final ImpersonationService siteImpersonationService)
	{
		this.impersonationService = siteImpersonationService;
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

	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	@Required
	public void setCheckoutFacade(final CheckoutFacade checkoutFacade)
	{
		this.checkoutFacade = checkoutFacade;
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

	protected AddressReversePopulator getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final AddressReversePopulator addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
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

	/**
	 * Create stored card subscription info for the PaymentUser@test.com and OrderHistoryUser@test.com customers
	 */
	public void createPaymentInfos()
	{
		createPaymentInfo("paymentuser@test.com", "USD", createVisaCardInfo(), createUkBillingInfo());
		createPaymentInfo("paymentuser@test.com", "USD", createMasterCardInfo(), createGermanyBillingInfo());

		createPaymentInfo("orderhistoryuser@test.com", "USD", createVisaCardInfo(), createUkBillingInfo());
	}

	public void createPaymentInfo(final String customerUid, final String currencyIso, final CardInfo cardInfo,
	                              final BillingInfo billingInfo)
	{
		// Lookup the site
		final CMSSiteModel cmsSite = getCmsAdminSiteService().getSiteForId("electronics");
		// Lookup the customer
		final CustomerModel customer = getUserService().getUserForUID(customerUid.toLowerCase(), CustomerModel.class);

		// Impersonate site and customer
		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(cmsSite);
		ctx.setUser(customer);
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

	protected BillingInfo createUkBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setFirstName("John");
		billingInfo.setLastName("Doe");
		billingInfo.setStreet1("Holborn Tower");
		billingInfo.setStreet2("137 High Holborn");
		billingInfo.setCity("London");
		billingInfo.setPostalCode("WC1V 6PL");
		billingInfo.setCountry("GB");
		billingInfo.setPhoneNumber("+44 (0)20 / 7429 4175");
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

	/**
	 * Create sample orders for the OrderHistoryUser@test.com customer
	 */
	public void createSampleOrders()
	{
		// Create sample order in the electronics site
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put("872912", Long.valueOf(1)); // Secure Digital Card 2GB
			products.put("479956", Long.valueOf(1)); // 4GB Memory Stick Pro Duo + adapter
			createSampleOrder("electronics", "OrderHistoryUser@test.com", "USD", products, createUkAddressData(), null);
		}

		// Create sample order in the apparel-uk site
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put("300310086", Long.valueOf(1)); // Bag Dakine Factor Pack bomber
			products.put("300147511", Long.valueOf(1)); // T-Shirt Men Playboard Logo Tee irish green M
			createSampleOrder("apparel-uk", "OrderHistoryUser@test.com", "GBP", products, createUkAddressData(), null);
		}

		// Create sample order in the apparel-de site
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put("300020465", Long.valueOf(1)); // Protector Dainese Waistcoat S7 black/silver M
			products.put("300044623", Long.valueOf(1)); // Shades Anon Legion crystal & black gray
			createSampleOrder("apparel-de", "OrderHistoryUser@test.com", "EUR", products, createGermanAddressData(), null);
		}
	}

	public void createSampleBOPiSOrders()
	{
		// Create sample order in the electronics site
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put("300938", Long.valueOf(1)); // Photosmart E317 Digital Camera
			products.put("1981415", Long.valueOf(1)); // PL60 Silver
			createSampleOrder("electronics", "OrderHistoryUser@test.com", "USD", products, createUkAddressData(), "Yokosuka");
		}

		// Create sample order in the apparel-uk site
		{
			final Map<String, Long> products = new HashMap<String, Long>();
			products.put("300737290", Long.valueOf(1)); // System Tee SS dirty plum S
			products.put("300737281", Long.valueOf(1)); // System Tee SS lime M
			createSampleOrder("apparel-uk", "OrderHistoryUser@test.com", "GBP", products, createUkAddressData(),
					"Newcastle upon Tyne College");
		}
	}

	public void createSampleOrder(final String siteUid, final String customerUid, final String currencyIso,
	                              final Map<String, Long> products, final AddressData deliveryAddress, final String storeId)
	{
		// Lookup the site
		// Lookup the customer
		final CMSSiteModel cmsSite = getCmsAdminSiteService().getSiteForId(siteUid);
		final CustomerModel customer = getUserService().getUserForUID(customerUid.toLowerCase(), CustomerModel.class);

		// Impersonate site and customer
		final ImpersonationContext ctx = new ImpersonationContext();
		ctx.setSite(cmsSite);
		ctx.setUser(customer);
		ctx.setCurrency(i18nService.getCurrency(currencyIso));
		getImpersonationService().executeInContext(ctx, new ImpersonationService.Executor<Object, ImpersonationService.Nothing>()
		{
			@Override
			public Object execute() throws ImpersonationService.Nothing
			{
				final BaseStoreModel baseStore = getBaseStoreSelectorStrategy().getCurrentBaseStore();
				final String submitOrderProcessCode = baseStore.getSubmitOrderProcessCode();
				final String originalPaymentProvider = baseStore.getPaymentProvider();
				baseStore.setPaymentProvider("Mockup");
				try
				{

					// Check if the order already exists
					final List<OrderModel> orderList = getCustomerAccountService().getOrderList(customer,
							getBaseStoreSelectorStrategy().getCurrentBaseStore(), null);
					if (!containsOrder(orderList, products))
					{
						baseStore.setSubmitOrderProcessCode("order-process");
						getModelService().save(baseStore);
						LOG.info("Creating order for [" + customerUid + "] for site [" + siteUid + "]");

						// Remove any existing cart
						getCartService().removeSessionCart();

						// Populate cart
						for (final Map.Entry<String, Long> productEntry : products.entrySet())
						{
							try
							{
								getCartFacade().addToCart(productEntry.getKey(), productEntry.getValue().longValue(), storeId);
							}
							catch (final CommerceCartModificationException e)
							{
								LOG.error("Exception...", e);
							}
						}

						// Begin checkout

						// Add an address to the address-book, set as the delivery address
						final AddressModel addressModel = getModelService().create(AddressModel.class);
						getAddressReversePopulator().populate(deliveryAddress, addressModel);
						getCustomerAccountService().saveAddressEntry(customer, addressModel);
						final CartModel sessionCart = getCartService().getSessionCart();
						final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
						parameter.setEnableHooks(true);
						parameter.setCart(sessionCart);
						parameter.setAddress(addressModel);
						parameter.setIsDeliveryAddress(false);

						if (!getCommerceCheckoutService().setDeliveryAddress(parameter))
						{
							LOG.error("Failed to set delivery address on cart");
						}

						if (sessionCart.getDeliveryAddress() == null)
						{
							LOG.error("Failed to set delivery address");
						}

						// Set delivery mode
						getCheckoutFacade().setDeliveryModeIfAvailable();

						// Set payment info
						getCheckoutFacade().setPaymentInfoIfAvailable();

						// Checkout
						getCheckoutFacade().authorizePayment("123");

						try
						{
							final OrderData orderData = getCheckoutFacade().placeOrder();
							if (orderData == null)
							{
								LOG.error("Failed to placeOrder");
							}
							else
							{
								LOG.info("Created order [" + orderData.getCode() + "]");

								// Sleep for 5s to allow the fulfilment processes to run for this order
								// Only have to worry about this here if we are running initialize from ant
								// as the process will exit immediately that we finish initialising.
								try
								{
									Thread.sleep(10000);
								}
								catch (final InterruptedException e)
								{
									LOG.error("Exception...", e);
								}
							}
						}
						catch (final InvalidCartException e)
						{
							LOG.error("Exception...", e);
						}
					}

				}
				catch (final Exception e)
				{
					LOG.error("Exception in createSampleOrder", e);
				}
				finally
				{
					baseStore.setPaymentProvider(originalPaymentProvider);
					baseStore.setSubmitOrderProcessCode(submitOrderProcessCode);
					getModelService().save(baseStore);
				}
				return null;
			}
		});
	}

	protected AddressData createUkAddressData()
	{
		final AddressData data = new AddressData();
		data.setTitle("Mr.");
		data.setTitleCode("mr");
		data.setFirstName("John");
		data.setLastName("Doe");

		data.setCompanyName("hybris");
		data.setLine1("137 High Holborn");
		data.setLine2("");
		data.setTown("London");
		data.setPostalCode("WC1V 6PL");

		final CountryData countryData = new CountryData();
		countryData.setIsocode("GB");
		countryData.setName("UK");
		data.setCountry(countryData);

		data.setPhone("+44 (0)20 / 7429 4175");
		data.setEmail("sales@hybris.local");
		data.setShippingAddress(true);
		data.setBillingAddress(true);

		return data;
	}

	protected AddressData createGermanAddressData()
	{
		final AddressData data = new AddressData();
		data.setTitle("Mr.");
		data.setTitleCode("mr");
		data.setFirstName("John");
		data.setLastName("Doe");

		data.setCompanyName("hybris");
		data.setLine1("Nymphenburger Str. 89");
		data.setLine2("");
		data.setTown("Munchen");
		data.setPostalCode("80636");

		final CountryData countryData = new CountryData();
		countryData.setIsocode("DE");
		countryData.setName("Germany");
		data.setCountry(countryData);

		data.setPhone("+49 (0)89 / 890 650");
		data.setEmail("sales@hybris.local");
		data.setShippingAddress(true);
		data.setBillingAddress(true);

		return data;
	}

	protected boolean containsOrder(final List<OrderModel> orderList, final Map<String, Long> products)
	{
		if (orderList != null && !orderList.isEmpty() && products != null)
		{
			for (final OrderModel order : orderList)
			{
				if (matchesOrder(order, products))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected boolean matchesOrder(final OrderModel order, final Map<String, Long> products)
	{
		final Map<String, Long> entryQuantityMap = getEntryQuantityMap(order);
		final Map<String, Long> productsTreeMap = new TreeMap<String, Long>(products);

		return entryQuantityMap.equals(productsTreeMap);
	}

	protected Map<String, Long> getEntryQuantityMap(final OrderModel order)
	{
		final TreeMap<String, Long> result = new TreeMap<String, Long>();

		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			final ProductModel product = entry.getProduct();
			if (product != null)
			{
				final String productCode = product.getCode();
				if (result.containsKey(productCode))
				{
					final long newQuantity = result.get(productCode).longValue() + entry.getQuantity().longValue();
					result.put(productCode, Long.valueOf(newQuantity));
				}
				else
				{
					result.put(productCode, entry.getQuantity());
				}
			}
		}
		return result;
	}

	protected CommonI18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final CommonI18NService i18nService)
	{
		this.i18nService = i18nService;
	}
}
