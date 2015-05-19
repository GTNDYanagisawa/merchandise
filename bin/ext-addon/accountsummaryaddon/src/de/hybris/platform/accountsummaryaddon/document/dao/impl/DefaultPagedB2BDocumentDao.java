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
package de.hybris.platform.accountsummaryaddon.document.dao.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.accountsummaryaddon.document.AccountSummaryDocumentQuery;
import de.hybris.platform.accountsummaryaddon.document.Range;
import de.hybris.platform.accountsummaryaddon.document.dao.PagedB2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;


public class DefaultPagedB2BDocumentDao extends DefaultPagedGenericDao<B2BDocumentModel> implements PagedB2BDocumentDao
{

	private static final String ORDER_BY_STATEMENT = " ORDER BY ";
	private static final String AND_STATEMENT = " AND ";
	private static final String WHERE_STATEMENT = " WHERE ";

	private static final String FIND_DOCUMENT = "SELECT {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.PK + "}  FROM { "
			+ B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join " + B2BDocumentTypeModel._TYPECODE + " as "
			+ B2BDocumentTypeModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.DOCUMENTTYPE + "} = {"
			+ B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.PK + "} join " + B2BUnitModel._TYPECODE + " as "
			+ B2BUnitModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.UNIT + "} = {"
			+ B2BUnitModel._TYPECODE + ":" + B2BUnitModel.PK + "} }  ";

	public DefaultPagedB2BDocumentDao()
	{
		super(B2BDocumentModel._TYPECODE);
	}

	@Override
	public SearchPageData<B2BDocumentModel> findDocuments(final AccountSummaryDocumentQuery query)
	{
		final Map<String, Object> mapCriteria = createMapCriteria(query.getSearchCriteria());

		return getPagedFlexibleSearchService().search(createQuery(query, mapCriteria), mapCriteria, query.getPageableData());
	}

	protected Map<String, Object> createMapCriteria(final Map<String, Object> searchCriteria)
	{
		final Map<String, Object> mapCriteria = new HashMap<String, Object>();

		for (final String key : searchCriteria.keySet())
		{
			if (searchCriteria.get(key) instanceof Range)
			{
				final Range range = (Range) searchCriteria.get(key);
				mapCriteria.put(key + "_min", range.getMinBoundary());
				mapCriteria.put(key + "_max", range.getMaxBoundary());
			}
			else
			{
				mapCriteria.put(key, searchCriteria.get(key));
			}
		}

		return mapCriteria;
	}


	protected String createQuery(final AccountSummaryDocumentQuery query, final Map<String, Object> mapCriteria)
	{
		final StringBuffer queryBuff = new StringBuffer();
		queryBuff.append(FIND_DOCUMENT);

		queryBuff.append(getWhereStatement(mapCriteria));

		queryBuff.append(getOrderStatement(query.getPageableData().getSort(), query.isAsc()));

		return queryBuff.toString();
	}

	protected String getWhereStatement(final Map<String, Object> criteria)
	{
		final StringBuffer whereStatement = new StringBuffer();
		whereStatement.append(documentTypeDisplayInAllListFilter(criteria));

		if (criteria != null && !criteria.isEmpty())
		{
			if (StringUtils.isBlank(whereStatement.toString()))
			{
				whereStatement.append(WHERE_STATEMENT);
			}
			else
			{
				whereStatement.append(AND_STATEMENT);
			}


			final String[] criteriaNames = criteria.keySet().toArray(new String[0]);
			for (int i = 0; i < criteriaNames.length; i++)
			{
				if (StringUtils.endsWith(criteriaNames[i], "_min"))
				{
					whereStatement.append(formatField(criteriaNames[i]) + " >= ?" + criteriaNames[i]);
				}
				else if (StringUtils.endsWith(criteriaNames[i], "_max"))
				{
					whereStatement.append(formatField(criteriaNames[i]) + " <= ?" + criteriaNames[i]);
				}
				else if (StringUtils.equalsIgnoreCase(B2BDocumentModel.UNIT, criteriaNames[i]))
				{
					whereStatement.append(formatField(criteriaNames[i]) + " = ?" + criteriaNames[i]);
				}
				else if (criteria.get(criteriaNames[i]) instanceof List<?>)
				{
					whereStatement.append(formatField(criteriaNames[i]) + " IN (?" + criteriaNames[i] + ")");
				}
				else
				{
					whereStatement.append(formatField(criteriaNames[i]) + " like ?" + criteriaNames[i]);
				}


				if (i + 1 < criteriaNames.length)
				{
					whereStatement.append(AND_STATEMENT);
				}
			}
		}
		return whereStatement.toString();
	}

	protected String documentTypeDisplayInAllListFilter(final Map<String, Object> criteria)
	{
		final StringBuffer whereStatement = new StringBuffer();

        // If the document type of one document is set to displayInAllList=false, this document can show up in result list
        // only when search by document type or number
        //
        // for example: for document "statement" STA-001, it is set to displayInAllList=false the "statement" documents
        // show up in the result list only when user searches by document type = "statement" or document#="STA-001"
		if (criteria == null || criteria.isEmpty()
				|| (!criteria.containsKey(B2BDocumentModel.DOCUMENTTYPE) && !criteria.containsKey(B2BDocumentModel.DOCUMENTNUMBER)))
		{
			whereStatement.append(WHERE_STATEMENT);
			whereStatement.append("{" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.DISPLAYINALLLIST + " } = true ");
		}

		return whereStatement.toString();
	}

	protected String getOrderStatement(final String sortField, final boolean isAsc)
	{
		return ORDER_BY_STATEMENT + formatField(sortField) + (isAsc ? " ASC " : " DESC ");
	}

	protected String formatField(final String fieldName)
	{
		if (StringUtils.equalsIgnoreCase(B2BDocumentModel.DOCUMENTTYPE, fieldName))
		{
			return " {" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.CODE + " } ";
		}
		else if (StringUtils.equalsIgnoreCase(B2BDocumentModel.UNIT, fieldName))
		{
			return " {" + B2BUnitModel._TYPECODE + ":" + B2BUnitModel.UID + " } ";
		}
		else
		{
			return " {" + B2BDocumentModel._TYPECODE + ":" + getFiedName(fieldName) + "} ";
		}
	}

	protected String getFiedName(final String fieldName)
	{
		if (StringUtils.endsWith(fieldName, "_max") || StringUtils.endsWith(fieldName, "_min"))
		{
			return StringUtils.substringBefore(fieldName, "_");
		}
		return fieldName;
	}
}
