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
package de.hybris.platform.webservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.dto.c2l.CountryDTO;
import de.hybris.platform.core.dto.c2l.RegionDTO;
import de.hybris.platform.core.dto.user.AddressDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * Test for checking HOR-940
 **/
public class CrudEventsTest extends AbstractWebServicesTest
{
	private static final Logger LOG = Logger.getLogger(CrudEventsTest.class);


	private static final String TEST_REGION_COUNTRY = "testCountry1";
	private static final String TEST_REGION_CODE = "testRegion1";
	private static final String TEST_REGION_NAME = TEST_REGION_CODE + " name";
	private static final String NEW_REGION_ISOCODE = "testRegion3";
	private static final String NEW_REGION_NAME = NEW_REGION_ISOCODE + " name";

	@Resource
	private EventService eventService;

	@Resource
	private CommonI18NService commonI18NService;

	private RegionModel testRegion;

	private TestCrudEventListener listener;

	public CrudEventsTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpRegions() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCountries();
		testRegion = commonI18NService.getRegion(commonI18NService.getCountry(TEST_REGION_COUNTRY), TEST_REGION_CODE);
		//C2LManager.getInstance().getCountryByIsoCode("testCountry1").getRegionByCode(TEST_REGION_CODE);
		listener = new TestCrudEventListener();
		eventService.registerEventListener(listener);

	}

	@After
	public void tearDown()
	{
		eventService.unregisterEventListener(listener);

	}

	//GET
	@Test
	public void testGetRegion()
	{
		final String URI = "regions/";

		final ClientResponse result = webResource.path(URI + testRegion.getIsocode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final RegionDTO region = result.getEntity(RegionDTO.class);
		assertNotNull("No region within body at response: " + result, region);
		assertEquals("Invalid region in response: " + result, testRegion.getIsocode(), region.getIsocode());
		assertEquals("Invalid region name in response: " + result, TEST_REGION_NAME, region.getName());
		assertEquals("Invalid country in response: ", TEST_REGION_COUNTRY, region.getCountry().getIsocode());



		final AbstractWebserviceActionEvent before = new AbstractWebserviceActionEvent(TEST_REGION_CODE, null, null, null,
				webResource.path(URI + testRegion.getIsocode()).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{
				// YTODO Auto-generated method stub
				return AbstractWebserviceActionEvent.CRUD_METHOD.GET;
			}

			@Override
			public TRIGGER getTriggered()
			{
				// YTODO Auto-generated method stub
				return AbstractWebserviceActionEvent.TRIGGER.BEFORE;
			}
		};

		final AbstractWebserviceActionEvent after = new AbstractWebserviceActionEvent(TEST_REGION_CODE, null, null, null,
				webResource.path(URI + testRegion.getIsocode()).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{
				// YTODO Auto-generated method stub
				return AbstractWebserviceActionEvent.CRUD_METHOD.GET;
			}

			@Override
			public TRIGGER getTriggered()
			{
				// YTODO Auto-generated method stub
				return AbstractWebserviceActionEvent.TRIGGER.AFTER;
			}
		};

		checkMessages(listener.getMessages(), before, after);
	}


	//PUT
	@Test
	public void testPutNewRegion() throws ConsistencyCheckException
	{
		final String URI = "regions/";

		final CountryDTO countryDto = new CountryDTO();
		countryDto.setIsocode(testRegion.getCountry().getIsocode());
		final RegionDTO newRegionDto = new RegionDTO();
		newRegionDto.setIsocode(NEW_REGION_ISOCODE);
		newRegionDto.setName(NEW_REGION_NAME);
		newRegionDto.setCountry(countryDto);

		//send message for creating of the region
		final ClientResponse result = webResource.path(URI + NEW_REGION_ISOCODE).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(newRegionDto).put(ClientResponse.class);
		result.bufferEntity();

		//check the response
		assertCreated(result, true);

		//check if the region has been created
		assertEquals("The number of country regions is incorrect", 3, testRegion.getCountry().getRegions().size());

		final RegionModel createdRegion = commonI18NService.getRegion(testRegion.getCountry(), NEW_REGION_ISOCODE);
		///final RegionModel createdRegion = C2LManager.getInstance().getRegionByCode(testRegion.getCountry(), NEW_REGION_ISOCODE);

		assertNotNull("The region wasn't created", createdRegion);
		assertEquals("The region's name property wasn't saved correctly", createdRegion.getName(), NEW_REGION_NAME);
		assertEquals("The region's country wasn't saved correctly", testRegion.getCountry().getIsocode(), createdRegion
				.getCountry().getIsocode());
		//no need to remove region here, it'll be removed automatically together with test countries.

		final AbstractWebserviceActionEvent before = new AbstractWebserviceActionEvent(NEW_REGION_ISOCODE, null, null, null,
				webResource.path(URI + NEW_REGION_ISOCODE).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{

				return AbstractWebserviceActionEvent.CRUD_METHOD.PUT;
			}

			@Override
			public TRIGGER getTriggered()
			{
				return AbstractWebserviceActionEvent.TRIGGER.BEFORE;
			}
		};

		final AbstractWebserviceActionEvent after = new AbstractWebserviceActionEvent(NEW_REGION_ISOCODE, null, null, null,
				webResource.path(URI + NEW_REGION_ISOCODE).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{

				return AbstractWebserviceActionEvent.CRUD_METHOD.PUT;
			}

			@Override
			public TRIGGER getTriggered()
			{
				return AbstractWebserviceActionEvent.TRIGGER.AFTER;
			}
		};

		checkMessages(listener.getMessages(), before, after);
	}

	//POST
	@Test
	public void testPostAddresses() throws ConsistencyCheckException, JaloBusinessException
	{

		final String ADRESSES_URI = "addresses";

		createTestCustomers();
		final Customer testCustomer = UserManager.getInstance().getCustomerByLogin("testCustomer1");

		final AddressDTO address = new AddressDTO();
		final UserDTO user = new UserDTO();
		//create one address for the test user,
		//since this user and all his addresses will be deleted after the whole test, we do not delete this address here
		user.setUid(testCustomer.getUID());
		address.setOwner(user);
		final ClientResponse result = webResource.path(ADRESSES_URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(address).post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);

		final AddressDTO resultAddress = result.getEntity(AddressDTO.class);

		assertEquals("Wrong customer id at response: " + result, testCustomer.getUID(), ((UserModel) modelService.get(PK
				.fromLong(resultAddress.getOwner().getPk().longValue()))).getUid());


		final AbstractWebserviceActionEvent before = new AbstractWebserviceActionEvent(null, null, null, null, webResource.path(
				ADRESSES_URI).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{

				return AbstractWebserviceActionEvent.CRUD_METHOD.POST;
			}

			@Override
			public TRIGGER getTriggered()
			{
				return AbstractWebserviceActionEvent.TRIGGER.BEFORE;
			}
		};

		final AbstractWebserviceActionEvent after = new AbstractWebserviceActionEvent(null, null, null, null, webResource.path(
				ADRESSES_URI).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{

				return AbstractWebserviceActionEvent.CRUD_METHOD.POST;
			}

			@Override
			public TRIGGER getTriggered()
			{
				return AbstractWebserviceActionEvent.TRIGGER.AFTER;
			}
		};

		checkMessages(listener.getMessages(), before, after);

	}

	//DELETE
	@Test
	public void testDeleteRegion() throws IOException
	{
		final String URI = "regions/";

		final String testRegionCode = testRegion.getIsocode();
		final CountryModel testRegionCountry = testRegion.getCountry();

		//assert there are only two regions at the beginning
		assertEquals("The number of country regions is incorrect", 2, testRegionCountry.getRegions().size());

		//send message for deleting of the region
		final ClientResponse deleteRegionResult = webResource.path(URI + testRegionCode).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		deleteRegionResult.bufferEntity();
		//check the response
		assertOk(deleteRegionResult, true);

		final AbstractWebserviceActionEvent before = new AbstractWebserviceActionEvent(TEST_REGION_CODE, null, null, null,
				webResource.path(URI + TEST_REGION_CODE).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{

				return AbstractWebserviceActionEvent.CRUD_METHOD.DELETE;
			}

			@Override
			public TRIGGER getTriggered()
			{
				return AbstractWebserviceActionEvent.TRIGGER.BEFORE;
			}
		};

		final AbstractWebserviceActionEvent after = new AbstractWebserviceActionEvent(TEST_REGION_CODE, null, null, null,
				webResource.path(URI + TEST_REGION_CODE).getURI())
		{
			@Override
			public CRUD_METHOD getCrudMethod()
			{

				return AbstractWebserviceActionEvent.CRUD_METHOD.DELETE;
			}

			@Override
			public TRIGGER getTriggered()
			{
				return AbstractWebserviceActionEvent.TRIGGER.AFTER;
			}
		};

		checkMessages(listener.getMessages(), before, after);
	}

	/**
	 * checks message event(s) , if they suit expected ones
	 */
	private void checkMessages(final List<AbstractWebserviceActionEvent> receivedMessages,
			final AbstractWebserviceActionEvent... expectedMessages)
	{
		Assert.assertEquals("Expected messages " + expectedMessages.length + " not equal received messages "
				+ receivedMessages.size(), receivedMessages.size(), expectedMessages.length);
		int index = 0;
		for (final AbstractWebserviceActionEvent singleMsg : expectedMessages)
		{

			Assert.assertEquals("Expected id " + singleMsg.getResourceId() + " not equal received id "
					+ receivedMessages.get(index).getResourceId(), singleMsg.getResourceId(), receivedMessages.get(index)
					.getResourceId());
			Assert.assertEquals("Expected uri " + singleMsg.getUri() + " not equal received uri "
					+ receivedMessages.get(index).getUri(), singleMsg.getUri(), receivedMessages.get(index).getUri());
			index++;
		}


	}




	private class TestCrudEventListener extends AbstractEventListener<AbstractEvent>
	{

		private final java.util.List<AbstractWebserviceActionEvent> messages = new ArrayList<AbstractWebserviceActionEvent>();

		@Override
		protected void onEvent(final AbstractEvent event)
		{

			if (event instanceof AbstractWebserviceActionEvent)
			{
				final AbstractWebserviceActionEvent eventImpl = (AbstractWebserviceActionEvent) event;
				messages.add(eventImpl);
				LOG.info(eventImpl);
				if (LOG.isDebugEnabled())
				{
					LOG.debug(event);

				}
			}
		}


		public java.util.List<AbstractWebserviceActionEvent> getMessages()
		{
			return messages;
		}

	}

}
