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
package de.hybris.platform.acceleratorservices.sitemap.generator;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.sitemap.generator.impl.CategoryPageSiteMapGenerator;
import de.hybris.platform.acceleratorservices.sitemap.generator.impl.PointOfServicePageSiteMapGenerator;
import de.hybris.platform.acceleratorservices.sitemap.generator.impl.ProductPageSiteMapGenerator;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commons.enums.RendererTypeEnum;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.Config;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SiteMapGeneratorIntegrationTest extends BaseCommerceBaseTest
{
	private static final Logger LOG = Logger.getLogger(SiteMapGeneratorIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testCmsSite";

	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private CategoryPageSiteMapGenerator categoryPageSiteMapGenerator;
	@Resource
	private ProductPageSiteMapGenerator productPageSiteMapGenerator;
	@Resource
	private PointOfServicePageSiteMapGenerator pointOfServicePageSiteMapGenerator;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private MediaService mediaService;
	@Resource
	private ModelService modelService;
	@Resource
	private CMSSiteService cmsSiteService;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception
	{
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/acceleratorservices/test/testSiteMapGenerators.impex", "utf-8");
		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		baseSiteService.setCurrentBaseSite(baseSiteForUID, false);

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		assertNotNull(catalogVersionModel);
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));
		Config.setParameter("website.testSite.http", "http://testSite.local:9001/yacceleratorstorefront");
		Config.setParameter("website.testSite.https", "https://testSite.local:9001/yacceleratorstorefront");
	}

	@Test
	public void testCategoryModelSiteMapGeneratorData() throws Exception
	{
		assertNotNull(catalogVersionService.getSessionCatalogVersions());
		final CatalogVersionModel catalogVersion = catalogVersionService.getSessionCatalogVersions().iterator().next();
		assertNotNull(productService.getProductForCode(catalogVersion, "HW1210-3422"));

		final CMSSiteModel currentBaseSite = cmsSiteService.getCurrentSite();
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));

		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode(System.currentTimeMillis() + "");
		media.setCatalogVersion(catalogVersion);
		modelService.save(media);
		mediaService.setStreamForMedia(media, this.getClass().getResourceAsStream("/acceleratorservices/test/sitemap.vm"),
				"sitemap.vm", "text/plain");
		modelService.save(media);

		final RendererTemplateModel templateModel = currentBaseSite.getSiteMapConfig().getSiteMapTemplate();
		templateModel.setCode("siteMapUrlTemplate");
		templateModel.setContextClass("de.hybris.platform.acceleratorservices.sitemap.renderer.SiteMapContext");
		templateModel.setRendererType(RendererTypeEnum.VELOCITY);
		templateModel.setContent(media);
		final List<CategoryModel> data = categoryPageSiteMapGenerator.getData(currentBaseSite);

		final File siteMapXmlEn = categoryPageSiteMapGenerator.render(currentBaseSite, commonI18NService.getCurrency("USD"),
				commonI18NService.getLanguage("en"), templateModel, data, "sitemap-categories", null);
		assertNotNull(siteMapXmlEn);
		LOG.info("created sitmap for english  " + siteMapXmlEn.getAbsolutePath());

		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("de"));

		final File siteMapXmlDE = categoryPageSiteMapGenerator.render(currentBaseSite, commonI18NService.getCurrency("EUR"),
				commonI18NService.getLanguage("de"), templateModel, data, "sitemap-categories", null);
		assertNotNull(siteMapXmlDE);
		LOG.info("created sitmap for german  " + siteMapXmlDE.getAbsolutePath());

	}

	@Test
	public void testProductModelSiteMapGeneratorData() throws Exception
	{
		assertNotNull(catalogVersionService.getSessionCatalogVersions());
		final CatalogVersionModel catalogVersion = catalogVersionService.getSessionCatalogVersions().iterator().next();
		assertNotNull(productService.getProductForCode(catalogVersion, "HW1210-3422"));

		final CMSSiteModel currentBaseSite = cmsSiteService.getCurrentSite();
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));

		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode(System.currentTimeMillis() + "");
		media.setCatalogVersion(catalogVersion);
		modelService.save(media);
		mediaService.setStreamForMedia(media, this.getClass().getResourceAsStream("/acceleratorservices/test/sitemap.vm"),
				"sitemap.vm", "text/plain");
		modelService.save(media);

		final RendererTemplateModel templateModel = currentBaseSite.getSiteMapConfig().getSiteMapTemplate();
		templateModel.setCode("siteMapUrlTemplate");
		templateModel.setContextClass("de.hybris.platform.acceleratorservices.sitemap.renderer.SiteMapContext");
		templateModel.setRendererType(RendererTypeEnum.VELOCITY);
		templateModel.setContent(media);
		final List<ProductModel> data = productPageSiteMapGenerator.getData(currentBaseSite);

		final File siteMapXmlEn = productPageSiteMapGenerator.render(currentBaseSite, commonI18NService.getCurrency("USD"),
				commonI18NService.getLanguage("en"), templateModel, data, "sitemap-products", null);
		assertNotNull(siteMapXmlEn);
		LOG.info("created sitmap for english  " + siteMapXmlEn.getAbsolutePath());

		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("de"));

		final File siteMapXmlDE = productPageSiteMapGenerator.render(currentBaseSite, commonI18NService.getCurrency("EUR"),
				commonI18NService.getLanguage("de"), templateModel, data, "sitemap-products", null);
		assertNotNull(siteMapXmlDE);
		LOG.info("created sitmap for german  " + siteMapXmlDE.getAbsolutePath());

	}

	@Test
	public void testPointOfServiceModelSiteMapGeneratorData() throws Exception
	{
		assertNotNull(catalogVersionService.getSessionCatalogVersions());
		final CatalogVersionModel catalogVersion = catalogVersionService.getSessionCatalogVersions().iterator().next();
		assertNotNull(productService.getProductForCode(catalogVersion, "HW1210-3422"));

		final CMSSiteModel currentBaseSite = cmsSiteService.getCurrentSite();
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));

		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode(System.currentTimeMillis() + "");
		media.setCatalogVersion(catalogVersion);
		modelService.save(media);
		mediaService.setStreamForMedia(media, this.getClass().getResourceAsStream("/acceleratorservices/test/sitemap.vm"),
				"sitemap.vm", "text/plain");
		modelService.save(media);

		final RendererTemplateModel templateModel = currentBaseSite.getSiteMapConfig().getSiteMapTemplate();
		templateModel.setCode("siteMapUrlTemplate");
		templateModel.setContextClass("de.hybris.platform.acceleratorservices.sitemap.renderer.SiteMapContext");
		templateModel.setRendererType(RendererTypeEnum.VELOCITY);
		templateModel.setContent(media);
		final List<PointOfServiceModel> data = pointOfServicePageSiteMapGenerator.getData(currentBaseSite);

		final File siteMapXmlEn = pointOfServicePageSiteMapGenerator.render(currentBaseSite, commonI18NService.getCurrency("USD"),
				commonI18NService.getLanguage("en"), templateModel, data, "sitemap-pointofservice", null);
		assertNotNull(siteMapXmlEn);
		LOG.info("created sitmap for english  " + siteMapXmlEn.getAbsolutePath());

		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("de"));

		final File siteMapXmlDE = pointOfServicePageSiteMapGenerator.render(currentBaseSite, commonI18NService.getCurrency("EUR"),
				commonI18NService.getLanguage("de"), templateModel, data, "sitemap-pointofservice", null);
		assertNotNull(siteMapXmlDE);
		LOG.info("created sitmap for german  " + siteMapXmlDE.getAbsolutePath());

	}
}
