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
package de.hybris.platform.cms2.servicelayer.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.RestrictionEvaluationException;
import de.hybris.platform.cms2.model.restrictions.CMSCatalogRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSInverseRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author karol.walczak
 */
@IntegrationTest
public class CMSRestrictionServiceTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(CMSRestrictionServiceTest.class);

	private CMSTimeRestrictionModel mockTimeRestriction;
	private CMSUserRestrictionModel mockUserRestriction;
	private CMSUserGroupRestrictionModel mockUserGroupRestriction;
	private CMSCatalogRestrictionModel mockCatalogRestriction;
	private CMSCategoryRestrictionModel mockCategoryRestriction;
	private CMSProductRestrictionModel mockProductRestriction;
	private CMSInverseRestrictionModel mockInversRestriction;
	private RestrictionData mockRestrictionData;

	protected UserModel userA;
	protected UserModel userB;
	protected CatalogModel catalog;
	protected CategoryModel categoryA;
	protected CategoryModel categoryB;
	protected ProductModel productA;
	protected ProductModel productB;

	protected final static String USER_A = "userA";
	protected final static String USER_B = "userB";
	protected final static String CATALOG_ID = "sampleCatalog";
	protected final static String CATEGORY_A = "sampleCategoryA";
	protected final static String CATEGORY_B = "sampleCategoryB";
	protected final static String PRODUCT_A = "productA";
	protected final static String PRODUCT_B = "productB";

	@Resource
	private CMSRestrictionService cmsRestrictionService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogService catalogService;
	@Resource
	private ProductService productService;
	@Resource
	private CategoryService categoryService;

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating btg test data ..");
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/test/cms2TestData.csv", "windows-1252");
		LOG.info("Finished creating btg test data " + (System.currentTimeMillis() - startTime) + "ms");

		userA = userService.getUser(USER_A);
		userB = userService.getUser(USER_B);

		catalog = catalogService.getCatalog(CATALOG_ID);
		categoryA = categoryService.getCategory(CATEGORY_A);
		categoryB = categoryService.getCategory(CATEGORY_B);
		productA = productService.getProduct(PRODUCT_A);
		productB = productService.getProduct(PRODUCT_B);

		mockRestrictionData = createMock(RestrictionData.class);
	}

	@Test
	public void testEvaluateTimeRestriction2True()
	{
		boolean ret = false;
		prepareTimeRestrictionMocks(-1, 1);
		try
		{
			ret = cmsRestrictionService.evaluate(mockTimeRestriction, null);

		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of time restriction has failed!.");
		}
		EasyMock.verify(mockTimeRestriction);
		assertTrue("Time restriction evaluate correctly.", ret);
	}

	@Test
	public void testEvaluateTimeRestriction2False()
	{
		boolean ret = false;

		prepareTimeRestrictionMocks(1, 2);
		try
		{
			ret = cmsRestrictionService.evaluate(mockTimeRestriction, null);

		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of time restriction has failed!.");
		}
		EasyMock.verify(mockTimeRestriction);
		assertFalse("Time restriction evaluate correctly", ret);
	}

	@Test
	public void testEvaluateUserRestriction2False()
	{
		boolean ret = false;

		prepareUserRestrictionMocks(userA);
		try
		{
			userService.setCurrentUser(userB);
			ret = cmsRestrictionService.evaluate(mockUserRestriction, null);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of user restriction has failed!.");
		}
		EasyMock.verify(mockUserRestriction);
		assertFalse("User restriction evaluate correctly", ret);
	}

	@Test
	public void testEvaluateUserRestriction2True()
	{
		boolean ret = false;

		prepareUserRestrictionMocks(userA);
		try
		{
			userService.setCurrentUser(userA);
			ret = cmsRestrictionService.evaluate(mockUserRestriction, null);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of user restriction has failed!.");
		}
		EasyMock.verify(mockUserRestriction);
		assertTrue("User restriction evaluate correctly", ret);
	}

	@Test
	public void testEvaluateUserGroupRestriction2True()
	{
		boolean ret = false;
		prepareUserGroupRestrictionMocks((UserGroupModel) userA.getGroups().iterator().next());
		try
		{
			userService.setCurrentUser(userA);
			ret = cmsRestrictionService.evaluate(mockUserGroupRestriction, null);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of user group restriction has failed!.");
		}
		EasyMock.verify(mockUserGroupRestriction);
		assertTrue("User group restriction evaluate not correctly (should be true)", ret);
	}

	@Test
	public void testEvaluateUserGroupRestriction2False()
	{
		boolean ret = false;
		prepareUserGroupRestrictionMocks((UserGroupModel) userA.getGroups().iterator().next());
		try
		{
			userService.setCurrentUser(userB);
			ret = cmsRestrictionService.evaluate(mockUserGroupRestriction, null);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of user group restriction has failed!.");
		}
		EasyMock.verify(mockUserGroupRestriction);
		assertFalse("User group restriction evaluate not correctly (should be false)", ret);
	}

	@Test
	public void testEvaluateCatalogRestriction2False()
	{
		boolean ret = false;
		prepareCatalogGroupRestrictionMocks(null);
		try
		{
			reset(mockRestrictionData);
			expect(mockRestrictionData.getCatalog()).andReturn(catalog);
			expect(Boolean.valueOf(mockRestrictionData.hasProduct())).andReturn(Boolean.FALSE);
			expect(Boolean.valueOf(mockRestrictionData.hasCategory())).andReturn(Boolean.FALSE);
			expect(Boolean.valueOf(mockRestrictionData.hasCatalog())).andReturn(Boolean.TRUE);
			replay(mockRestrictionData);
			ret = cmsRestrictionService.evaluate(mockCatalogRestriction, mockRestrictionData);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of catalog restriction has failed!.");
		}
		EasyMock.verify(mockCatalogRestriction);
		assertFalse("Catalog restriction evaluate not correctly (should be false)", ret);
	}

	@Test
	public void testEvaluateCatalogRestriction2True()
	{
		boolean ret = false;
		prepareCatalogGroupRestrictionMocks(catalog);
		try
		{
			reset(mockRestrictionData);
			expect(mockRestrictionData.getCatalog()).andReturn(catalog);
			expect(Boolean.valueOf(mockRestrictionData.hasProduct())).andReturn(Boolean.FALSE);
			expect(Boolean.valueOf(mockRestrictionData.hasCategory())).andReturn(Boolean.FALSE);
			expect(Boolean.valueOf(mockRestrictionData.hasCatalog())).andReturn(Boolean.TRUE);
			replay(mockRestrictionData);
			ret = cmsRestrictionService.evaluate(mockCatalogRestriction, mockRestrictionData);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of catalog restriction has failed!.");
		}
		EasyMock.verify(mockCatalogRestriction);
		assertTrue("Catalog restriction evaluate not correctly (should be true)", ret);
	}

	@Test
	public void testEvaluateCategoryRestriction2True()
	{
		boolean ret = false;
		prepareCategoryRestrictionMocks(categoryA);

		try
		{
			reset(mockRestrictionData);
			expect(mockRestrictionData.getCategory()).andReturn(categoryA);
			expect(Boolean.valueOf(mockRestrictionData.hasProduct())).andReturn(Boolean.FALSE);
			expect(Boolean.valueOf(mockRestrictionData.hasCategory())).andReturn(Boolean.TRUE);
			replay(mockRestrictionData);
			ret = cmsRestrictionService.evaluate(mockCategoryRestriction, mockRestrictionData);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of category restriction has failed!.");
		}
		EasyMock.verify(mockCategoryRestriction);
		assertTrue("Category restriction evaluate not correctly (should ne true)", ret);
	}

	@Test
	public void testEvaluateCategoryRestriction2False()
	{
		boolean ret = false;
		prepareCategoryRestrictionMocks(categoryA);

		try
		{
			reset(mockRestrictionData);
			expect(mockRestrictionData.getCategory()).andReturn(categoryB);
			expect(Boolean.valueOf(mockRestrictionData.hasProduct())).andReturn(Boolean.FALSE);
			expect(Boolean.valueOf(mockRestrictionData.hasCategory())).andReturn(Boolean.TRUE);
			replay(mockRestrictionData);
			ret = cmsRestrictionService.evaluate(mockCategoryRestriction, mockRestrictionData);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of category restriction has failed!.");
		}
		EasyMock.verify(mockCategoryRestriction);
		assertFalse("Category restriction evaluate not correctly (should be false)", ret);
	}

	@Test
	public void testEvaluateProductRestriction2True()
	{
		boolean ret = false;
		prepareProductRestrictionMocks(productA);

		try
		{
			reset(mockRestrictionData);
			expect(mockRestrictionData.getProduct()).andReturn(productA);
			expect(Boolean.valueOf(mockRestrictionData.hasProduct())).andReturn(Boolean.TRUE);
			replay(mockRestrictionData);
			ret = cmsRestrictionService.evaluate(mockProductRestriction, mockRestrictionData);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of product restriction has failed!.");
		}
		EasyMock.verify(mockProductRestriction);
		assertTrue("Product restriction evaluate not correctly (should be true)", ret);
	}

	@Test
	public void testEvaluateProductRestriction2False()
	{
		boolean ret = false;
		prepareProductRestrictionMocks(productA);

		try
		{
			reset(mockRestrictionData);
			expect(mockRestrictionData.getProduct()).andReturn(productB);
			expect(Boolean.valueOf(mockRestrictionData.hasProduct())).andReturn(Boolean.TRUE);
			replay(mockRestrictionData);
			ret = cmsRestrictionService.evaluate(mockProductRestriction, mockRestrictionData);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of product restriction has failed!.");
		}
		EasyMock.verify(mockProductRestriction);
		assertFalse("Product restriction evaluate not correctly (should be false)", ret);
	}

	@Test
	public void testEvaluateInverseTimeRestriction2False()
	{
		boolean ret = false;
		prepareInverseTimeRestrictionMocks(-1, 1);

		try
		{
			ret = cmsRestrictionService.evaluate(mockInversRestriction, null);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of inverse restriction has failed!.");
		}
		EasyMock.verify(mockInversRestriction);
		assertFalse("Inverse restriction evaluate not correctly (should be false)", ret);
	}

	@Test
	public void testEvaluateInverseTimeRestriction2True()
	{
		boolean ret = false;
		prepareInverseTimeRestrictionMocks(1, 2);

		try
		{
			ret = cmsRestrictionService.evaluate(mockInversRestriction, null);
		}
		catch (final RestrictionEvaluationException e)
		{
			Assert.fail("Evaluation of inverse restriction has failed!.");
		}
		EasyMock.verify(mockInversRestriction);
		assertTrue("Inverse restriction evaluate not correctly (should be true)", ret);
	}

	/**
	 * Prepare time restriction accordingly to given parameters.
	 * 
	 * @param before
	 *           indicates how many days before now we will set as activeFrom
	 * @param after
	 *           indicates how many days after now will set as activeTo
	 */
	private void prepareTimeRestrictionMocks(final int before, final int after)
	{
		mockTimeRestriction = EasyMock.createMock(CMSTimeRestrictionModel.class);
		EasyMock.expect(mockTimeRestriction.getActiveFrom()).andReturn(DateUtils.addDays(new Date(), before));
		EasyMock.expect(mockTimeRestriction.getActiveUntil()).andReturn(DateUtils.addDays(new Date(), after));
		EasyMock.expect(mockTimeRestriction.getItemtype()).andReturn(Cms2Constants.TC.CMSTIMERESTRICTION);
		EasyMock.replay(mockTimeRestriction);
	}

	/**
	 * Prepare user accordingly to given parameters.
	 * 
	 * @param user
	 *           given user
	 */
	private void prepareUserRestrictionMocks(final UserModel user)
	{
		mockUserRestriction = EasyMock.createMock(CMSUserRestrictionModel.class);
		EasyMock.expect(mockUserRestriction.getUsers()).andReturn(Collections.singletonList(user));
		EasyMock.expect(mockUserRestriction.getItemtype()).andReturn(Cms2Constants.TC.CMSUSERRESTRICTION);
		EasyMock.replay(mockUserRestriction);
	}

	/**
	 * Prepare user group restriction accordingly to given parameters.
	 * 
	 * @param userGroup
	 *           given user group
	 */
	private void prepareUserGroupRestrictionMocks(final UserGroupModel userGroup)
	{
		mockUserGroupRestriction = EasyMock.createMock(CMSUserGroupRestrictionModel.class);
		EasyMock.expect(mockUserGroupRestriction.getUserGroups()).andReturn(Collections.singletonList(userGroup));
		EasyMock.expect(mockUserGroupRestriction.getItemtype()).andReturn(Cms2Constants.TC.CMSUSERGROUPRESTRICTION);
		EasyMock.expect(Boolean.valueOf(mockUserGroupRestriction.isIncludeSubgroups())).andReturn(Boolean.FALSE);
		EasyMock.replay(mockUserGroupRestriction);
	}

	/**
	 * Prepare catalog restriction accordingly to given parameters.
	 * 
	 * @param catalog
	 *           given catalog
	 */
	private void prepareCatalogGroupRestrictionMocks(final CatalogModel catalog)
	{
		mockCatalogRestriction = EasyMock.createMock(CMSCatalogRestrictionModel.class);
		EasyMock.expect(mockCatalogRestriction.getCatalogs()).andReturn(Collections.singletonList(catalog));
		EasyMock.expect(mockCatalogRestriction.getItemtype()).andReturn(Cms2Constants.TC.CMSCATALOGRESTRICTION);
		EasyMock.replay(mockCatalogRestriction);
	}

	/**
	 * Prepare category restriction accordingly to given parameters.
	 * 
	 * @param category
	 *           given category
	 */
	private void prepareCategoryRestrictionMocks(final CategoryModel category)
	{
		mockCategoryRestriction = EasyMock.createMock(CMSCategoryRestrictionModel.class);
		EasyMock.expect(mockCategoryRestriction.getCategoryCodes()).andReturn(Collections.singletonList(category.getCode()));
		EasyMock.expect(mockCategoryRestriction.getItemtype()).andReturn(Cms2Constants.TC.CMSCATEGORYRESTRICTION);
		EasyMock.expect(Boolean.valueOf(mockCategoryRestriction.isRecursive())).andReturn(Boolean.FALSE);
		EasyMock.replay(mockCategoryRestriction);
	}

	/**
	 * Prepare product restriction accordingly to given parameters.
	 * 
	 * @param product
	 *           given product
	 */
	private void prepareProductRestrictionMocks(final ProductModel product)
	{
		mockProductRestriction = EasyMock.createMock(CMSProductRestrictionModel.class);
		EasyMock.expect(mockProductRestriction.getProductCodes()).andReturn(Collections.singletonList(product.getCode()));
		EasyMock.expect(mockProductRestriction.getItemtype()).andReturn(Cms2Constants.TC.CMSPRODUCTRESTRICTION);
		EasyMock.replay(mockProductRestriction);
	}

	/**
	 * Prepare inverse restriction accordingly to given parameters.
	 * 
	 * @param before
	 *           indicates how many days before now we will set as activeFrom
	 * 
	 * @param after
	 *           indicates how many days after now will set as activeTo
	 */
	private void prepareInverseTimeRestrictionMocks(final int before, final int after)
	{
		prepareTimeRestrictionMocks(before, after);
		mockInversRestriction = EasyMock.createMock(CMSInverseRestrictionModel.class);
		EasyMock.expect(mockInversRestriction.getOriginalRestriction()).andReturn(mockTimeRestriction);
		EasyMock.replay(mockInversRestriction);

	}
}
