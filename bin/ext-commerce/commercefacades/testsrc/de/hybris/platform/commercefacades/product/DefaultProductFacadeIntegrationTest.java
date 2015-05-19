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
package de.hybris.platform.commercefacades.product;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.customerreview.setup.CustomerReviewSystemSetup;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultProductFacade}
 */
@IntegrationTest
public class DefaultProductFacadeIntegrationTest extends ServicelayerTest
{

	private static final String LANG_EN = "en";
	private static final Double TEST_RATING = Double.valueOf(5d);
	private static final String TEST_COMMENT = "comment";
	private static final String TEST_HEADLINE = "headline";
	private static final String TEST_PRODUCT_CODE_3423 = "HW1210-3423";
	private static final String TEST_PRODUCT_CODE_3424 = "HW1210-3424";
	private static final String TEST_PRODUCT_CODE_3425 = "HW1210-3425";
	private static final String TEST_PRODUCT_CODE_2356 = "HW2300-2356";
	private static final String TEST_BASESITE_UID = "testSite";

	private static final Logger LOG = Logger.getLogger(DefaultProductFacadeIntegrationTest.class);

	@Resource
	private ProductFacade productFacade;

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;

	@Resource
	private UserService userService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private CustomerReviewSystemSetup customerReviewSystemSetup;

	@Resource
	private TypeService typeService;

	@Resource
	private BaseSiteService baseSiteService;


	@Before
	public void setUp() throws Exception
	{
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_EN));
		createUserGroups();
		final Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("customerreview.searchrestrictions.create", new String[]
		{ "true" });
		final SystemSetupContext ctx = new SystemSetupContext(params, Type.ESSENTIAL, Process.ALL, "customerreview");
		customerReviewSystemSetup.createSearchRestrictions(ctx);

		// Create data for tests
		LOG.info("Creating data for product facade..");
		userService.setCurrentUser(userService.getAnonymousUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/commercefacades/test/testProductFacade.csv", "utf-8");
		importCsv("/commercefacades/test/testProductReferences.csv", "utf-8");
		createCoreData();
		createHardwareCatalog();
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_EN));

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);

		LOG.info("Finished data for product facade " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Test
	public void testGetProductForCodeBasic()
	{
		//There is no variant type created for testing. Variants must be tested in proper extension, where any variant is available.
		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3424,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));

		Assert.assertNotNull(productData);
		Assert.assertEquals(TEST_PRODUCT_CODE_3424, productData.getCode());
		//		Assert.assertEquals(5, productData.getImages().size());

		Assert.assertNotNull(productData.getPrice());
		Assert.assertEquals(BigDecimal.valueOf(32.95), productData.getPrice().getValue());

		//		Assert.assertNotNull(productData.getCategories());
		//		Assert.assertEquals("testCategory1", productData.getCategories().iterator().next().getCode());
		Assert.assertEquals(Double.valueOf(3.5), productData.getAverageRating());
		Assert.assertEquals("testManufacturer", productData.getManufacturer());
		Assert.assertEquals(Boolean.TRUE, productData.getPurchasable());
	}

	@Test
	public void testGetProductForCodeImagesAndCategories()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.PRICE);
		options.add(ProductOption.GALLERY);
		options.add(ProductOption.CATEGORIES);
		//There is no variant type created for testing. Variants must be tested in proper extension, where any variant is available.
		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3424, options);

		Assert.assertNotNull(productData);
		Assert.assertEquals(TEST_PRODUCT_CODE_3424, productData.getCode());

		Assert.assertNotNull(productData.getPrice());
		Assert.assertEquals(BigDecimal.valueOf(32.95), productData.getPrice().getValue());

		Assert.assertNotNull(productData.getCategories());
		Assert.assertEquals("testCategory1", productData.getCategories().iterator().next().getCode());
		Assert.assertEquals(Double.valueOf(3.5), productData.getAverageRating());
		Assert.assertEquals("testManufacturer", productData.getManufacturer());
		Assert.assertEquals(Boolean.TRUE, productData.getPurchasable());
	}

	@Test
	public void testGetProductForCodePromotions()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.PROMOTIONS);
		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3425, options);

		Assert.assertNotNull(productData);
		Assert.assertEquals(TEST_PRODUCT_CODE_3425, productData.getCode());
		Assert.assertEquals(1, productData.getPotentialPromotions().size());
		Assert.assertEquals("Fixed Price for default", productData.getPotentialPromotions().iterator().next().getDescription());
	}

	@Test
	public void testGetProductForCodeStock()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.STOCK);

		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3423, options);

		Assert.assertNotNull(productData);

		Assert.assertEquals(TEST_PRODUCT_CODE_3423, productData.getCode());
		Assert.assertEquals(Long.valueOf(122), productData.getStock().getStockLevel());
	}

	/*
	 * @Test public void testGetProductByCodeReview() { userService.setCurrentUser(userService.getAnonymousUser()); final
	 * Set<ProductOption> options = new HashSet<ProductOption>(); options.add(ProductOption.REVIEW);
	 * 
	 * final ProductData productData = productFacade.getProductByCode(TEST_PRODUCT_CODE_3424, options);
	 * 
	 * Assert.assertNotNull(productData); Assert.assertEquals(TEST_PRODUCT_CODE_3424, productData.getCode());
	 * Assert.assertNotNull(productData.getReviews()); Assert.assertEquals(2, productData.getReviews().size()); }
	 */

	@Test
	public void testGetProductForCodeClassification()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.CLASSIFICATION); //no data for that found

		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_2356, options);

		Assert.assertNotNull(productData);

		Assert.assertEquals(TEST_PRODUCT_CODE_2356, productData.getCode());
		Assert.assertNotNull(productData.getClassifications());
		Assert.assertEquals(3, productData.getClassifications().size());
		Assert.assertEquals(1, productData.getClassifications().iterator().next().getFeatures().iterator().next()
				.getFeatureValues().size());
		Assert.assertEquals("PCI Express", productData.getClassifications().iterator().next().getFeatures().iterator().next()
				.getFeatureValues().iterator().next().getValue());

	}

	@Test
	public void testGetProductReferencesForCode()
	{
		final List<ProductOption> options = Arrays.asList(ProductOption.BASIC);

		final List<ProductReferenceData> productReferenceDatas = productFacade.getProductReferencesForCode("camera1",
				ProductReferenceTypeEnum.ACCESSORIES, options, null);

		Assert.assertNotNull(productReferenceDatas);

		Assert.assertEquals(1, productReferenceDatas.size());
		Assert.assertEquals("ACCESSORIES", productReferenceDatas.get(0).getReferenceType().getCode());
	}

	@Test
	public void testGetProductReferencesForCodeWithLimit()
	{
		final List<ProductOption> options = Arrays.asList(ProductOption.BASIC);

		final List<ProductReferenceData> productReferenceDatas = productFacade.getProductReferencesForCode("camera1",
				ProductReferenceTypeEnum.ACCESSORIES, options, Integer.valueOf(1));

		Assert.assertNotNull(productReferenceDatas);

		Assert.assertEquals(1, productReferenceDatas.size());
		Assert.assertEquals("ACCESSORIES", productReferenceDatas.get(0).getReferenceType().getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetAllReviewsError()
	{
		productFacade.getReviews("unknown");
		Assert.fail("UnknownIdentifier expected");
	}

	@Test
	public void testPostReview()
	{
		final ReviewData reviewData = new ReviewData();
		reviewData.setComment(TEST_COMMENT);
		reviewData.setHeadline(TEST_HEADLINE);
		reviewData.setRating(TEST_RATING);
		final int currentReviewsCount = productFacade.getReviews(TEST_PRODUCT_CODE_3423).size();
		productFacade.postReview(TEST_PRODUCT_CODE_3423, reviewData);
		List<ReviewData> reviews = productFacade.getReviews(TEST_PRODUCT_CODE_3423);
		Assert.assertEquals(currentReviewsCount + 1, reviews.size());

		final ProductModel productModel = productService.getProductForCode(TEST_PRODUCT_CODE_3423);
		for (final CustomerReviewModel customerReviewModel : productModel.getProductReviews())
		{
			customerReviewModel.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
			modelService.save(customerReviewModel);
		}
		reviews = productFacade.getReviews(TEST_PRODUCT_CODE_3423);
		final ReviewData review = reviews.get(0);
		Assert.assertNotNull(review);
		Assert.assertNotSame(reviewData, review);
		Assert.assertEquals(reviewData.getRating(), review.getRating());
		Assert.assertEquals(reviewData.getComment(), review.getComment());
		Assert.assertEquals(reviewData.getHeadline(), review.getHeadline());
		Assert.assertEquals(userService.getAdminUser().getUid(), review.getPrincipal().getUid());
		Assert.assertEquals(userService.getAdminUser().getName(), review.getPrincipal().getName());
	}

	protected void createUserGroups()
	{
		UserGroup customerGroup = null;
		try
		{
			customerGroup = UserManager.getInstance().getUserGroupByGroupID(Constants.USER.CUSTOMER_USERGROUP);
		}
		catch (final JaloItemNotFoundException e)
		{
			try
			{
				customerGroup = UserManager.getInstance().createUserGroup(Constants.USER.CUSTOMER_USERGROUP);
			}
			catch (final ConsistencyCheckException ex)
			{
				LOG.error("Could not create a customer user group.");
			}
		}

		final Set<UserGroup> customerGroupList = new LinkedHashSet<UserGroup>();
		customerGroupList.add(customerGroup);
		typeService.getAttributeDescriptor(CustomerModel._TYPECODE, Customer.GROUPS).setDefaultValue(customerGroupList);
		UserManager.getInstance().getAnonymousCustomer().addToGroups(jaloSession.getSessionContext(), customerGroup);
	}

}
