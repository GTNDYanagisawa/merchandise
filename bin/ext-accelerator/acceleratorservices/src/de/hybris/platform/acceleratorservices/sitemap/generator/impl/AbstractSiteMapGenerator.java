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
package de.hybris.platform.acceleratorservices.sitemap.generator.impl;


import de.hybris.platform.acceleratorservices.enums.SiteMapPageEnum;
import de.hybris.platform.acceleratorservices.sitemap.data.SiteMapUrlData;
import de.hybris.platform.acceleratorservices.sitemap.generator.SiteMapGenerator;
import de.hybris.platform.acceleratorservices.sitemap.renderer.SiteMapContext;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public abstract class AbstractSiteMapGenerator<T> implements SiteMapGenerator<T>, ApplicationContextAware
{

	private ImpersonationService impersonationService;
	private RendererService rendererService;
	private Converter<T, SiteMapUrlData> siteMapUrlDataConverter;
	private ApplicationContext applicationContext;
	private CommonI18NService commonI18NService;
	private FlexibleSearchService flexibleSearchService;
	private SiteMapPageEnum siteMapPageEnum;
	private CatalogVersionService catalogVersionService;

	public List<T> getData(final CMSSiteModel site)
	{
		final ImpersonationContext context = new ImpersonationContext();
		context.setSite(site);
		context.setCatalogVersions(getCatalogVersionService().getSessionCatalogVersions());

		return getImpersonationService().executeInContext(context, new ImpersonationService.Executor<List<T>, RuntimeException>()
		{
			@Override
			public List<T> execute() throws RuntimeException
			{
				return getDataInternal(site);
			}
		});
	}

	public File render(final CMSSiteModel site, final CurrencyModel currencyModel, final LanguageModel languageModel,
			final RendererTemplateModel rendererTemplateModel, final List<T> models, final String filePrefix,final Integer index) throws IOException
	{
		final String prefix = (index != null) ? String.format(filePrefix + "-%s-%s-%s-", languageModel.getIsocode(), currencyModel.getIsocode(), index)
				:String.format(filePrefix + "-%s-%s-", languageModel.getIsocode(), currencyModel.getIsocode());
		final File siteMap = File.createTempFile(prefix, ".xml");

		final ImpersonationContext context = new ImpersonationContext();
		context.setSite(site);
		context.setCurrency(currencyModel);
		context.setLanguage(languageModel);

		return getImpersonationService().executeInContext(context, new ImpersonationService.Executor<File, IOException>()
		{
			@Override
			public File execute() throws IOException
			{
				final List<SiteMapUrlData> siteMapUrlDataList = getSiteMapUrlData(models);

				final SiteMapContext context = (SiteMapContext) applicationContext.getBean("siteMapContext");

				context.init(site, getSiteMapPageEnum());
				context.setSiteMapUrlData(siteMapUrlDataList);

				final BufferedWriter output = new BufferedWriter(new FileWriter(siteMap));
				try
				{
					// the template media is loaded only for english language.
					getCommonI18NService().setCurrentLanguage(getCommonI18NService().getLanguage("en"));
					getRendererService().render(rendererTemplateModel, context, output);
				}
				finally
				{
					IOUtils.closeQuietly(output);
				}

				return siteMap;
			}

		});
	}

	public abstract List<SiteMapUrlData> getSiteMapUrlData(List<T> models);

	protected <T> List<T> doSearch(final String query, final Map<String, Object> params, final Class<T> resultClass)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		if (params != null)
		{
			fQuery.addQueryParameters(params);
		}

		fQuery.setResultClassList(Collections.singletonList(resultClass));

		final SearchResult<T> searchResult = getFlexibleSearchService().search(fQuery);
		return searchResult.getResult();
	}

	protected abstract List<T> getDataInternal(final CMSSiteModel siteModel);


	public ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	public RendererService getRendererService()
	{
		return rendererService;
	}

	@Required
	public void setRendererService(final RendererService rendererService)
	{
		this.rendererService = rendererService;
	}

	public Converter<T, SiteMapUrlData> getSiteMapUrlDataConverter()
	{
		return siteMapUrlDataConverter;
	}

	@Required
	public void setSiteMapUrlDataConverter(final Converter<T, SiteMapUrlData> siteMapUrlDataConverter)
	{
		this.siteMapUrlDataConverter = siteMapUrlDataConverter;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public SiteMapPageEnum getSiteMapPageEnum()
	{
		return siteMapPageEnum;
	}

	@Required
	public void setSiteMapPageEnum(final SiteMapPageEnum siteMapPageEnum)
	{
		this.siteMapPageEnum = siteMapPageEnum;
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
