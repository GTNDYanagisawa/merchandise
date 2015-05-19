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
 package de.hybris.platform.lucenesearch.jalo;

import static de.hybris.platform.testframework.Assert.assertCollectionElements;
import static de.hybris.platform.testframework.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.jalo.security.PrincipalGroup;
import de.hybris.platform.jalo.type.AtomicType;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.Employee;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.lucenesearch.constants.LucenesearchConstants;
import de.hybris.platform.lucenesearch.jalo.Facet.FacetValue;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class LucenesearchTest extends HybrisJUnit4TransactionalTest
{
	private LucenesearchManager manager;
	private Product product865, product905;
	private SessionContext ctxDe, ctxEn;
	private Language langEn, langDe;
	private LuceneIndex index;
	private IndexConfiguration indexConfig;

	@Before
	public void setUp() throws Exception
	{
		manager = (LucenesearchManager) jaloSession.getExtensionManager().getExtension(LucenesearchConstants.EXTENSIONNAME);
		assertNotNull(product865 = jaloSession.getProductManager().createProduct("865"));
		assertNotNull(product905 = jaloSession.getProductManager().createProduct("905"));
		assertNotNull(langDe = jaloSession.getC2LManager().createLanguage("test-de"));
		assertNotNull(langEn = jaloSession.getC2LManager().createLanguage("test-en"));
		ctxDe = jaloSession.createSessionContext();
		ctxDe.setLanguage(langDe);
		ctxEn = jaloSession.createSessionContext();
		ctxEn.setLanguage(langEn);
		product865.setName(ctxDe, "Der hoechst universell einsetzbare i865 bietet professionelle Fotoqualitaet und "
				+ "Funktionalitaet bei Geschaeftsanwendungen.");
		product865.setName(ctxEn, "The highly adaptable i865 delivers professional photo quality plus business functionality.");
		product905.setName(ctxDe, "Der unverselle i905D bietet schnellen und einfachen Fotodirektdruck von Speicherkarten "
				+ "oder zu PictBridge und Bubble Jet Direct kompatiblen Digitalkameras, bzw. Camcordern.");
		product905.setName(ctxEn, "The flexible i905D provides fast and easy direct photo printing from memory "
				+ "cards or PictBridge and Bubble Jet Direct compatible digital cameras.");
		assertNotNull(index = manager.createLuceneIndex("LucenesearchTest"));
		index.createLanguageConfiguration(langDe);
		index.createLanguageConfiguration(langEn);
		final ComposedType productType = jaloSession.getTypeManager().getComposedType(Product.class);
		indexConfig = index.createIndexConfiguration(productType,
				list(productType.getAttributeDescriptor(Product.CODE), productType.getAttributeDescriptor(Product.NAME)));
	}

	@Test
	public void testSetUp()
	{
		assertEquals(jaloSession.getTypeManager().getComposedType(Product.class), indexConfig.getIndexedType());
		assertCollectionElements(index.getLanguages(), langDe, langEn);
	}

	@Test
	public void testReindexAll() throws ParseException
	{
		manager.rebuildAllIndexes();
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865", 0, -1));
		product865.setCode("865x");
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865", 0, -1));
		assertSearchResult(list(), index.searchItems(ctxDe, "865x", 0, -1));
		manager.updateAllIndexesForItem(product865);
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865x", 0, -1));
		assertSearchResult(list(), index.searchItems(ctxDe, "\"865\"", 0, -1)); //searchpattern: "865" and not 865
		product865.setCode("865y");
		manager.rebuildAllIndexes();
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865y", 0, -1));
		assertSearchResult(list(), index.searchItems(ctxDe, "865x", 0, -1));
	}

	@Test
	public void testReindexSingle() throws ParseException
	{
		index.rebuildIndex();
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865", 0, -1));
		product865.setCode("865x");
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxDe, "865x", 0, -1));
		index.updateIndexForItem(product865);
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865x", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxDe, "\"865\"", 0, -1));
		product865.setCode("865y");
		index.rebuildIndex();
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865y", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxDe, "865x", 0, -1));
	}

	@Test
	public void testSearch() throws ParseException
	{
		index.rebuildIndex();
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865", 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxEn, "865", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxDe, "905", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxEn, "905", 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxDe, "einsetzbar~", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxEn, "einsetzbar~", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxDe, "provides~", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxEn, "provides~", 0, -1));
		//TODO: without " it does not work, no search result, WHY?
		assertSearchResult(list(product905), index.searchItems(ctxDe, "PictBridge", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxEn, "PictBridge", 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxDe, "Fotoqualitaet", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxEn, "Fotoqualitaet", 0, -1));
		assertSearchResult(list(product865, product905), index.searchItems(ctxDe, "\"bietet\"", 0, -1));
		assertSearchResult(list(product865, product905), index.searchItems(ctxEn, "\"photo\"", 0, -1));

		assertSearchResult(list(product865), index.searchItems(ctxDe, "Geschaeftsanwendungen", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxEn, "Geschaeftsanwendungen", 0, -1));
	}

	//PLA-9186
	@Test
	public void testSearchWithWhiteSpace() throws ParseException
	{
		index.rebuildIndex();

		assertSearchResult(list(product865), index.searchItems(ctxDe, "\"i865 bietet professionelle\"", 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxEn, "\"i865 delivers professional\"", 0, -1));
	}

	@Test
	public void testHybrisQueryParser()
	{
		index.rebuildIndex();
		// test Hybris query parser
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxDe, "anwendung", 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxDe, "*anwendung*", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxEn, "*anwendung*", 0, -1));
	}

	@Test
	public void testGrouping() throws ParseException
	{
		final Unit unitA = jaloSession.getProductManager().createUnit("-", "test-a");
		assertNotNull(unitA);
		final Unit unitB = jaloSession.getProductManager().createUnit("-", "test-b");
		assertNotNull(unitB);
		product865.setUnit(unitA);
		product905.setUnit(unitB);
		final ComposedType productType = jaloSession.getTypeManager().getComposedType(Product.class);
		indexConfig.setGroupingAttribute(productType.getAttributeDescriptor(Product.UNIT));
		index.rebuildIndex();
		assertSearchResult(list(product865, product905), index.searchItems(ctxEn, "photo", list(unitA, unitB), 0, -1));
		assertSearchResult(list(product865, product905), index.searchItems(ctxEn, "photo", 0, -1));
		assertSearchResult(list(product865, product905), index.searchItems(ctxEn, "photo", null, 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxEn, "photo", list(unitA), 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxEn, "photo", list(unitB), 0, -1));
	}

	@Test
	public void testActivation() throws JaloBusinessException, ParseException
	{
		final String QUALIFIER = "testactivation";
		final AtomicType atomicBoolean = jaloSession.getTypeManager().getRootAtomicType(Boolean.class);
		final AttributeDescriptor descr = product865.getComposedType().createAttributeDescriptor(
				QUALIFIER,
				atomicBoolean,
				AttributeDescriptor.SEARCH_FLAG | AttributeDescriptor.WRITE_FLAG | AttributeDescriptor.READ_FLAG
						| AttributeDescriptor.REMOVE_FLAG);
		assertNotNull(descr);
		product905.setAttribute(QUALIFIER, Boolean.FALSE);
		product865.setAttribute(QUALIFIER, Boolean.TRUE);
		assertEquals(Boolean.TRUE, product865.getAttribute(QUALIFIER));
		assertEquals(Boolean.FALSE, product905.getAttribute(QUALIFIER));
		indexConfig.setActivationAttribute(descr);
		index.rebuildIndex();
		assertSearchResult(list(product865), index.searchItems(ctxEn, "photo", 0, -1));
		product905.setAttribute(QUALIFIER, Boolean.TRUE);
		index.rebuildIndex();
		assertSearchResult(list(product865, product905), index.searchItems(ctxEn, "photo", 0, -1));
		product905.setAttribute(QUALIFIER, Boolean.FALSE);
		index.updateIndexForItem(product905);
		assertSearchResult(list(product865), index.searchItems(ctxEn, "photo", 0, -1));
	}

	@Test
	public void testRange() throws ParseException
	{
		index.rebuildIndex();
		SearchResult searchResult = index.searchItems(ctxEn, "photo", null, 0, 1);
		assertEquals(1, searchResult.getCount());
		assertEquals(1, searchResult.getRequestedCount());
		assertEquals(0, searchResult.getRequestedStart());
		assertEquals(2, searchResult.getTotalCount());
		assertEquals(list(product865), searchResult.getResult());
		searchResult = index.searchItems(ctxEn, "photo", null, 1, 2);
		assertEquals(1, searchResult.getCount());
		assertEquals(2, searchResult.getRequestedCount());
		assertEquals(1, searchResult.getRequestedStart());
		assertEquals(2, searchResult.getTotalCount());
		assertEquals(list(product905), searchResult.getResult());
	}

	@Test
	public void testBoost() throws ParseException
	{
		product905.setName(ctxEn, product905.getName(ctxEn) + " ... also see 865");

		getAttributeConfiguration(indexConfig, Product.CODE).setWeight(2.0);
		index.rebuildIndex();
		assertSearchResult(list(product865, product905), index.searchItems(ctxEn, "name:865 code:865", 0, -1));

		getAttributeConfiguration(indexConfig, Product.NAME).setWeight(3000.0);
		index.rebuildIndex();
		assertSearchResult(list(product905, product865), index.searchItems(ctxEn, "name:865 code:865", 0, -1));
	}

	@Test
	public void testDataFactoryParams()
	{
		index.rebuildIndex();
		/*
		 * search normally
		 */
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865", 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxEn, "865", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxDe, "905", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxEn, "905", 0, -1));
		/*
		 * now alter indexing query
		 */
		indexConfig.setIndexedDataParams("{" + Product.CODE + "} LIKE '9%'");
		assertEquals("{" + Product.CODE + "} LIKE '9%'", indexConfig.getIndexedDataParams());
		index.rebuildIndex();
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxDe, "865", 0, -1));
		assertSearchResult(Collections.EMPTY_LIST, index.searchItems(ctxEn, "865", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxDe, "905", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxEn, "905", 0, -1));

		/*
		 * back to normal again
		 */
		indexConfig.setIndexedDataParams(null);
		assertNull(indexConfig.getIndexedDataParams());
		index.rebuildIndex();
		assertSearchResult(list(product865), index.searchItems(ctxDe, "865", 0, -1));
		assertSearchResult(list(product865), index.searchItems(ctxEn, "865", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxDe, "905", 0, -1));
		assertSearchResult(list(product905), index.searchItems(ctxEn, "905", 0, -1));
	}

	private static void assertSearchResult(final List items, final SearchResult actual)
	{
		final String msg = "expected " + items + " but got " + actual;
		assertEquals(msg, 0, actual.getRequestedStart());
		assertEquals(msg, items.size(), actual.getTotalCount());
		assertEquals(msg, items.size(), actual.getCount());
		assertEquals(msg, items, actual.getResult());
	}

	private AttributeConfiguration getAttributeConfiguration(final IndexConfiguration cfg, final String qualifier)
	{
		for (final AttributeConfiguration acfg : cfg.getAttributeConfigurations())
		{
			if (qualifier.equalsIgnoreCase(acfg.getIndexedAttribute().getQualifier()))
			{
				return acfg;
			}
		}
		return null;
	}

	@Test
	public void testFacetSearch() throws ConsistencyCheckException
	{
		final UserManager userManager = UserManager.getInstance();
		Employee employee1, employee2, employee3;
		Customer customer1, customer2;
		UserGroup usergroup1;
		assertNotNull(employee1 = userManager.createEmployee("hy001"));
		assertNotNull(employee2 = userManager.createEmployee("hy002"));
		assertNotNull(employee3 = userManager.createEmployee("hy003"));
		assertNotNull(customer1 = userManager.createCustomer("cy101"));
		assertNotNull(customer2 = userManager.createCustomer("cy102"));
		assertNotNull(usergroup1 = userManager.createUserGroup("hy111"));
		employee1.addToGroup(usergroup1);
		employee2.addToGroup(usergroup1);
		employee3.addToGroup(usergroup1);

		employee1.setName("Thomas Hertz");
		employee2.setName("Andreas Bucksteeg");
		employee3.setName("Axel Gro\u00DFmann");
		customer1.setName("Bernd das Brot");
		customer2.setName("Au Schwarte");
		usergroup1.setName("hybris");

		LuceneIndex idx;
		assertNotNull(idx = manager.createLuceneIndex("FacetIdx"));
		idx.createLanguageConfiguration(langDe);

		final ComposedType principalType = jaloSession.getTypeManager().getComposedType(Principal.class);
		final ComposedType principalGroupType = jaloSession.getTypeManager().getComposedType(PrincipalGroup.class);
		final ComposedType userGroupType = jaloSession.getTypeManager().getComposedType(UserGroup.class);
		final ComposedType userType = jaloSession.getTypeManager().getComposedType(User.class);
		final ComposedType employeeType = jaloSession.getTypeManager().getComposedType(Employee.class);
		final ComposedType customerType = jaloSession.getTypeManager().getComposedType(Customer.class);

		idx.createIndexConfiguration(principalType,
				list(principalType.getAttributeDescriptor(Principal.UID), principalType.getAttributeDescriptor(Principal.NAME)));
		idx.rebuildIndex();

		assertTrue(idx.isUpToDateAsPrimitive());

		LuceneSearchResult searchResult = idx.searchItems(ctxDe, "uid:hy*", 0, -1);

		assertEquals(4, searchResult.getTotalCount());
		assertEquals(new HashSet(Arrays.asList(employee1, employee2, employee3, usergroup1)), new HashSet(searchResult.getResult()));

		final Facet nameFacet = new Facet("f1");
		nameFacet.addValue("thomas", "+name:Thomas");
		nameFacet.addValue("hertz", "+name:Hertz");
		nameFacet.addValue("axel", "+name:Axel");
		nameFacet.addValue("grossmann", "+name:Gro\u00DFmann");
		nameFacet.addValue("aaa", "+name:A*");
		nameFacet.addValue("bernd", "+name:Bernd");
		nameFacet.addValue("brot", "+name:Brot");
		nameFacet.addValue("bbb", "+name:B*");

		final Facet typeFacet = new Facet("f2");
		typeFacet.addValue("pcpl", "+type:" + principalType.getCode());
		typeFacet.addValue("pcplgrp", "+type:" + principalGroupType.getCode());
		typeFacet.addValue("grp", "+type:" + userGroupType.getCode());
		typeFacet.addValue("usr", "+type:" + userType.getCode());
		typeFacet.addValue("empl", "+type:" + employeeType.getCode());
		typeFacet.addValue("cust", "+type:" + customerType.getCode());
		typeFacet.addValue("employeesAndGroups", "+(type:" + userGroupType.getCode() + " type:" + employeeType.getCode() + ")");

		final Set<Facet> facets = new LinkedHashSet<Facet>(Arrays.asList(nameFacet, typeFacet));

		searchResult = idx.searchItems(ctxDe, "uid:hy*", null, null, null, facets, 0, -1);

		assertEquals(4, searchResult.getTotalCount());
		assertEquals(new HashSet(Arrays.asList(employee1, employee2, employee3, usergroup1)), new HashSet(searchResult.getResult()));

		final Set<Facet> resultFacets = searchResult.getFacets();
		assertEquals(facets.size(), resultFacets.size());

		final Facet resNameFacet = searchResult.getFacet(nameFacet.getCode());
		assertEquals(nameFacet.getValues().size(), resNameFacet.getValues().size());
		assertEquals(3, resNameFacet.getAllHitsCount()); // -> axel + thomas + andreas

		checkFacetValue(resNameFacet, "thomas", 1); // -> thomas hertz
		checkFacetValue(resNameFacet, "hertz", 1); // -> thomas hertz
		checkFacetValue(resNameFacet, "axel", 1); // -> axel grossmann
		checkFacetValue(resNameFacet, "grossmann", 1);// -> axel grossmann
		checkFacetValue(resNameFacet, "aaa", 2); // -> axel grossmann + andreas bucksteeg
		checkFacetValue(resNameFacet, "bernd", 0); // nope
		checkFacetValue(resNameFacet, "brot", 0); // nope
		checkFacetValue(resNameFacet, "bbb", 1); // -> andreas bucksteeg

		Facet resTypeFacet = searchResult.getFacet(typeFacet.getCode());
		assertEquals(typeFacet.getValues().size(), resTypeFacet.getValues().size());

		assertEquals(4, resTypeFacet.getAllHitsCount());

		checkFacetValue(resTypeFacet, "pcpl", 4);
		checkFacetValue(resTypeFacet, "pcplgrp", 1);
		checkFacetValue(resTypeFacet, "grp", 1);
		checkFacetValue(resTypeFacet, "usr", 3);
		checkFacetValue(resTypeFacet, "empl", 3);
		checkFacetValue(resTypeFacet, "cust", 0);
		checkFacetValue(resTypeFacet, "employeesAndGroups", 4);

		// test limiting the search result by facet value -> limit types to Customer + Employee

		final Facet typeFilter = new Facet("f3");
		typeFilter.addValue("pcplOverview", "+type:" + principalType.getCode()); // just to get all principals count
		final FacetValue filter1 = typeFilter.addValue("emplOnly", "+type:" + employeeType.getCode());
		filter1.setUseAsFilter(true);
		final FacetValue filter2 = typeFilter.addValue("custOnly", "+type:" + customerType.getCode());
		filter2.setUseAsFilter(true);

		searchResult = idx.searchItems(ctxDe, "name:axel name:bernd name:hybris", null, null, null, Collections.singleton(typeFilter), 0, -1);

		assertEquals(2, searchResult.getTotalCount());
		assertEquals(new HashSet(Arrays.asList(employee3, customer1)), new HashSet(searchResult.getResult()));

		resTypeFacet = searchResult.getFacet(typeFilter.getCode());
		assertEquals(2, resTypeFacet.getAllHitsCount());
		checkFacetValue(resTypeFacet, "pcplOverview", 2);
		checkFacetValue(resTypeFacet, "emplOnly", 1);
		checkFacetValue(resTypeFacet, "custOnly", 1);
	}

	@Test
	public void testDBParameterLimitProblem()
	{
		// tests both oracle (1000) and SQL server (2000) parameter limit

		final ProductManager productManager = ProductManager.getInstance();
		final Collection<Item> items = new ArrayList<Item>(2200);
		for (int i = 0; i < 2200; i++)
		{
			items.add(productManager.createProduct("p<" + i + ">"));
		}

		try
		{
			index.updateIndexForItem(items);
		}
		catch (final Exception e)
		{
			e.printStackTrace(System.err);
			fail(e.getMessage());
		}
	}

	/**
	 * platform PLA-10264
	 * 
	 * @throws ConsistencyCheckException
	 */
	@Test
	public void testLanguageConfigurationOverflow() throws ConsistencyCheckException
	{

		final String languageTemplate = "overFlowTestLanguage-%s";

		final SessionContext ctx = jaloSession.createSessionContext();

		//set some srtange langugae configuration , update also a product with some localized property specific value
		for (int i = 0; i < 72; i++)
		{
			final Language lang = C2LManager.getInstance().createLanguage(String.format(languageTemplate, Integer.valueOf(i)));
			index.createLanguageConfiguration(lang);
			ctx.setLanguage(lang);
			product865.setDescription(ctx, String.format(languageTemplate, Integer.valueOf(i)));
		}
		final ComposedType productType = jaloSession.getTypeManager().getComposedType(Product.class);

		indexConfig = index.createIndexConfiguration(productType,
				list(productType.getAttributeDescriptor(Product.CODE), productType.getAttributeDescriptor(Product.DESCRIPTION)));

		try
		{
			index.rebuildIndex();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			Assert.fail("Should be able to rebuild index " + index.getCode() + ", " + e.getMessage());

		}
		//always force rebuild index
		index.setRebuildStartTimestamp(new Date(0));
		try
		{

			indexConfig.isUpToDate();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			Assert.fail("Should be able to check if index is up todate " + index.getCode() + ", " + e.getMessage());

		}
		//verify the search in strange languages for a description
		for (int i = 0; i < 72; i++)
		{
			final Language indexedLanguage = C2LManager.getInstance().getLanguageByIsoCode("overFlowTestLanguage-" + i);
			ctx.setLanguage(indexedLanguage);
			assertSearchResult(list(product865), index.searchItems(ctx, "overFlowTestLanguage-" + i, 0, -1));
		}


	}

	protected void checkFacetValue(final Facet facet, final String code, final int count)
	{
		assertNotNull(facet);
		final FacetValue facetValue = facet.getValue(code);
		assertNotNull(facetValue);
		assertEquals(count, facetValue.getHitCount());
	}

}
