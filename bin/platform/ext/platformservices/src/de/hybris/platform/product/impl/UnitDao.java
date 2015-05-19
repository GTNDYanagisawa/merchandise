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
package de.hybris.platform.product.impl;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;


/**
 * The {@link UnitModel} DAO.
 * 
 * @deprecated as of release 4.3, please use {@link de.hybris.platform.product.daos.UnitDao}
 */
@Deprecated
public interface UnitDao extends Dao
{
	/**
	 * @deprecated as of release 4.3, please use {@link de.hybris.platform.product.daos.UnitDao#findUnitsByCode(String)}
	 * 
	 * @param code
	 *           the {@link UnitModel#CODE}
	 * @return for the given <code>code</code> the {@link UnitModel}
	 * @throws IllegalArgumentException
	 *            if <code>code</code> is <code>null</code>.
	 * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
	 *            if no unit was found
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if more than one unit was found by this code
	 */
	@Deprecated
	UnitModel findUnit(final String code);
}
