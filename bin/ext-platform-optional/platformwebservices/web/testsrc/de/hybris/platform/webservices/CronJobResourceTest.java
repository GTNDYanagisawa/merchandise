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

import de.hybris.platform.core.dto.c2l.CurrencyDTO;
import de.hybris.platform.core.dto.c2l.LanguageDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.cronjob.dto.CronJobDTO;
import de.hybris.platform.cronjob.dto.JobDTO;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.jalo.ConsistencyCheckException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CronJobResourceTest extends AbstractWebServicesTest
{
	private static final String CODECRONJOB = "testCronJob1";
	private static final String URI = "cronjobs/" + CODECRONJOB;
	private static final String URI_BASE = "cronjobs/";

	private CronJobModel testCronJobModel;

	/**
	 * @throws Exception
	 */
	public CronJobResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpCatalogs() throws ConsistencyCheckException
	{
		createTestsCronJobs();
		testCronJobModel = cronJobService.getCronJob(CODECRONJOB);
	}

	@Test
	public void testGetCronJob()
	{

		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CronJobDTO testCronJobDTO = result.getEntity(CronJobDTO.class);
		assertNotNull("No cronjob within body at response: " + result, testCronJobDTO);
		assertEqual(testCronJobModel, testCronJobDTO, "code", "logToDatabase", "logToFile", "emailAddress", "requestAbort",
				"changeRecordingEnabled", "nodeID", "singleExecutable", "removeOnExit", "requestAbortStep", "priority", "active");
		assertEquals("Wrong sessionUser at response: " + result, testCronJobModel.getSessionUser().getUid(), testCronJobDTO
				.getSessionUser().getUid());
		assertEquals("Wrong sessionLanguage at response: " + result, testCronJobModel.getSessionLanguage().getIsocode(),
				testCronJobDTO.getSessionLanguage().getIsocode());
		assertEquals("Wrong sessionCurrency at response: " + result, testCronJobModel.getSessionCurrency().getIsocode(),
				testCronJobDTO.getSessionCurrency().getIsocode());
		assertEquals("Wrong logLevelFile at response: " + result, testCronJobModel.getLogLevelFile().getCode(), testCronJobDTO
				.getLogLevelFile());
		assertEquals("Wrong logLevelDatabase at response: " + result, testCronJobModel.getLogLevelDatabase().getCode(),
				testCronJobDTO.getLogLevelDatabase());
		assertEquals("Wrong errorMode at response: " + result, testCronJobModel.getErrorMode().getCode(), testCronJobDTO
				.getErrorMode());


		//assertEquals("Wrong job at response: " + result, testCronJobModel.getJob().getCode(), testCronJobDTO.getJob());
		// NEW
		assertEquals("Wrong job at response: " + result, testCronJobModel.getJob().getCode(), testCronJobDTO.getJob().getCode());

	}

	@Test
	public void testPutNewPlainCronJob()
	{

		final CronJobDTO cronJobDTO = new CronJobDTO();
		cronJobDTO.setCode("newCronJob1");
		//cronJobDTO.setJob("Job1");
		// NEW
		final JobDTO job = new JobDTO();
		job.setCode("Job1");
		cronJobDTO.setJob(job);

		cronJobDTO.setLogToDatabase(Boolean.FALSE);
		cronJobDTO.setLogToFile(Boolean.FALSE);
		cronJobDTO.setEmailAddress("new@hybris.de");
		cronJobDTO.setRequestAbort(Boolean.FALSE);
		cronJobDTO.setChangeRecordingEnabled(Boolean.FALSE);
		cronJobDTO.setNodeID(Integer.valueOf(0));
		cronJobDTO.setSingleExecutable(Boolean.FALSE);
		cronJobDTO.setRemoveOnExit(Boolean.FALSE);
		cronJobDTO.setRequestAbortStep(Boolean.FALSE);
		cronJobDTO.setPriority(Integer.valueOf(1));
		cronJobDTO.setActive(Boolean.FALSE);
		final UserDTO userDTO = new UserDTO();
		userDTO.setUid("admin");
		final LanguageDTO languageDTO = new LanguageDTO();
		languageDTO.setIsocode("testLang1");
		final CurrencyDTO currencyDTO = new CurrencyDTO();
		currencyDTO.setIsocode("EUR");
		cronJobDTO.setSessionUser(userDTO);
		cronJobDTO.setSessionLanguage(languageDTO);
		cronJobDTO.setSessionCurrency(currencyDTO);
		cronJobDTO.setLogLevelFile("INFO");
		cronJobDTO.setLogLevelDatabase("WARNING");
		cronJobDTO.setErrorMode("IGNORE");

		ClientResponse result = webResource.path(URI_BASE + cronJobDTO.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(cronJobDTO).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		result = webResource.path(URI_BASE + cronJobDTO.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CronJobDTO cronJobResultDTO = result.getEntity(CronJobDTO.class);

		assertEqual(cronJobDTO, cronJobResultDTO, "code", "logToDatabase", "logToFile", "emailAddress", "requestAbort",
				"changeRecordingEnabled", "nodeID", "singleExecutable", "removeOnExit", "requestAbortStep", "priority", "active");
		assertEquals("Wrong sessionUser at response: " + result, cronJobResultDTO.getSessionUser().getUid(), cronJobDTO
				.getSessionUser().getUid());
		assertEquals("Wrong sessionLanguage at response: " + result, cronJobResultDTO.getSessionLanguage().getIsocode(), cronJobDTO
				.getSessionLanguage().getIsocode());
		assertEquals("Wrong sessionCurrency at response: " + result, cronJobResultDTO.getSessionCurrency().getIsocode(), cronJobDTO
				.getSessionCurrency().getIsocode());
		assertEquals("Wrong logLevelFile at response: " + result, cronJobResultDTO.getLogLevelFile(), cronJobDTO.getLogLevelFile());
		assertEquals("Wrong logLevelDatabase at response: " + result, cronJobResultDTO.getLogLevelDatabase(), cronJobDTO
				.getLogLevelDatabase());
		assertEquals("Wrong errorMode at response: " + result, cronJobResultDTO.getErrorMode(), cronJobDTO.getErrorMode());


		//assertEquals("Wrong job at response: " + result, cronJobResultDTO.getJob(), cronJobDTO.getJob());
		// NEW
		assertEquals("Wrong job at response: " + result, cronJobResultDTO.getJob().getCode(), cronJobDTO.getJob().getCode());
	}

	@Test
	public void testPutUpdateCronJob()
	{

		final CronJobDTO cronJobDTO = new CronJobDTO();
		cronJobDTO.setCode("testCronJob1");
		//could not set given values because attribute is not writable
		//cronJobDTO.setJob("Job1");
		cronJobDTO.setLogToDatabase(Boolean.FALSE);
		cronJobDTO.setLogToFile(Boolean.FALSE);
		cronJobDTO.setEmailAddress("new@hybris.de");
		cronJobDTO.setRequestAbort(Boolean.FALSE);
		cronJobDTO.setChangeRecordingEnabled(Boolean.FALSE);
		//cronJobDTO.setNodeID(Integer.valueOf(1));
		cronJobDTO.setSingleExecutable(Boolean.FALSE);
		cronJobDTO.setRemoveOnExit(Boolean.FALSE);
		cronJobDTO.setRequestAbortStep(Boolean.FALSE);
		cronJobDTO.setPriority(Integer.valueOf(1));
		cronJobDTO.setActive(Boolean.FALSE);
		final UserDTO userDTO = new UserDTO();
		userDTO.setUid("admin");
		final LanguageDTO languageDTO = new LanguageDTO();
		languageDTO.setIsocode("testLang1");
		final CurrencyDTO currencyDTO = new CurrencyDTO();
		currencyDTO.setIsocode("EUR");
		cronJobDTO.setSessionUser(userDTO);
		cronJobDTO.setSessionLanguage(languageDTO);
		cronJobDTO.setSessionCurrency(currencyDTO);
		cronJobDTO.setLogLevelFile("INFO");
		cronJobDTO.setLogLevelDatabase("WARNING");
		cronJobDTO.setErrorMode("IGNORE");

		ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).entity(cronJobDTO).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CronJobDTO cronJobResultDTO = result.getEntity(CronJobDTO.class);

		assertEqual(cronJobDTO, cronJobResultDTO, "code", "logToDatabase", "logToFile", "emailAddress", "requestAbort",
				"changeRecordingEnabled", "singleExecutable", "removeOnExit", "requestAbortStep", "priority", "active");
		assertEquals("Wrong sessionUser at response: " + result, cronJobResultDTO.getSessionUser().getUid(), cronJobDTO
				.getSessionUser().getUid());
		assertEquals("Wrong sessionLanguage at response: " + result, cronJobResultDTO.getSessionLanguage().getIsocode(), cronJobDTO
				.getSessionLanguage().getIsocode());
		assertEquals("Wrong sessionCurrency at response: " + result, cronJobResultDTO.getSessionCurrency().getIsocode(), cronJobDTO
				.getSessionCurrency().getIsocode());
		assertEquals("Wrong logLevelFile at response: " + result, cronJobResultDTO.getLogLevelFile(), cronJobDTO.getLogLevelFile());
		assertEquals("Wrong logLevelDatabase at response: " + result, cronJobResultDTO.getLogLevelDatabase(), cronJobDTO
				.getLogLevelDatabase());
		assertEquals("Wrong errorMode at response: " + result, cronJobResultDTO.getErrorMode(), cronJobDTO.getErrorMode());


		//assertEquals("Wrong job at response: " + result, cronJobResultDTO.getJob(), "Job1");
		// NEW
		assertEquals("Wrong job at response: " + result, cronJobResultDTO.getJob().getCode(), "Job1");

	}

	@Test
	public void testPostCreateCronJobCommand()
	{

		final String copiedCronJob = "copyOfTestCronJob1";
		final CronJobDTO cronJobDTO = new CronJobDTO();
		cronJobDTO.setCode(copiedCronJob);

		ClientResponse result = webResource.path(URI).queryParam("cmd", "CreateCronJobCommand").cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(cronJobDTO).post(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		result = webResource.path(URI_BASE + copiedCronJob).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CronJobDTO testCronJobDTO = result.getEntity(CronJobDTO.class);
		assertNotNull("No cronjob within body at response: " + result, testCronJobDTO);
		assertEquals("Wrong logLevelFile at response: " + result, testCronJobModel.getLogLevelFile().getCode(),
				testCronJobDTO.getLogLevelFile());
		assertEquals("Wrong logLevelDatabase at response: " + result, testCronJobModel.getLogLevelDatabase().getCode(),
				testCronJobDTO.getLogLevelDatabase());
		assertEquals("Wrong errorMode at response: " + result, testCronJobModel.getErrorMode().getCode(), testCronJobDTO
				.getErrorMode());


		//assertEquals("Wrong job at response: " + result, testCronJobModel.getJob().getCode(), testCronJobDTO.getJob());
		// NEW
		assertEquals("Wrong job at response: " + result, testCronJobModel.getJob().getCode(), testCronJobDTO.getJob().getCode());

	}

	@Test
	public void testPutStartCronJobCommand()
	{

		final CronJobDTO cronJobDTO = new CronJobDTO();
		cronJobDTO.setCode("testCronJob1");

		ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		CronJobDTO testCronJobDTO = result.getEntity(CronJobDTO.class);
		assertEquals("Wrong result at response: " + result, "UNKNOWN", testCronJobDTO.getResult());

		result = webResource.path(URI).queryParam("cmd", "StartCronJobCommand").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(cronJobDTO).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);
		while (cronJobService.isRunning(testCronJobModel))
		{
			//
		}
		result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		testCronJobDTO = result.getEntity(CronJobDTO.class);
		//assertEquals("Wrong result at response: " + result, "SUCCESS", testCronJobDTO.getResult());

	}

	@Test
	public void testDeleteCronJob()
	{

		ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(
				MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());

	}

}
