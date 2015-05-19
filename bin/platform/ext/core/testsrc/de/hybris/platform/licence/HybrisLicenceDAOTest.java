/*
 *
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
package de.hybris.platform.licence;

import static org.fest.assertions.Assertions.assertThat;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.licence.internal.HybrisLicenceDAO;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.Date;

import org.junit.Test;

public class HybrisLicenceDAOTest extends HybrisJUnit4Test
{
	private final HybrisLicenceDAO dao = new HybrisLicenceDAO();

	@Test
	public void testGetStartingPointDateForPlatformInstance() throws Exception
	{
		// given
		final HybrisDataSource dataSource = JaloSession.getCurrentSession().getTenant().getDataSource();

		// when
		final Date result = dao.getStartingPointDateForPlatformInstance(dataSource);

		// then
		assertThat(result).isNotNull();
	}
}
