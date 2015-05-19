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
package de.hybris.platform.acceleratorcms.tags2;

import de.hybris.platform.acceleratorcms.component.slot.CMSPageSlotComponentService;
import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.util.HtmlElementHelper;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class CMSComponentTag extends TagSupport implements DynamicAttributes
{
	private static final Logger LOG = Logger.getLogger(CMSComponentTag.class.getName());

	// Tag Attributes
	protected String uidAttribute;
	protected AbstractCMSComponentModel componentAttribute;
	protected AbstractCMSComponentModel parentComponentAttribute;
	protected boolean evaluateRestrictionAttribute;
	protected String elementAttribute = "";
	protected Map<String, String> dynamicAttributes;

	// Services
	protected HtmlElementHelper htmlElementHelper;
	protected CMSPageContextService cmsPageContextService;
	protected CMSPageSlotComponentService cmsPageSlotComponentService;

	// State
	protected CmsPageRequestContextData currentCmsPageRequestContextData;
	protected List<AbstractCMSComponentModel> currentCmsComponents;
	protected AbstractCMSComponentModel currentComponent;


	public CMSComponentTag()
	{
		super();
		resetAttributes();
		resetState();
	}

	public void setUid(final String uid)
	{
		this.uidAttribute = uid;
	}

	public void setComponent(final AbstractCMSComponentModel component)
	{
		this.componentAttribute = component;
	}

	public void setParentComponent(final AbstractCMSComponentModel parentComponent)
	{
		this.parentComponentAttribute = parentComponent;
	}

	public void setEvaluateRestriction(final boolean evaluateRestriction)
	{
		this.evaluateRestrictionAttribute = evaluateRestriction;
	}

	public void setElement(final String element)
	{
		this.elementAttribute = element;
	}

	@Override
	public void setDynamicAttribute(final String uri, final String localName, final Object value) throws JspException
	{
		final String attributeValue = attributeToString(value);
		if (attributeValue != null)
		{
			// Store the dynamic attributes
			dynamicAttributes.put(localName, attributeValue);
		}
	}

	protected String attributeToString(final Object value)
	{
		if (value instanceof String)
		{
			return (String) value;
		}
		if (value == null)
		{
			return null;
		}
		return value.toString();
	}

	@Override
	public void release()
	{
		super.release();
		resetState();
		resetAttributes();
	}

	protected final void resetAttributes()
	{
		uidAttribute = null;
		componentAttribute = null;
		parentComponentAttribute = null;
		evaluateRestrictionAttribute = false;
		elementAttribute = "";
		dynamicAttributes = new HashMap<>();
	}

	protected final void resetState()
	{
		currentCmsPageRequestContextData = null;
		currentCmsComponents = null;
		currentComponent = null;
	}

	protected CMSPageContextService lookupCMSPageContextService()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "cmsPageContextService", CMSPageContextService.class, true);
	}

	protected CMSPageSlotComponentService lookupCMSPageSlotComponentService()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "cmsPageSlotComponentService",
				CMSPageSlotComponentService.class, true);
	}

	protected HtmlElementHelper lookupHtmlElementHelper()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "htmlElementHelper", HtmlElementHelper.class, true);
	}

	protected void loadServices()
	{
		if (htmlElementHelper == null)
		{
			htmlElementHelper = lookupHtmlElementHelper();
		}
		if (cmsPageContextService == null)
		{
			cmsPageContextService = lookupCMSPageContextService();
		}
		if (cmsPageSlotComponentService == null)
		{
			cmsPageSlotComponentService = lookupCMSPageSlotComponentService();
		}
	}

	protected void prepare()
	{
		// Lookup the CMS Page Context for the current request
		currentCmsPageRequestContextData = cmsPageContextService.getCmsPageRequestContextData(pageContext.getRequest());
		currentCmsPageRequestContextData.setParentComponent(parentComponentAttribute);

		// Lookup the CMS components to render
		currentCmsComponents = resolveComponents();
	}

	protected List<AbstractCMSComponentModel> resolveComponents()
	{
		final AbstractCMSComponentModel currentComponent = resolveComponent();
		if (currentComponent != null)
		{
			return cmsPageSlotComponentService.getCMSComponentsForComponent(currentCmsPageRequestContextData, currentComponent,
					evaluateRestrictionAttribute, -1);
		}
		return null;
	}

	protected AbstractCMSComponentModel resolveComponent()
	{
		// Work out the target component
		final AbstractCMSComponentModel configuredComponent = componentAttribute;
		if (configuredComponent != null)
		{
			return configuredComponent;
		}

		if (StringUtils.isNotEmpty(uidAttribute))
		{
			return cmsPageSlotComponentService.getComponentForId(uidAttribute);
		}

		return null;
	}

	protected Map<String, String> createLiveEditAttributes()
	{
		final Map<String, String> data = new HashMap<>();

		final ContentSlotModel contentSlot = (ContentSlotModel) pageContext.getAttribute("contentSlot", PageContext.REQUEST_SCOPE);
		if (contentSlot != null)
		{
			data.put("data-cms-content-slot", contentSlot.getUid());
		}
		data.put("data-cms-component", currentComponent.getUid());
		data.put("data-cms-component-type", currentComponent.getItemtype());

		return data;
	}

	@Override
	public int doStartTag() throws JspException
	{
		loadServices();
		prepare();

		if (currentCmsComponents != null && !currentCmsComponents.isEmpty())
		{
			beforeAllItems();

			for (final AbstractCMSComponentModel currentComponent : currentCmsComponents)
			{
				this.currentComponent = currentComponent;
				beforeItem();
				renderItem();
				afterItem();
			}

			afterAllItems();
		}
		else
		{
			noItems();
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}

	protected void beforeAllItems()
	{
		// template method hook
	}

	protected void afterAllItems()
	{
		// template method hook
	}

	protected void noItems()
	{
		// template method hook
	}

	protected void beforeItem()
	{
		writeOpenElement();
	}

	protected void renderItem()
	{
		try
		{
			cmsPageSlotComponentService.renderComponent(pageContext, currentComponent);
		}
		catch (final ServletException | IOException e)
		{
			LOG.warn("Error processing component tag. currentComponent [" + currentComponent + "] exception: " + e.getMessage());
		}
	}

	protected void afterItem()
	{
		writeEndElement();
	}

	protected void writeOpenElement()
	{
		final String elementName = getElementName();
		if (elementName != null)
		{
			// Write the open element
			htmlElementHelper.writeOpenElement(pageContext, elementName, getElementAttributes());
		}
	}

	protected Map<String, String> getElementAttributes()
	{
		final Map<String, String> elementCssClassMap = Collections.singletonMap("class", getElementCssClass());

		if (currentCmsPageRequestContextData.isLiveEdit())
		{
			// Combine together the attribute maps
			return htmlElementHelper.mergeAttributeMaps(elementCssClassMap, dynamicAttributes, createLiveEditAttributes());
		}
		else
		{
			return htmlElementHelper.mergeAttributeMaps(elementCssClassMap, dynamicAttributes);
		}
	}

	protected void writeEndElement()
	{
		final String elementName = getElementName();
		if (elementName != null)
		{
			htmlElementHelper.writeEndElement(pageContext, elementName);
		}
	}

	protected String getElementCssClass()
	{
		return "yCmsComponent";
	}

	protected String getElementName()
	{
		// Work out the element name to use - if no element name and live edit then use 'div'
		if (elementAttribute != null && !elementAttribute.isEmpty())
		{
			return elementAttribute;
		}
		else if (currentCmsPageRequestContextData.isLiveEdit())
		{
			return "div";
		}
		return null;
	}
}
