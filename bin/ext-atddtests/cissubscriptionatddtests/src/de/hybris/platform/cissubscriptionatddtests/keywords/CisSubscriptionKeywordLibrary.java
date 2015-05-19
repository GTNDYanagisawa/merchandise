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
 */
package de.hybris.platform.cissubscriptionatddtests.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.atddengine.xml.XmlAssertions;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.xstream.alias.AttributeAliasMapping;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.entitlementfacades.data.UsageUnitData;
import de.hybris.platform.integration.cis.subscription.populators.CisUsageChargePopulator;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.security.auth.AuthenticationService;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.action.SubscriptionUpdateActionEnum;
import de.hybris.platform.subscriptionfacades.converters.SubscriptionXStreamAliasConverter;
import de.hybris.platform.subscriptionfacades.data.OverageUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPaymentData;
import de.hybris.platform.subscriptionfacades.data.TierUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeTypeData;
import de.hybris.platform.subscriptionfacades.data.VolumeUsageChargeData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.enums.UsageChargeType;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.hybris.cis.api.subscription.model.CisSubscriptionOrder;
import com.hybris.cis.api.subscription.model.CisUsageCharge;
import com.hybris.cis.api.subscription.model.CisUsageChargeTier;
import com.thoughtworks.xstream.XStream;


/**
 * Definition of the keyword library for the CisSubscription ATDD tests
 */
public class CisSubscriptionKeywordLibrary extends AbstractKeywordLibrary
{
	private static final Logger LOG = Logger.getLogger(CisSubscriptionKeywordLibrary.class);

	private static final String DEFAULT_PASSWORD = "12341234";

	private static final String HIGHEST_APPLICABLE_TIER = "HIGHEST_APPLICABLE_TIER";

	private static final String EACH_RESPECTIVE_TIER = "EACH_RESPECTIVE_TIER";


	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private BaseStoreService baseStoreService;

	@Autowired
	private CheckoutFacade checkoutFacade;

	@Autowired
	private SubscriptionFacade subscriptionFacade;

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private Converter<OrderData, CisSubscriptionOrder> cisSubscriptionOrderConverter;

	@Autowired
	private Converter<UsageChargeEntryData, CisUsageChargeTier> cisUsageChargeTierConverter;

	@Autowired
	private SubscriptionXStreamAliasConverter subscriptionXStreamAliasConverter;

	@Autowired
	private UserService userService;

	private XStream xstream;

	public String convertToCisSubscriptionOrder()
	{
		OrderData orderData = null;

		try
		{
			orderData = checkoutFacade.placeOrder();
		}
		catch (final InvalidCartException e)
		{
			LOG.error("An exception occured while adding a product to cart", e);
			fail(e.getMessage());
		}

		Assert.assertNotNull(orderData);
		final CisSubscriptionOrder cisSubscriptionOrder = cisSubscriptionOrderConverter.convert(orderData);

		Assert.assertNotNull(cisSubscriptionOrder);

		if (getXstream() != null)
		{
			return getXstream().toXML(cisSubscriptionOrder);
		}

		return null;
	}

	private XStream getXstream()
	{
		if (xstream == null)
		{
			xstream = subscriptionXStreamAliasConverter.getXstream();

			final Map<String, AttributeAliasMapping> attributeAliases = BeanFactoryUtils.beansOfTypeIncludingAncestors(
					applicationContext, AttributeAliasMapping.class);

			for (final AttributeAliasMapping attributeAlias : attributeAliases.values())
			{
				xstream.useAttributeFor(attributeAlias.getAliasedClass(), attributeAlias.getAttributeName());
			}
		}

		return xstream;
	}

	public void verifyXmlConversionResult(final String cisXmlActual, final String cisXmlExpected)
	{
		XmlAssertions.assertXmlEqual("The conversion result does not match the expected value", cisXmlExpected, cisXmlActual,
				"transformation/ignoreIdsAndDates.xsl");
	}

	public String registerAndLoginCustomer(final String firstName, final String lastName, final String login)
	{
		final RegisterData registerData = new RegisterData();
		registerData.setTitleCode("dr");
		registerData.setFirstName(firstName);
		registerData.setLastName(lastName);
		registerData.setLogin(login);
		registerData.setPassword(DEFAULT_PASSWORD);

		try
		{
			customerFacade.register(registerData);
			authenticationService.login(login, DEFAULT_PASSWORD);
			customerFacade.loginSuccess();

			// create billing provider account via the SBG
			subscriptionFacade.updateProfile(new HashMap<String, String>());

			return ((CustomerModel) userService.getCurrentUser()).getCustomerID();
		}
		catch (UnknownIdentifierException | IllegalArgumentException | DuplicateUidException e)
		{
			LOG.error("Registration failed", e);
			fail(e.getMessage());
		}
		catch (final InvalidCredentialsException e)
		{
			LOG.error("Login failed", e);
			fail(e.getMessage());
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error("Account creation via the SBG failed", e);
		}

		return null;
	}

	public void createDefaultPaymentMethod()
	{
		try
		{
			final SubscriptionPaymentData initResult = subscriptionFacade.initializeTransaction("127.0.0.1",
					"http://localhost:9090", "http://localhost:9090", Collections.EMPTY_MAP);

			final Map<String, String> paymentParameters = new HashMap<>();

			paymentParameters.put("cardNumber", "************1234");
			paymentParameters.put("cardType", "visa");
			paymentParameters.put("expiryMonth", "10");
			paymentParameters.put("expiryYear", "2030");
			paymentParameters.put("issueNumber", null);
			paymentParameters.put("nameOnCard", "John Doe");
			paymentParameters.put("startMonth", null);
			paymentParameters.put("startYear", null);
			paymentParameters.put("billingAddress_countryIso", "US");
			paymentParameters.put("billingAddress_firstName", "John");
			paymentParameters.put("billingAddress_lastName", "Doe");
			paymentParameters.put("billingAddress_line1", "Default Street 1");
			paymentParameters.put("billingAddress_line2", StringUtils.EMPTY);
			paymentParameters.put("billingAddress_postcode", "12345");
			paymentParameters.put("billingAddress_townCity", "Default Town");

			final SubscriptionPaymentData finalizeResult = subscriptionFacade.finalizeTransaction("127.0.0.1", initResult
					.getParameters().get("sessionTransactionToken"), paymentParameters);

			final CCPaymentInfoData paymentInfo = subscriptionFacade.createPaymentSubscription(finalizeResult.getParameters());

			userFacade.setDefaultPaymentInfo(paymentInfo);
			checkoutFacade.setPaymentDetails(paymentInfo.getId());
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error("Payment method creation failed", e);
			fail(e.getMessage());
		}
	}

	public void placeOrderAndCreateSubscriptions()
	{
		try
		{
			final OrderData order = checkoutFacade.placeOrder();

			subscriptionFacade.createSubscriptions(order, new HashMap<String, String>());
		}
		catch (final InvalidCartException e)
		{
			LOG.error("Unable to create order from cart", e);
			fail(e.getMessage());
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error("createSubscriptions call failed", e);
			fail(e.getMessage());
		}
	}

	public void verifyCurrentCustomerHasSubscriptions()
	{
		try
		{
			final Collection<SubscriptionData> subscriptions = subscriptionFacade.getSubscriptions();
			assertTrue("Customer subscriptions may not be empty", CollectionUtils.isNotEmpty(subscriptions));
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error("getSubscriptions call failed", e);
		}
	}

	/**
	 * Cancels an existing subscription using the facadelayer
	 * 
	 * @param subscriptionId
	 */
	public void cancelSubscription(final String subscriptionId)
	{
		try
		{
			subscriptionFacade.updateSubscription(subscriptionId, true, SubscriptionUpdateActionEnum.CANCEL, null);
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error("Unable to cancel subscription", e);
		}
	}

	/**
	 * Tries to cancel a subscription, but this action should fail due to wrong subscriptionId, wrong subscriptionStatus,
	 * ...
	 * 
	 * @param subscriptionId
	 * @param exception
	 */
	public void cancelSubscriptionExpectException(final String subscriptionId, final String exception)
	{
		try
		{
			subscriptionFacade.updateSubscription(subscriptionId, true, SubscriptionUpdateActionEnum.CANCEL, null);
			fail();
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.info("ExceptionName: " + e.getClass().getSimpleName());
			assertEquals(exception, e.getClass().getSimpleName());
		}
	}

	/**
	 * Checks, if all parameters of a cancelled subscription are correctly set
	 * 
	 * @param subscriptionId
	 */
	public void verifyCancelSubscription(final String subscriptionId)
	{
		try
		{
			final SubscriptionData subscription = subscriptionFacade.getSubscription(subscriptionId);

			assertEquals(SubscriptionStatus.CANCELLED.getCode(), subscription.getSubscriptionStatus());
			assertNotNull(subscription.getCancelledDate());
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.error("Subscription with id[" + subscriptionId + "] cannot be cancelled", e);
		}
	}

	public CisUsageCharge populateUsageChargeFromPreUnitUsageWithType(final String name, final String unit, final String type,
			final List<UsageChargeEntryData> usageCharges)
	{
		final CisUsageCharge cisUsageCharge = new CisUsageCharge();

		final CisUsageChargePopulator cisUsageChargePopulator = new CisUsageChargePopulator();

		final PerUnitUsageChargeData preUnitUsageCharge = new PerUnitUsageChargeData();

		preUnitUsageCharge.setName(name);

		final UsageUnitData usageUnit = new UsageUnitData();
		usageUnit.setId(unit);
		preUnitUsageCharge.setUsageUnit(usageUnit);

		final UsageChargeTypeData usageChargeType = new UsageChargeTypeData();

		if (HIGHEST_APPLICABLE_TIER.equalsIgnoreCase(type))
		{
			usageChargeType.setCode(UsageChargeType.HIGHEST_APPLICABLE_TIER.getCode());
			usageChargeType.setName(UsageChargeType.HIGHEST_APPLICABLE_TIER.name());
		}
		else if (EACH_RESPECTIVE_TIER.equalsIgnoreCase(type))
		{
			usageChargeType.setCode(UsageChargeType.EACH_RESPECTIVE_TIER.getCode());
			usageChargeType.setName(UsageChargeType.EACH_RESPECTIVE_TIER.name());
		}
		preUnitUsageCharge.setUsageChargeEntries(usageCharges);
		preUnitUsageCharge.setUsageChargeType(usageChargeType);

		cisUsageChargePopulator.setCisUsageChargeTierConverter(cisUsageChargeTierConverter);
		cisUsageChargePopulator.populate(preUnitUsageCharge, cisUsageCharge);


		return cisUsageCharge;
	}

	public CisUsageCharge populateUsageChargeFromVolumeUsage(final String name, final String unit,
			final List<UsageChargeEntryData> usageCharges)
	{
		final CisUsageCharge cisUsageCharge = new CisUsageCharge();

		final CisUsageChargePopulator cisUsageChargePopulator = new CisUsageChargePopulator();

		final VolumeUsageChargeData volumeUsageCharge = new VolumeUsageChargeData();

		volumeUsageCharge.setName(name);

		final UsageUnitData usageUnit = new UsageUnitData();
		usageUnit.setId(unit);
		volumeUsageCharge.setUsageUnit(usageUnit);

		volumeUsageCharge.setUsageChargeEntries(usageCharges);

		cisUsageChargePopulator.setCisUsageChargeTierConverter(cisUsageChargeTierConverter);
		cisUsageChargePopulator.populate(volumeUsageCharge, cisUsageCharge);


		return cisUsageCharge;
	}

	public CisUsageCharge populateUsageChargeFromVolumnUsageWithTiers(final String name, final String unit,
			final List<UsageChargeEntryData> usageCharges)
	{
		final CisUsageCharge cisUsageCharge = new CisUsageCharge();

		final CisUsageChargePopulator cisUsageChargePopulator = new CisUsageChargePopulator();

		final VolumeUsageChargeData volumUsageCharge = new VolumeUsageChargeData();

		volumUsageCharge.setName(name);

		final UsageUnitData usageUnit = new UsageUnitData();
		usageUnit.setId(unit);

		volumUsageCharge.setUsageUnit(usageUnit);
		volumUsageCharge.setUsageChargeEntries(usageCharges);

		cisUsageChargePopulator.populate(volumUsageCharge, cisUsageCharge);

		return cisUsageCharge;
	}

	public OverageUsageChargeEntryData createOverageUsageCharge(final String chargePrice)
	{
		final OverageUsageChargeEntryData overage = new OverageUsageChargeEntryData();

		final PriceData price = new PriceData();

		price.setValue(new BigDecimal(chargePrice));

		overage.setPrice(price);

		return overage;
	}

	public TierUsageChargeEntryData createTierUsageCharge(final String chargePrice, final String start, final String end)
	{
		final TierUsageChargeEntryData tierUsage = new TierUsageChargeEntryData();

		final PriceData price = new PriceData();

		price.setValue(new BigDecimal(chargePrice));

		tierUsage.setPrice(price);
		tierUsage.setTierStart(Integer.parseInt(start));
		tierUsage.setTierEnd(Integer.parseInt(end));

		return tierUsage;
	}

	@Autowired
	private CustomerFacade customerFacade;

	@Autowired
	private AuthenticationService authenticationService;

}
