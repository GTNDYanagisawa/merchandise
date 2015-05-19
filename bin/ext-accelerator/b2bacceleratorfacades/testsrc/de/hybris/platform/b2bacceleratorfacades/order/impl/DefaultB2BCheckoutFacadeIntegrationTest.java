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
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.order.impl.B2BAcceleratorCartToOrderJob;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.datasetup.ServiceLayerDataSetup;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.Utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations =
{ "classpath:/payment-spring-test.xml" })
@IntegrationTest
public class DefaultB2BCheckoutFacadeIntegrationTest extends BaseCommerceBaseTest
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BCheckoutFacadeIntegrationTest.class);

	@Resource
	CronJobService cronJobService;
	@Resource
	ModelService modelService;
	@Resource
	CartService cartService;
	@Resource
	UserService userService;
	@Resource
	ServiceLayerDataSetup serviceLayerDataSetup;
	@Resource
	B2BAcceleratorCartToOrderJob b2bAcceleratorCartToOrderJob;
	@Resource
	BusinessProcessService businessProcessService;
	@Resource
	CommandFactoryRegistry mockupCommandFactoryRegistry;
	@Resource
	DefaultCardPaymentServiceImpl cardPaymentService;
	@Resource
	DefaultPaymentServiceImpl paymentService;
	@Resource
	DefaultCommerceCheckoutService commerceCheckoutService;
	@Mock
	EventService mockEventService;
	@Resource
	B2BCheckoutFacade b2bCheckoutFacade;
	@Resource
	SessionService sessionService;
	@Resource
	BaseSiteService baseSiteService;

	@Before
	public void beforeTest() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/b2bacceleratorfacades/test/testOrganizations.csv", "utf-8");
		importCsv("/b2bacceleratorfacades/test/testB2BCommerceCart.csv", "utf-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("b2bstoretemplate"), false);

		final CartModel modelByExample = new CartModel();
		modelByExample.setCode("dc_shhCart_b2baf");
		final CartModel cart = flexibleSearchService.getModelByExample(modelByExample);
		Assert.assertNotNull("dc_shhCart_b2baf cart.code should have been found", cart);
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

		// inject a mock payment provider
		cardPaymentService.setCommandFactoryRegistry(mockupCommandFactoryRegistry);
		paymentService.setCardPaymentService(cardPaymentService);
		commerceCheckoutService.setPaymentService(paymentService);
		commerceCheckoutService.setBaseSiteService(baseSiteService);
	}

	@Test
	public void testScheduleOrder() throws Exception
	{
		Assert.assertNotNull("cart not null", cartService.getSessionCart());
		Assert.assertNotNull("user not null", cartService.getSessionCart().getUser());
		Assert.assertEquals("DC S HH", cartService.getSessionCart().getUser().getUid());
		final TriggerData triggerData = new TriggerData();
		triggerData.setDay(Integer.valueOf(1));
		triggerData.setActivationTime(DateUtils.addDays(new Date(), 1));
		triggerData.setRelative(Boolean.TRUE);
		triggerData.setDaysOfWeek(Collections.singletonList(DayOfWeek.FRIDAY));
		triggerData.setMonth(Integer.valueOf(1));
		triggerData.setWeekInterval(Integer.valueOf(1));
		final ScheduledCartData scheduledCartData = b2bCheckoutFacade.scheduleOrder(triggerData);
		Assert.assertNotNull(scheduledCartData);

		// scheduleOrder method triggers a replenishmentOrderPlacedEmailProcess we want to try and wait for it to complete otherwise
		// the hybris test framwork will start removing items created during the test and the Process will encounter de.hybris.platform.servicelayer.exceptions.ModelSavingException: Entity not found
		TestUtils.disableFileAnalyzer("GenerateEmailAction logs ERROR when content catalog can't be found");
		waitForProcessToEnd("replenishmentOrderPlacedEmailProcess", 2000);
		TestUtils.enableFileAnalyzer();
	}

	@Override
	protected List<BusinessProcessModel> getProcesses(final String processDefinitionName, final List<ProcessState> processStates)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("select {" + BusinessProcessModel.PK + "} from {"
				+ BusinessProcessModel._TYPECODE + "} where {" + BusinessProcessModel.STATE + "} in (?state) AND {"
				+ BusinessProcessModel.PROCESSDEFINITIONNAME + "} = ?processDefinitionName");
		query.addQueryParameter(BusinessProcessModel.PROCESSDEFINITIONNAME, processDefinitionName);
		query.addQueryParameter(BusinessProcessModel.STATE, processStates);
		final SearchResult<BusinessProcessModel> result = flexibleSearchService.search(query);
		return result.getResult();
	}


	@Override
	protected boolean waitForProcessToEnd(final String processDefinitionName, final long maxWait) throws InterruptedException
	{
		final long start = System.currentTimeMillis();
		while (true)
		{
			final List<BusinessProcessModel> processes = getProcesses(processDefinitionName, Arrays.asList(new ProcessState[]
			{ ProcessState.RUNNING, ProcessState.CREATED, ProcessState.WAITING }));

			if (CollectionUtils.isEmpty(processes))
			{
				return true;
			}
			if (System.currentTimeMillis() - start > maxWait)
			{
				LOG.warn(String.format("BusinessProcesses with processDefinitionName %s are still in running! Waited for %s",
						processDefinitionName, Utilities.formatTime(System.currentTimeMillis() - start)));
				return false;
			}
			else
			{
				Thread.sleep(1000);
			}
		}
	}
}
