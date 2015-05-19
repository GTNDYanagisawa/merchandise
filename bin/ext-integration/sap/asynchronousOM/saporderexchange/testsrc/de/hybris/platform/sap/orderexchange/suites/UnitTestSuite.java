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

package de.hybris.platform.sap.orderexchange.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.hybris.platform.sap.orderexchange.cancellation.DefaultSAPOrderCancelStateMappingStrategyTest;
import de.hybris.platform.sap.orderexchange.cancellation.DefaultSapOrderCancelServiceTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubDeliveryTranslatorTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubGoodsIssueTranslatorTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubOrderCancelTranslatorTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubOrderCreationTranslatorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.AbstractRawItemBuilderTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderContributorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderEntryContributorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPartnerContributorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPaymentContributorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSalesConditionsContributorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendOrderCancelRequestToDataHubHelperTest;
import de.hybris.platform.sap.orderexchange.taskrunners.SendOrderCancelRequestAsCSVTaskRunnerTest;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
{ DefaultSapOrderCancelServiceTest.class, DefaultSAPOrderCancelStateMappingStrategyTest.class, AbstractRawItemBuilderTest.class,
		DefaultOrderContributorTest.class, DefaultOrderEntryContributorTest.class, DataHubDeliveryTranslatorTest.class,
		DataHubGoodsIssueTranslatorTest.class, DataHubOrderCreationTranslatorTest.class, DataHubOrderCancelTranslatorTest.class,
		DefaultPaymentContributorTest.class, DefaultPartnerContributorTest.class, DefaultSalesConditionsContributorTest.class,
		SendOrderCancelRequestAsCSVTaskRunnerTest.class, DefaultSendOrderCancelRequestToDataHubHelperTest.class })
public class UnitTestSuite
{

}
