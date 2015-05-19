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
package de.hybris.platform.yb2bacceleratorcore.actions.replenishment;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.b2bacceleratorservices.order.impl.B2BAcceleratorCartToOrderJob;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.datasetup.ServiceLayerDataSetup;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations =
{ "classpath:/payment-spring-test.xml" })
@IntegrationTest
public class ReplenishmentOrderProcessActionIntegrationTest extends BaseCommerceBaseTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ReplenishmentOrderProcessActionIntegrationTest.class);
	@Resource
	CronJobService cronJobService;
	@Resource
	ModelService modelService;
	@Resource
	CartService cartService;
	@Resource
	FlexibleSearchService flexibleSearchService;
	@Resource
	UserService userService;
	@Resource
	ServiceLayerDataSetup serviceLayerDataSetup;
	@Resource
	B2BAcceleratorCartToOrderJob b2bAcceleratorCartToOrderJob;
	@Resource
	BusinessProcessService businessProcessService;
	@Resource
	CloneCartAction cloneCartAction;
	@Resource
	CalculateCartAction calculateCartAction;
	@Resource
	ProcessParameterHelper processParameterHelper;
	@Resource
	AuthorizePaymentAction authorizePaymentAction;
	@Resource
	CommandFactoryRegistry mockupCommandFactoryRegistry;
	@Resource
	DefaultCardPaymentServiceImpl cardPaymentService;
	@Resource
	DefaultPaymentServiceImpl paymentService;
	@Resource
	DefaultCommerceCheckoutService commerceCheckoutService;
	@Resource
	PlaceOrderAction placeOrderAction;
	@Resource
	CalculationService calculationService;
	@Resource
	ConfirmationAction confirmationAction;
	@Resource
	private BaseSiteService baseSiteService;
	@Mock
	EventService mockEventService;


	private TriggerModel triggerModel = null;
	private CartToOrderCronJobModel cartToOrderCronJob = null;


	protected void setCronJobToTrigger(final CronJobModel cronJob, final List<TriggerModel> triggers)
	{
		for (final TriggerModel trigger : triggers)
		{
			trigger.setCronJob(cronJob);
		}
		cronJob.setTriggers(triggers);
	}

	@Before
	public void beforeTest() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		// inject a mock payment provider
		cardPaymentService.setCommandFactoryRegistry(mockupCommandFactoryRegistry);
		paymentService.setCardPaymentService(cardPaymentService);
		commerceCheckoutService.setPaymentService(paymentService);

		createCoreData();
		createDefaultCatalog();

		importCsv("/yb2bacceleratorcore/test/testOrganizations.csv", "utf-8");
		importCsv("/yb2bacceleratorcore/test/testB2BCommerceCart.csv", "utf-8");

		final CartModel modelByExample = new CartModel();
		modelByExample.setCode("dc_shhCart_b2bas");

		final CartModel cart = flexibleSearchService.getModelByExample(modelByExample);
		Assert.assertNotNull(cart);
		cartService.setSessionCart(cart);
		userService.setCurrentUser(cart.getUser());

		if (flexibleSearchService
				.search(
						"SELECT {" + ServicelayerJobModel.PK + "} FROM {" + ServicelayerJobModel._TYPECODE + "} WHERE " + "{"
								+ ServicelayerJobModel.SPRINGID + "}=?springid",
						Collections.singletonMap("springid", "b2bAcceleratorCartToOrderJob")).getResult().isEmpty())
		{
			final ServicelayerJobModel servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
			servicelayerJobModel.setCode("b2bAcceleratorCartToOrderJob");
			servicelayerJobModel.setSpringId("b2bAcceleratorCartToOrderJob");
			modelService.save(servicelayerJobModel);
		}

		final Date startDate = new Date();
		final Integer day = Integer.valueOf(5);
		final Integer week = Integer.valueOf(2);
		final List<DayOfWeek> days = new ArrayList<DayOfWeek>();
		days.add(DayOfWeek.TUESDAY);
		days.add(DayOfWeek.FRIDAY);
		triggerModel = modelService.create(TriggerModel.class);
		triggerModel.setRelative(Boolean.TRUE);
		triggerModel.setActivationTime(startDate);
		triggerModel.setDay(day);
		triggerModel.setWeekInterval(week);
		triggerModel.setDaysOfWeek(days);

		cartToOrderCronJob = modelService.create(CartToOrderCronJobModel.class);
		cartToOrderCronJob.setCart(cartService.getSessionCart());
		cartToOrderCronJob.setDeliveryAddress(userService.getCurrentUser().getDefaultShipmentAddress());
		cartToOrderCronJob.setPaymentAddress(userService.getCurrentUser().getDefaultPaymentAddress());
		cartToOrderCronJob.setPaymentInfo(cartService.getSessionCart().getPaymentInfo());
		setCronJobToTrigger(cartToOrderCronJob, Collections.singletonList(triggerModel));
		cartToOrderCronJob.setJob(cronJobService.getJob("b2bAcceleratorCartToOrderJob"));
		modelService.save(cartToOrderCronJob);
		final BaseSiteModel site = baseSiteService.getBaseSiteForUID("b2bstoretemplate");
		Assert.assertNotNull("no site found for id 'site'", site);
		baseSiteService.setCurrentBaseSite(site, false);
	}

	@Test
	public void test() throws Exception
	{//One test method in order to have beforeTest method called only once. don't use BeforeClass annotation
		testCloneCartAction();
		testRecalculateCartAction();
		testAuthorizePaymentAction();
		testPlaceOrderAction();
		testConfirmationAction();
	}

	public void testCloneCartAction() throws Exception
	{
		Assert.assertNotNull("cart not null", cartService.getSessionCart());
		Assert.assertNotNull("user not null", cartService.getSessionCart().getUser());
		Assert.assertEquals("DC S No", cartService.getSessionCart().getUser().getUid());
		final ReplenishmentProcessModel replenishmentProcessModel = createReplenishmentProcess();
		cloneCartAction.executeAction(replenishmentProcessModel);
		this.modelService.save(replenishmentProcessModel);
		Assert.assertNotNull(processParameterHelper.getProcessParameterByName(replenishmentProcessModel, "cart"));
	}

	public void testRecalculateCartAction() throws Exception
	{
		Assert.assertNotNull("cart not null", cartService.getSessionCart());
		Assert.assertNotNull("user not null", cartService.getSessionCart().getUser());
		Assert.assertEquals("DC S No", cartService.getSessionCart().getUser().getUid());
		final ReplenishmentProcessModel replenishmentProcessModel = createReplenishmentProcess();
		Assert.assertFalse(processParameterHelper.containsParameter(replenishmentProcessModel, "cart"));
		processParameterHelper.setProcessParameter(replenishmentProcessModel, "cart", cartService.getSessionCart());
		modelService.save(replenishmentProcessModel);
		Assert.assertTrue(processParameterHelper.containsParameter(replenishmentProcessModel, "cart"));
		calculateCartAction.executeAction(replenishmentProcessModel);
	}

	public void testAuthorizePaymentAction() throws Exception
	{
		Assert.assertNotNull("cart not null", cartService.getSessionCart());
		Assert.assertNotNull("user not null", cartService.getSessionCart().getUser());
		Assert.assertEquals("DC S No", cartService.getSessionCart().getUser().getUid());
		Assert.assertNotNull(cartService.getSessionCart().getPaymentInfo());
		Assert.assertThat(cartService.getSessionCart().getPaymentInfo(), instanceOf(CreditCardPaymentInfoModel.class));
		final ReplenishmentProcessModel replenishmentProcessModel = createReplenishmentProcess();
		Assert.assertFalse(processParameterHelper.containsParameter(replenishmentProcessModel, "cart"));
		processParameterHelper.setProcessParameter(replenishmentProcessModel, "cart", cartService.getSessionCart());
		modelService.save(replenishmentProcessModel);
		Assert.assertTrue(processParameterHelper.containsParameter(replenishmentProcessModel, "cart"));
		Assert.assertEquals(AbstractSimpleDecisionAction.Transition.OK,
				authorizePaymentAction.executeAction(replenishmentProcessModel));
	}

	public void testPlaceOrderAction() throws Exception
	{
		final CartModel sessionCart = cartService.getSessionCart();
		modelService.refresh(sessionCart);
		sessionCart.setCalculated(Boolean.TRUE);
		modelService.save(sessionCart);
		Assert.assertEquals(Boolean.TRUE, sessionCart.getCalculated());
		Assert.assertNotNull("cart not null", sessionCart);
		Assert.assertNotNull("user not null", sessionCart.getUser());
		Assert.assertEquals("DC S No", sessionCart.getUser().getUid());
		Assert.assertNotNull(sessionCart.getPaymentInfo());
		Assert.assertThat(sessionCart.getPaymentInfo(), instanceOf(CreditCardPaymentInfoModel.class));
		final ReplenishmentProcessModel replenishmentProcessModel = createReplenishmentProcess();
		Assert.assertFalse(processParameterHelper.containsParameter(replenishmentProcessModel, "cart"));
		processParameterHelper.setProcessParameter(replenishmentProcessModel, "cart", sessionCart);
		modelService.save(replenishmentProcessModel);
		Assert.assertTrue(processParameterHelper.containsParameter(replenishmentProcessModel, "cart"));
		placeOrderAction.execute(replenishmentProcessModel);
	}

	public void testConfirmationAction() throws Exception
	{
		Assert.assertNotNull(mockEventService);
		confirmationAction.setEventService(mockEventService);
		final CartModel sessionCart = cartService.getSessionCart();
		modelService.refresh(sessionCart);
		sessionCart.setCalculated(Boolean.TRUE);
		modelService.save(sessionCart);
		Assert.assertEquals(Boolean.TRUE, sessionCart.getCalculated());
		Assert.assertNotNull("cart not null", sessionCart);
		Assert.assertNotNull("user not null", sessionCart.getUser());
		Assert.assertEquals("DC S No", sessionCart.getUser().getUid());
		Assert.assertNotNull(sessionCart.getPaymentInfo());
		final ReplenishmentProcessModel replenishmentProcessModel = createReplenishmentProcess();
		Assert.assertFalse(processParameterHelper.containsParameter(replenishmentProcessModel, "order"));
		processParameterHelper.setProcessParameter(replenishmentProcessModel, "order",
				commerceCheckoutService.placeOrder(cartService.getSessionCart()));
		modelService.save(replenishmentProcessModel);
		Assert.assertTrue(processParameterHelper.containsParameter(replenishmentProcessModel, "order"));
		confirmationAction.executeAction(replenishmentProcessModel);
	}


	protected ReplenishmentProcessModel createReplenishmentProcess()
	{
		final ReplenishmentProcessModel replenishmentProcessModel = modelService.create(ReplenishmentProcessModel.class);
		replenishmentProcessModel.setCartToOrderCronJob(cartToOrderCronJob);
		replenishmentProcessModel.setCode(String.valueOf(System.currentTimeMillis()));
		replenishmentProcessModel.setProcessDefinitionName("replenishmentOrderProcess");
		modelService.save(replenishmentProcessModel);
		return replenishmentProcessModel;
	}

}
