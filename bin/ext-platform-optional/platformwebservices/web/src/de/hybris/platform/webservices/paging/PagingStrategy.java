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
package de.hybris.platform.webservices.paging;

import de.hybris.platform.core.model.type.RelationDescriptorModel;

import java.util.List;
import java.util.Map;


/**
 * Webservices paging.
 */
public interface PagingStrategy
{
	/**
	 * Returns page context if query parameters and collection property name suit each other. If not, the null is
	 * returned.
	 *<p/>
	 * 
	 * @param collectionPropertyName
	 *           The Name of the collection property.
	 * @param queryParams
	 *           Query parameters which comes from requested URL.
	 * 
	 * @return page context if query parameters and collection property name suit each other. If not, the null is
	 *         returned.
	 */
	PageInfoCtx findPageContext(final String collectionPropertyName, final Map<String, List<String>> queryParams);

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
	Object executeRootCollectionPaging(final PageInfoCtx pagingInfo, final String typeName);

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
	Object executeRelationTypePaging(final PageInfoCtx pagingInfo, final RelationDescriptorModel relDesc, final Object model);

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
	Object executeCollectionTypePaging(final PageInfoCtx pagingInfo, final Object value);
}
