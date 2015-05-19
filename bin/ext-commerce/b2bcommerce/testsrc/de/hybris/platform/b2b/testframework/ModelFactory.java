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
package de.hybris.platform.b2b.testframework;

import com.google.common.collect.Lists;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantCategoryModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.order.ZoneDeliveryModeService;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.user.daos.TitleDao;
import de.hybris.platform.servicelayer.user.daos.UserGroupDao;
import de.hybris.platform.variants.jalo.VariantProduct;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;


/**
 * This is a class for easy model creation, it should be used in test as well as for initial creation of a valid model
 * of any type. The purpose is to avoid complicated fixture setup or to simplify any model creation.
 * 
 * TODO in the future the factory class should be split in general model creation methods and in extension specific ones
 */

public class ModelFactory
{
	@Resource
	ModelService modelService;

	@Resource
	CatalogService catalogService;

	@Resource
	CatalogVersionService catalogVersionService;

	@Resource
	UnitService unitService;

	@Resource
	CommonI18NService commonI18NService;

	@Resource
	CategoryService categoryService;

	@Resource
	FlexibleSearchService flexibleSearchService;

	@Resource
	UserGroupDao userGroupDao;

	@Resource
	MediaService mediaService;

	@Resource
	ZoneDeliveryModeService zoneDeliveryModeService;

	@Resource
	PaymentModeService paymentModeService;

	@Resource
	AddressService addressService;

	@Resource
	CurrencyDao currencyDao;

	@Resource
	PriceService priceService;

	@Resource
	TitleDao titleDao;

	@Resource
	UserService userService;

	@Resource
	TypeService typeService;

	@Resource
	ProductService productService;

	private String defaultCatalogId = "testCatalog";
	private String defaultCatalogVersion = "Staged";

	public ProductModel createProductWithPrice(final String code, final Double price, final String currencyIso,
			final String currencySymbol, final Boolean isNet, final CatalogVersionModel catalogVersion,
			final String variantTypeCode, final VariantValueCategoryModel... superCategories)
	{
		final ProductModel product = createProduct(code, catalogVersion, variantTypeCode, createUnit("STK"), superCategories);
		createPriceRow(createCurrency(currencyIso, currencySymbol), price, createUnit("STK"), product, isNet);
		modelService.save(product);
		return product;
	}

	/**
	 * @param code
	 * @param catalogVersion
	 * @param variantTypeCode
	 *           The type code of the variant, usually it can be retrieved from the variant class it self. E.g. if your
	 *           variant type is VariantProduct its type code can be retrieved by calling
	 *           {@link VariantProduct._TYPECODE}
	 * @param unit
	 *           Unit that this product is measured in
	 * @param superCategories
	 * @return
	 */
	public ProductModel createProduct(final String code, final CatalogVersionModel catalogVersion, final String variantTypeCode,
			final UnitModel unit, final CategoryModel... superCategories)
	{
		ProductModel product = null;
		try
		{
			productService.getProductForCode(catalogVersion, code);
		}
		catch (UnknownIdentifierException e)
		{
			// no product, we will create a new one
		}
		if (product == null)
		{
			product = new ProductModel();
			product.setCatalogVersion(catalogVersion);
			product.setCode(code);
		}

		product.setUnit(unit);
		product.setVariantType(createVariantType(variantTypeCode));
		product.setSupercategories(Lists.<CategoryModel> newArrayList(superCategories));

		modelService.save(product);
		return product;
	}

	public PriceRowModel createPriceRow(final CurrencyModel currency, final Double price, final UnitModel unit,
			final ProductModel product, final Boolean isNet)
	{
		final PriceRowModel priceRow = new PriceRowModel();
		priceRow.setCurrency(currency);
		priceRow.setPrice(price);
		priceRow.setUnit(unit);
		priceRow.setNet(isNet);
		priceRow.setProduct(product);
		priceRow.setCatalogVersion(createDefaultCatalogVersion());
		modelService.save(priceRow);
		return priceRow;
	}

	public LanguageModel createLanguage(final String isoCode)
	{
		LanguageModel language = null;
		try
		{
			language = commonI18NService.getLanguage(isoCode);
		}
		catch (final UnknownIdentifierException e)
		{
			// nothing found,create a new one
		}
		if (language == null)
		{
			language = new LanguageModel();
			language.setIsocode(isoCode);
			language.setActive(Boolean.TRUE);
			modelService.save(language);
		}
		return language;
	}

	public AddressModel createAddressByUniqueEmail(final String email, final ItemModel owner, final AddressModel addressData)
	{
		Assert.assertNotNull("Owner must be given for the address, it can not be null!", owner);
		Assert.assertNotNull("Email must be given for the address, since it is used as uid for it.", email);
		Assert.assertNotNull("Address data can contain no values but must be at least initialized!", addressData);
		AddressModel address = null;
		try
		{
			final Collection<AddressModel> addresses = addressService.getAddressesForOwner(owner);
			if (CollectionUtils.isNotEmpty(addresses))
			{
				address = (AddressModel) CollectionUtils.find(addresses, new Predicate()
				{
					@Override
					public boolean evaluate(final Object arg0)
					{
						final AddressModel a = (AddressModel) arg0;
						return StringUtils.equals(a.getEmail(), email);
					}
				});
			}
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (address == null)
		{
			address = new AddressModel();
			address.setOwner(owner);
			address.setEmail(email);
			address.setFirstname(addressData.getFirstname());
			address.setLastname(addressData.getLastname());
			address.setStreetname(addressData.getStreetname());
			address.setStreetnumber(addressData.getStreetnumber());
			address.setPostalcode(addressData.getPostalcode());
			address.setTown(addressData.getTown());
			address.setPhone1(addressData.getPhone1());
			modelService.save(address);
		}
		return address;
	}

	public ZoneModel createZone(final String code)
	{
		ZoneModel zone = null;
		try
		{
			zone = zoneDeliveryModeService.getZoneForCode(code);
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (zone == null)
		{
			zone = new ZoneModel();
			zone.setCode(code);
			modelService.save(zone);
		}
		return zone;
	}

	public CurrencyModel createCurrency(final String isoCode, final String symbol)
	{
		CurrencyModel currency = null;
		try
		{
			final List<CurrencyModel> currencies = currencyDao.findCurrenciesByCode(isoCode);
			if (CollectionUtils.isNotEmpty(currencies))
			{
				currency = currencies.get(0);
			}
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (currency == null)
		{
			currency = new CurrencyModel();
			currency.setSymbol(symbol);
			currency.setIsocode(isoCode);
			modelService.save(currency);
		}
		return currency;
	}

	public CountryModel createCountry(final String isoCode)
	{
		CountryModel country = null;
		try
		{
			country = commonI18NService.getCountry(isoCode);
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (country == null)
		{
			country = new CountryModel();
			country.setIsocode(isoCode);
			modelService.save(country);
		}
		return country;
	}

	public MediaFolderModel createMediaFolder(final String qualifier, final String path)
	{
		MediaFolderModel folder = null;
		try
		{
			folder = mediaService.getFolder(qualifier);
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (folder == null)
		{
			folder = new MediaFolderModel();
			folder.setQualifier(qualifier);
			folder.setPath(path);
			modelService.save(folder);
		}
		return folder;
	}

	public MediaFormatModel createMediaFormat(final String qualifier)
	{
		MediaFormatModel format = null;
		try
		{
			format = mediaService.getFormat(qualifier);
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (format == null)
		{
			format = new MediaFormatModel();
			format.setQualifier(qualifier);
			modelService.save(format);
		}
		return format;
	}

	public TitleModel createTitle(final String code)
	{
		TitleModel title = null;
		try
		{
			title = titleDao.findTitleByCode(code);
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (title == null)
		{
			title = new TitleModel();
			title.setCode(code);
			modelService.save(title);
		}
		return title;
	}

	public MediaModel createMedia(final String code, final CatalogVersionModel catalogVersion)
	{
		Assert.assertNotNull(catalogVersion);
		MediaModel format = null;
		try
		{
			format = mediaService.getMedia(catalogVersion, code);
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (format == null)
		{
			format = new MediaModel();
			format.setCode(code);
			format.setCatalogVersion(catalogVersion);
			format.setRealFileName(code);
			modelService.save(format);
		}
		return format;
	}

	public UserGroupModel createUserGroup(final String uid)
	{
		UserGroupModel group = null;
		try
		{
			group = userGroupDao.findUserGroupByUid(uid);
		}
		catch (final Exception e)
		{
			// nothing found,create a new one
		}
		if (group == null)
		{
			group = new UserGroupModel();
			group.setUid(uid);
			modelService.save(group);
		}
		return group;
	}

	public CatalogVersionModel createCatalogVersion(final String catalogId, final String catalogVersion)
	{
		CatalogVersionModel version = null;
		try
		{
			version = catalogVersionService.getCatalogVersion(catalogId, catalogVersion);
		}
		catch (final Exception e)
		{
			// nothing found new one will be created
		}
		if (version == null)
		{
			version = new CatalogVersionModel();
			version.setCatalog(createCatalog(catalogId));
			version.setVersion(catalogVersion);
			modelService.save(version);
		}

		return version;
	}

	public CatalogModel createCatalog(final String catalogId)
	{
		CatalogModel catalog = null;
		try
		{
			catalog = catalogService.getCatalogForId(catalogId);
		}
		catch (final Exception e)
		{
			// nothing found new one will be created
		}
		if (catalog == null)
		{
			catalog = new CatalogModel();
			catalog.setId(catalogId);
			modelService.save(catalog);
		}
		return catalog;
	}

	public CategoryModel createCategory(final String code, final CatalogVersionModel catalogVersion)
	{
		CategoryModel category = null;
		try
		{
			category = categoryService.getCategoryForCode(code);
		}
		catch (final Exception e)
		{
			// nothing found new one will be created
		}
		if (category == null)
		{
			category = new CategoryModel();
			category.setCode(code);
			category.setCatalogVersion(catalogVersion);
			modelService.save(category);
		}
		return category;
	}


	public CategoryModel createDefaultCategory(final String code)
	{
		return createCategory(code, createDefaultCatalogVersion());
	}

	public CatalogVersionModel createOnlineCatalogVersion(final String catalogId)
	{
		return createCatalogVersion(catalogId, "Online");
	}

	public CatalogVersionModel createStageCatalogVersion(final String catalogId)
	{
		return createCatalogVersion(catalogId, "Staged");
	}

	public CatalogVersionModel createDefaultCatalogVersion()
	{
		Assert.assertNotNull("Please setup a default catalog version, by calling the setter on the service",
				getDefaultCatalogVersion());
		Assert.assertNotNull("Please setup a default catalog id, by calling the setter on the service", getDefaultCatalogId());
		return createCatalogVersion(getDefaultCatalogId(), getDefaultCatalogVersion());
	}

	public UnitModel createUnit(final String code)
	{
		UnitModel unit = null;
		try
		{
			unit = unitService.getUnitForCode(code);
		}
		catch (final Exception e)
		{
			// nothing found create a new one
		}
		if (unit == null)
		{
			unit = new UnitModel();
			unit.setCode(code);
			unit.setUnitType("<autogenerated unit type>");
			unit.setConversion(new Double(1.0));
			modelService.save(unit);
		}
		return unit;
	}

	public ProductReferenceModel createProductReference(final ProductModel sourceProduct, final ProductModel targetProduct,
			final ProductReferenceTypeEnum referenceType)
	{
		final ProductReferenceModel reference = new ProductReferenceModel();
		reference.setActive(Boolean.TRUE);
		reference.setPreselected(Boolean.TRUE);
		reference.setSource(sourceProduct);
		reference.setTarget(targetProduct);
		reference.setReferenceType(referenceType);
		modelService.save(reference);
		return reference;
	}

	public CustomerModel createCustomer(final String uid)
	{
		CustomerModel customer = null;
		try
		{
			customer = (CustomerModel) userService.getUserForUID(uid);
		}
		catch (final Exception e)
		{
			// nothing found create a new one
		}
		if (customer == null)
		{
			customer = new CustomerModel();
			customer.setUid(uid);
			final AddressModel addressData = new AddressModel();
			addressData.setOwner(customer);
			addressData.setGender(Gender.MALE);
			customer.setDefaultPaymentAddress(createAddressByUniqueEmail(uid + "@gmail.com", customer, addressData));
			customer.setAddresses(Lists.newArrayList(addressData));
			modelService.save(customer);
		}
		return customer;
	}

	public String getDefaultCatalogVersion()
	{
		return defaultCatalogVersion;
	}

	public void setDefaultCatalogVersion(final String defaultCatalogVersion)
	{
		this.defaultCatalogVersion = defaultCatalogVersion;
	}

	public String getDefaultCatalogId()
	{
		return defaultCatalogId;
	}

	public void setDefaultCatalogId(final String defaultCatalogId)
	{
		this.defaultCatalogId = defaultCatalogId;
	}

	public void save(final Object modelToSave)
	{
		modelService.save(modelToSave);
	}

	public ComposedTypeModel createComposedType(final String typeCode)
	{
		return typeService.getComposedTypeForCode(typeCode);
	}

	// **************   B2B specific methods   ************************ 

	public VariantCategoryModel createVariantCategory(final String code, final CatalogVersionModel catalogVersion)
	{
		VariantCategoryModel category = null;
		try
		{
			category = (VariantCategoryModel) categoryService.getCategoryForCode(code);
		}
		catch (final Exception e)
		{
			// nothing found new one will be created
		}
		if (category == null)
		{
			category = new VariantCategoryModel();
			category.setCode(code);
			category.setCatalogVersion(catalogVersion);
			modelService.save(category);
		}
		return category;
	}

	public VariantValueCategoryModel createVariantValueCategory(final String code, final VariantCategoryModel parentCategory,
			final int sequenceNumber, final CatalogVersionModel catalogVersion)
	{
		VariantValueCategoryModel category = null;
		try
		{
			category = (VariantValueCategoryModel) categoryService.getCategoryForCode(code);
		}
		catch (final Exception e)
		{
			// nothing found new one will be created
		}
		if (category == null)
		{
			category = new VariantValueCategoryModel();
			category.setCode(code);
		}
		category.setCatalogVersion(catalogVersion);
		category.setSupercategories(Lists.<CategoryModel> newArrayList(parentCategory));
		category.setSequence(sequenceNumber);
		save(category);
		save(parentCategory);
		return category;
	}

	public GenericVariantProductModel createGenericVariantProduct(final String code, final ProductModel baseProduct,
			final CatalogVersionModel catalogVersion, final VariantValueCategoryModel... variantSuperCategories)
	{
		final GenericVariantProductModel variant = new GenericVariantProductModel();
		variant.setCode(code);
		variant.setBaseProduct(baseProduct);
		variant.setCatalogVersion(catalogVersion);
		variant.setSupercategories((Collection) Lists.<VariantValueCategoryModel> newArrayList(variantSuperCategories));
		save(variant);
		return variant;
	}

	public VariantProductModel createVariantProduct(final String code, final ProductModel baseProduct,
			final CatalogVersionModel catalogVersion, final VariantTypeModel variantType)
	{
		final VariantProductModel product = new VariantProductModel();
		product.setCatalogVersion(createDefaultCatalogVersion());
		product.setCode(code);
		product.setVariantType(variantType);
		save(product);
		return product;
	}

	public VariantTypeModel createVariantType(final String variantTypeCode)
	{
		return (VariantTypeModel) typeService.getComposedTypeForCode(variantTypeCode);
	}

}
