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
package de.hybris.platform.webservices.impl;


import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.Company;
import de.hybris.platform.catalog.jalo.Keyword;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.core.MasterTenant;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.jalo.Job;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.c2l.Region;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.order.payment.PaymentMode;
import de.hybris.platform.jalo.order.price.Discount;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloDuplicateCodeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NDao;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.BadRequestException;
import de.hybris.platform.webservices.WsUtilService;
import de.hybris.platform.webservices.YWebservicesException;
import de.hybris.platform.webservices.objectgraphtransformer.YNodeFactory;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeFactory;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.CachedClassLookupMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class implements interface <code>WsUtilService</code> and temporarily inholds service method definitions, that
 * was needed in restjersey and was not yet defined in expected platform service classes.
 * 
 * @since 4.0
 * @spring.bean restjerseyService
 * @see de.hybris.platform.webservices.WsUtilService#getAddress(java.lang.Long)
 */
public class DefaultWsUtilService extends AbstractBusinessService implements WsUtilService
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultWsUtilService.class);


	// dependencies
	private FlexibleSearchService flexibleSearchService;
	private I18NService i18nService;
	private I18NDao i18nDao;
	private UserService userService;
	private CronJobService cronJobService;

	private Map<Object, NodeFactory> nodeFactoriesMap = null;
	private final CachedClassLookupMap<NodeFactory> cachedNodeFactoriesMap = new CachedClassLookupMap<NodeFactory>();

	/**
	 * @param dtoClass
	 *           the dtoClass to set
	 * @return the NodeFactory or null
	 */
	@Override
	public NodeFactory findNodeFactory(final Class dtoClass)
	{
		YNodeFactory nodeFactory = null;
		if (cachedNodeFactoriesMap.isEmpty())
		{
			for (final Object iter : nodeFactoriesMap.keySet())
			{
				cachedNodeFactoriesMap.put(iter.getClass(), nodeFactoriesMap.get(iter));
			}
		}
		nodeFactory = (YNodeFactory) cachedNodeFactoriesMap.get(dtoClass);
		return nodeFactory;
	}

	/**
	 * @param nodeFactoriesMap
	 *           the nodeFactoriesMap to set
	 */
	public void setNodeFactoriesMap(final Map<Object, NodeFactory> nodeFactoriesMap)
	{
		this.nodeFactoriesMap = nodeFactoriesMap;
	}

	/**
	 * @param cronJobService
	 *           the cronJobService to set
	 */
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	/**
	 * @return the i18nService
	 */
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}


	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAddress(java.lang.Long)
	 */
	@Override
	public AddressModel getAddress(final Long pk)
	{
		final PK _pk = PK.fromLong(pk.longValue());
		return getModelService().get(_pk);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllAddresses()
	 */
	@Override
	public List<AddressModel> getAllAddresses()
	{
		final SearchResult<AddressModel> result = flexibleSearchService.search("Select {pk} From {Address}");
		final List<AddressModel> userAddresses = new ArrayList<AddressModel>();
		for (final AddressModel address : result.getResult())
		{
			if (address.getOwner() instanceof UserModel)
			{
				userAddresses.add(address);
			}
		}
		return userAddresses;
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllRegions(java.lang.String)
	 */
	@Override
	public Set<RegionModel> getAllRegions(final String countryCode)
	{
		final Set<RegionModel> result = new HashSet<RegionModel>();
		final Collection<RegionModel> regions = i18nDao.findCountry(countryCode).getRegions();
		result.addAll(regions);
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.WsUtilService#getAllRegions()
	 */
	@Override
	public Collection<RegionModel> getAllRegions()
	{
		final Collection<Region> regions = C2LManager.getInstance().getAllRegions();
		return getModelService().getAll(regions, new ArrayList());

	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllUnits()
	 */
	@Override
	public Collection<UnitModel> getAllUnits()
	{
		final Collection<Unit> units = ProductManager.getInstance().getAllUnits();
		final List<UnitModel> unitModels = new ArrayList<UnitModel>();
		getModelService().getAll(units, unitModels);
		return unitModels;
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getRegion(java.lang.String)
	 */
	@Override
	public RegionModel getRegion(final String isocode)
	{
		return i18nDao.findRegion(isocode);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getRegion(java.lang.String,java.lang.String)
	 */
	@Override
	public RegionModel getRegion(final String countryCode, final String isocode)
	{
		final RegionModel result = i18nDao.findRegion(isocode);
		if (result != null && result.getCountry() != null && !countryCode.equals(result.getCountry().getIsocode()))
		{
			throw new UnknownIdentifierException("No region with isocode " + isocode + "found for country with isocode "
					+ countryCode);
		}
		return result;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @param dao
	 *           the i18nDao to set
	 */
	@Required
	public void setI18nDao(final I18NDao dao)
	{
		i18nDao = dao;
	}


	/**
	 * @see de.hybris.platform.webservices.WsUtilService#setRequestUserIntoJaloSession(java.lang.String)
	 */
	@Override
	public void setRequestUserIntoJaloSession(final String userID)
	{
		final UserModel userModel = userService.getUserForUID(userID);
		final User user = getModelService().getSource(userModel);
		JaloSession.getCurrentSession().setUser(user);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#setCatalogVersionsIntoJaloSession()
	 */
	@Override
	public void setCatalogVersionsIntoJaloSession()
	{
		final JaloSession jaloSession = JaloSession.getCurrentSession();
		final Collection<CatalogVersion> collCatVersions = getCatalogVersions(jaloSession.getUser());

		if (!collCatVersions.isEmpty())
		{
			jaloSession.setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, collCatVersions);
		}
	}

	/**
	 * Method returns appropriate catalog versions for current authenticated user.
	 * 
	 * @param currentAuthenticatedUser
	 *           current authenticated user
	 * 
	 * @return collection of catalogversion for (session)user.
	 */
	private Collection<CatalogVersion> getCatalogVersions(final User currentAuthenticatedUser)
	{
		Collection<CatalogVersion> availableCatalogVersions = new ArrayList<CatalogVersion>();

		//part1: store only readable catalog versions
		availableCatalogVersions = CatalogManager.getInstance().getAllReadableCatalogVersions(null, currentAuthenticatedUser);

		//part2(optional): if user doesn't have any assigned readable catalog versions then get all available catalog versions.
		if (availableCatalogVersions.isEmpty())
		{
			availableCatalogVersions = CatalogManager.getInstance().getAllCatalogVersions();
		}

		return availableCatalogVersions;
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllCarts()
	 */
	@Override
	public Collection<CartModel> getAllCarts()
	{
		final SearchResult<CartModel> result = flexibleSearchService.search("Select {pk} From {Cart}");
		return result.getResult();
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllDiscounts()
	 */
	@Override
	public Collection<DiscountModel> getAllDiscounts()
	{
		final Collection<Discount> discounts = OrderManager.getInstance().getAllDiscounts();
		final Collection<DiscountModel> discountsModel = new ArrayList<DiscountModel>();
		getModelService().getAll(discounts, discountsModel);
		return discountsModel;
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getDiscount(java.lang.String)
	 */
	@Override
	public DiscountModel getDiscount(final String code)
	{
		final Discount discount = OrderManager.getInstance().getDiscountByCode(code);
		DiscountModel discountModel = null;
		if (discount == null)
		{
			throw new UnknownIdentifierException("Discount with code: " + code + " doesn't exist.");
		}
		else
		{
			discountModel = getModelService().get(discount);
		}
		return discountModel;
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllCustomers()
	 */
	@Override
	public Collection<CustomerModel> getAllCustomers()
	{
		final Collection<Customer> customers = UserManager.getInstance().getAllCustomers();
		final Collection<CustomerModel> customerModels = new ArrayList<CustomerModel>();
		getModelService().getAll(customers, customerModels);
		return customerModels;
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getCustomer(java.lang.String)
	 */
	@Override
	public CustomerModel getCustomer(final String login)
	{
		final Customer customer = UserManager.getInstance().getCustomerByLogin(login);
		return getModelService().get(customer);
	}


	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllUserGroups()
	 */
	@Override
	public Collection<UserGroupModel> getAllUserGroups()
	{
		final Collection<UserGroupModel> result = new ArrayList<UserGroupModel>();
		getModelService().getAll(UserManager.getInstance().getAllUserGroups(), result);
		return result;
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getDeliveryModeByCode(java.lang.String)
	 */
	@Override
	public DeliveryModeModel getDeliveryModeByCode(final String code)
	{
		final OrderManager orderManager = OrderManager.getInstance();
		final DeliveryMode deliveryMode = orderManager.getDeliveryModeByCode(code);

		return getModelService().get(deliveryMode);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getDeliveryModesByCode(java.lang.String)
	 */
	@Override
	public Collection<DeliveryModeModel> getDeliveryModesByCode(final String code)
	{
		final OrderManager orderManager = OrderManager.getInstance();
		final Collection<DeliveryMode> deliveryModes = orderManager.getAllDeliveryModes();

		return getModelService().getAll(deliveryModes, new ArrayList<DeliveryModeModel>());
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getPaymentModeByCode(java.lang.String)
	 */
	@Override
	public PaymentModeModel getPaymentModeByCode(final String code)
	{
		final OrderManager orderManager = OrderManager.getInstance();
		final PaymentMode paymentMode = orderManager.getPaymentModeByCode(code);

		return getModelService().get(paymentMode);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getPaymentModesByCode(java.lang.String)
	 */
	@Override
	public Collection<PaymentModeModel> getPaymentModesByCode(final String code)
	{
		final OrderManager orderManager = OrderManager.getInstance();
		final Collection<PaymentMode> paymentModes = orderManager.getAllPaymentModes();

		return getModelService().getAll(paymentModes, new ArrayList<PaymentModeModel>());
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllEnumerationTypes()
	 */
	@Override
	public Collection<EnumerationMetaTypeModel> getAllEnumerationTypes()
	{
		final EnumerationManager enumManager = EnumerationManager.getInstance();
		final Collection enumerations = enumManager.getAllEnumerationTypes();
		return getModelService().getAll(enumerations, new ArrayList<EnumerationMetaTypeModel>());
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#createEnumerationValue(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public EnumerationValueModel createEnumerationValue(final String typeCode, final String valueCode, final String name)
	{
		try
		{
			final EnumerationManager enumManager = EnumerationManager.getInstance();
			final EnumerationValue enumValue = enumManager.createEnumerationValue(typeCode, valueCode);
			enumValue.setName(name); //saving of the name
			return this.getModelService().get(enumValue.getPK());
		}
		catch (final ConsistencyCheckException e)
		{
			throw new BadRequestException("Error creating enumeration", e);
		}
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#createEnumerationType(java.lang.String)
	 */
	@Override
	public void createEnumerationType(final String typeCode) throws JaloDuplicateCodeException
	{
		final EnumerationManager enumManager = EnumerationManager.getInstance();
		enumManager.createDefaultEnumerationType(typeCode);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getCompany(java.lang.String)
	 */
	@Override
	public CompanyModel getCompany(final String uid)
	{
		final Company company = CatalogManager.getInstance().getCompanyByUID(uid);

		if (company == null)
		{
			throw new UnknownIdentifierException("Company with uid: " + uid + " doesn't exist.");
		}

		return getModelService().get(company);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#getAllCompanies()
	 */
	@Override
	public Collection<CompanyModel> getAllCompanies()
	{
		final Collection<Company> companies = getAllCompaniesHelper();
		final Collection<CompanyModel> companyModels = new ArrayList<CompanyModel>();
		getModelService().getAll(companies, companyModels);
		return companyModels;
	}

	/**
	 * Helper method that should be placed in CatalogManger. Returns collection of all Companies
	 * 
	 * @return Collection of all Companies
	 */
	private Collection<Company> getAllCompaniesHelper()
	{
		final de.hybris.platform.jalo.type.ComposedType companyType = TypeManager.getInstance().getComposedType(Company.class);
		return UserManager.getInstance().findUserGroups(companyType, null, null, null);
	}

	/**
	 * @see de.hybris.platform.webservices.WsUtilService#createCronJobBasedOnCurrent(CronJobModel, String)
	 */
	@Override
	public CronJobModel createCronJobBasedOnCurrent(final CronJobModel model, final String code)
	{
		CronJobModel result = null;
		final CronJob cronJobDelivered = getModelService().getSource(model);
		final Job job = cronJobDelivered.getJob();
		final int node = MasterTenant.getInstance().getClusterID();
		final Map params = new HashMap();

		params.put(CronJob.JOB, job);
		params.put(CronJob.CODE, code);
		params.put(CronJob.ACTIVE, Boolean.TRUE); // default
		params.put(CronJob.NODEID, Integer.valueOf(node));

		//to avoid creating the same code
		try
		{
			cronJobService.getCronJob(code);
		}
		catch (final UnknownIdentifierException e)
		{
			final ComposedType type = TypeManager.getInstance().getComposedType(cronJobDelivered.getClass().getSimpleName());
			try
			{
				final CronJob cronJob = (CronJob) type.newInstance(JaloSession.getCurrentSession().getSessionContext(), params);
				result = getModelService().get(cronJob);

			}
			catch (final JaloGenericCreationException e2)
			{
				throw new YWebservicesException(e.getMessage(), e2);
			}
			catch (final JaloAbstractTypeException e2)
			{
				throw new YWebservicesException(e.getMessage(), e2);
			}
		}

		return result;
	}

	@Override
	public Collection<KeywordModel> getAllKeywords(final CatalogVersionModel versionModel)
	{
		final CatalogVersion version = getModelService().getSource(versionModel);
		return getModelService().getAll(version.getAllKeywords(), new ArrayList());
	}

	@Override
	public KeywordModel getKeyword(final CatalogVersionModel versionModel, final LanguageModel language, final String keyword)
	{
		final CatalogVersion version = getModelService().getSource(versionModel);
		final Collection<Keyword> list = version.getKeywords(keyword);
		final Collection<KeywordModel> keylist = getModelService().getAll(list, new ArrayList());

		for (final KeywordModel keyModel : keylist)
		{
			if (language.equals(keyModel.getLanguage()))
			{
				return keyModel;
			}
		}
		return null;
	}

	@Override
	public KeywordModel getKeyword(final CatalogVersionModel versionModel, final String keyword)
	{
		final Language lang = JaloSession.getCurrentSession().getSessionContext().getLanguage();
		final LanguageModel language = getModelService().get(lang);

		return getKeyword(versionModel, language, keyword);
	}

	@Override
	public Set<String> getConfigurationForType(final int level, final Class<?> runtimeNodeType,
			final Map<String, List<String>> httpQueryParameters, final Set<String> allowedAttributesNames,
			final List<String> possibleConfigNodeNames)
	{
		if (possibleConfigNodeNames != null && !possibleConfigNodeNames.isEmpty())
		{
			/*
			 * Matching between runtime attribute names (property names) and configuration values require case-insensitive
			 * comparison. On the other hand, we need to return attribute names exactly as they are in
			 * allowedAttributesNames set. This map provides a way to do case-insensitive lookup (on map key) and holds
			 * case-sensitive attribute name (as map value).
			 */
			final Map<String, String> comparisonMap = new HashMap<String, String>();
			for (final String attribute : allowedAttributesNames)
			{
				comparisonMap.put(attribute.toLowerCase(Locale.getDefault()), attribute);
			}

			//1.) Try to process dynamic configuration.
			if (httpQueryParameters != null && !httpQueryParameters.isEmpty())
			{

				/*
				 * 1.1.) Prepare Map for case-insensitive comparison. This map holds a lower-case query parameter name as a
				 * map key, and a corresponding entry from httpQueryParameters as a value.
				 */
				final Map<String, Entry<String, List<String>>> queryParamComparisonMap = new HashMap<String, Entry<String, List<String>>>();
				for (final Entry<String, List<String>> parameter : httpQueryParameters.entrySet())
				{
					queryParamComparisonMap.put(parameter.getKey().toLowerCase(), parameter);
				}

				//1.2.) Try to match dynamic configuration to one of the names from possibleConfigurationNodeNames

				for (final String possibleConfigName : possibleConfigNodeNames)
				{
					final Set<String> dynamicSelectionResult = getRuntimeConfigurationForType(level, runtimeNodeType,
							queryParamComparisonMap, comparisonMap, possibleConfigName);
					if (dynamicSelectionResult != null)
					{
						return dynamicSelectionResult;
					}
				}
			}

			//2.) No runtime configuration found. Process static configuration.
			//2.1.) Try to get static configuration for current node
			//		final Set<String> staticAttributeSelection = getStaticConfigurationForType(level, comparisonMap, xmlNodeName);
			//		if (staticAttributeSelection != null)
			//		{
			//			return staticAttributeSelection;
			//		}
			//		else
			//		{
			//2.2.) Try to get static configuration for fallback nodes
			//if (possibleConfigurationNames != null && !possibleConfigurationNames.isEmpty())

			for (final String fallbackName : possibleConfigNodeNames)
			{
				final Set<String> fallbackSelectionResult = getStaticConfigurationForType(level, runtimeNodeType, comparisonMap,
						fallbackName);
				if (fallbackSelectionResult != null)
				{
					return fallbackSelectionResult;
				}
			}

		}
		//		}

		//if anything else fails, return null.
		return null;

	}

	/**
	 * Gets a static configuration for attributes of given node in resource representation.
	 * 
	 * @param xmlNodeName
	 *           name of the XML element as it appears in resource output XML data.
	 * @param recognizedAttributes
	 *           Map of attributes (properties) that are recognized for the current node by the graph library. The key in
	 *           this map is lowercase value of the attribute name to use in case-insensitive lookups. The value in this
	 *           map is the attribute name as it exists in graph library runtime (it is equal the java property name and
	 *           is case-sensitive).
	 * @return Set of attribute qualifiers that are permitted by static configuration for given node and level. Returns
	 *         null if no static configuration exists for the node at the given level. The returned Set, if any, contains
	 *         ONLY elements from recognizedAttributes.values().
	 */
	private Set<String> getStaticConfigurationForType(final int level, final Class<?> runtimeNodeType,
			final Map<String, String> recognizedAttributes, final String xmlNodeName)
	{
		final boolean isDetailRepresentation = (level == 0);
		final String configKeySuffix = isDetailRepresentation ? "detail" : "reference";
		final String configKey = ("ws." + xmlNodeName + "." + configKeySuffix).toLowerCase();
		final String configValue = Config.getParameter(configKey);
		if (configValue != null && !configValue.isEmpty())
		{
			final String[] attributeQualifierTokens = configValue.split("[,]");
			if (attributeQualifierTokens != null && attributeQualifierTokens.length > 0)
			{
				final Set<String> result = new HashSet<String>();

				for (final String qualifierToken : attributeQualifierTokens)
				{
					if (qualifierToken != null && !qualifierToken.isEmpty())
					{
						final String qualifierTrimmed = qualifierToken.trim();
						final String lowerQualifier = qualifierTrimmed.toLowerCase(Locale.getDefault());
						if (recognizedAttributes.containsKey(lowerQualifier))
						{
							result.add(recognizedAttributes.get(lowerQualifier));
						}
						else
						{
							log.error("Attribute Selector API: Statically configured attribute: \"" + qualifierTrimmed
									+ "\" is not recognized for node: \"" + runtimeNodeType.getName()
									+ "\" that was matched using qualifier: \"" + xmlNodeName + "\" at level: " + level);

						}
					}
				}
				if (!result.isEmpty())
				{
					return result;
				}
			}
		}
		return null;
	}


	/**
	 * Gets configuration from query parameters for the node that is currently processed.
	 * 
	 * @param level
	 *           node level. It is the same as distance of graph processing.
	 * @param xmlNodeName
	 *           name of the currently processed node.
	 * @param recognizedAttributes
	 *           Map of attributes (properties) that are recognized for the current node by the graph library. The key in
	 *           this map is lowercase value of the attribute name to use in case-insensitive lookups. The value in this
	 *           map is the attribute name as it exists in graph library runtime (it is equal the java property name and
	 *           is case-sensitive).
	 * @return Set of attribute qualifiers that are permitted by runtime configuration for given node and level. Returns
	 *         null if no runtime configuration exists for the node at the given level. The returned Set, if any,
	 *         contains ONLY elements from recognizedAttributes.values().
	 */
	private Set<String> getRuntimeConfigurationForType(final int level, final Class<?> runtimeNodeType,
			final Map<String, Entry<String, List<String>>> queryParamComparisonMap, final Map<String, String> recognizedAttributes,
			final String xmlNodeName)
	{

		if (queryParamComparisonMap != null && !queryParamComparisonMap.isEmpty())
		{
			/**
			 * A query parameter that configures current node can have four different forms:
			 * <ul>
			 * <li>1.) xmlNodeName_attributes_level - This is explicit configuration for a node/level combination</li>
			 * <li>2.) xmlNodeName_attributes_detail - This is "convenience" form for xmlNodeName_attributes_0</li>
			 * <li>3.) xmlNodeName_attributes_reference - This is "convenience" form for a whole set of:
			 * xmlNodeName_attributes_X, where X > 0</li>
			 * <li>4.) xmlNodeName_attributes - This is "convenient" shortcut for a whole set of: xmlNodeName_attributes_X,
			 * where X >= 0</li>
			 * </ul>
			 */

			final String attributeNamePrefix = (xmlNodeName + "_attributes").toLowerCase();

			List<String> queryParameterValues = null;

			if (queryParamComparisonMap.containsKey(attributeNamePrefix + "_" + level))
			{
				queryParameterValues = queryParamComparisonMap.get(attributeNamePrefix + "_" + level).getValue();
			}
			else
			{
				//No explicit match. Try to match shortest (most general) form first
				if (queryParamComparisonMap.containsKey(attributeNamePrefix))
				{
					queryParameterValues = queryParamComparisonMap.get(attributeNamePrefix).getValue();
				}
				else
				{
					//Try to match convenience forms. Warning! These forms are NOT mutual exclusive! (It is valid to specify both in one query)
					if (queryParamComparisonMap.containsKey(attributeNamePrefix + "_" + "detail"))
					{
						if (level == 0)
						{
							queryParameterValues = queryParamComparisonMap.get(attributeNamePrefix + "_" + "detail").getValue();
						}
					}

					if (queryParameterValues == null && queryParamComparisonMap.containsKey(attributeNamePrefix + "_" + "reference"))
					{
						if (level > 0)
						{
							queryParameterValues = queryParamComparisonMap.get(attributeNamePrefix + "_" + "reference").getValue();
						}
					}
				}
			}

			//At this point queryParameterValues contains values of query parameters that match current node/level. Or is null/empty that means we have NO configuration for current node/level.

			if (queryParameterValues != null && !queryParameterValues.isEmpty())
			{
				final Set<String> result = new HashSet<String>();

				//Iterate over query parameter values (each query parameter can have multiple values!)
				for (final String attributeTokens : queryParameterValues)
				{
					//Tokenize query parameter value using comma. Resulting strings are attribute names that user wanted to be displayed.
					final String[] attributes = attributeTokens.split(",");
					if (attributes != null && attributes.length > 0)
					{
						//Iterate over found attribute names. Check if they are allowed.
						for (final String attributeQualifier : attributes)
						{
							final String qualifierTrimmed = attributeQualifier.trim();
							final String lowerQualifier = qualifierTrimmed.toLowerCase(Locale.getDefault());

							if (recognizedAttributes.containsKey(lowerQualifier))
							{
								result.add(recognizedAttributes.get(lowerQualifier));
							}
							else
							{
								log.error("Attribute Selector API: Dynamically configured attribute: \"" + qualifierTrimmed
										+ "\" is not recognized for node: \"" + runtimeNodeType.getName()
										+ "\" that was matched using qualifier: \"" + xmlNodeName + "\" at level: " + level);
							}
						}
					}
				}
				if (!result.isEmpty())
				{
					return result;
				}
				else
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean isMemberOf(final String userID, final String userGroupID, final boolean includingSupergroups)
	{
		final UserManager userManager = UserManager.getInstance();
		final User user = userManager.getUserByLogin(userID);
		UserGroup userGroup = null;
		try
		{
			userGroup = userManager.getUserGroupByGroupID(userGroupID);
		}
		catch (final JaloItemNotFoundException e)
		{
			//the group is not found - is not a member
			return false;
		}

		return user.isMemberOf(userGroup, includingSupergroups);
	}
}
