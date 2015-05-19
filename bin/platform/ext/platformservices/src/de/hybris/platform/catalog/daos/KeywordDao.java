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
package de.hybris.platform.catalog.daos;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.KeywordModel;

import java.util.List;


/**
 * Interface define routine of getting keywords from DB.
 */
public interface KeywordDao
{
	/**
	 * Returns the Keyword for the specified keyword value and <code>CatalogVersion</code>.
	 * 
	 * @param catalogVersion
	 *           The <code>CatalogVersion</code> the <code>Keyword</code> belongs to.
	 * @param keywordValue
	 *           The value of the searched <code>Keyword</code>.
	 * @return All matching <code>Keywords</code>.
	 * @deprecated use {@link #getKeywords(CatalogVersionModel, String)}
	 */
	@Deprecated
	List<KeywordModel> getKeyword(final CatalogVersionModel catalogVersion, final String keywordValue);

	/**
	 * Returns the Keyword for the specified keyword value and <code>CatalogVersion</code>.
	 * 
	 * @param catalogVersion
	 *           The <code>CatalogVersion</code> the <code>Keyword</code> belongs to.
	 * @param keywordValue
	 *           The value of the searched <code>Keyword</code>.
	 * @return All matching <code>Keywords</code>.
	 */
	List<KeywordModel> getKeywords(final CatalogVersionModel catalogVersion, final String keywordValue);


	/**
	 * Returns the Keyword for the specified keyword value and <code>CatalogVersion</code>.
	 * 
	 * @param typeCode
	 *           Code of type (or subtype) of keyword to allow to search for subclasses
	 * @param catalogVersion
	 *           The <code>CatalogVersion</code> the <code>Keyword</code> belongs to.
	 * @param keywordValue
	 *           The value of the searched <code>Keyword</code>.
	 * @return All matching <code>Keywords</code>.
	 * 
	 * @deprecated use {@link KeywordDao#getKeywords(String, CatalogVersionModel, String)}
	 */
	@Deprecated
	List<KeywordModel> getKeyword(String typeCode, CatalogVersionModel catalogVersion, String keywordValue);


	/**
	 * Returns the Keyword for the specified keyword value and <code>CatalogVersion</code>.
	 * 
	 * @param typeCode
	 *           Code of type (or subtype) of keyword to allow to search for subclasses
	 * @param catalogVersion
	 *           The <code>CatalogVersion</code> the <code>Keyword</code> belongs to.
	 * @param keywordValue
	 *           The value of the searched <code>Keyword</code>.
	 * 
	 * @return All matching <code>Keywords</code>.
	 */
	List<KeywordModel> getKeywords(String typeCode, CatalogVersionModel catalogVersion, String keywordValue);
}
