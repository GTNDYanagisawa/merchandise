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
package de.hybris.platform.sap.productconfig.frontend.controllers;


import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.Sapproductconfigb2baddonConstants;
import de.hybris.platform.sap.productconfig.frontend.util.impl.PathExtractor;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;
import de.hybris.platform.sap.productconfig.frontend.validator.ConflictChecker;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.pages.AbstractPageController;
import de.hybris.platform.yb2bacceleratorstorefront.forms.ReviewForm;
import de.hybris.platform.yb2bacceleratorstorefront.util.MetaSanitizerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;


public abstract class AbstractProductConfigController extends AbstractPageController
{
	public static final String PRODUCT_SESSION_PREFIX = "product_code_";
	public static final String PRODUCT_CART_ITEM_SESSION_PREFIX = "product_code_cart_pk_";
	public static final String CART_ITEM_HANDLE_SESSION_PREFIX = "product_config_cart_handle_";
	static final String PRODUCT_ATTRIBUTE = "product";


	static final Logger LOG = Logger.getLogger(AbstractProductConfigController.class);

	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade configFacade;

	@Resource(name = "sapProductConfigCartIntegrationFacade")
	private ConfigurationCartIntegrationFacade configCartFacade;

	@Resource(name = "sapProductConfigValidator")
	private Validator productConfigurationValidator;

	@Resource(name = "sapProductConfigConflictChecker")
	private ConflictChecker productConfigurationConflictChecker;

	@Resource(name = "b2bProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "productService")
	private ProductService productService;

	public AbstractProductConfigController()
	{
		super();
	}

	@InitBinder(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE)
	protected void initBinder(final WebDataBinder binder)
	{
		binder.setValidator(productConfigurationValidator);
	}

	protected BindingResult restoreValidationErrorsAfterUpdate(final Map<String, FieldError> userInputToRestore,
			final ConfigurationData latestConfiguration)
	{
		//discard the old error binding and create a new one instead, that only contains errors for visible cstics
		final BindingResult bindingResult = new BeanPropertyBindingResult(latestConfiguration,
				Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE);
		for (final UiGroupData group : latestConfiguration.getGroups())
		{
			restoreValidationErrorsInGroup(userInputToRestore, bindingResult, group);
		}
		return bindingResult;
	}

	private void restoreValidationErrorsInGroup(final Map<String, FieldError> userInputToRestore,
			final BindingResult bindingResult, final UiGroupData group)
	{
		for (final CsticData latestCstic : group.getCstics())
		{
			final UiType uiType = latestCstic.getType();
			final boolean restoreValidationError = latestCstic.isVisible() && uiType != UiType.READ_ONLY
					&& userInputToRestore.containsKey(latestCstic.getKey());
			if (restoreValidationError)
			{
				latestCstic.setCsticStatus(CsticStatusType.ERROR);
				final FieldError fieldError = userInputToRestore.get(latestCstic.getKey());
				final String errorValue = fieldError.getRejectedValue().toString();
				if (UiType.DROPDOWN_ADDITIONAL_INPUT == uiType || UiType.RADIO_BUTTON_ADDITIONAL_INPUT == uiType)
				{
					latestCstic.setAdditionalValue(errorValue);
				}
				else
				{
					latestCstic.setValue(errorValue);
				}
				bindingResult.addError(fieldError);
				group.setGroupStatus(GroupStatusType.ERROR);
			}
		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (null == subGroups)
		{
			return;
		}

		for (final UiGroupData subGroup : subGroups)
		{
			restoreValidationErrorsInGroup(userInputToRestore, bindingResult, subGroup);
		}
	}

	protected Map<String, FieldError> handleValidationErrorsBeforeUpdate(final ConfigurationData configData,
			final BindingResult bindingResult)
	{
		Map<String, FieldError> userInputToRestore;
		final int capacity = (int) (bindingResult.getErrorCount() / 0.75) + 1;
		userInputToRestore = new HashMap(capacity);
		for (final FieldError error : bindingResult.getFieldErrors())
		{
			final String fieldPath = error.getField();
			final PathExtractor extractor = new PathExtractor(fieldPath);
			final int groupIndex = extractor.getGroupIndex();
			final int csticIndex = extractor.getCsticsIndex();
			UiGroupData group = configData.getGroups().get(groupIndex);
			for (int i = 0; i < extractor.getSubGroupCount(); i++)
			{
				group = group.getSubGroups().get(extractor.getSubGroupIndex(i));
			}
			final CsticData cstic = group.getCstics().get(csticIndex);
			userInputToRestore.put(cstic.getKey(), error);
			cstic.setValue(cstic.getLastValidValue());
			cstic.setAdditionalValue("");
		}
		return userInputToRestore;
	}

	protected void populateConfigurationModel(final KBKeyData kbKey, final String configId, final String selectedGroup,
			final Model model, final UiStatus uiStatus, final String cartEntryHandleFromRequest)
	{
		if (model.containsAttribute(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE))
		{
			return;
		}

		final ConfigurationData configData;
		if (configId != null)
		{
			configData = reloadConfiguration(kbKey, configId, selectedGroup, uiStatus);
		}
		else
		{
			configData = loadNewConfiguration(kbKey, selectedGroup);
		}

		if (cartEntryHandleFromRequest == null)
		{
			setCartItemPk(configData);
		}
		else
		{
			configData.setCartItemPK(cartEntryHandleFromRequest);
		}

		model.addAttribute(Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE, configData);

		final BindingResult errors = getBindingResultForConfig(configData);
		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE, errors);

	}

	private BindingResult getBindingResultForConfig(final ConfigurationData configData)
	{
		final BindingResult errors = new BeanPropertyBindingResult(configData, Sapproductconfigb2baddonConstants.CONFIG_ATTRIBUTE);

		resetGroupStatus(configData);
		productConfigurationConflictChecker.checkConflicts(configData, errors);
		if (configData.getCartItemPK() != null && !configData.getCartItemPK().isEmpty())
		{
			productConfigurationConflictChecker.checkMandatoryFields(configData, errors);
		}
		return errors;
	}

	private void setCartItemPk(final ConfigurationData configData)
	{
		final String cartItemKey = getSessionService().getAttribute(
				PRODUCT_CART_ITEM_SESSION_PREFIX + configData.getKbKey().getProductCode());
		if (cartItemKey != null)
		{
			final boolean isItemInCart = configCartFacade.isItemInCartByKey(cartItemKey);
			if (!isItemInCart)
			{
				getSessionService().removeAttribute(PRODUCT_CART_ITEM_SESSION_PREFIX + configData.getKbKey().getProductCode());

			}
			else
			{
				configData.setCartItemPK(cartItemKey);
			}
		}
	}

	protected ConfigurationData loadNewConfiguration(final KBKeyData kbKey, final String selectedGroup)
	{
		final ConfigurationData configData;
		configData = configFacade.getConfiguration(kbKey);
		final UiStatusSync uiStatusSync = new UiStatusSync();

		uiStatusSync.setInitialStatus(configData);
		if (selectedGroup != null && !selectedGroup.isEmpty())
		{
			configData.setSelectedGroup(selectedGroup);
		}
		final UiStatus uiStatus = uiStatusSync.storeUiStatusInSession(configData);
		getSessionService().setAttribute(PRODUCT_SESSION_PREFIX + configData.getKbKey().getProductCode(), uiStatus);
		return configData;
	}

	protected ConfigurationData reloadConfiguration(final KBKeyData kbKey, final String configId, final String selectedGroup,
			final UiStatus uiStatus)
	{
		final ConfigurationData configData = this.getConfigData(kbKey, configId);
		final UiStatusSync uiStatusSync = new UiStatusSync();
		if (selectedGroup != null && !selectedGroup.isEmpty())
		{
			uiStatusSync.updateUiStatus(configData, uiStatus, selectedGroup);
		}
		else
		{
			uiStatusSync.updateUiStatus(configData, uiStatus);
		}
		return configData;
	}

	protected ConfigurationData getConfigData(final KBKeyData kbKey, final String configId)
	{
		final ConfigurationData configData;
		final ConfigurationData configContent = new ConfigurationData();
		configContent.setConfigId(configId);
		configContent.setKbKey(kbKey);
		configData = configFacade.getConfiguration(configContent);
		return configData;
	}


	protected void populateProductModel(final ProductModel productModel, final Model model) throws CMSItemNotFoundException
	{
		populateProductDetailForDisplay(productModel, model);
		model.addAttribute(new ReviewForm());
		model.addAttribute("pageType", "productConfigPage");

		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(productModel.getKeywords());
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(productModel.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);
	}

	protected void populateProductDetailForDisplay(final ProductModel productModel, final Model model)
			throws CMSItemNotFoundException
	{
		final ProductData productData = getProductFacade().getProductForOptions(
				productModel,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
						ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS,
						ProductOption.CLASSIFICATION, ProductOption.VARIANT_FULL, ProductOption.STOCK, ProductOption.VOLUME_PRICES,
						ProductOption.PRICE_RANGE, ProductOption.VARIANT_MATRIX));

		final AbstractPageModel configPage = getPageForProduct();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Using CMS page: " + configPage.getName() + "[" + configPage.getUid() + "]");
		}
		storeCmsPageInModel(model, configPage);
		populateProductData(productData, model);
	}

	protected void populateProductData(final ProductData productData, final Model model)
	{
		model.addAttribute("galleryImages", getGalleryImages(productData));
		model.addAttribute(PRODUCT_ATTRIBUTE, productData);
	}

	protected List<Map<String, ImageData>> getGalleryImages(final ProductData productData)
	{
		final List<Map<String, ImageData>> galleryImages = new ArrayList<Map<String, ImageData>>();
		if (CollectionUtils.isNotEmpty(productData.getImages()))
		{
			final List<ImageData> images = new ArrayList<ImageData>();
			for (final ImageData image : productData.getImages())
			{
				if (ImageDataType.GALLERY.equals(image.getImageType()))
				{
					images.add(image);
				}
			}
			Collections.sort(images, new Comparator<ImageData>()
			{
				@Override
				public int compare(final ImageData image1, final ImageData image2)
				{
					return image1.getGalleryIndex().compareTo(image2.getGalleryIndex());
				}
			});

			if (CollectionUtils.isNotEmpty(images))
			{
				int currentIndex = images.get(0).getGalleryIndex().intValue();
				Map<String, ImageData> formats = new HashMap<String, ImageData>();
				for (final ImageData image : images)
				{
					if (currentIndex != image.getGalleryIndex().intValue())
					{
						galleryImages.add(formats);
						formats = new HashMap<String, ImageData>();
						currentIndex = image.getGalleryIndex().intValue();
					}
					formats.put(image.getFormat(), image);
				}
				if (!formats.isEmpty())
				{
					galleryImages.add(formats);
				}
			}
		}
		return galleryImages;
	}

	protected AbstractPageModel getPageForProduct() throws CMSItemNotFoundException
	{
		return getCmsPageService().getPageForId("productConfig");
	}

	protected KBKeyData createKBKeyForProduct(final ProductModel productModel)
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productModel.getCode());
		return kbKey;
	}

	protected void removeNullCsticsFromGroup(final List<CsticData> dirtyList)
	{
		if (dirtyList == null)
		{
			return;
		}

		final List<CsticData> cleanList = new ArrayList<>(dirtyList.size());

		for (final CsticData data : dirtyList)
		{
			if (data.getName() != null && data.getType() != UiType.READ_ONLY)
			{
				cleanList.add(data);
			}
		}

		dirtyList.clear();
		dirtyList.addAll(cleanList);

	}

	protected void removeNullCstics(final List<UiGroupData> groups)
	{
		if (groups == null)
		{
			return;
		}

		for (final UiGroupData group : groups)
		{
			removeNullCsticsFromGroup(group.getCstics());

			final List<UiGroupData> subGroups = group.getSubGroups();
			removeNullCstics(subGroups);
		}
	}

	protected void resetGroupStatus(final ConfigurationData configData)
	{
		final List<UiGroupData> uiGroups = configData.getGroups();
		resetGroupWithSubGroups(uiGroups);
	}

	private void resetGroupWithSubGroups(final List<UiGroupData> uiGroups)
	{
		for (final UiGroupData group : uiGroups)
		{
			group.setGroupStatus(GroupStatusType.DEFAULT);
			if (group.getSubGroups() != null && !group.getSubGroups().isEmpty())
			{
				resetGroupWithSubGroups(group.getSubGroups());
			}
		}
	}


	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	protected ConfigurationCartIntegrationFacade getConfigCartFacade()
	{
		return configCartFacade;
	}

	protected void setConfigCartFacade(final ConfigurationCartIntegrationFacade configCartFacade)
	{
		this.configCartFacade = configCartFacade;
	}

	protected void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

	protected Validator getProductConfigurationValidator()
	{
		return productConfigurationValidator;
	}

	protected void setProductConfigurationValidator(final Validator productConfigurationValidator)
	{
		this.productConfigurationValidator = productConfigurationValidator;
	}

	protected ConflictChecker getProductConfigurationConflictChecker()
	{
		return productConfigurationConflictChecker;
	}

	protected void setProductConfigurationConflictChecker(final ConflictChecker productConfigurationConflictChecker)
	{
		this.productConfigurationConflictChecker = productConfigurationConflictChecker;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	protected void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

}
