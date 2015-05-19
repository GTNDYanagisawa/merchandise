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
package de.hybris.platform.print.jalo;

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for extension Print
 */
public class PrintTest extends HybrisJUnit4TransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PrintTest.class.getName());

	private AutoCometJob job1;
	private AutoCometJob job2;
	private AutoCometJob job3;
	private AutoCometJob job4;

	private Currency eur;
	private Currency usd;

	private Language langDE;
	private Language langEN;

	@SuppressWarnings("boxing")
	@Before
	public void setUp() throws Exception
	{
		final C2LManager c2lMan = C2LManager.getInstance();
		// Creating Currencies, Languages, SessionContexts etc. for test
		usd = c2lMan.createCurrency("usd");
		usd.setSymbol("$");
		eur = c2lMan.createCurrency("eur");
		eur.setSymbol("€");

		langEN = c2lMan.getLanguageByIsoCode("en");
		langDE = c2lMan.createLanguage("de");

		assertNotNull(usd);
		assertNotNull(eur);
		assertNotNull(langEN);
		assertNotNull(langDE);

		final SessionContext ctxEN = jaloSession.createSessionContext();
		ctxEN.setLanguage(c2lMan.getLanguageByIsoCode("en"));
		final SessionContext ctxDE = jaloSession.createSessionContext();
		ctxDE.setLanguage(c2lMan.getLanguageByIsoCode("de"));

		final Map<String, Object> values = new HashMap<String, Object>();


		// Creating AutoCometJobs for test
		values.put(AutoCometJob.ID, Integer.valueOf(1));
		values.put(AutoCometJob.NAME, "Job 1");
		values.put(AutoCometJob.STATUSID, Integer.valueOf(1000));
		values.put(AutoCometJob.STARTTIME, new Date());
		job1 = PrintManager.getInstance().createAutoCometJob(values);

		values.put(AutoCometJob.ID, Integer.valueOf(2));
		values.put(AutoCometJob.NAME, "Job 2");
		values.put(AutoCometJob.STARTTIME, new Date());
		job2 = PrintManager.getInstance().createAutoCometJob(values);

		values.put(AutoCometJob.ID, Integer.valueOf(3));
		values.put(AutoCometJob.NAME, "Job 3");
		values.put(AutoCometJob.STARTTIME, new Date());
		job3 = PrintManager.getInstance().createAutoCometJob(values);

		values.put(AutoCometJob.ID, Integer.valueOf(4));
		values.put(AutoCometJob.NAME, "Job 4");
		values.put(AutoCometJob.STARTTIME, new Date());
		values.put(AutoCometJob.STATUSID, Integer.valueOf(1005));
		job4 = PrintManager.getInstance().createAutoCometJob(values);

		assertNotNull(job1);
		assertNotNull(job2);
		assertNotNull(job3);
		assertNotNull(job4);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testFormatter() throws Exception
	{
		final double decimal = 123.23;
		final Double percent = new Double(0.42);
		final int integer = 424242442;
		final Date date = new Date();

		final JaloSession j = JaloSession.getCurrentSession();
		j.getSessionContext().setCurrency(usd);
		j.getSessionContext().setLanguage(langEN);

		log.debug("LOCALE: " + j.getSessionContext().getLocale().toString());

		log.debug(PrintManager.getInstance().getFormatUtils().getDateTime(date));
		log.debug(PrintManager.getInstance().getFormatUtils().getDecimal(Double.valueOf(decimal)));
		log.debug(PrintManager.getInstance().getFormatUtils().getPercent(percent));
		log.debug(PrintManager.getInstance().getFormatUtils().getInteger(Integer.valueOf(integer)));

		log.debug(PrintManager.getInstance().getFormatUtils().getPrice(Double.valueOf(decimal)));
		j.getSessionContext().setLanguage(langDE);
		j.getSessionContext().setCurrency(eur);
		log.debug("LOCALE: " + j.getSessionContext().getLocale().toString());

		log.debug(PrintManager.getInstance().getFormatUtils().getPrice(Double.valueOf(decimal), "#,##0.0#"));
		log.debug("Lang: " + j.getSessionContext().getLanguage().getIsoCode());
	}


	//	public void testAutoCometJobGet()
	//	{
	//		AutoCometJob job = PrintManager.getInstance().getAutoCometJob(1);
	//		assertEquals(job1, job);
	//		job = PrintManager.getInstance().getAutoCometJob(2);
	//		assertEquals(job2, job);
	//		job = PrintManager.getInstance().getAutoCometJob(3);
	//		assertEquals(job3, job);
	//		job = PrintManager.getInstance().getAutoCometJob(4);
	//		assertEquals(job4, job);
	//	}
	//
	//	public void testAutoCometJobGetNext()
	//	{
	//		AutoCometJob job = PrintManager.getInstance().getNextAutoCometJob();
	//		assertEquals(job1, job);
	//		job.setStatusId(1004);
	//
	//		job = PrintManager.getInstance().getNextAutoCometJob();
	//		assertEquals(job2, job);
	//		job.setStatusId(1004);
	//
	//		job = PrintManager.getInstance().getNextAutoCometJob();
	//		assertEquals(job3, job);
	//		job.setStatusId(1004);
	//	}
}
