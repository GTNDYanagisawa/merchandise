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
 */
package de.hybris.platform.commercewebservicescommons.mapping;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Interface for data mapper that wraps {@link ma.glasnost.orika.MapperFacade} and adds property filtering.
 */
public interface DataMapper
{
	/**
	 * Field set name used in mapping context
	 */
	static final String FIELD_SET_NAME = "FIELD_SET_NAME";
	/**
	 * Flag used in mapping context. Decides whether nulls are mapped or ignored.
	 */
	static final String MAP_NULLS = "MAP_NULLS";
	/**
	 * Field prefix added to destination object field name
	 */
	static final String FIELD_PREFIX = "destination";

	/**
	 * Create and return a new instance of type D mapped with the properties of <code>sourceObject</code>.
	 * 
	 * @param sourceObject
	 *           the object to map from
	 * @param destinationClass
	 *           the type of the new object to return
	 * @return a new instance of type D mapped with the properties of <code>sourceObject</code>
	 */
	<S, D> D map(S sourceObject, Class<D> destinationClass);

	/**
	 * Create and return a new instance of type D mapped with the specified <code>fields</code> of
	 * <code>sourceObject</code>.
	 * 
	 * @param sourceObject
	 *           the object to map from
	 * @param destinationClass
	 *           the type of the new object to return
	 * @param fields
	 *           comma-separated fields to map
	 * @return a new instance of type D mapped with the <code>fields</code> of <code>sourceObject</code>
	 */
	<S, D> D map(S sourceObject, Class<D> destinationClass, String fields);

	/**
	 * Create and return a new instance of type D mapped with the specified <code>fields</code> of
	 * <code>sourceObject</code>.
	 * 
	 * @param sourceObject
	 *           the object to map from
	 * @param destinationClass
	 *           the type of the new object to return
	 * @param fields
	 *           set of fully qualified field names to map
	 * @return a new instance of type D mapped with the <code>fields</code> of <code>sourceObject</code>
	 */
	<S, D> D map(S sourceObject, Class<D> destinationClass, Set<String> fields);

	/**
	 * Maps the properties of <code>sourceObject</code> onto <code>destinationObject</code>.
	 * 
	 * @param sourceObject
	 *           the object from which to read the properties
	 * @param destinationObject
	 *           the object onto which the properties should be mapped
	 */
	<S, D> void map(S sourceObject, D destinationObject);

	/**
	 * Maps the properties of <code>sourceObject</code> onto <code>destinationObject</code>.
	 * 
	 * @param sourceObject
	 *           the object from which to read the properties
	 * @param destinationObject
	 *           the object onto which the properties should be mapped
	 * @param mapNulls
	 *           controls whether nulls are mapped or ignored
	 */
	<S, D> void map(S sourceObject, D destinationObject, boolean mapNulls);

	/**
	 * Maps the properties of <code>sourceObject</code> onto <code>destinationObject</code>.
	 * 
	 * @param sourceObject
	 *           the object from which to read the properties
	 * @param destinationObject
	 *           the object onto which the properties should be mapped
	 * @param fields
	 *           comma-separated fields to map
	 */
	<S, D> void map(S sourceObject, D destinationObject, String fields);

	/**
	 * Maps the properties of <code>sourceObject</code> onto <code>destinationObject</code>.
	 * 
	 * @param sourceObject
	 *           the object from which to read the properties
	 * @param destinationObject
	 *           the object onto which the properties should be mapped
	 * @param fields
	 *           comma-separated fields to map
	 * @param mapNulls
	 *           controls whether nulls are mapped or ignored
	 */
	<S, D> void map(S sourceObject, D destinationObject, String fields, boolean mapNulls);

	/**
	 * Maps the source iterable into a new Set parameterized by <code>destinationClass</code>.
	 * 
	 * @param source
	 *           the Iterable from which to map
	 * @param destinationClass
	 *           the type of elements to be contained in the returned Set.
	 * @param fields
	 *           comma-separated fields to map
	 * @return a new Set containing elements of type <code>destinationClass</code> mapped from the elements of
	 *         <code>source</code>.
	 */
	<S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass, String fields);

	/**
	 * Maps the source Iterable into a new List parameterized by <code>destinationClass</code>.
	 * 
	 * @param source
	 *           the Iterable from which to map
	 * @param destinationClass
	 *           the type of elements to be contained in the returned Set.
	 * @param fields
	 *           comma-separated fields to map
	 * @return a new List containing elements of type <code>destinationClass</code> mapped from the elements of
	 *         <code>source</code>.
	 */
	<S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass, String fields);

	/**
	 * Map an iterable onto an existing collection
	 * 
	 * @param source
	 *           the source iterable
	 * @param destination
	 *           the destination collection
	 * @param destinationClass
	 *           the type of elements in the destination
	 * @param fields
	 *           comma-separated fields to map
	 */
	<S, D> void mapAsCollection(Iterable<S> source, Collection<D> destination, Class<D> destinationClass, String fields);

	/**
	 * Maps the properties of <code>sourceObject</code> onto <code>destinationObject</code>, using
	 * <code>sourceActualTypeArguments</code> and <code>destActualTypeArguments</code> to specify the arguments of
	 * parameterized types of the source and destination object.
	 * 
	 * @param sourceObject
	 *           the object from which to read the properties
	 * @param destObject
	 *           the object onto which the properties should be mapped
	 * @param sourceActualTypeArguments
	 *           arguments of source type<br/>
	 *           e.g. if we have type class like ProductSearchPageData<SearchStateData, ProductData> we should give
	 *           {SearchStateData.class,ProductData.class}
	 * @param destActualTypeArguments
	 *           arguments of dest type<br\>
	 *           e.g. if we have type class like ProductSearchPageData<SearchStateData, ProductData> we should give
	 *           {SearchStateData.class,ProductData.class}
	 * @param fields
	 *           comma-separated fields to map
	 * @param destTypeVariableMap
	 *           - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}
	 */
	<S, D> void mapGeneric(final S sourceObject, final D destObject, final Type[] sourceActualTypeArguments,
			final Type[] destActualTypeArguments, final String fields, final Map<String, Class> destTypeVariableMap);

}
