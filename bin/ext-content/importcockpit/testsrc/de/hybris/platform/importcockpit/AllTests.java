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
package de.hybris.platform.importcockpit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
{
		de.hybris.platform.importcockpit.components.contentbrowser.mapping.impl.AllTests.class, //
		de.hybris.platform.importcockpit.components.contentbrowser.util.AllTests.class, //
		de.hybris.platform.importcockpit.components.listview.impl.AllTests.class, //
		de.hybris.platform.importcockpit.daos.impl.AllTests.class, //
		de.hybris.platform.importcockpit.format.AllTests.class, //
		de.hybris.platform.importcockpit.format.model.AllTests.class, //
		de.hybris.platform.importcockpit.model.mappingview.impl.AllTests.class, //
		de.hybris.platform.importcockpit.services.classification.impl.AllTests.class, //
		de.hybris.platform.importcockpit.services.impex.generator.operations.AllTests.class, //
		de.hybris.platform.importcockpit.services.impex.generator.operations.impl.AllTests.class, //
		de.hybris.platform.importcockpit.services.mapping.impl.AllTests.class, //
		de.hybris.platform.importcockpit.wizzard.mapping.page.AllTests.class, //
		de.hybris.platform.importcockpit.services.login.impl.AllTests.class, //
		de.hybris.platform.importcockpit.services.media.impl.AllTests.class //
})
public class AllTests
{
	//NOPMD
}
