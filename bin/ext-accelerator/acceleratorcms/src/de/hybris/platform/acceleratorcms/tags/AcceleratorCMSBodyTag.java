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
package de.hybris.platform.acceleratorcms.tags;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2lib.cmstags.CMSBodyTag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * This CMSBodyTag creates the RestrictionData for the page, adding it to the request as "restrictionData". This is then
 * picked up by the {@link AcceleratorCMSComponentTag}.
 *
 * @deprecated The AcceleratorCMSBodyTag is deprecated. Load the live edit CSS
 * and JavaScript files you want as required rather than relying on the default
 * behaviour of he CMSBodyTag.
 */
@Deprecated
public class AcceleratorCMSBodyTag extends CMSBodyTag
{
	private static final Logger LOG = Logger.getLogger(AcceleratorCMSBodyTag.class);

	private String cssClass;
	private String style;
	private String liveEditCssPath;
	private String liveEditJsPath;

	public String getCssClass()
	{
		return cssClass;
	}

	public void setCssClass(final String cssClass)
	{
		this.cssClass = cssClass;
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle(final String style)
	{
		this.style = style;
	}

	public String getLiveEditCssPath()
	{
		return liveEditCssPath;
	}

	public void setLiveEditCssPath(final String liveEditCssPath)
	{
		this.liveEditCssPath = liveEditCssPath;
	}

	public String getLiveEditJsPath()
	{
		return liveEditJsPath;
	}

	public void setLiveEditJsPath(final String liveEditJsPath)
	{
		this.liveEditJsPath = liveEditJsPath;
	}

	@Override
	public int doStartTag() throws JspException
	{
		/*
		 * Use for CMSCockpit integration - please provide here some logic that informs CMSCockpit about store specific
		 * information.
		 */
		final AbstractPageModel currentPage = (AbstractPageModel) pageContext.getRequest().getAttribute("currentPage");

		try
		{
			final JspWriter pageContextOut = pageContext.getOut();
			pageContextOut.print(getBodyOpenTag(currentPage));
			if (isLiveEdit())
			{
				pageContextOut.print(getLiveEditCssAndJs(currentPage));
			}
			pageContextOut.print(getScriptInBodyTag(currentPage));

			// Create the page restriction data in the request attributes
			final WebApplicationContext appContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());
			populateRestrictionData((HttpServletRequest) this.pageContext.getRequest(),
					(CMSDataFactory) appContext.getBean("cmsDataFactory"));
		}
		catch (final IOException e)
		{
			LOG.warn("Error processing tag: " + e.getMessage());
		}
		return EVAL_BODY_INCLUDE;
	}

	protected String getBodyOpenTag(final AbstractPageModel currentPage)
	{
		final StringBuilder bodyTagBuilder = new StringBuilder();
		bodyTagBuilder.append("body");
		bodyTagBuilder.append(getBodyTagCssClass(currentPage));
		bodyTagBuilder.append(getBodyTagStyle(currentPage));
		bodyTagBuilder.append(getBodyOnLoad(currentPage));
		bodyTagBuilder.append(getBodyOnClick(currentPage));

		return "<" + bodyTagBuilder.toString() + ">\n";
	}

	protected String getBodyTagCssClass(final AbstractPageModel currentPage)
	{
		if (getCssClass() != null && !getCssClass().isEmpty())
		{
			return " class=\"" + getCssClass() + "\" ";
		}
		return "";
	}

	protected String getBodyTagStyle(final AbstractPageModel currentPage)
	{
		if (getCssClass() != null && !getCssClass().isEmpty())
		{
			return " style=\"" + getStyle() + "\" ";
		}
		return "";
	}

	protected String getBodyOnLoad(final AbstractPageModel currentPage)
	{
		if (isPreviewDataModelValid())
		{
			String currentPagePk = null;
			if (currentPage != null)
			{
				currentPagePk = currentPage.getPk().toString();
			}

			return " onload=\"getCurrentPageLocation(window.location.href, '" + currentPagePk + "' , currentUserId " + ", currentJaloSessionId)\" ";
		}
		return "";
	}

	protected String getBodyOnClick(final AbstractPageModel currentPage)
	{
		if (isLiveEdit())
		{
			return " onclick=\"return getCMSElement(event)\"";
		}
		return "";
	}

	protected String getLiveEditCssAndJs(final AbstractPageModel currentPage)
	{
		// Load the CSS
		// Load the JS async
		return
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getLiveEditCssPath() + "\"/>\n" +
				"<script type=\"text/javascript\">\n" +
				"(function() {\n" +
				"	var le = document.createElement('script'); le.type = 'text/javascript'; le.async = true;\n" +
				"	le.src = '" + getLiveEditJsPath() + "';\n" +
				"	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(le, s);\n" +
				"})();\n" +
				"</script>\n";
	}


	protected String getScriptInBodyTag(final AbstractPageModel currentPage)
	{
		if (isPreviewDataModelValid())
		{
			return
					"<script type=\"text/javascript\">\n" +
					" var currentUserId;\n" +
					" var currentJaloSessionId;\n" +
					" function getCurrentPageLocation(url, pagePk, userUid, jaloSessionId){\n" +
					"   if (url != \"\") {\n" +
					"   		if(parent.notifyIframeAboutUrlChanage){\n" +
					"       	parent.notifyIframeAboutUrlChanage(url, pagePk, userUid, jaloSessionId);\n" +
					" 	  	}\n" +
					"   }\n"
					+ "}\n" +
					"</script>\n";
		}
		return "";
	}

	protected void populateRestrictionData(final HttpServletRequest request, final CMSDataFactory cmsDataFactory)
	{
		final Object catalog = request.getAttribute("catalogId");
		final Object category = request.getAttribute("currentCategoryCode");
		final Object product = request.getAttribute("currentProductCode");
		final String catalogId = (catalog instanceof String) ? catalog.toString() : "";
		final String categoryCode = (category instanceof String) ? category.toString() : "";
		final String productCode = (product instanceof String) ? product.toString() : "";
		request.setAttribute("restrictionData", cmsDataFactory.createRestrictionData(categoryCode, productCode, catalogId));
	}

	@Override
	protected boolean isLiveEdit()
	{
		final PreviewDataModel previewData = getPreviewData(pageContext.getRequest());
		return previewData != null && Boolean.TRUE.equals(previewData.getLiveEdit());
	}
}
