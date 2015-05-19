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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.bootstrap.codegenerator.platformwebservices.WebservicesConfig;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.converter.ModelConverter;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.BadRequestException;
import de.hybris.platform.webservices.ServiceLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;


/**
 * This class delivers two main functions (that refer to the context aware resources):
 * <p>
 * 1) creating URL's for specified model instance
 * <p>
 * 2) retrieving specified model from information contained in the URL
 * 
 */
public class YContextResourceResolver
{
	public final static String QUERY_PARAMETERS_PREFIX = Config.getString("webservices.queryparameters.prefix", "um");
	public final static String QUERY_PARAMETERS_PAIRSEPARATOR = Config.getString("webservices.queryparameters.pairseparator", ";");
	public final static String QUERY_PARAMETERS_KEY_SEPARATOR = Config
			.getString("webservices.queryparameters.key.separator", "__");
	public final static String QUERY_PARAMETERS_VALUE_SEPARATOR = Config.getString("webservices.queryparameters.value.separator",
			",");
	public final static int TO_TYPE_OR_VALUE = 0;
	public final static int FROM_TYPE_OR_VALUE = 1;

	private final ServiceLocator serviceLocator;
	private String baseUrl;

	public YContextResourceResolver(final ServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}

	public ServiceLocator getServiceLocator()
	{
		return serviceLocator;
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public void setBaseUrl(final String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	/**
	 * Method creates appropriate URL for the given model. Some models include unique attributes(which are also models)
	 * and this information about them will be contained within URL. The URL may consist of nested URL path segments and
	 * query parameter. The unique attribute of the give model which will have the largest number of nested unique
	 * attributes will be placed in URL. The rest of them will be in query parameter.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * 1) given model - CarModel(unique primitive attribute: c1)
	 * 2) its three non-primitive unique attributes:
	 * 	2a) ManufacturerModel(m1)
	 * 	2b) UserModel(u1)
	 * 	2c) ProductModel(p1) and its unique:
	 * 		3) CatalogVersionModel(Online) and its unique:
	 * 			4) CatalogModel(hwcatalog)
	 * </pre>
	 * <p>
	 * Result url:
	 * 
	 * <pre>
	 * http://{host_port_webroot}/catalogs/hwcatalog/catalogversions/Online/products/p1/cars/c1?um__car;manufacturer__car;user=c1;m1,c1:u1
	 * </pre>
	 * 
	 * 
	 * @param modelValue
	 *           model Value
	 * @param propValue
	 *           value of the non-primitive unique attribute of the modelValue
	 * 
	 * @return the URL for given model
	 */
	public String buildUri(final Object modelValue, final Object propValue)
	{
		String url = "";
		final List<List<YUniqueMember>> allUniqueMemberModels = new ArrayList<List<YUniqueMember>>();

		List<YUniqueMember> urlList = null;
		List<YUniqueMember> queryParametersList = null;

		final YUniqueMember rootUniqueMember = new YUniqueMember((ItemModel) modelValue, null, null, false, propValue);

		prepareUniqueStructure(allUniqueMemberModels, rootUniqueMember, false);
		if (!allUniqueMemberModels.isEmpty())
		{
			final int listSize = allUniqueMemberModels.size();
			//last level, first element
			final YUniqueMember farthestLeafUniqueMember = allUniqueMemberModels.get(listSize - 1).iterator().next();

			urlList = storePathForUrl(farthestLeafUniqueMember, rootUniqueMember);
			queryParametersList = storeQueryParametersForUrl(allUniqueMemberModels, urlList);
		}
		url = createUri(urlList, queryParametersList, modelValue, propValue);

		return url;
	}

	/**
	 * Method obtains existing(saved) model from empty(not saved) model which is bulit from information that are pull out
	 * from URL path(uniqueMember attribute) and query parameter.
	 * 
	 * @param modelToSet
	 *           empty(not saved) model(template)
	 * @param composedType
	 *           the code of the composedType of the modelToSet
	 * @param uriInfo
	 *           uri information and by the way the special query parameter with information about unique members of
	 *           modelToSet.
	 * @param uniqueMember
	 *           the unique member(of modelToSet) which is passed from resource to resource(via URL path)
	 * 
	 * @return existing(saved) model
	 */
	public ItemModel readResourceInternal(final ItemModel modelToSet, final String composedType, final UriInfo uriInfo,
			final ItemModel uniqueMember) throws BadRequestException
	{
		final List<List<YUniqueMember>> structureWithUniqueMembers = new ArrayList<List<YUniqueMember>>();

		final YUniqueMember rootUniqueMember = new YUniqueMember(serviceLocator.getTypeService().getComposedType(composedType),
				null, null, true, null);
		prepareUniqueStructure(structureWithUniqueMembers, rootUniqueMember, true);

		if (structureWithUniqueMembers.isEmpty())
		{
			return serviceLocator.getFlexibleSearchService().getModelByExample(modelToSet);
		}
		//also throw a BadRequestException if queryParameter is invalid(e.g. key or value doesn't exist)
		final Map<String, String> uniqueMembersFromQueryParameters = obtainAllUniqueMembersFromQueryParamaters(uriInfo);
		//throw a BadRequestException if queryParameter key or value is invalid
		verifyUniqueMembersFromQueryParameter(uriInfo, uniqueMembersFromQueryParameters, structureWithUniqueMembers, modelToSet);

		return setAllProvidedUniqueModelsToModel(modelToSet, uniqueMembersFromQueryParameters, uniqueMember);
	}

	/**
	 * Method returns unique primitive attribute name of the given composedTypeModel.
	 * 
	 * @param composedTypeModel
	 *           the given model
	 * 
	 * @return unique primitive attribute name of the given composedTypeModel
	 */
	public String getUniquePropertyName(final ComposedTypeModel composedTypeModel)
	{
		final TypeService typeService = serviceLocator.getTypeService();
		final Set<String> uMembersString = typeService.getUniqueAttributes(composedTypeModel.getCode());
		final Set<String> resultSet = new TreeSet<String>();
		for (final String string : uMembersString)
		{
			if (typeService.getAttributeDescriptor(composedTypeModel, string).getAttributeType() instanceof AtomicTypeModel)
			{
				final AtomicTypeModel atomicTypeModel = (AtomicTypeModel) typeService.getAttributeDescriptor(composedTypeModel,
						string).getAttributeType();
				if (atomicTypeModel.getJavaClass().equals(String.class))
				{
					resultSet.add(string);
				}
			}
		}
		final String uniqueIdPrepared = (resultSet.isEmpty()) ? "pk" : resultSet.iterator().next();

		return uniqueIdPrepared;
	}

	private void prepareUniqueStructure(final List<List<YUniqueMember>> structureWithUniqueMembers, final YUniqueMember parent,
			final boolean isMetatype)
	{
		final ComposedTypeModel composedTypeModel = (parent.isMetatype()) ? (ComposedTypeModel) parent.getModel() : serviceLocator
				.getTypeService().getComposedType(parent.getModel().getClass());

		final Set<String> uMembersString = serviceLocator.getTypeService().getUniqueAttributes(composedTypeModel.getCode());
		final List<YUniqueMember> oneLevelUniqueMemberModels = new ArrayList<YUniqueMember>();
		for (final String string : uMembersString)
		{
			final AttributeDescriptorModel attrDescModel = serviceLocator.getTypeService().getAttributeDescriptor(composedTypeModel,
					string);
			if (attrDescModel.getUnique().booleanValue()
					&& attrDescModel.getAttributeType().getClass().equals(ComposedTypeModel.class))
			{
				if (isMetatype)
				{
					final ComposedTypeModel composedModel = (ComposedTypeModel) attrDescModel.getAttributeType();
					final YUniqueMember uniqueMember = new YUniqueMember(composedModel, parent, attrDescModel.getQualifier(), true,
							null);
					oneLevelUniqueMemberModels.add(uniqueMember);
				}
				else
				{
					final Object uniqueModel = serviceLocator.getModelService().getAttributeValue(parent.getModel(),
							attrDescModel.getQualifier());
					if (uniqueModel instanceof ItemModel)
					{
						final YUniqueMember uniqueMember = new YUniqueMember((ItemModel) uniqueModel, parent, attrDescModel
								.getAttributeType().getCode(), false, null);
						oneLevelUniqueMemberModels.add(uniqueMember);
					}
				}
			}
		}

		if (!oneLevelUniqueMemberModels.isEmpty())
		{
			structureWithUniqueMembers.add(oneLevelUniqueMemberModels);
			for (final YUniqueMember uniqueMember : oneLevelUniqueMemberModels)
			{
				prepareUniqueStructure(structureWithUniqueMembers, uniqueMember, isMetatype);
			}
		}
	}

	//----------------------------------------------------
	//- private methods that are used by public buildUri -
	//----------------------------------------------------

	/**
	 * A leaf node on a working path that is farthest from the root node.
	 * 
	 * @return the list with elements(well-ordered, from url root path segment(leaf) to more specific path segments) for
	 *         URL path
	 */
	private List<YUniqueMember> storePathForUrl(final YUniqueMember farthestLeafUniqueMember, final YUniqueMember rootUniqueMember)
	{
		YUniqueMember tempUniqueMember = farthestLeafUniqueMember;
		final List<YUniqueMember> urlList = new ArrayList<YUniqueMember>();
		while (!rootUniqueMember.getModel().equals(tempUniqueMember.getModel()))
		{
			urlList.add(tempUniqueMember);
			tempUniqueMember = tempUniqueMember.getParent();
		}

		return urlList;
	}

	/**
	 * @return the list with all unique members that are query parameters for URL. (ordered from parent level to leaf
	 *         level, within each level not ordered)
	 */
	private List<YUniqueMember> storeQueryParametersForUrl(final List<List<YUniqueMember>> allUniqueMemberModels,
			final List<YUniqueMember> urlReservedNodesList)
	{
		final List<YUniqueMember> queryParametersList = new ArrayList<YUniqueMember>();
		for (final List<YUniqueMember> levelUniqueMember : allUniqueMemberModels)
		{
			for (final YUniqueMember uniqueMember : levelUniqueMember)
			{
				if (!urlReservedNodesList.contains(uniqueMember))
				{
					queryParametersList.add(uniqueMember);
				}
			}
		}
		return queryParametersList;
	}

	private String createUri(final List<YUniqueMember> urlList, final List<YUniqueMember> queryParametersList,
			final Object modelValue, final Object uniqueValue)
	{
		String url = "";
		String queryParameters = "";

		//Url part
		if (urlList != null)
		{
			for (final YUniqueMember uniqueMember : urlList)
			{
				url += createUrlPathSegment(uniqueMember) + "/";
			}
		}

		//QueryParameter part
		if (queryParametersList != null && !queryParametersList.isEmpty())
		{
			String key = "";
			String value = "";

			int counter = 1;
			for (final YUniqueMember uniqueMember : queryParametersList)
			{
				final List<String> queryParamList = createQueryParameter(uniqueMember);
				key += queryParamList.get(0) + (counter == queryParametersList.size() ? "" : QUERY_PARAMETERS_KEY_SEPARATOR);
				value += queryParamList.get(1) + (counter == queryParametersList.size() ? "" : QUERY_PARAMETERS_VALUE_SEPARATOR);
				counter++;
			}

			queryParameters = "?" + QUERY_PARAMETERS_PREFIX + QUERY_PARAMETERS_KEY_SEPARATOR + key + "=" + value;
		}

		//essential url creation
		final String code = serviceLocator.getTypeService().getComposedType(modelValue.getClass()).getCode();
		final String pluralSimpleName = WebservicesConfig.getPluralNoun(code).toLowerCase();
		url += pluralSimpleName.toLowerCase() + "/" + uniqueValue;

		return baseUrl + url + queryParameters;
	}

	/**
	 * @return catalogs/hwcatalog, catalogversions/Online
	 */
	private String createUrlPathSegment(final YUniqueMember uniqueMember)
	{
		final String code = serviceLocator.getTypeService().getComposedType(uniqueMember.getModel().getClass()).getCode();
		final String pluralName = WebservicesConfig.getPluralNoun(code).toLowerCase();

		final String urlPathSegment = pluralName + "/" + uniqueMember.getUniquePropertyValue();

		return urlPathSegment;
	}

	/**
	 * @return list with key and value, e.g. 0) car;manufacturer 1) c1;m1
	 */
	private List<String> createQueryParameter(final YUniqueMember uniqueMember)
	{
		final String tempQueryParameterKey = serviceLocator.getTypeService().getComposedType(
				uniqueMember.getParent().getModel().getClass()).getCode().toLowerCase()
				+ QUERY_PARAMETERS_PAIRSEPARATOR + uniqueMember.getAttributeTypeCode().toLowerCase();
		final String tempQueryParameterValue = uniqueMember.getParent().getUniquePropertyValue() + QUERY_PARAMETERS_PAIRSEPARATOR
				+ uniqueMember.getUniquePropertyValue().toString();

		final List<String> queryParamList = new ArrayList<String>();
		queryParamList.add(tempQueryParameterKey);
		queryParamList.add(tempQueryParameterValue);

		return queryParamList;
	}

	//----------------------------------------------------------------
	//- private methods that are used by public readResourceInternal -
	//----------------------------------------------------------------


	/**
	 * Also preverification of query parameter construction.
	 * 
	 * @return map: e.g. key = car;manufacturer, value = c2;m1
	 */
	private Map<String, String> obtainAllUniqueMembersFromQueryParamaters(final UriInfo uriInfo)
	{
		final Map<String, String> result = new HashMap<String, String>();

		final MultivaluedMap<String, String> valueMap = uriInfo.getQueryParameters();

		for (final Entry<String, List<String>> entry : valueMap.entrySet())
		{
			String key = entry.getKey();

			if (key.startsWith(QUERY_PARAMETERS_PREFIX + QUERY_PARAMETERS_KEY_SEPARATOR))
			{
				key = key.substring(QUERY_PARAMETERS_PREFIX.length() + QUERY_PARAMETERS_KEY_SEPARATOR.length());
				if (key.isEmpty())
				{
					throw new BadRequestException("Provide key for query parameter with: " + QUERY_PARAMETERS_PREFIX
							+ QUERY_PARAMETERS_KEY_SEPARATOR + " prefix.");
				}

				final List<String> typePairs = Arrays.asList(key.split(QUERY_PARAMETERS_KEY_SEPARATOR));
				List<String> valuesList = entry.getValue();
				valuesList = Arrays.asList(valuesList.get(0).split(QUERY_PARAMETERS_VALUE_SEPARATOR));
				if (valuesList.get(0).isEmpty())
				{
					throw new BadRequestException("Provide value for query parameter with: " + QUERY_PARAMETERS_PREFIX
							+ QUERY_PARAMETERS_KEY_SEPARATOR + " prefix.");
				}

				for (int i = 0; i < typePairs.size(); i++)
				{
					result.put(typePairs.get(i), valuesList.get(i).trim());
				}
				//only one query parameter is intended for unique members
				break;
			}
		}

		return result;
	}

	private void verifyUniqueMembersFromQueryParameter(final UriInfo uriInfo,
			final Map<String, String> uniqueMembersFromQueryParameter, final List<List<YUniqueMember>> structureWithUniqueMembers,
			final ItemModel modelToSet) throws BadRequestException
	{
		String exceptionMessage = null;
		for (final Entry<String, String> entry : uniqueMembersFromQueryParameter.entrySet())
		{
			// part1: separator check
			final String keyTypes = entry.getKey();
			final String valueInstances = entry.getValue();
			if (!verifySeparator(keyTypes))
			{
				exceptionMessage = "Only one pair separator: '" + QUERY_PARAMETERS_PAIRSEPARATOR
						+ "' is allowed within query parameter key which represents types.\nWithin query parameter from URL:\n "
						+ uriInfo.getRequestUri() + "\nthe " + keyTypes + " element is invalid!";
			}
			else if (!verifySeparator(valueInstances))
			{
				exceptionMessage = "Only one pair separator: '"
						+ QUERY_PARAMETERS_PAIRSEPARATOR
						+ "' is allowed within query parameter value which represents unique value of the specified type.\nWithin query parameter from URL:\n "
						+ uriInfo.getRequestUri() + "\nthe " + valueInstances + " element is invalid!";
			}
			if (exceptionMessage != null)
			{
				throw new BadRequestException(exceptionMessage);
			}

			// part2: types check
			final String[] entries = keyTypes.split(QUERY_PARAMETERS_PAIRSEPARATOR);
			final String toType = entries[TO_TYPE_OR_VALUE];
			final String fromType = entries[FROM_TYPE_OR_VALUE];

			// verify query parameter only if toType == modelToSet code 
			if (toType.equalsIgnoreCase(serviceLocator.getTypeService().getComposedType(modelToSet.getClass()).getCode()))
			{
				boolean found = false;
				for (final List<YUniqueMember> listOfUniqueMembers : structureWithUniqueMembers)
				{
					for (final YUniqueMember uniqueMemberElement : listOfUniqueMembers)
					{
						final String codeUniqueMember = ((ComposedTypeModel) uniqueMemberElement.getModel()).getCode();
						if (fromType.equalsIgnoreCase(codeUniqueMember))
						{
							found = true;
						}
					}
				}
				if (!found)
				{
					exceptionMessage = "Within query parameter from URL:\n " + uriInfo.getRequestUri() + "\nthe " + keyTypes
							+ " element is invalid because the resource with type: " + fromType
							+ " is not a unique member of the resource with type: " + toType;
					throw new BadRequestException(exceptionMessage);
				}
			}
		}
	}

	/**
	 * @return true only if the separator is used once
	 */
	private boolean verifySeparator(final String entry)
	{
		final int TWO_ELEMENTS = 2;
		final int size = Arrays.asList(entry.split(QUERY_PARAMETERS_PAIRSEPARATOR)).size();

		return size == TWO_ELEMENTS;
	}

	private ItemModel setAllProvidedUniqueModelsToModel(final ItemModel modelToSet,
			final Map<String, String> uniqueMembersFromQueryParameters, final ItemModel uniqueMember)
	{
		final ComposedTypeModel modelToSetComposedType = serviceLocator.getTypeService().getComposedType(modelToSet.getClass());
		final Set<String> uMembersString = serviceLocator.getTypeService().getUniqueAttributes(modelToSetComposedType.getCode());
		for (final String string : uMembersString)
		{
			final AttributeDescriptorModel attrDescModel = serviceLocator.getTypeService().getAttributeDescriptor(
					modelToSetComposedType, string);
			if (attrDescModel.getUnique().booleanValue()
					&& attrDescModel.getAttributeType().getClass().equals(ComposedTypeModel.class))
			{
				//unique member from Resource
				if (uniqueMember != null
						&& isTypeOrSubtype((ComposedTypeModel) attrDescModel.getAttributeType(), serviceLocator.getTypeService()
								.getComposedType(uniqueMember.getClass())))
				{
					serviceLocator.getModelService().setAttributeValue(modelToSet, attrDescModel.getQualifier(), uniqueMember);
				}
				//unique members from query parameter
				else
				{
					//e.g. entry: key = car;manufacturer, value = c2;m1
					for (final Entry entry : uniqueMembersFromQueryParameters.entrySet())
					{
						final List<String> keyList = Arrays.asList(((String) entry.getKey()).split(QUERY_PARAMETERS_PAIRSEPARATOR));
						final List<String> valueList = Arrays.asList(((String) entry.getValue()).split(QUERY_PARAMETERS_PAIRSEPARATOR));

						final String keyFrom = keyList.get(TO_TYPE_OR_VALUE); //car
						final String keyTo = keyList.get(FROM_TYPE_OR_VALUE); //manufacturer
						final String valueFrom = valueList.get(TO_TYPE_OR_VALUE); //c2
						final String valueTo = valueList.get(FROM_TYPE_OR_VALUE); //m1

						//the uniqueId of the requested Model
						String uniqueID = getUniquePropertyName(modelToSetComposedType);
						final String valueOfUniqueAttribute = serviceLocator.getModelService().getAttributeValue(modelToSet, uniqueID);

						ComposedTypeModel keyFromComposedTypeModel;
						try
						{
							keyFromComposedTypeModel = serviceLocator.getTypeService().getComposedType(keyFrom);
						}
						catch (final UnknownIdentifierException uie)
						{
							//means that this unique member is incorrect and won't be taken into account
							break;
						}
						if (keyFromComposedTypeModel.equals(modelToSetComposedType) && valueOfUniqueAttribute.equals(valueFrom))
						{
							if (isTypeOrSubtype((ComposedTypeModel) attrDescModel.getAttributeType(), serviceLocator.getTypeService()
									.getComposedType(keyTo)))
							{
								// we don't want any default initialization of model, because it return not correct result in this usage of getModelByExample
								// get pure unsaved model from typeCode
								final DefaultModelService defaultModelService = ((DefaultModelService) serviceLocator.getModelService());
								final ModelConverter conv = defaultModelService.getConverterRegistry().getModelConverterBySourceType(
										keyTo);
								final ItemModel keyToModel = (ItemModel) conv.create(keyTo);

								final ComposedTypeModel keyToComposedTypeModel = serviceLocator.getTypeService().getComposedType(
										keyToModel.getClass());
								//the unique id of the keyTo Model
								uniqueID = getUniquePropertyName(keyToComposedTypeModel);

								final ItemModel savedKeyToModel;
								//if pk, then getByPk
								if ("pk".equals(uniqueID))
								{
									savedKeyToModel = serviceLocator.getModelService().get(PK.parse(valueTo));
								}
								else
								{
									// set a unique value to keyTo model
									serviceLocator.getModelService().setAttributeValue(keyToModel, uniqueID, valueTo);
									//go deeper and fulfill this not saved model with its unique models and return saved one
									savedKeyToModel = setAllProvidedUniqueModelsToModel(keyToModel, uniqueMembersFromQueryParameters,
											uniqueMember);
								}
								//set saved keyTo model to the parent model
								serviceLocator.getModelService().setAttributeValue(modelToSet, attrDescModel.getQualifier(),
										savedKeyToModel);
							}
						}
					}
				}
			}
		}
		//return saved model
		return serviceLocator.getFlexibleSearchService().getModelByExample(modelToSet);
	}

	private boolean isTypeOrSubtype(final ComposedTypeModel base, final ComposedTypeModel test)
	{
		return base.equals(test) || base.getAllSubTypes().contains(test);
	}

	/**
	 * The instance of this class represents the model or metatype of model. Includes parent in relation to the instance
	 * which is a unique member.
	 */
	class YUniqueMember
	{
		private final ItemModel model;
		private final String attributeTypeCode;
		private final YUniqueMember parent;
		private Object uniquePropertyValue = null;
		private final boolean metatype;

		/**
		 * @param metatype
		 *           if true then metatype(ComposedTypeModel); if false then type(ItemModel)
		 */
		public YUniqueMember(final ItemModel model, final YUniqueMember parent, final String attributeTypeCode,
				final boolean metatype, final Object uniquePropertyValue)
		{
			this.model = model;
			this.attributeTypeCode = attributeTypeCode;
			this.parent = parent;
			this.metatype = metatype;
			this.uniquePropertyValue = uniquePropertyValue;
			if (!this.metatype)
			{
				setUniquePropertyValue();
			}

		}

		private void setUniquePropertyValue()
		{
			final TypeService typeService = serviceLocator.getTypeService();
			final ModelService modelService = serviceLocator.getModelService();
			final String uniqueId = getUniquePropertyName(typeService.getComposedType(this.model.getClass()));
			uniquePropertyValue = modelService.getAttributeValue(model, uniqueId);
		}

		public ItemModel getModel()
		{
			return model;
		}

		public String getAttributeTypeCode()
		{
			return attributeTypeCode;
		}

		public YUniqueMember getParent()
		{
			return parent;
		}

		public Object getUniquePropertyValue()
		{
			return uniquePropertyValue;
		}

		public boolean isMetatype()
		{
			return metatype;
		}
	}
}
