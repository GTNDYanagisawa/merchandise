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
package de.hybris.platform.solrfacetsearch.config;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttribute;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeValue;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystem;
import de.hybris.platform.catalog.jalo.classification.ClassificationSystemVersion;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.solrfacetsearch.config.impl.FacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.integration.AbstractSolrTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.testframework.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;


@IntegrationTest
@Transactional
public class FacetSearchConfigServiceTest extends AbstractSolrTest
{

	@Resource
	private ProductService productService;
	@Resource
	private FacetSearchConfigDao facetSearchConfigDao;
	@Resource
	protected MediaService mediaService;

	private ClassificationSystem system;
	private ClassificationSystemVersion version;
	private ClassificationClass clTest;
	private ClassificationAttribute attrColor, attrDescription, attrSpeed;
	private ClassificationAttributeValue vRed, vGreen;
	private ClassificationAttributeUnit speedUnit;

	private Language de, en;
	private SessionContext deCtx, enCtx;

	private Product product0, product1;

	private CatalogVersionModel catalogVersion;


	@Override
	public void setUp() throws Exception
	{
		setUpBasic();
		setUpProductData();
		localConfig = setUpSolrFacetSearchConfig();
		modelService.save(localConfig);
	}

	@Override
	protected void setUpBasic() throws Exception
	{
		createCoreData();
	}

	@Override
	protected void setUpProductData() throws Exception
	{
		importCsv("/test/testFacetSearchConfig.csv", "windows-1252");
		setUpClassificationSystem();

		catalogVersion = catalogVersionService.getCatalogVersion(TESTCATALOG1, WINTER);
	}


	/**
	 * This test examines proper operation of FacetSearchConfigDao.
	 */

	@Test
	public void testFacetSearchConfigDao()
	{

		final SolrFacetSearchConfigModel result = facetSearchConfigDao.findSolrFacetSearchConfigByName(localConfig.getName());
		Assert.assertEquals(localConfig, result);
		final Collection<SolrFacetSearchConfigModel> results = facetSearchConfigDao
				.findSolrFacetSearchConfigByCatalogVersion(catalogVersion);
		Assert.assertTrue(results.contains(modelService.getSource(localConfig)));
	}

	@Test
	public void testFacetSearchConfigService() throws Exception
	{
		final FacetSearchConfig config = facetSearchConfigService.getConfiguration(localConfig.getName());
		assertNotNull("Could not get config from facetSearchConfigService", config);
		assertNotNull("Could not get indexConfig from FacetSearchConfig", config.getIndexConfig());
	}


	@Override
	protected List<CatalogVersionModel> getCatalogVersionsForSolrFacetSearchConfig()
	{

		return Collections.singletonList(catalogVersion);
	}


	@Override
	protected List<CurrencyModel> setUpCurrencies()
	{
		final List<CurrencyModel> currencies = new ArrayList<CurrencyModel>();
		currencies.add(commonI18NService.getCurrency("EUR"));
		currencies.add(commonI18NService.getCurrency("GBP"));
		currencies.add(commonI18NService.getCurrency("USD"));
		return currencies;
	}



	protected void setUpClassificationSystem() throws Exception
	{

		final EnumerationValue T_ENUM = EnumerationManager.getInstance().getEnumerationValue(
				CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.ENUM);

		final EnumerationValue T_STRING = EnumerationManager.getInstance().getEnumerationValue(
				CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.STRING);

		final EnumerationValue T_NUMBER = EnumerationManager.getInstance().getEnumerationValue(
				CatalogConstants.TC.CLASSIFICATIONATTRIBUTETYPEENUM,
				CatalogConstants.Enumerations.ClassificationAttributeTypeEnum.NUMBER);

		en = getOrCreateLanguage("en");
		de = getOrCreateLanguage("de");

		deCtx = jaloSession.createSessionContext();
		deCtx.setLanguage(de);
		enCtx = jaloSession.createSessionContext();
		enCtx.setLanguage(en);

		system = CatalogManager.getInstance().createClassificationSystem("System");
		version = system.createSystemVersion("version_en", en);
		clTest = version.createClass("clTest");

		attrColor = version.createClassificationAttribute("color");
		attrColor.setName("color");
		attrDescription = version.createClassificationAttribute("description");
		attrDescription.setName("description");
		attrSpeed = version.createClassificationAttribute("speed");
		attrSpeed.setName("speed");

		speedUnit = version.createAttributeUnit("kmph", "km/h");



		vRed = version.createClassificationAttributeValue("RED");
		vRed.setName(deCtx, "rot");
		vRed.setName(enCtx, "red");

		vGreen = version.createClassificationAttributeValue("GREEN");
		vGreen.setName(deCtx, "gruen");
		vGreen.setName(enCtx, "green");

		clTest.assignAttribute(attrColor, T_ENUM, null, // no unit
				Arrays.asList(new Object[]
				{ vGreen, vRed }), 0 // position
		);

		clTest.assignAttribute(attrDescription, T_STRING, null, null, 1);

		clTest.assignAttribute(attrSpeed, T_NUMBER, speedUnit, null, 2);

		clTest.setLocalized(attrDescription, true);
		clTest.setLocalized(attrColor, true);

		product0 = (Product) modelService.getSource(productService.getProduct("testProduct0"));
		product1 = (Product) modelService.getSource(productService.getProduct("testProduct1"));

		clTest.addProduct(product0);
		clTest.addProduct(product1);

		FeatureContainer cont = FeatureContainer.load(product0);
		cont.getFeature(attrColor).createValue(enCtx, vRed);
		cont.getFeature(attrColor).createValue(deCtx, vRed);
		cont.getFeature(attrDescription).createValue(enCtx, "description of super product0");
		cont.getFeature(attrDescription).createValue(deCtx, "eins zwei drei");
		cont.getFeature(attrSpeed).createValue(new Double(125.56));
		cont.store();
		cont = FeatureContainer.load(product1);
		cont.getFeature(attrColor).createValue(enCtx, vGreen);
		cont.getFeature(attrColor).createValue(deCtx, vGreen);
		cont.getFeature(attrDescription).createValue(enCtx, "description of super product1");
		cont.getFeature(attrDescription).createValue(deCtx, "funf, sechs, sieben");
		cont.getFeature(attrSpeed).createValue(new Double(15.6));
		cont.store();
	}

	private static final String TESTCATALOG1 = "testCatalog1";
	private static final String WINTER = "Winter";

	@Test
	public void testConfigSerializable() throws Exception
	{
		final FacetSearchConfig configIn = facetSearchConfigService.getConfiguration(localConfig.getName());
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(configIn);
		out.close();

		final ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		final FacetSearchConfig configOut = (FacetSearchConfig) in.readObject();
		in.close();
		assertNotNull(configOut);
	}
}
