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
package de.hybris.platform.catalog.interceptors;

import de.hybris.platform.catalog.daos.CatalogVersionDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Checks if all {@link CatalogModel#CATALOGVERSIONS} are removable when removing {@link CatalogModel}. When removing
 * {@link CatalogVersionModel} check if it is removable.
 */
public class CheckVersionsRemoveInterceptor implements RemoveInterceptor
{
	private CatalogVersionDao catalogVersionDao;
	private L10NService l10nService;

	@Override
	public void onRemove(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		final Set<CatalogVersionModel> nonRemCatalogVersion = new HashSet<CatalogVersionModel>();

		if (model instanceof CatalogModel)
		{
			for (final CatalogVersionModel catalogVersion : ((CatalogModel) model).getCatalogVersions())
			{
				if (!isRemovable(catalogVersion))
				{
					nonRemCatalogVersion.add(catalogVersion);
				}

			}
		}

		if (model instanceof CatalogVersionModel)
		{
			if (!isRemovable((CatalogVersionModel) model))
			{
				nonRemCatalogVersion.add((CatalogVersionModel) model);
			}
		}


		if (!nonRemCatalogVersion.isEmpty())
		{
			throw new InterceptorException(l10nService.getLocalizedString("error.catalog.contains_non_removable_versions",
					new Object[]
					{ nonRemCatalogVersion }));
		}
	}

	private boolean isRemovable(final CatalogVersionModel catalogVersion) throws InterceptorException
	{


		if ((catalogVersion.getActive() != null) && (catalogVersion.getActive().booleanValue()))
		{
			return false;
		}

		if (catalogVersionDao.findAllCategoriesCount(catalogVersion).intValue() > 0)
		{
			return false;
		}

		if (catalogVersionDao.findAllProductsCount(catalogVersion).intValue() > 0)
		{
			return false;
		}

		if (catalogVersionDao.findAllKeywordsCount(catalogVersion).intValue() > 0)
		{
			return false;
		}

		if (catalogVersionDao.findAllMediasCount(catalogVersion).intValue() > 0)
		{
			return false;
		}

		return true;
	}


	@Required
	public void setCatalogVersionDao(final CatalogVersionDao catalogVersionDao)
	{
		this.catalogVersionDao = catalogVersionDao;
	}

	@Required
	public void setL10nService(final L10NService l10nService)
	{
		this.l10nService = l10nService;
	}
}
