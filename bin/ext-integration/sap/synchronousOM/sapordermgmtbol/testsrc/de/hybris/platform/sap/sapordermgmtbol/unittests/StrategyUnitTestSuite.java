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
package de.hybris.platform.sap.sapordermgmtbol.unittests;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.BackendUtilImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.BaseStrategyERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.DocumentTypeMappingTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.GetAllStrategyERP605Test;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.GetAllStrategyERPJcoRecTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.GetAllStrategyERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.HeaderMapperJCoRecTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.HeaderMapperTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.HeaderTextMapperJCoRecTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.HeaderTextMapperTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.IncompletionMapperImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ItemMapperTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ItemTextMapperJCoRecTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ItemTextMapperTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.LrdActionsStrategyERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.LrdCloseStrategyImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.PartnerMapperTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ProductConfigurationStrategyImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ScheduleLineJcoRecTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.SetStrategyERPJcoRecTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.SetStrategyERPTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.StrategyFactoryERP604Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
{ HeaderTextMapperTest.class, //
		ItemTextMapperTest.class, //
		PartnerMapperTest.class, //
		HeaderMapperTest.class, //
		ItemMapperTest.class, //
		GetAllStrategyERPTest.class, //
		SetStrategyERPTest.class, //
		LrdActionsStrategyERPTest.class, //
		StrategyFactoryERP604Test.class, //
		DocumentTypeMappingTest.class, //
		ItemTextMapperJCoRecTest.class, //
		HeaderTextMapperJCoRecTest.class, //
		HeaderMapperJCoRecTest.class, //
		GetAllStrategyERPJcoRecTest.class, //
		GetAllStrategyERP605Test.class, //
		ScheduleLineJcoRecTest.class, //
		SetStrategyERPJcoRecTest.class, //
		ProductConfigurationStrategyImplTest.class, //
		BaseStrategyERPTest.class, //
		BackendUtilImplTest.class, //
		IncompletionMapperImplTest.class, //
		LrdCloseStrategyImplTest.class //
})
@SuppressWarnings("javadoc")
public class StrategyUnitTestSuite
{
	//nothing
}
