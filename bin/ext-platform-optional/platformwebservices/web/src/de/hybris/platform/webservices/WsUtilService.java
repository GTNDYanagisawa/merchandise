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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.catalog.model.KeywordModel;
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
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.jalo.type.JaloDuplicateCodeException;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This interface temporarily inholds service method signatures, that was needed in restjersey and was not yet defined
 * in expected platform service classes.
 * 
 * @since 4.0
 * @spring.bean wsUtilService
 */
public interface WsUtilService
{
	/**
	 * @param pk
	 *           pk of the address to obtain
	 * @return AddressModel
	 */
	AddressModel getAddress(final Long pk);

	/**
	 * Gets all addresses that belong to users
	 * 
	 * @return all addresses
	 */
	List<AddressModel> getAllAddresses();

	/**
	 * @param countryCode
	 *           ISO code of a country for which we're retrieving regions
	 * @return all regions
	 */
	Set<RegionModel> getAllRegions(final String countryCode);

	Collection<RegionModel> getAllRegions();

	/**
	 * Returns collection of all UnitModel objects
	 * 
	 * @return collection of UnitModel objects
	 */
	Collection<UnitModel> getAllUnits();

	/**
	 * gets back the region upon the iso code
	 * 
	 * @param isocode
	 *           code of the region
	 * @return found region
	 */
	RegionModel getRegion(String isocode);

	/**
	 * gets back the region upon the iso code
	 * 
	 * @param isocode
	 *           code of the region
	 * @param countryCode
	 *           code of the region's country
	 * @return found region
	 */
	RegionModel getRegion(String countryCode, String isocode);

	/**
	 * Set request user to jalosession.
	 * 
	 * @param userID
	 *           authenticated user or anonymous user
	 */
	void setRequestUserIntoJaloSession(String userID);

	/**
	 * Set appropriate catalog versions to the jalosession for current authenticated user.
	 */
	void setCatalogVersionsIntoJaloSession();

	/**
	 * Gets all carts of system.
	 * 
	 * @return all carts
	 */
	Collection<CartModel> getAllCarts();

	/**
	 * Gets all discounts of system.
	 * 
	 * @return all discounts
	 */
	Collection<DiscountModel> getAllDiscounts();

	/**
	 * gets back the discount upon the code
	 * 
	 * @param code
	 *           code value of DiscountModel
	 * @return found discount
	 */
	DiscountModel getDiscount(String code);

	/**
	 * Gets all customers of system.
	 * 
	 * @return all customers
	 */
	Collection<CustomerModel> getAllCustomers();

	/**
	 * Returns single CustomerModel object based on CostomerModel login attribute
	 * 
	 * @param login
	 *           login value of CustomerModel
	 * @return CustomerModel object
	 */
	CustomerModel getCustomer(String login);

	/**
	 * Returns collection of all UserGroupModel objects
	 * 
	 * @return collection of UserGroupModel objects
	 */
	Collection<UserGroupModel> getAllUserGroups();

	/**
	 * Returns single DeliveryModeModel object based on DeliveryModeModel code attribute
	 * 
	 * @param code
	 *           code value of DeliveryModeModel
	 * @return DeliveryModeModel object
	 */
	DeliveryModeModel getDeliveryModeByCode(final String code);

	/**
	 * Returns collection of DeliveryModeModel objects based on DeliveryModeModel code attribute
	 * 
	 * @param code
	 *           code value of DeliveryModeModel
	 * @return collection of DeliveryModeModel objects
	 */
	Collection<DeliveryModeModel> getDeliveryModesByCode(final String code);

	/**
	 * Returns single PaymentModeModel object based on PaymentModeModel code attribute
	 * 
	 * @param code
	 *           code value of PaymentModeModel
	 * @return PaymentModeModel object
	 */
	PaymentModeModel getPaymentModeByCode(final String code);

	/**
	 * Returns collection of PaymentModeModel objects based on PaymentModeModel code attribute
	 * 
	 * @param code
	 *           code value of PaymentModeModel
	 * @return collection of PaymentModeModel objects
	 */
	Collection<PaymentModeModel> getPaymentModesByCode(final String code);

	/**
	 * Gets back all enumeration types
	 * 
	 * @return collection of EnumerationMetaTypeModel
	 */
	Collection<EnumerationMetaTypeModel> getAllEnumerationTypes();

	/**
	 * Create enumeration value of given enumeration type
	 * 
	 * @param typeCode
	 *           enumeration type code
	 * @param valueCode
	 *           enumeration value
	 * @param name
	 *           name of the enumeration value
	 * @return created enumeration value model
	 */
	EnumerationValueModel createEnumerationValue(final String typeCode, final String valueCode, final String name);

	/**
	 * Create enumeration value of given enumeration type
	 * 
	 * @param typeCode
	 *           enumerationType type code
	 * @throws JaloDuplicateCodeException
	 */
	void createEnumerationType(final String typeCode) throws JaloDuplicateCodeException;

	/**
	 * Returns single CompanyModel object based on CompanyModel uid attribute
	 * 
	 * @param uid
	 *           uid value of CustomerModel
	 * @return CompanyModel object
	 */
	CompanyModel getCompany(String uid);

	/**
	 * Returns collection of all CompanyModel objects
	 * 
	 * @return collection of CompanyModel objects
	 */
	Collection<CompanyModel> getAllCompanies();

	/**
	 * Create a cronjob based on delivered cronjob.
	 * 
	 * @param model
	 *           current cronjob model
	 * @param code
	 *           of the new cronjob
	 * @return new CronJobModel based on delivered cronjob
	 */
	CronJobModel createCronJobBasedOnCurrent(CronJobModel model, final String code);


	/**
	 * Returns collection of all Keywords for a specific catalog version
	 * 
	 * @param versionModel
	 *           catalogVersionModel for which we wish to get all Keywords
	 * @return Collection of Keywords
	 */
	Collection<KeywordModel> getAllKeywords(final CatalogVersionModel versionModel);

	/**
	 * Returns a specified KeywordModel object based on its catalogVersion and value of the keyword
	 * 
	 * @param versionModel
	 *           catalog version of the Keyword
	 * @param keyword
	 *           value of the keyword
	 * @return KeywordModel object
	 */
	KeywordModel getKeyword(final CatalogVersionModel versionModel, final String keyword);

	/**
	 * Returns a specified KeywordModel object based on its catalogVersion, Language and value of the keyword
	 * 
	 * @param versionModel
	 *           catalog version of the Keyword
	 * @param keyword
	 *           value of the keyword
	 * @param language
	 *           language in which the keyword is specified
	 * @return KeywordModel object
	 */
	KeywordModel getKeyword(final CatalogVersionModel versionModel, final LanguageModel language, final String keyword);

	/**
	 * Returns a NodeFactory for specified dtoClass
	 * 
	 * @param dtoClass
	 *           the dto class
	 * @return the NodeFactory or null
	 */
	NodeFactory findNodeFactory(Class dtoClass);

	/**
	 * Gets a "attribute selector API" configuration for attributes of currently processed graph node. Node means a DTO
	 * instance. The implementation tries to match entries from dynamic or static configuration to the possible node
	 * names that are passed in possibleConfigurationNodeNames.
	 * 
	 * @param possibleConfigNodeNames
	 *           Ordered list of names to look for in configuration. Used to find configuration entries that are valid
	 *           for currently processed node. More than one configuration entry can be valid for a node. Generally this
	 *           list should contain node type names and name of the property that refers to the node. The reasons for
	 *           this are explained below:
	 *           <ul>
	 *           <li>1.) Consider type hierarchy. A resource can be of type (the DTO type is important here) that is a
	 *           subtype of some other type. Configuration may be specified for either of these types, or for both, or
	 *           for any. To handle this scenario possibleConfigurationNodeNames must contain currently processed DTO's
	 *           type name, and the names of all it's supertypes. The first match "wins", so if the list is sorted so
	 *           that the most specific type is first, and the least specific type is last, we get automatically
	 *           "best match" strategy.</li>
	 *           <li>2.) Consider complex property of a resource. If a resource has a complex property (a property that
	 *           evaluates to DTO), then the property name is the natural way for users to configure attributes for the
	 *           resource (DTO) returned by this property. So the property name is a candidate for configuration entry
	 *           matching. If the property name is placed first on this list, it takes precedence over DTO type name
	 *           matching.</li>
	 *           </ul>
	 * @param level
	 *           Level of graph processing at which the node appears. The same node may has different configurations on
	 *           different levels. Level == 0 is for the nodes that represents root element in the output XML. This means
	 *           that for root resources every element of collection has level == 1 (root element is the collection
	 *           wrapper)
	 * @param runtimeNodeType
	 *           actual runtime type of the currently processed node.
	 * @param httpQueryParameters
	 *           query parameters as returned by
	 *           de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphContext
	 *           .getUriInfo().getQueryParameters(). These are used to provide dynamic configuration entries.
	 * @param allowedAttributesNames
	 *           set of attribute names that are detected by the graph library for current node. The set of attribute
	 *           names returned by this method, if any, is a subset of allowedAttributes.
	 * @return Set of attribute qualifiers that configurations permits for the given node at given level. The returned
	 *         set is a subset of allowedAttributes. Returns null if no configuration for the node was found.
	 */
	Set<String> getConfigurationForType(final int level, final Class<?> runtimeNodeType,
			final Map<String, List<String>> httpQueryParameters, final Set<String> allowedAttributesNames,
			final List<String> possibleConfigNodeNames);

	/**
	 * Checks whether the principal is member of the given group or any of its subgroups.
	 * 
	 * @param userID
	 *           the user ID.
	 * 
	 * @param includingSupergroups
	 *           if <code> true </code> indirect membership through group-in-group is evaluated, otherwise only direct
	 *           membership
	 * @param userGroupID
	 *           the group to check membership for
	 * 
	 * @return true if this principal is member of the given group or any of its subgroups.
	 */
	boolean isMemberOf(final String userID, final String userGroupID, boolean includingSupergroups);
}
