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
package de.hybris.platform.mobileservices.text;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.voucher.model.PromotionVoucherModel;
import de.hybris.platform.voucher.model.SerialVoucherModel;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class UseCaseMTBS09VoucherCodesTest extends MobileOptionalsTestBase
{
	private static final Logger LOG = Logger.getLogger(UseCaseMTBS09VoucherCodesTest.class.getName());

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createHardwareCatalog();

		createConfiguration1();
	}

	private void createConfiguration1() throws Exception
	{
		final PromotionVoucherModel votest1 = modelService.create(PromotionVoucherModel.class);
		final SerialVoucherModel votest2 = modelService.create(SerialVoucherModel.class);
		votest1.setCode("votest1");
		votest1.setVoucherCode("PROMO1");
		votest1.setName("test1");
		votest1.setValue(Double.valueOf(1.0));
		modelService.save(votest1);
		votest2.setCode("vot");
		votest2.setName("test2");
		votest2.setDescription("asssa");
		votest2.setValue(Double.valueOf(5.0));
		modelService.save(votest2);


		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/testdata10.csv", "UTF-8");
	}

	@Test
	public void testPromotionVoucher() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Hi Ho");
		assertNotNull("Message could not be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageStatus status = blockUsingModel(pk);
		final MobileMessageContextModel ctx = getMessage(pk);
		LOG.info("text status returned :" + status);
		LOG.info("text info:" + ctx.getUsingDefaultAction());
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(pk));
		assertEquals("test catalog failed", status, MobileMessageStatus.SENT);
		assertFalse("test catalog failed", Boolean.TRUE.equals(ctx.getUsingDefaultAction()));
	}

	@Test
	public void testSerialVoucher() throws Exception
	{
		final String pk = messageReceived("ES", "ES", "111", "34699111222", "Ho Ho");
		assertNotNull("Message could not be passed to the reception layer", pk);
		LOG.info("Got pk " + pk);
		final MobileMessageStatus status = blockUsingModel(pk);
		final MobileMessageContextModel ctx = getMessage(pk);
		LOG.info("text status returned :" + status);
		LOG.info("text info:" + ctx.getUsingDefaultAction());
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(pk));
		assertEquals("test catalog failed", status, MobileMessageStatus.SENT);
		assertFalse("test catalog failed", Boolean.TRUE.equals(ctx.getUsingDefaultAction()));
	}

}
