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
package de.hybris.platform.b2bacceleratorservices.dao.impl;


import de.hybris.platform.b2bacceleratorservices.dao.PagedB2BWorkflowActionDao;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultPagedB2BWorkflowActionDao extends DefaultPagedGenericDao<WorkflowActionModel> implements
		PagedB2BWorkflowActionDao
{
	private static final String SORT_BY_DATE = " ORDER BY {" + WorkflowActionModel.CREATIONTIME + "} DESC, {"
			+ WorkflowActionModel.PK + "}";

	private static final String SORT_BY_CODE = " ORDER BY {" + WorkflowActionModel.CODE + "}, {" + WorkflowActionModel.CREATIONTIME
			+ "} DESC, {" + WorkflowActionModel.PK + "}";

	private static final String FIND_WORKFLOW_ACTIONS_BY_USER_QUERY = "SELECT  {" + WorkflowActionModel.PK + "} FROM {"
			+ WorkflowActionModel._TYPECODE + "} WHERE { " + WorkflowActionModel.PRINCIPALASSIGNED + "} = ?principalAssigned"
			+ " AND {" + WorkflowActionModel.ACTIONTYPE + " } IN (?actionType)";

	public DefaultPagedB2BWorkflowActionDao(final String code)
	{
		super(code);
	}

	@Override
	public SearchPageData<WorkflowActionModel> findPagedWorkflowActionsByUserAndActionTypes(final UserModel user,
			final WorkflowActionType[] actionTypes, final PageableData pageableData)
	{
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put(WorkflowActionModel.PRINCIPALASSIGNED, user);
		queryParams.put(WorkflowActionModel.ACTIONTYPE, Arrays.asList(actionTypes));

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byDate", FIND_WORKFLOW_ACTIONS_BY_USER_QUERY + SORT_BY_DATE),
				createSortQueryData("byOrderNumber", FIND_WORKFLOW_ACTIONS_BY_USER_QUERY + SORT_BY_CODE));

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}
}
