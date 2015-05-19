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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.converter.impl.DefaultLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.webservices.provider.DtoClassContainer;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeValueCreatedListener;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.AbstractNodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.BidiGraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.CollectionNodeProcessor;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultGraphTransformer;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultNodeConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultNodeMapping;
import de.hybris.platform.webservices.util.objectgraphtransformer.impl.DefaultPropertyMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Hybris specific implementation of DTO -> Model conversion for an {@link DefaultGraphTransformer} Adds new behavior to
 * conversion process which is:
 * <ul>
 * <li>a target model is not created when a modelfinder already provides an instance</li>
 * <li>newly created model gets assigned a {@link LocaleProvider}</li>
 * </ul>
 */
public class YObjectGraphTransformer extends BidiGraphTransformer
{
	private static class ModelNodeCreatedListener implements NodeValueCreatedListener
	{
		@Override
		public void performCreated(final NodeContext nodeCtx, final Object created)
		{
			if (created instanceof ItemModel)
			{
				final LocaleProvider locale = new DefaultLocaleProvider((I18NService) ServicelayerUtils.getApplicationContext()
						.getBean("i18nService"));
				setLocaleProvider((ItemModel) created, locale);
			}
		}
	}

	@Deprecated
	static void setLocaleProvider(final AbstractItemModel model, final LocaleProvider localeProvider)
	{
		((ItemModelContextImpl) ModelContextUtils.getItemModelContext(model)).setLocaleProvider(localeProvider);
	}

	private static final Logger log = Logger.getLogger(YObjectGraphTransformer.class);


	public YObjectGraphTransformer()
	{
		this(null);
	}

	public YObjectGraphTransformer(final Class graph)
	{
		super(graph);
		setNodeValueCreatedListener(new ModelNodeCreatedListener());
		final ConfigurationService cfg = (ConfigurationService) Registry.getApplicationContext().getBean("configurationService");
		final String debugFilter = cfg.getConfiguration().getString("graphtransformer.debug.log4j.filter");
		if (debugFilter.length() > 0)
		{
			final String[] simpleClassNames = debugFilter.split("\\s*,\\s*");
			getDebugNodes().addAll(Arrays.asList(simpleClassNames));
		}
	}



	@Override
	public GraphContext createGraphContext()
	{
		// "model to dto" context
		final YObjectGraphContext result = new YObjectGraphContext(this);
		result.setModelToDtoTransformation(false);
		return result;
	}

	@Override
	public GraphContext createSecondGraphContext()
	{
		// "dto to model" context
		final YObjectGraphContext result = new YObjectGraphContext(this);
		result.setModelToDtoTransformation(true);
		return result;
	}


	/**
	 * Introduced for Spring only. (Spring only invokes setter)
	 */
	public void setGraphConfig(final Collection<NodeMapping> config)
	{
		super.addNodeMappings(config);
	}

	/**
	 * Introduced for Spring only. (Spring only invokes setter)
	 * 
	 * @param graph
	 *           the node class to set as root node
	 */
	public void setGraph(final Class graph)
	{
		super.addNodes(graph);
	}

	public void setGraphNodes(final DtoClassContainer dtoClassContainer)
	{
		super.addNodes(dtoClassContainer.getSingleDtoNodes());
	}

	private final List<NodeMapping> customNodeMappings = new ArrayList();

	@Override
	public void addNodeMapping(final NodeMapping sourceTargetMapping)
	{
		super.addNodeMapping(sourceTargetMapping);

		if (ItemModel.class.isAssignableFrom(sourceTargetMapping.getTargetConfig().getType()))
		{
			final String[] uidProps = ((DefaultNodeConfig) sourceTargetMapping.getSourceConfig()).getUidPropertyNames();

			if (uidProps != null)
			{
				// create a new NodeMapping
				final NodeMapping targetSourceMapping = getNodeMapping(sourceTargetMapping.getTargetConfig().getType());
				final DefaultNodeMapping nodeMapping = new DefaultNodeMapping(this, targetSourceMapping, true);

				// disallow auto-detection of available properties
				nodeMapping.setAutoDetectMappingEnabled(false);

				// add property 'uri' and all unique properties 
				nodeMapping.putPropertyMapping(new DefaultPropertyMapping(nodeMapping, "uri"));
				for (int i = 0; i < uidProps.length; i++)
				{
					nodeMapping.putPropertyMapping(new DefaultPropertyMapping(nodeMapping, uidProps[i]));
				}

				// add to list of custom mappings
				customNodeMappings.add(nodeMapping);
			}
			else
			{
				log.error(sourceTargetMapping.getSourceConfig().getType() + " has no uid properties defined");
			}
		}
	}

	private static final CollectionNodeProcessor YCOLLECTION_NODEPROC = new YCollectionNodeProcessor();

	@Override
	public void initialize()
	{
		super.initialize();
		// XXX: maybe implement as callback
		//		final DefaultNodeMapping cfg = (DefaultNodeMapping) getNodeMapping(AddressModel.class);
		//		if (cfg != null)
		//		{
		//			final DefaultPropertyMapping pCfg = new DefaultPropertyMapping(cfg, "owner");
		//			pCfg.getSourceConfig().setReadInterceptor(new ItemModelToUserModelConverter());
		//			cfg.putPropertyMapping(pCfg);
		//		}

		// add special processor with sorting/paging capabilities for collections
		// maybe in future graph-spec allows setting a processor directly, for now we must cast
		//((AbstractNodeMapping) modelGraph.getNodeMapping(Collection.class)).setNodeProcessor(YCOLLECTION_NODEPROC);
		((AbstractNodeMapping) getNodeMapping(Collection.class)).setNodeProcessor(YCOLLECTION_NODEPROC);
	}



}
