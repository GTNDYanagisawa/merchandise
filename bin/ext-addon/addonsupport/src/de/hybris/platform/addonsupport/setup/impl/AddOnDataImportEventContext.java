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
package de.hybris.platform.addonsupport.setup.impl;

import de.hybris.platform.addonsupport.data.AddonExtensionMetadata;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.commerceservices.setup.events.AbstractDataImportEvent;


public class AddOnDataImportEventContext
{
	private final AbstractDataImportEvent sourceEvent;
	private final AddonExtensionMetadata addonExtensionMetadata;
	private CatalogModel productCatalog;
	private ContentCatalogModel contentCatalog;
	private BaseSiteModel baseSite;

	public AddOnDataImportEventContext(final AbstractDataImportEvent sourceEvent,
			final AddonExtensionMetadata addonExtensionMetadata)
	{
		this.sourceEvent = sourceEvent;
		this.addonExtensionMetadata = addonExtensionMetadata;
	}

	/**
	 * @return the sourceEvent
	 */
	public AbstractDataImportEvent getSourceEvent()
	{
		return sourceEvent;
	}

	/**
	 * @return the configExtensionName
	 */
	public AddonExtensionMetadata getAddonExtensionMetadata()
	{
		return addonExtensionMetadata;
	}

	/**
	 * @return the productCatalog
	 */
	public CatalogModel getProductCatalog()
	{
		return productCatalog;
	}

	/**
	 * @param productCatalog
	 *           the productCatalog to set
	 */
	public void setProductCatalog(final CatalogModel productCatalog)
	{
		this.productCatalog = productCatalog;
	}

	/**
	 * @return the contentCatalog
	 */
	public ContentCatalogModel getContentCatalog()
	{
		return contentCatalog;
	}

	/**
	 * @param contentCatalog
	 *           the contentCatalog to set
	 */
	public void setContentCatalog(final ContentCatalogModel contentCatalog)
	{
		this.contentCatalog = contentCatalog;
	}

	/**
	 * @return the baseSite
	 */
	public BaseSiteModel getBaseSite()
	{
		return baseSite;
	}

	/**
	 * @param baseSite
	 *           the baseSite to set
	 */
	public void setBaseSite(final BaseSiteModel baseSite)
	{
		this.baseSite = baseSite;
	}

}
