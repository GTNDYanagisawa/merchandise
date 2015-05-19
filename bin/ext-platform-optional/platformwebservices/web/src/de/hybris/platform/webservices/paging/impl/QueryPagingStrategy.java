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
package de.hybris.platform.webservices.paging.impl;

import de.hybris.platform.core.enums.RelationEndCardinalityEnum;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.BadRequestException;
import de.hybris.platform.webservices.objectgraphtransformer.DynamicComparator;
import de.hybris.platform.webservices.paging.PageInfoCtx;
import de.hybris.platform.webservices.paging.PagingStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;


public class QueryPagingStrategy implements PagingStrategy
{
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * Returns page context if query parameters and collection property name suit each other. If not, the null is
	 * returned.
	 * <p/>
	 * 
	 * @param collectionPropertyName
	 *           The Name of the collection property.
	 * @param queryParams
	 *           Query parameters which comes from requested URL.
	 * 
	 * @return page context if query parameters and collection property name suit each other. If not, the null is
	 *         returned.
	 */
	@Override
	public PageInfoCtx findPageContext(final String collectionPropertyName, final Map<String, List<String>> queryParams)
	{
		PageInfoCtx result = null;
		if (queryParams != null)
		{
			//Nice trick: using TreeMap with case-insensitive comparator gives a case-insensitive Map!
			final Map<String, Collection<String>> caseInsensitiveMap = new TreeMap<String, Collection<String>>(
					new Comparator<String>()
					{
						@Override
						public int compare(final String object1, final String object2)
						{
							return object1.compareToIgnoreCase(object2);
						}
					});
			caseInsensitiveMap.putAll(queryParams);

			final String _page = findParameter(caseInsensitiveMap, collectionPropertyName, "page");
			final String _size = findParameter(caseInsensitiveMap, collectionPropertyName, "size");
			final String _sort = findParameter(caseInsensitiveMap, collectionPropertyName, "sort");

			final String _query = findParameter(caseInsensitiveMap, collectionPropertyName, "query");
			final String _subtypes = findParameter(caseInsensitiveMap, collectionPropertyName, "subtypes");

			if (_page != null || _size != null || _sort != null || _query != null || _subtypes != null)
			{
				Integer page = null;
				Integer size = null;

				try
				{
					page = _page != null ? Integer.valueOf(_page) : Integer.valueOf(0);
					size = _size != null ? Integer.valueOf(_size) : Integer.valueOf(Config.getInt(
							"webservices.paging.default_page_size", PageInfoCtx.DEFAULT_PAGE_SIZE.intValue()));
				}
				catch (final NumberFormatException nfe)
				{
					throw new BadRequestException("Query parameter [" + collectionPropertyName + "_page or " + collectionPropertyName
							+ "_size] has inappropriate value!", nfe);
				}

				final boolean subtypes = _subtypes != null ? Boolean.parseBoolean(_subtypes) : PageInfoCtx.DEFAULT_SUBTYPES;
				result = new PageInfoCtx(page, size, _sort, _query, subtypes);
				result.setCollectionPropertyName(collectionPropertyName);
			}
		}

		return result;
	}

	/**
	 * Returns a collection which comes from Root Resource(possibly paginated).
	 * <p/>
	 * Solution based on FlexibleSearch.
	 * <p/>
	 * 
	 * @param pagingInfo
	 *           Holds all required information about paging settings.
	 * @param typeCode
	 *           The requested type code of collection elements.
	 * 
	 * @return a collection which comes from Root Resource(possibly paginated).
	 */
	@Override
	public Object executeRootCollectionPaging(final PageInfoCtx pagingInfo, final String typeCode)
	{
		final StringBuilder query = new StringBuilder("select {PK} from {");
		final FlexibleSearchQuery fsq;
		if (pagingInfo == null)
		{
			// building flexible search query
			query.append(typeCode).append("}");
			fsq = new FlexibleSearchQuery(query.toString());
		}
		else
		{
			// building flexible search paginated query
			query.append(typeCode).append(pagingInfo.isSubtypes() ? "" : "!").append("} ");
			if (pagingInfo.getQuery() != null)
			{
				query.append("where ").append(pagingInfo.getQuery());
			}
			if (pagingInfo.getSortProperty() != null)
			{
				query.append(" order by {").append(pagingInfo.getSortProperty()).append("} ");
				query.append(pagingInfo.isDescending() ? "DESC" : "ASC");
			}

			fsq = new FlexibleSearchQuery(query.toString());
			// user wants to get all elements if pageSize is set to -1
			if (!(pagingInfo.getPageSize().intValue() == -1))
			{
				fsq.setStart(pagingInfo.getPageNumber().intValue() * pagingInfo.getPageSize().intValue());
				fsq.setCount(pagingInfo.getPageSize().intValue());
			}
		}

		return handleFlexibleSearchExecution(fsq, pagingInfo);
	}

	/**
	 * Returns a paginated collection which is specified by pagingInfo and comes from N:M relation OR a paginated
	 * collection which comes from 1:N relation.
	 * <p/>
	 * Solution based on FlexibleSearch.
	 * <p/>
	 * 
	 * @param pagingInfo
	 *           Holds all required information about paging settings.
	 * @param relDesc
	 *           The relation descriptor.
	 * @param model
	 *           The instance of model which holds the returned collection.
	 * 
	 * @return a paginated collection which is specified by pagingInfo and comes from N:M relation OR a paginated
	 *         collection which comes from 1:N relation.
	 */
	@Override
	public Object executeRelationTypePaging(final PageInfoCtx pagingInfo, final RelationDescriptorModel relDesc, final Object model)
	{

		final RelationMetaTypeModel relMetaType = relDesc.getRelationType();
		final RelationEndCardinalityEnum sourceCard = relMetaType.getSourceTypeCardinality();
		final RelationEndCardinalityEnum targetCard = relMetaType.getTargetTypeCardinality();

		String query;
		// n:m relation
		if ("many".equals(sourceCard.getCode()) && "many".equals(targetCard.getCode()))
		{
			query = buildFsPaginatedQueryForRelationNM(pagingInfo, relMetaType);
		}
		// 1:n relation
		else
		{
			query = buildFsPaginatedQueryForRelation1N(pagingInfo, relMetaType);
		}

		final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);
		// user wants to get all elements if pageSize is set to -1
		if (!(pagingInfo.getPageSize().intValue() == -1))
		{
			fsQuery.setStart(pagingInfo.getPageNumber().intValue() * pagingInfo.getPageSize().intValue());
			fsQuery.setCount(pagingInfo.getPageSize().intValue());
		}
		final Item item = getModelService().getSource(model);
		fsQuery.addQueryParameter("param", item);

		return handleFlexibleSearchExecution(fsQuery, pagingInfo);
	}

	/**
	 * If input collection is not empty then will be paginated otherwise passed without any change.
	 * <p/>
	 * 
	 * @param pagingInfo
	 *           Holds all required information about paging settings.
	 * @param value
	 *           The collection which will be paginated.
	 * 
	 * @deprecated after CollectionTypes will be replaced with RelationTypes(since 5.0.0) this method will be useless.
	 *             There will be no need to implement this method. The executeRelationTypePaging method will be used
	 *             instead of this.
	 * 
	 * @return if input collection is not empty then will be paginated otherwise passed without any change.
	 */
	@Deprecated
	@Override
	public Object executeCollectionTypePaging(final PageInfoCtx pagingInfo, final Object value)
	{
		// they are not supported within collectionType paging
		StringBuilder message = null;
		if (pagingInfo.getQuery() != null)
		{
			message = new StringBuilder("FlexibleSearch querying is not supported for: ").append(
					pagingInfo.getCollectionPropertyName()).append(" collection property.");

		}
		if (!pagingInfo.isSubtypes())
		{
			if (message == null)
			{
				message = new StringBuilder("The subtyping is not supported for: ").append(pagingInfo.getCollectionPropertyName())
						.append(" collection property.");
			}
			else
			{
				message.append("\nThe subtyping is not supported for: ").append(pagingInfo.getCollectionPropertyName())
						.append(" collection property.");
			}
		}
		if (message != null)
		{
			throw new BadRequestException(message.toString());
		}

		final Collection<?> col = (Collection<?>) value;
		//report the "total size" to the client via graph context attribute.
		pagingInfo.setTotalSize(col.size());

		//Detect special case of empty source collection.
		if (col.isEmpty())
		{
			//re-setting page number in case of empty source collection is done to be consistent with 
			//behavior of paging non-empty collection when the page number is too big.
			pagingInfo.setPageNumber(Integer.valueOf(0));
		}
		else
		{
			//We need a List to do sorting. I am not sure if every source Collection is a List, so I am creating a List here.
			final List<Object> pagingList = new java.util.ArrayList<Object>(col);
			return sortAndPage(pagingList, pagingInfo);
		}

		return value;
	}

	/**
	 * Searches Wraps FlexibleSearchException in BadRequestException.
	 */
	private Object handleFlexibleSearchExecution(final FlexibleSearchQuery fsQuery, final PageInfoCtx pagingInfo)
	{
		Object coll = null;
		try
		{
			coll = getFlexibleSearchService().search(fsQuery).getResult();
		}
		catch (final FlexibleSearchException fse)
		{
			if (pagingInfo == null)
			{
				//if root collection query will fail(paging not used)
				throw fse;
			}
			else
			{
				throw new BadRequestException("Query parameters[for " + pagingInfo.getCollectionPropertyName()
						+ "] are not properly constructed.\n\nWhole Query: \n" + fsQuery.getQuery() + "\n\nMore details: ", fse);
			}
		}

		return coll;
	}

	/**
	 * Sorts (if requested) and returns requested "page" of given list.
	 * <p/>
	 * 
	 * @param pagingTarget
	 *           list to be sorted and "paged"
	 * @param pagingInfo
	 *           represents user requirements (sorting order, page number, page size, etc.)
	 * @return "page of results" according to user requirements
	 */
	private List<Object> sortAndPage(final List<Object> pagingTarget, final PageInfoCtx pagingInfo)
	{
		//SORTING
		if (pagingInfo.getSortProperty() != null)
		{
			final Comparator<Object> sortingComparator = new DynamicComparator(pagingInfo.getSortProperty(),
					pagingInfo.isDescending());
			java.util.Collections.sort(pagingTarget, sortingComparator);
		}

		if (pagingInfo.getPageNumber() == null && pagingInfo.getPageSize() == null) //only SORTING
		{
			return pagingTarget;
		}
		else
		//PAGING
		{
			//user-requirements
			final int pageNumber = pagingInfo.getPageNumber().intValue();
			final int pageSize = pagingInfo.getPageSize().intValue();


			//pages are numbered starting from 0
			int pagingIndexFrom = pageNumber * pageSize;

			//if page number is too big re-position page number so that it points to the last page of results.
			if (pagingIndexFrom >= pagingTarget.size())
			{
				final int numberOfPages = pagingTarget.size() / pageSize //full pages
						+ ((pagingTarget.size() % pageSize != 0) ? 1 : 0); //partial last page

				pagingIndexFrom = (numberOfPages - 1) * pageSize;
				//update page number, so that user can be notified of this forced page number change. 
				pagingInfo.setPageNumber(Integer.valueOf(numberOfPages));
			}

			int pagingIndexTo = pagingIndexFrom + pageSize;
			//if page size is too big given  page number, trim the index.
			if (pagingIndexTo > pagingTarget.size())
			{
				pagingIndexTo = pagingTarget.size();
			}
			return pagingTarget.subList(pagingIndexFrom, pagingIndexTo);
		}

	}

	/**
	 * Returns properly built flexible search query for getting a collection of elements which is specified by pagingInfo
	 * and comes from N:M relation.
	 * <p/>
	 * 
	 * @param pagingInfo
	 *           Holds all required information about paging settings.
	 * @param relMetaType
	 *           Describes currently processed relation.
	 * 
	 * @return properly built flexible search query for getting a collection of elements which is specified by pagingInfo
	 *         and comes from N:M relation.
	 */
	private String buildFsPaginatedQueryForRelationNM(final PageInfoCtx pagingInfo, final RelationMetaTypeModel relMetaType)
	{
		// some pre-preparation
		final String typeCode, subelementNameFirst, subelementNameSecond;
		if (pagingInfo.getCollectionPropertyName().equals(relMetaType.getSourceTypeRole()))
		{
			typeCode = relMetaType.getSourceType().getCode();
			subelementNameFirst = "source";
			subelementNameSecond = "target";
		}
		else
		{
			typeCode = relMetaType.getTargetType().getCode();
			subelementNameFirst = "target";
			subelementNameSecond = "source";
		}

		// building query for flexibleSearch
		final StringBuilder query = new StringBuilder("select {PK} from {");
		query.append(typeCode).append(pagingInfo.isSubtypes() ? "" : "!").append(" join ").append(relMetaType.getCode())
				.append(" as REL on {PK} = {REL:").append(subelementNameFirst).append("} } ");
		query.append("where {REL:").append(subelementNameSecond).append("} = ?param ");
		if (pagingInfo.getQuery() != null)
		{
			query.append("and ").append(pagingInfo.getQuery());
		}
		if (pagingInfo.getSortProperty() != null)
		{
			query.append(" order by {").append(pagingInfo.getSortProperty()).append("} ");
			query.append(pagingInfo.isDescending() ? "DESC" : "ASC");
		}

		return query.toString();
	}

	/**
	 * Returns properly built flexible search query for getting a collection of elements which comes from 1:N relation.
	 * <p/>
	 * 
	 * @param pagingInfo
	 *           Holds all required information about paging settings.
	 * @param relMetaType
	 *           Describes currently processed relation.
	 * 
	 * @return properly built flexible search query for getting a collection of elements which comes from 1:N relation.
	 */
	private String buildFsPaginatedQueryForRelation1N(final PageInfoCtx pagingInfo, final RelationMetaTypeModel relMetaType)
	{
		// some pre-preparation
		String typeCode, typeRole;
		if ("many".equals(relMetaType.getSourceTypeCardinality().getCode()))
		{
			typeCode = relMetaType.getSourceType().getCode();
			typeRole = relMetaType.getTargetTypeRole();
		}
		else
		{
			typeCode = relMetaType.getTargetType().getCode();
			typeRole = relMetaType.getSourceTypeRole();
		}

		// building query for flexibleSearch
		final StringBuilder query = new StringBuilder("select {PK} from {");
		query.append(typeCode).append(pagingInfo.isSubtypes() ? "" : "!").append("} ");
		query.append("where {").append(typeRole).append("} = ?param ");
		if (pagingInfo.getQuery() != null)
		{
			query.append("and ").append(pagingInfo.getQuery());
		}
		if (pagingInfo.getSortProperty() != null)
		{
			query.append(" order by {").append(pagingInfo.getSortProperty()).append("} ");
			query.append(pagingInfo.isDescending() ? "DESC" : "ASC");
		}

		return query.toString();
	}

	/**
	 * Helper method which tries to extracts a sub-parameter for a specific property from a Map. Map-key must follow the
	 * format: [property-name]_[sub-parameter] and Map-value contains the value for [sub-parameter].
	 * <p/>
	 * Note: origin of map-values are http-requests query parameters.
	 * 
	 * @param paramMap
	 *           Map which holds all available parameters
	 * @param propertyName
	 *           [property-name]: the property (as bean-property name) which is asked for the parameter value
	 * @param propertyParameter
	 *           [sub-parameter]: the parameter which is asked for (page, sort, size)
	 * @return the parameter value, null if not available, first element if value is a Collection
	 */
	private String findParameter(final Map<String, Collection<String>> paramMap, final String propertyName,
			final String propertyParameter)
	{
		String result = null;
		if (propertyParameter != null && propertyParameter.length() > 0)
		{
			if (propertyName != null && propertyName.length() > 0)
			{
				// final map-key is a concatenation of 'property name' and 'property parameter' 
				final String key = propertyName + "_" + propertyParameter;
				if (paramMap.containsKey(key))
				{
					result = (paramMap.get(key)).iterator().next();
					if (StringUtils.isBlank(result))
					{
						result = null;
					}
				}
			}
		}
		return result;
	}
}
