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
package de.hybris.platform.ychinaaccelerator.storefront.controllers.cms;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.chinaaccelerator.facades.data.EnhancedCategoryData;
import de.hybris.platform.chinaaccelerator.services.model.cms.components.CategoryNavComponentModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ychinaaccelerator.storefront.controllers.ControllerConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS ProductReferencesComponent.
 */
@Controller("CategoryNavComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.CategoryNavComponent)
public class CategoryNavComponentController extends AbstractCMSComponentController<CategoryNavComponentModel>
{
	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);

	@Resource(name = "categoryConverter")
	private Converter<CategoryModel, CategoryData> categoryConverter;


	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final CategoryNavComponentModel component)
	{
		final List<EnhancedCategoryData> categories = new ArrayList<EnhancedCategoryData>();

		categories.addAll(collectLinkedCategories(component.getCategories()));

		model.addAttribute("categoryData", categories);
	}

	protected List<EnhancedCategoryData> collectLinkedCategories(final List<CategoryModel> list)
	{
		final List<EnhancedCategoryData> categories = new ArrayList<EnhancedCategoryData>();

		for (final CategoryModel catModel : list)
		{
			final EnhancedCategoryData catData = new EnhancedCategoryData();
			categoryConverter.convert(catModel, catData);
			if (catModel.getCategories() != null)
			{
				catData.setCategories(collectLinkedCategories(catModel.getCategories()));
			}
			categories.add(catData);
		}

		return categories;
	}
}
