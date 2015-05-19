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
package de.hybris.platform.acceleratorservices.dataexport.googlelocal.converter;

import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.acceleratorservices.dataexport.googlelocal.model.Product;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.converter.impl.AbstractConverter;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.Message;


/**
 * Converts {@link ProductModel} to {@link Product}.
 */
public class ProductConverter extends AbstractConverter<Message<ProductModel>, Product>
{
	private static final Logger LOG = Logger.getLogger(ProductConverter.class);

	public static final int MAXIMUM_ADDITIONAL_IMAGES = 10;

	private UrlResolver<ProductModel> productModelUrlResolver;
	private CommercePriceService commercePriceService;
	private CategoryService categoryService;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	/**
	 * Constans with product condition.
	 */
	public static interface Condition
	{
		String NEW = "New";
		String USED = "Used";
		String REFURBISHED = "Refurbished";
	}

	protected UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	@Required
	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

	protected CommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	@Required
	public void setCommercePriceService(final CommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	/**
	 * All items that are color/material/pattern/size variants of the same product must have the same item group id. If
	 * you have a "Parent SKU" that is shared by all variants of a product, you can provide that as the value for 'item
	 * group id'.
	 * 
	 * @param productModel
	 *           the product
	 * @return groupId
	 */
	protected String getItemGroupId(final ProductModel productModel)
	{
		ProductModel currentProduct = productModel;
		while (currentProduct instanceof VariantProductModel)
		{
			final VariantProductModel variant = (VariantProductModel) currentProduct;
			currentProduct = variant.getBaseProduct();
		}
		return currentProduct.getCode();
	}

	protected String generateProductType(final ProductModel productModel)
	{
		final List<CategoryModel> categoryList = getCategoryService().getCategoryPathForProduct(productModel, CategoryModel.class);
		final StringBuilder productType = new StringBuilder();
		for (int i = 0; i < categoryList.size(); i++)
		{
			final CategoryModel categoryModel = categoryList.get(i);
			productType.append(i != 0 ? " > " : "");
			productType.append(categoryModel.getName());
		}
		return productType.toString();
	}

	protected String getProductCondition(final ProductModel productModel)
	{
		//always return new
		return Condition.NEW;
	}

	protected String getAdditionalImages(final String mediaUrl, final ProductModel productModel)
	{
		final List<MediaContainerModel> images = productModel.getGalleryImages();
		final StringBuilder imagesLinks = new StringBuilder();
		for (int i = 0; i < MAXIMUM_ADDITIONAL_IMAGES && i < images.size(); i++)
		{
			final MediaContainerModel mediaContainerModel = images.get(i);
			final Collection<MediaModel> media = mediaContainerModel.getMedias();
			if (media.iterator().hasNext())
			{
				imagesLinks.append(i != 0 ? "," : "");
				imagesLinks.append(mediaUrl).append(media.iterator().next().getURL());
			}
		}
		return imagesLinks.toString();
	}

	protected String generateGoogleProductCategory(final ProductModel productModel)
	{
		//some magical stuff that maps google categories to hybris accel ones
		return "";
	}

	@Override
	protected Product createTarget()
	{
		return new Product();
	}

	@Override
	public void populate(final Message<ProductModel> message, final Product product)
	{
		final ProductModel productModel = message.getPayload();
		if (productModel != null)
		{
			product.setItemid(productModel.getCode());
			product.setTitle(productModel.getName());

			product.setWebItemId(productModel.getCode());
			product.setGtin(productModel.getEan());
			product.setMpn(productModel.getManufacturerAID());
			product.setBrand(productModel.getManufacturerName());
			final PriceInformation info = getCommercePriceService().getWebPriceForProduct(productModel);
			product.setPrice(info != null ? (info.getPriceValue() != null ? Double.toString(info.getPriceValue().getValue()) : "")
					: "");
			product.setCondition(getProductCondition(productModel));

			product.setDescription(productModel.getDescription());
			product.setGoogleProductCategory(generateGoogleProductCategory(productModel));
			product.setProductType(generateProductType(productModel));

			if (message.getHeaders().get("event") instanceof ExportDataEvent)
			{
				final String relUrl = getProductModelUrlResolver().resolve(productModel);
				final ExportDataEvent ede = (ExportDataEvent) message.getHeaders().get("event");
				if (ede.getSite() != null)
				{
					final String urlForSite = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(ede.getSite(), false, relUrl);
					product.setLink(urlForSite);

					final String mediaUrl = getSiteBaseUrlResolutionService().getMediaUrlForSite(ede.getSite(), false);
					product.setImageLink(productModel.getPicture() != null ? mediaUrl != null ? mediaUrl
							+ productModel.getPicture().getURL() : productModel.getPicture().getURL() : "");
					product.setAdditionalImageLink(getAdditionalImages(mediaUrl != null ? mediaUrl : "", productModel));
				}
				else
				{
					LOG.info("Site on ExportDataEvent Object is null, URL Resolution skipped");
				}
			}

			product.setItemGroupId(getItemGroupId(productModel));
		}
	}
}
