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
package de.hybris.platform.acceleratorcms.component.slot.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorcms.component.container.CMSComponentContainerStrategy;
import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.acceleratorcms.component.slot.CMSPageSlotComponentService;
import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Stopwatch;


/**
 */
public class DefaultCMSPageSlotComponentService implements CMSPageSlotComponentService
{
	private static final Logger LOG = Logger.getLogger(DefaultCMSPageSlotComponentService.class);

	private CMSRestrictionService cmsRestrictionService;
	private CMSContentSlotService cmsContentSlotService;
	private CMSComponentService cmsComponentService;
	private CMSComponentRenderer cmsComponentRenderer;
	private CMSComponentContainerStrategy cmsComponentContainerStrategy;


	protected CMSRestrictionService getCmsRestrictionService()
	{
		return cmsRestrictionService;
	}

	@Required
	public void setCmsRestrictionService(final CMSRestrictionService cmsRestrictionService)
	{
		this.cmsRestrictionService = cmsRestrictionService;
	}

	protected CMSContentSlotService getCmsContentSlotService()
	{
		return cmsContentSlotService;
	}

	@Required
	public void setCmsContentSlotService(final CMSContentSlotService cmsContentSlotService)
	{
		this.cmsContentSlotService = cmsContentSlotService;
	}

	protected CMSComponentService getCmsComponentService()
	{
		return cmsComponentService;
	}

	@Required
	public void setCmsComponentService(final CMSComponentService cmsComponentService)
	{
		this.cmsComponentService = cmsComponentService;
	}

	protected CMSComponentRenderer getCmsComponentRenderer()
	{
		return cmsComponentRenderer;
	}

	@Required
	public void setCmsComponentRenderer(final CMSComponentRenderer cmsComponentRenderer)
	{
		this.cmsComponentRenderer = cmsComponentRenderer;
	}

	protected CMSComponentContainerStrategy getCmsComponentContainerStrategy()
	{
		return cmsComponentContainerStrategy;
	}

	@Required
	public void setCmsComponentContainerStrategy(final CMSComponentContainerStrategy cmsComponentContainerStrategy)
	{
		this.cmsComponentContainerStrategy = cmsComponentContainerStrategy;
	}

	@Override
	public ContentSlotModel getContentSlotForId(final String id)
	{
		validateParameterNotNull(id, "Parameter id must not be null");

		try
		{
			return getCmsContentSlotService().getContentSlotForId(id);
		}
		catch (final AmbiguousIdentifierException e)
		{
			LOG.warn("Failed to lookup ContentSlot for id [" + id + "]. " + e.getMessage());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Failed to lookup ContentSlot for id [" + id + "]. " + e.getMessage());
		}
		return null;
	}

	@Override
	public ContentSlotData getContentSlotForPosition(final CmsPageRequestContextData cmsPageRequestContextData,
			final String position)
	{
		validateParameterNotNull(cmsPageRequestContextData, "Parameter cmsPageRequestContextData must not be null");
		validateParameterNotNull(position, "Parameter position must not be null");

		final ContentSlotData contentSlot = (ContentSlotData) cmsPageRequestContextData.getPositionToSlot().get(position);
		if (contentSlot == null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Failed to lookup ContentSlot for position [" + position + "] in page ["
						+ cmsPageRequestContextData.getPage().getUid() + "].");
			}
		}
		return contentSlot;
	}

	@Override
	public List<AbstractCMSComponentModel> getCMSComponentsForContentSlot(
			final CmsPageRequestContextData cmsPageRequestContextData, final ContentSlotModel contentSlot,
			final boolean evaluateRestrictions, final int limit)
	{
		validateParameterNotNull(cmsPageRequestContextData, "Parameter cmsPageRequestContextData must not be null");
		validateParameterNotNull(contentSlot, "Parameter contentSlot must not be null");

		final List<AbstractCMSComponentModel> ret = new ArrayList<AbstractCMSComponentModel>();
		for (final AbstractCMSComponentModel component : contentSlot.getCmsComponents())
		{
			if (isComponentVisible(cmsPageRequestContextData, component, evaluateRestrictions))
			{
				final int remainingLimit = limit == -1 ? -1 : limit - ret.size();
				ret.addAll(flattenComponentHierarchy(cmsPageRequestContextData, component, evaluateRestrictions, remainingLimit));
			}
		}
		return ret;
	}

	protected List<AbstractCMSComponentModel> flattenComponentHierarchy(final CmsPageRequestContextData cmsPageRequestContextData,
			final AbstractCMSComponentModel component, final boolean evaluateRestrictions, final int limit)
	{
		if (limit == 0)
		{
			return Collections.emptyList();
		}
		else if (component instanceof SimpleCMSComponentModel)
		{
			// We assume that Simple CMS Component should not be decomposed further
			return Arrays.asList(component);
		}
		else if (component instanceof AbstractCMSComponentContainerModel)
		{
			final AbstractCMSComponentContainerModel container = (AbstractCMSComponentContainerModel) component;
			return flattenComponentContainerHierarchy(cmsPageRequestContextData, container, evaluateRestrictions, limit);
		}
		else
		{
			// Don't know what this component is - just return it
			return Arrays.asList(component);
		}
	}

	protected List<AbstractCMSComponentModel> flattenComponentContainerHierarchy(
			final CmsPageRequestContextData cmsPageRequestContextData, final AbstractCMSComponentContainerModel container,
			final boolean evaluateRestrictions, final int limit)
	{
		final List<AbstractCMSComponentModel> ret = new ArrayList<AbstractCMSComponentModel>();

		// Add the child components from the container
		final List<AbstractCMSComponentModel> replacementComponents = getComponentsForContainer(container);
		if (replacementComponents != null && !replacementComponents.isEmpty())
		{
			for (final AbstractCMSComponentModel component : replacementComponents)
			{
				if (isComponentVisible(cmsPageRequestContextData, component, evaluateRestrictions))
				{
					ret.add(component);
					if (limit > 0 && ret.size() >= limit)
					{
						return ret;
					}
				}
			}
		}

		return ret;
	}

	protected List<AbstractCMSComponentModel> getComponentsForContainer(final AbstractCMSComponentContainerModel container)
	{
		return getCmsComponentContainerStrategy().getDisplayComponentsForContainer(container);
	}

	@Override
	public List<AbstractCMSComponentModel> getCMSComponentsForComponent(final CmsPageRequestContextData cmsPageRequestContextData,
			final AbstractCMSComponentModel component, final boolean evaluateRestrictions, final int limit)
	{
		validateParameterNotNull(cmsPageRequestContextData, "Parameter cmsPageRequestContextData must not be null");
		validateParameterNotNull(component, "Parameter component must not be null");

		final List<AbstractCMSComponentModel> ret = new ArrayList<AbstractCMSComponentModel>();

		if (isComponentVisible(cmsPageRequestContextData, component, evaluateRestrictions))
		{
			final int remainingLimit = limit == -1 ? -1 : limit - ret.size();
			ret.addAll(flattenComponentHierarchy(cmsPageRequestContextData, component, evaluateRestrictions, remainingLimit));
		}

		return ret;
	}

	@Override
	public AbstractCMSComponentModel getComponentForId(final String id)
	{
		validateParameterNotNull(id, "Parameter id must not be null");

		try
		{
			return getCmsComponentService().getAbstractCMSComponent(id);
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.warn("Failed to lookup ContentSlot for id [" + id + "]. " + e.getMessage());
		}
		return null;
	}

	/**
	 * Checks whether component should be displayed.<br/>
	 * <p/>
	 * Note: Computation takes into account:
	 * <ul>
	 * <li>Checks whether component is visible</li>
	 * <li>If component is restricted checks whether we should display it</li>
	 * </ul>
	 */
	@Override
	public boolean isComponentVisible(final CmsPageRequestContextData cmsPageRequestContextData,
			final AbstractCMSComponentModel component, final boolean evaluateRestrictions)
	{
		validateParameterNotNull(cmsPageRequestContextData, "Parameter cmsPageRequestContextData must not be null");
		validateParameterNotNull(component, "Parameter component must not be null");

		boolean allowed = true;
		if (Boolean.FALSE.equals(component.getVisible()))
		{
			allowed = false;
		}
		else if (doesComponentHaveRestrictions(component))
		{
			if (evaluateRestrictions)
			{
				final RestrictionData restrictionData = (RestrictionData) cmsPageRequestContextData.getRestrictionData();
				restrictionData.setValue("parentComponent", cmsPageRequestContextData.getParentComponent());
				restrictionData.setValue("component", component);
				allowed = getCmsRestrictionService().evaluateCMSComponent(component,
						restrictionData);
			}
		}
		return allowed;
	}

	protected boolean doesComponentHaveRestrictions(final AbstractCMSComponentModel component)
	{
		final List<AbstractRestrictionModel> restrictions = component.getRestrictions();
		return restrictions != null && !restrictions.isEmpty();
	}

	@Override
	public void renderComponent(final PageContext pageContext, final AbstractCMSComponentModel component) throws ServletException,
			IOException
	{
		validateParameterNotNull(pageContext, "Parameter pageContext must not be null");
		validateParameterNotNull(component, "Parameter component must not be null");

		if (LOG.isDebugEnabled())
		{
			final Stopwatch stopwatch = new Stopwatch();

			stopwatch.start();
			getCmsComponentRenderer().renderComponent(pageContext, component);
			stopwatch.stop();

			if (stopwatch.elapsedMillis() > 1)
			{
				LOG.debug("Rendered component [" + component.getUid() + "] of type [" + component.getItemtype() + "].. ("
						+ stopwatch.toString() + ")");
			}
		}
		else
		{
			getCmsComponentRenderer().renderComponent(pageContext, component);
		}
	}
}
