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
package de.hybris.platform.voucher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.voucher.model.ProductCategoryRestrictionModel;
import de.hybris.platform.voucher.model.VoucherModel;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the {@link VoucherRestrictionService}
 */
public class VoucherRestrictionServiceTest extends AbstractVoucherServiceTest
{

	@Resource
	private VoucherService voucherService;
	@Resource
	private VoucherRestrictionService voucherRestrictionService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private ProductService productService;
	@Resource
	private CategoryService categoryService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;

	private CatalogVersionModel catVersion;
	private CartModel cart;
	private ProductModel product;
	private CategoryModel category1;
	private CategoryModel category2;
	private UserModel userDemo;
	private ProductCategoryRestrictionModel productCategoryRestriction;

	private VoucherModel voucher;
	private final String voucherCode = "vo1";

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importCsv("/test/variantProduct.csv", "windows-1252");

		prepareData();
	}

	private void prepareData()
	{
		catVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(catVersion);

		userDemo = userService.getUserForUID("demo");
		userService.setCurrentUser(userDemo);

		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));

		voucher = voucherService.getVoucher(voucherCode);
		createProductCategoryRestriction();
	}

	private void createProductCategoryRestriction()
	{
		productCategoryRestriction = modelService.create(ProductCategoryRestrictionModel.class);
		productCategoryRestriction.setVoucher(voucher);
		modelService.save(productCategoryRestriction);
	}

	/**
	 * Tests the product category restriction with a normal product.
	 * <ul>
	 * <li>creates a cart with product "HW1100-0024" which is within the category "HW1100",</li>
	 * <li>for "HW1100", if positive is true, the restriction is fulfilled,</li>
	 * <li>for "HW1100", if positive is false, the restriction is NOT fulfilled,</li>
	 * <li>for "HW2300", if positive is true, the restriction is NOT fulfilled,</li>
	 * <li>for "HW2300", if positive is false, the restriction is fulfilled.</li>
	 * </ul>
	 */
	@Test
	public void testNormalProduct()
	{
		testIsFulfilledOrder("HW1100-0024", "HW1100", "HW2300");
	}

	/**
	 * Tests the product category restriction with a variant product. In this test, "ds453-00" belongs to "Sales"
	 * directly. "Alone" is a category which contains no products or sub-categories.
	 */
	@Test
	public void testVariantProduct()
	{
		testIsFulfilledOrder("ds453-00", "Sales", "Alone");
	}

	/**
	 * Tests the product category restriction with a variant product. In this test, "ds453-01" doesn't belong to "Shoes"
	 * directly, but its base product "ds453" belongs to "Shoes" directly. "Sales" is super-category of "ds453-00", but
	 * has nothing to do with "ds453-01" or its base product "ds453" or their super-categories.
	 */
	@Test
	public void testVariantProduct2()
	{
		testIsFulfilledOrder("ds453-01", "Shoes", "Sales");
	}

	private void testIsFulfilledOrder(final String productCode, final String categoryCode1, final String categoryCode2)
	{
		product = productService.getProductForCode(productCode);
		category1 = categoryService.getCategoryForCode(categoryCode1);
		category2 = categoryService.getCategoryForCode(categoryCode2);

		cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);

		prepareProductCategoryRestriction(category1, Boolean.TRUE);
		boolean fulfilled = voucherRestrictionService.isFulfilled(productCategoryRestriction, cart);
		assertTrue("product inside the collection and positive is true", fulfilled);

		prepareProductCategoryRestriction(category1, Boolean.FALSE);
		fulfilled = voucherRestrictionService.isFulfilled(productCategoryRestriction, cart);
		assertFalse("product inside the collection and positive is false", fulfilled);

		prepareProductCategoryRestriction(category2, Boolean.TRUE);
		fulfilled = voucherRestrictionService.isFulfilled(productCategoryRestriction, cart);
		assertFalse("product is not included in the collection and positive is true", fulfilled);

		prepareProductCategoryRestriction(category2, Boolean.FALSE);
		fulfilled = voucherRestrictionService.isFulfilled(productCategoryRestriction, cart);
		assertTrue("product is not included in the collection and positive is false", fulfilled);
	}

	private void prepareProductCategoryRestriction(final CategoryModel category, final Boolean positive)
	{
		productCategoryRestriction.setCategories(Collections.singletonList(category));
		productCategoryRestriction.setPositive(positive);
		modelService.save(productCategoryRestriction);
	}

}
