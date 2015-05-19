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

import de.hybris.platform.core.dto.ItemDTO;
import de.hybris.platform.jalo.security.AccessManager;
import de.hybris.platform.webservices.ForbiddenException;
import de.hybris.platform.webservices.SecurityStrategy;
import de.hybris.platform.webservices.WsUtilService;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultNodeProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.NodeContextImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;


/**
 * Node Processor that implements Attribute Selector API and Attribute Security.
 */
public class YNodeProcessor extends DefaultNodeProcessor
{
	private static final Logger LOG = Logger.getLogger(YNodeProcessor.class);

	/**
	 * Processes selected properties and checks the security constrains('type' and 'attribute') on attribute level for
	 * current session user and Node.
	 * <p>
	 * This method differs from the original implementation in superclass in that it is not processing all available
	 * properties, but instead it is processing those properties that have been selected for processing. The rules for
	 * selection are simple: If there is dynamic configuration for properties (query parameters) use it first. If there
	 * is no dynamic configuration, but a static configuration exists, use it. If there's neither dynamic nor static
	 * configuration, use defaults. By default detail representation contains all attributes (it is the case of
	 * processing non-root resource at distance=0), and reference representation contains only "uri" and "unique"
	 * properties (it is the case for root resources and non-root resources at distance > 0).
	 * <p>
	 * The Attribute Security part.
	 * <ul>
	 * <li>During model to dto transformation attribute security limits number of attributes or allows to process all of
	 * them.</li>
	 * <li>During dto to model transformation attribute('atribute') security throws the {@link ForbiddenException} or
	 * allows to process all modified attributes.</li>
	 * <li>'type' - type based checking (read, change, create, delete) on the attribute level.</li>
	 * <li>'attribute - attribute based checking (read, change) on the attribute level.</li>
	 * <li>More about attribute security here: {@link SecurityStrategy}</li>
	 * </ul>
	 * 
	 * @param nodeCtx
	 *           {@link NodeContextImpl} current node context
	 * @param source
	 *           source node value
	 * @param target
	 *           target node value (never null)
	 */
	@Override
	protected void processProperties(final NodeContextImpl nodeCtx, final Object source, final Object target)
	{
		// no magic stuff or fallbacks here
		// if configuration went properly we must have that instance
		//		if (!(nodeCtx.getGraphContext() instanceof YObjectGraphContext))
		//		{
		//			throw new YWebservicesException(GraphContext.class.getSimpleName() + " must be of type "
		//					+ YObjectGraphContext.class.getSimpleName());
		//		}


		if (nodeCtx.getGraphContext() instanceof YObjectGraphContext)
		{
			final YObjectGraphContext graphCtx = (YObjectGraphContext) nodeCtx.getGraphContext();
			final SecurityStrategy securityStrategy = graphCtx.getRequestResource().getSecurityStrategy();

			//The attribute selector API concerns only Model->DTO transformation
			if (graphCtx.isModelToDtoTransformation())
			{
				//We only deal with nodes that are annotated with XmlRootElement annotation.
				//This is because that annotation provides a name for the element, and this name is used to get attribute selector configuration.
				//In other words without this name, we'll not be able to understand configuration, which is based on the type name.
				final boolean isAnnotationPresent = nodeCtx.getTargetNodeValue().getClass().isAnnotationPresent(XmlRootElement.class);
				if (isAnnotationPresent)
				{
					//get the attributes that should be processed for this node
					final Map<String, List<String>> queryParameters = graphCtx.getUriInfo() != null ? graphCtx.getUriInfo()
							.getQueryParameters() : null;

					//part1: attribute selection
					final Collection<PropertyMapping> selectedAttributes = performAttributeSelection(nodeCtx, queryParameters,
							graphCtx.getServices().getWsUtilService());

					final Class<?> nodeDtoClass = nodeCtx.getNodeMapping().getTargetConfig().getType();
					//process selected attributes.
					for (final PropertyMapping pm : selectedAttributes)
					{
						//part2: security
						if (performModelToDtoTransformationSecurity(securityStrategy, pm, nodeDtoClass))
						{
							final PropertyContext propCtx = createChildProperty(nodeCtx, pm);
							pm.getProcessor().process(propCtx, source, target);
						}
					}
				}
				else
				{
					//We have An object is a graph node but it does not map into XML element... Let the superclass handle it.
					super.processProperties(nodeCtx, source, target);
				}
			}
			else
			{
				//security part
				if (securityStrategy != null)
				{
					performDtoToModelTransformationSecurity(securityStrategy, nodeCtx, graphCtx, source, target);
				}
				//if all attributes are allowed, let the superclass deal with this case
				super.processProperties(nodeCtx, source, target);
			}
		}
		else
		{
			super.processProperties(nodeCtx, source, target);
		}
	}

	/**
	 * Method collects all security invocations within model to dto transformation. This method is used within
	 * {@link #processProperties(NodeContextImpl, Object, Object)} method.
	 */
	private boolean performModelToDtoTransformationSecurity(final SecurityStrategy securityStrategy,
			final PropertyMapping property, final Class<?> nodeDtoClass)
	{
		final Class<?> attrDtoClass = property.getTargetConfig().getReadType();

		//attribute security | type based attribute security level(for single attribute) and attribute security level
		if (securityStrategy == null
				|| (securityStrategy.isDtoOperationAllowed(attrDtoClass, AccessManager.READ) && securityStrategy
						.isAttributeOperationAllowed(nodeDtoClass, property.getId(), AccessManager.READ)))
		{
			return true;
		}
		return false;
	}

	/**
	 * Method collects all security invocations within dto to model transformation. This method is used within
	 * {@link #processProperties(NodeContextImpl, Object, Object)} method.
	 */
	private void performDtoToModelTransformationSecurity(final SecurityStrategy securityStrategy, final NodeContext nodeCtx,
			final YObjectGraphContext graphCtx, final Object source, final Object target)
	{
		//attribute security
		final Map<String, PropertyMapping> propCfg = nodeCtx.getNodeMapping().getPropertyMappings();
		final String operation = (graphCtx.getRequestResource().getServiceLocator().getModelService().isNew(target)) ? AccessManager.CREATE
				: AccessManager.CHANGE;

		final Class<?> nodeDtoClass = nodeCtx.getNodeMapping().getSourceConfig().getType();
		//part1: type based attribute security level(for single node); if not allowed - ForbiddenException is thrown
		final boolean isDtoAllowed = securityStrategy.isDtoOperationAllowed(nodeDtoClass, operation);
		if (!isDtoAllowed)
		{
			//throw exception
			throw new ForbiddenException("You do not have permission to " + operation + ": " + source.getClass().getSimpleName()
					+ " dto.");

		}
		//part2: attribute security level; if any attribute isn't allowed to change - ForbiddenException is thrown
		final StringBuffer notAllowed = new StringBuffer();
		for (final PropertyMapping property : prepareAttrQualifiers(nodeCtx, propCfg.values()))
		{
			if (!securityStrategy.isAttributeOperationAllowed(nodeDtoClass, property.getId(), AccessManager.CHANGE))
			{
				notAllowed.append(property.getId()).append(", \n");
			}
		}
		if (notAllowed.length() != 0)
		{
			//throw exception
			throw new ForbiddenException("You do not have permission to " + operation + ": " + notAllowed + " attributes of the "
					+ source.getClass().getSimpleName() + " dto.");
		}
	}

	/**
	 * Method achieves the list of property mappings that are involved in Dto2Model transformation from all available
	 * node property mappings.
	 * 
	 * @param nodeCtx
	 * @param properties
	 * 
	 * @return list of attribute qualifiers.
	 */
	private List<PropertyMapping> prepareAttrQualifiers(final NodeContext nodeCtx, final Collection<PropertyMapping> properties)
	{
		final List<PropertyMapping> attrQualifiers = new ArrayList<PropertyMapping>();
		for (final PropertyMapping property : properties)
		{
			final Method readMethod = property.getSourceConfig().getReadMethod();
			try
			{
				final Object value = readMethod.invoke(nodeCtx.getSourceNodeValue(), (Object[]) null);
				if (value != null)
				{
					attrQualifiers.add(property);
				}
			}
			catch (final Exception e)
			{
				LOG.error("YNode processing: error during invocation of the read method. ", e);
			}
		}

		return attrQualifiers;
	}

	/**
	 * Adds the names of XML elements found in DTO's type hierarchy to the given list. This method has a side-effect of
	 * modifying passed list!
	 * 
	 * @param xmlNodeNames
	 *           input list. The same instance will be returned from this method, possibly after adding several elements.
	 * @param _dtoClass
	 *           class of the DTO. This method tries to find this DTO XmlRootElement annotation and extract the "name"
	 *           attribute value. This value is the "XML type name" for the DTO. The value is appended to the list. Then
	 *           The DTO superclass is processed, and so on until ItemDTO.class is reached (ItemDTO itself IS included in
	 *           results).
	 * @return the same list that is passed as xmlNodeNames argument but possible with several elements added.
	 */
	private List<String> addXMLTypeNamesForDTO(final List<String> xmlNodeNames, final Class<?> _dtoClass)
	{
		Class<?> currentClass = _dtoClass;

		boolean end = false;

		while (!end)
		{
			/*
			 * helper variable that is not really needed. However, this is how compiler can help to check correctness of
			 * algorithm. Making the variable final ensures that every branch of if/else initializes the variable, so it is
			 * not "forgotten" - it is especially important when refactoring.
			 */
			final boolean _end;

			//always end processing on Object.class. This is just additional protection, normally we should stop on ItemDTO.class
			if (currentClass == Object.class)
			{
				_end = true;
			}
			else
			{
				//if it is XML node, try add it to the list
				if (currentClass.isAnnotationPresent(XmlRootElement.class))
				{
					final XmlRootElement annotationValue = currentClass.getAnnotation(XmlRootElement.class);
					if (annotationValue.name() != null && !annotationValue.name().isEmpty())
					{
						xmlNodeNames.add(annotationValue.name());
					}
				}

				//If we reached ItemDTO (which is by now already added to the list), end processing. Else proceed with superclass.
				if (currentClass == ItemDTO.class)
				{
					_end = true;
				}
				else
				{
					currentClass = currentClass.getSuperclass();
					//we do not end processing yet...
					_end = false;
				}
			}

			end = _end;
		}

		return xmlNodeNames;
	}

	/**
	 * Perfoms attribute selection for the currently processed node in Model->DTO transformation. The algorithm is more
	 * or less following:
	 * <ul>
	 * <li>1.) get all possible attributes</li>
	 * <li>2.) read attribute selector configuration - it gives a set of "allowed" attributes</li>
	 * <li>3.) Filter out attributes which are not allowed by step 2.)</li>
	 * </ul>
	 * If there is no configuration data available, there is a "default" behavior implemented: for root resources it
	 * allows only "reference representation" of attributes, for "normal" (non-root) resources it allows all attributes
	 * for distance=0, and "reference representation" for distance > 0.
	 * 
	 * @param nodeCtx
	 * @param wsUtilService
	 * @return
	 */
	protected Collection<PropertyMapping> performAttributeSelection(final NodeContextImpl nodeCtx,
			final Map<String, List<String>> httpQueryParameters, final WsUtilService wsUtilService)
	{
		final List<PropertyMapping> result = new ArrayList<PropertyMapping>();

		// all properties which have to be processed (includes those which are itself nodes)
		final Map<String, PropertyMapping> propCfg = nodeCtx.getNodeMapping().getPropertyMappings();

		final List<String> possibleConfigNodeNames = new ArrayList<String>();

		final String nodePropertyName;
		if (nodeCtx.getParentContext() != null)
		{
			if (nodeCtx.getParentContext().getPropertyMapping() != null)
			{
				nodePropertyName = nodeCtx.getParentContext().getPropertyMapping().getId();
			}
			else
			{
				nodePropertyName = null;
			}
		}
		else
		{
			nodePropertyName = null;
		}

		if (nodePropertyName != null)
		{
			possibleConfigNodeNames.add(nodePropertyName);
		}

		//Perform attribute filtering.
		final Class targetNodeClass = nodeCtx.getTargetNodeValue().getClass();
		final List<String> list = addXMLTypeNamesForDTO(possibleConfigNodeNames, targetNodeClass);
		final Set<String> selectedAttributes = wsUtilService.getConfigurationForType(nodeCtx.getDistance(), targetNodeClass,
				httpQueryParameters, propCfg.keySet(), list);

		//Filter out "unwanted" attributes
		if (selectedAttributes != null && !selectedAttributes.isEmpty())
		{
			for (final Entry<String, PropertyMapping> pmEntry : propCfg.entrySet())
			{
				if (selectedAttributes.contains(pmEntry.getKey()) || //
						//HOR-605 - "uri" attributes should always be displayed.
						"uri".equalsIgnoreCase(pmEntry.getKey()) //
				)
				{
					result.add(pmEntry.getValue());
				}
			}
		}
		else
		{
			Collection<PropertyMapping> processedProps = null;
			//No attribute selection information available - use defaults
			if (nodeCtx.getDistance() == 0)
			{
				//Return default "detail" representation of resource attributes : all attributes.
				processedProps = propCfg.values();
			}
			else
			{
				//Return default "reference representation" of resource attributes i.e. only "uri" and "unique" attributes.
				processedProps = getDefaultPropertySelection(nodeCtx, propCfg);
			}
			result.addAll(processedProps);
		}

		return result;
	}

	/**
	 * This method provides default "reference representation" of attributes for current Node.
	 * 
	 * @param nodeCtx
	 * @param propCfg
	 * @return
	 */
	private Collection<PropertyMapping> getDefaultPropertySelection(final NodeContextImpl nodeCtx,
			final Map<String, PropertyMapping> propCfg)
	{
		//handle "unique" properties, if any
		final Set<PropertyMapping> result = new HashSet<PropertyMapping>();

		// add additionally configured UID properties
		final PropertyConfig[] uidProps = nodeCtx.getNodeMapping().getTargetConfig().getUidProperties();
		if (uidProps != null && uidProps.length > 0)
		{
			// ... add these
			for (final PropertyConfig uidProp : uidProps)
			{
				final String propertyName = uidProp.getName();
				final PropertyMapping uniqueProperty = propCfg.get(propertyName);
				if (uniqueProperty != null)
				{
					result.add(uniqueProperty);
				}
			}
		}

		// add XMLAttribute annotated properties
		// this should at least include 'pk', 'uri'
		final Collection<PropertyConfig> allProps = nodeCtx.getNodeMapping().getTargetConfig().getProperties().values();
		for (final PropertyConfig pCfg : allProps)
		{
			if (pCfg.getReadMethod().isAnnotationPresent(XmlAttribute.class) &&//
			 propCfg.get(pCfg.getName()) != null // aspectJ injected method
			)
			{
				result.add(propCfg.get(pCfg.getName()));
			}
		}


		return result;
	}

}
