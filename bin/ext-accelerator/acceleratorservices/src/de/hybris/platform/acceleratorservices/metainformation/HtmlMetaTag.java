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
package de.hybris.platform.acceleratorservices.metainformation;

import de.hybris.platform.acceleratorservices.util.HtmlElementHelper;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.acceleratorservices.storefront.data.MetaElementData;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * JSP tag that will render as {@code meta} HTML element and will be able to translate fields to tag attributes in a
 * generic way.
 */
public class HtmlMetaTag extends TagSupport implements TryCatchFinally
{
	private static final Logger LOG = Logger.getLogger(HtmlMetaTag.class.getName());

	// Services
	protected HtmlElementHelper htmlElementHelper;
	protected MetaElementAttributeNameResolver metaElementAttributeNameResolver;

	/** List of items for which {@code <meta>} tags should be generated. */
	protected List<MetaElementData> metaElementDataList;

	/** Resets internal state of object. */
	protected void reset()
	{
		htmlElementHelper = null;
		metaElementAttributeNameResolver = null;
		metaElementDataList = null;
	}

	@Override
	public void release()
	{
		super.release();
		reset();
	}

	/**
	 * Setter for {@link #metaElementDataList} field. It's part of taglib interface.
	 * 
	 * @param metaElementDataList
	 *           List of items for which {@code <meta>} tags should be generated.
	 */
	public void setItems(final List<MetaElementData> metaElementDataList)
	{
		this.metaElementDataList = metaElementDataList;
	}

	protected HtmlElementHelper lookupHtmlElementHelper()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "htmlElementHelper", HtmlElementHelper.class, true);
	}

	protected MetaElementAttributeNameResolver lookupMetaElementAttributeNameResolver()
	{
		return SpringHelper.getSpringBean(pageContext.getRequest(), "metaElementAttributeNameResolver",
				MetaElementAttributeNameResolver.class, true);
	}

	protected void loadServices()
	{
		if (htmlElementHelper == null)
		{
			htmlElementHelper = lookupHtmlElementHelper();
		}
		if (metaElementAttributeNameResolver == null)
		{
			metaElementAttributeNameResolver = lookupMetaElementAttributeNameResolver();
		}
	}

	@Override
	public int doStartTag() throws JspException
	{
		loadServices();

		if (metaElementDataList != null && !metaElementDataList.isEmpty())
		{
			for (final MetaElementData metaElementData : metaElementDataList)
			{
				writeMetaElement(metaElementData);
			}
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException
	{
		return EVAL_PAGE;
	}


	protected void writeMetaElement(final MetaElementData metaElementData)
	{
		// Write the open element
		final Map<String, String> elementAttributes = getAttributesMap(metaElementData);
		if (elementAttributes != null && elementAttributes.size() > 0)
		{
			htmlElementHelper.writeOpenElement(pageContext, "meta", elementAttributes);
		}
	}

	protected Map<String, String> getAttributesMap(final MetaElementData metaElementData)
	{
		final Map<String, String> attributes = new LinkedHashMap<>();
		try
		{
			final Map<String, Object> fieldValues = PropertyUtils.describe(metaElementData);
			for (final Entry<String, Object> fieldData : fieldValues.entrySet())
			{
				if ("class".equals(fieldData.getKey()))
				{
					continue;
				}
				final String name = metaElementAttributeNameResolver.resolveName(fieldData.getKey());
				String value = fieldData.getValue() == null ? null : fieldData.getValue().toString();
				if (value != null)
				{
					value = StringEscapeUtils.escapeHtml(value);
				}

				if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value))
				{
					attributes.put(name, value);
				}
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			LOG.error("Exception while running PropertyUtils.describe", e);
		}
		return attributes;
	}

	@Override
	public void doCatch(final Throwable throwable) throws Throwable
	{
		// Nothing to do
	}

	@Override
	public void doFinally()
	{
		reset();
	}
}
