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
package de.hybris.platform.webservices;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;


public interface ServiceLocator
{
	UserService getUserService();

	I18NService getI18nService();

	MediaService getMediaService();

	ProductService getProductService();

	CatalogService getCatalogService();

	CategoryService getCategoryService();

	WsUtilService getWsUtilService();

	ModelService getModelService();

	TypeService getTypeService();

	CartService getCartService();

	OrderService getOrderService();

	CronJobService getCronJobService();

	FlexibleSearchService getFlexibleSearchService();

	EventService getEventService();

	ImportService getImportService();

}
