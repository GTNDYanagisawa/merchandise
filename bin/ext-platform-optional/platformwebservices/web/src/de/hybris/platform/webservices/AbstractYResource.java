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



import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservices.objectgraphtransformer.YContextResourceResolver;
import de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;

import java.util.Set;

import org.apache.log4j.Logger;


/**
 * The Class AbstractYResource.
 */
public abstract class AbstractYResource<RESOURCE> extends AbstractResource<RESOURCE>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AbstractYResource.class);

	/** the graph which defines this resource value and is used for transformations DTO<->Model. */
	private YObjectGraphTransformer objectGraph = null;

	private String composedType = null;

	/**
	 * The unique member(from URL) for this resource.
	 */
	private ItemModel uniqueMember;

	/**
	 * Current not saved model of type(RESOURCE).
	 */
	private ItemModel notsavedModelForCreate;

	public AbstractYResource(final String composedTypeName)
	{
		super();
		this.composedType = composedTypeName;
	}

	public void setObjectGraph(final YObjectGraphTransformer objectGraph)
	{
		this.objectGraph = objectGraph;
	}

	public YObjectGraphTransformer getObjectGraph()
	{
		return this.objectGraph;
	}

	public String getResourceType()
	{
		return this.composedType;
	}

	public void setUniqueMember(final ItemModel uniqueMember)
	{
		this.uniqueMember = uniqueMember;
	}

	public ItemModel getUniqueMember()
	{
		return uniqueMember;
	}

	public ItemModel getNotsavedModelForCreate()
	{
		return notsavedModelForCreate;
	}

	protected String setUserIntoJaloSession()
	{
		String userID = null;
		// take user from HTTP-Authentication
		if (securityCtx.getUserPrincipal() != null)
		{
			userID = securityCtx.getUserPrincipal().getName();
			serviceLocator.getWsUtilService().setRequestUserIntoJaloSession(userID);
		}
		else
		{
			// or set anonymous
			userID = "anonymous";
			serviceLocator.getWsUtilService().setRequestUserIntoJaloSession(userID);
		}

		return userID;
	}

	protected String prepareJaloSession()
	{
		final String userID = setUserIntoJaloSession();

		//set catalog versions to the jalosession for current authenticated user
		serviceLocator.getWsUtilService().setCatalogVersionsIntoJaloSession();

		return userID;
	}

	/**
	 * Method performs security check using the type based or property file based strategy. By default the typed based
	 * security strategy is in use (configurable via platformwebservices-xml-spring.xml file).
	 * 
	 * Method also checks whether the user belongs to the specified group(configurable via project.properties). If user
	 * is not a member of this group he is not allowed to work with web services at all. If the special group is not
	 * defined in this properties file then user is allowed to the web services.
	 * 
	 * @return the response
	 */
	@Override
	protected boolean isAccessGranted()
	{
		final String userID = prepareJaloSession();

		final String securityGroup = Config.getParameter("webservices.security.group");
		if (securityGroup != null)
		{
			return (serviceLocator.getWsUtilService().isMemberOf(userID, securityGroup, true)) ? super.isAccessGranted() : false;
		}
		//the group is not defined and user is allowed to the authorization part.
		return super.isAccessGranted();
	}

	/**
	 * Internal helper method.
	 * <p/>
	 * Compares resource id with value of passed dto property for equality. If property value is null, dto property gets
	 * set resource id. If property value is not null then, based on 'strict' parameter an error response is created.
	 * 
	 * @param dto
	 *           dto to validate
	 * @return true when DTO is valid for this resource
	 */
	@Override
	protected boolean processDtoId(final Object dto, final String expPropValueLiteral, final boolean strict)
	{
		// by default resourceId is of type 'String'
		Object dtoId = "";
		Object expPropValue = expPropValueLiteral;
		boolean isPK = false;

		final NodeMapping node = this.objectGraph.getNodeMapping(dto.getClass());
		final PropertyConfig[] uidProps = node.getSourceConfig().getUidProperties();
		if (uidProps != null && uidProps.length > 0)
		{
			final PropertyConfig uidProp = uidProps[0];
			isPK = "pk".equals(uidProp.getName());
			if (isPK)
			{
				expPropValue = Long.valueOf(expPropValueLiteral);
			}
			try
			{
				dtoId = uidProp.getReadMethod().invoke(dto, (Object[]) null);
				// set dtoId if not provided
				if (dtoId == null)
				{
					uidProp.getWriteMethod().invoke(dto, expPropValue);
					dtoId = expPropValue;
				}
			}
			catch (final Exception e)
			{
				LOG.error(e.getMessage());
			}
		}

		if (strict && !expPropValue.equals(dtoId))
		{
			if (isPK)
			{
				throw new BadRequestException("Resource identifier '" + expPropValueLiteral + "' doesn't match DTO identifier '"
						+ dtoId + "'");
			}
			else if (getResourceValue() == null)
			{
				throw new BadRequestException("Resource identifier '" + expPropValueLiteral
						+ "' doesn't exist and doesn't match DTO identifier '" + dtoId + "'");
			}
		}
		return true;
	}

	/**
	 * This method creates new one or updates existing response model. In case of creation, if response model doesn't
	 * have set some required unique members then they are copied from not saved model obtained from
	 * {@link AbstractYResource#readResourceInternal} method.
	 */
	@Override
	protected void createOrUpdateResource(final Object reqEntity, final RESOURCE resrcEntity, final boolean mustBeCreated)
			throws Exception
	{
		prepareResponseEntity(resrcEntity);
		this.serviceLocator.getModelService().save(resrcEntity);
	}

	/**
	 * This method creates new one or updates existing response model. In case of creation, if response model doesn't
	 * have set some required unique members then they are copied from not saved model obtained from
	 * {@link AbstractYResource#readResourceInternal} method.
	 */
	@Override
	protected void createOrUpdatePostResource(final Object reqEntity, final Object resrcEntity, final boolean mustBeCreated)
			throws Exception
	{
		prepareResponseEntity(resrcEntity);
		this.serviceLocator.getModelService().save(resrcEntity);
	}

	private void prepareResponseEntity(final Object resrcEntity)
	{
		if (((ItemModel) resrcEntity).getPk() == null)
		{
			passRequiredNotPrimitiveFieldsIfNotProvided(resrcEntity);
		}
	}

	private void passRequiredNotPrimitiveFieldsIfNotProvided(final Object resrcEntity)
	{
		final TypeService typeService = serviceLocator.getTypeService();
		final ComposedTypeModel composedTypeModel = typeService.getComposedType(resrcEntity.getClass());
		final Set<String> uMembersString = typeService.getUniqueAttributes(composedTypeModel.getCode());
		for (final String attributeName : uMembersString)
		{
			final AttributeDescriptorModel attrDescModel = typeService.getAttributeDescriptor(composedTypeModel, attributeName);
			if (attrDescModel.getUnique().booleanValue()
					&& attrDescModel.getAttributeType().getClass().equals(ComposedTypeModel.class))
			{
				final ModelService modelService = serviceLocator.getModelService();
				//copying null2null is possible and allowed, because some unique attributes can be set to null(optional=true, unique=true)
				if (modelService.getAttributeValue(resrcEntity, attributeName) == null)
				{
					final Object uniqueValue = modelService.getAttributeValue(this.notsavedModelForCreate, attributeName);
					modelService.setAttributeValue(resrcEntity, attributeName, uniqueValue);
				}
			}
		}
	}

	@Override
	protected void deleteResource(final RESOURCE respEntity)
	{
		this.serviceLocator.getModelService().remove(respEntity);
	}

	/**
	 * This method sets the unique members to the input unsaved model and return the saved instance.
	 * 
	 * @param modelToSet
	 *           the unsaved empty model(only unique field is set)
	 * 
	 * @return the supplemented model.
	 */
	protected ItemModel readResourceInternal(final ItemModel modelToSet)
	{
		final YContextResourceResolver contextResourceResolver = new YContextResourceResolver(serviceLocator);
		//not saved prepared model
		this.notsavedModelForCreate = modelToSet;
		ItemModel resultModel = null;
		resultModel = contextResourceResolver.readResourceInternal(modelToSet, composedType, getUriInfo(), uniqueMember);

		return resultModel;
	}

	/**
	 * @param resource
	 *           the child resource where unique member model will be injected.
	 */
	protected void passUniqueMember(final AbstractYResource resource)
	{
		resource.setUniqueMember((ItemModel) getResourceValue());
		if (resource.getUniqueMember() == null)
		{
			final String message = "The " + getResourceId() + " instance of " + getResourceType()
					+ " type not recognised or not available to logged-in user.";
			processException(message, new BadRequestException(message));
		}
	}
}
