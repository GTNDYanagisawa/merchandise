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
package de.hybris.platform.secureportaladdon.dao.impl;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.secureportaladdon.dao.B2BRegistrationDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BRegistrationDao} that deals with registration specific elements
 */
public class DefaultB2BRegistrationDao implements B2BRegistrationDao
{

	private FlexibleSearchService flexibleSearchService;

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.secureportaladdon.dao.B2BRegistrationDao#getEmployeesInUserGroup(java.lang.String)
	 */
	@Override
	public List<EmployeeModel> getEmployeesInUserGroup(final String userGroup)
	{

		final StringBuilder query = new StringBuilder();
		query.append(" SELECT 	{e.").append(EmployeeModel.PK).append("} ");
		query.append(" FROM 		{").append(PrincipalGroupModel._PRINCIPALGROUPRELATION).append(" AS pgr ");
		query.append(" 	JOIN ").append(UserGroupModel._TYPECODE).append(" AS pg ");
		query.append(" 		ON {pgr:target} = {pg:pk} ");
		query.append(" 	JOIN ").append(EmployeeModel._TYPECODE).append("! AS e ");
		query.append(" 		ON {pgr:source} = {e:pk} ");
		query.append(" } ");
		query.append(" WHERE {pg:uid} = ?userGroup ");

		final Map<String, Object> params = new HashMap<>();
		params.put("userGroup", userGroup);

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString(), params);
		final SearchResult<EmployeeModel> result = flexibleSearchService.search(searchQuery);

		return result.getResult();

	}

}
