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
package de.hybris.platform.integration.cis.payment.populators;

import com.hybris.cis.api.fraud.model.CisFraudTransactionResult;
import com.hybris.cis.api.model.CisDecision;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * @author florent
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisFraudTransactionResultPopulatorTest
{

	private CisFraudTransactionResultPopulator populator;

	@Mock
	private CisFraudTransactionResult cisFraudTransactionResult;

	@Before
	public void setup()
	{
		populator = new CisFraudTransactionResultPopulator();
		MockitoAnnotations.initMocks(this.getClass());
	}

	@Test
	public void shouldPopulate()
	{
		final PaymentTransactionEntryModel transactionEntry = new PaymentTransactionEntryModel();
		BDDMockito.when(cisFraudTransactionResult.getNewDecision()).thenReturn(CisDecision.ACCEPT);


		populator.populate(cisFraudTransactionResult, transactionEntry);
		org.junit.Assert.assertEquals(PaymentTransactionType.REVIEW_DECISION, transactionEntry.getType());
		org.junit.Assert.assertEquals(TransactionStatus.ACCEPTED.toString(), transactionEntry.getTransactionStatus());
		org.junit.Assert.assertEquals(TransactionStatusDetails.SUCCESFULL.toString(),
				transactionEntry.getTransactionStatusDetails());
	}


}
