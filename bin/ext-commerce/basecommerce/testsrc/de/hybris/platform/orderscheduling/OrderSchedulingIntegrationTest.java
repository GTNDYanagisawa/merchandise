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
package de.hybris.platform.orderscheduling;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.orderscheduling.impl.CartToOrderJob;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * PLA-11094
 */
@IntegrationTest
public class OrderSchedulingIntegrationTest extends ServicelayerTest
{

	private CartModel sessionCart;
	private UserModel sessionUser;
	private AddressModel address;
	private CartEntryModel cartEntryModel;

	private TriggerModel inactiveTrigger;

	@Resource
	private CartService cartService;
	@Resource
	private ScheduleOrderService scheduleOrderService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private CronJobService cronJobService;
	@Resource
	private TypeService typeService;
	@Resource
	private CartToOrderJob cartToOrderJob;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

	@Before
	public void setUp() throws Exception
	{
		prepareSlayerJob();

		final UnitModel unit = modelService.create(UnitModel.class);
		unit.setCode("myUnit");
		unit.setName("myUnit");
		unit.setUnitType("test");

		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId("testCatalog");

		final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setCatalog(catalog);
		catalogVersion.setVersion("test");

		final ProductModel product = modelService.create(ProductModel.class);
		product.setCatalogVersion(catalogVersion);
		product.setCode("testProduct");
		product.setUnit(unit);

		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setIsocode("EUR1");
		currency.setSymbol("EUR1");
		currency.setBase(Boolean.TRUE);
		currency.setActive(Boolean.TRUE);
		currency.setConversion(Double.valueOf(1));

		final PriceRowModel pRow = modelService.create(PriceRowModel.class);
		pRow.setPrice(Double.valueOf(10));
		pRow.setProduct(product);
		pRow.setCatalogVersion(catalogVersion);
		pRow.setCurrency(currency);
		pRow.setMinqtd(Long.valueOf(1));
		pRow.setNet(Boolean.TRUE);
		pRow.setUnit(unit);
		modelService.saveAll(unit, catalog, catalogVersion, product, currency, pRow);

		sessionUser = userService.getCurrentUser();
		sessionCart = cartService.getSessionCart();

		cartEntryModel = cartService.addNewEntry(sessionCart, product, 1, unit);

		address = modelService.create(AddressModel.class);
		address.setFirstname("Krzysztof");
		address.setLastname("Kwiatosz");
		address.setStreetname("5th Av");
		address.setStreetnumber("12");
		address.setOwner(sessionUser);

		modelService.saveAll();

		inactiveTrigger = modelService.create(TriggerModel.class);
		inactiveTrigger.setActive(Boolean.FALSE);
		inactiveTrigger.setSecond(Integer.valueOf(1));
		inactiveTrigger.setMinute(Integer.valueOf(-1));
		inactiveTrigger.setHour(Integer.valueOf(-1));
		inactiveTrigger.setDay(Integer.valueOf(-1));
		inactiveTrigger.setMonth(Integer.valueOf(-1));
		inactiveTrigger.setRelative(Boolean.TRUE);

	}

	private void prepareSlayerJob()
	{
		final String jobOfInterest = "cartToOrderJob";
		try
		{
			cronJobService.getJob(jobOfInterest);
		}
		catch (final UnknownIdentifierException e)
		{
			final ServicelayerJobModel servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
			servicelayerJobModel.setCode(jobOfInterest);
			servicelayerJobModel.setSpringId(jobOfInterest);
			modelService.save(servicelayerJobModel);
		}
	}

	/**
	 * Test scheduling for cloned cart
	 */
	@Test
	public void testScheduleOrderFromClonedCart()
	{
		Assertions.assertThat(sessionCart).isSameAs(cartService.getSessionCart());
		Assertions.assertThat(sessionCart).isNotNull();
		Assertions.assertThat(sessionCart.getEntries()).isNotNull();
		Assertions.assertThat(sessionCart.getEntries()).containsOnly(cartEntryModel);

		final String clonedCartCode = "clonedCart1";
		final CartModel clonedCart1 = cartService.clone(typeService.getComposedTypeForClass(CartModel.class),
				typeService.getComposedTypeForClass(CartEntryModel.class), sessionCart, clonedCartCode);

		this.modelService.save(clonedCart1);

		Assertions.assertThat(clonedCart1).isNotSameAs(sessionCart);
		Assertions.assertThat(clonedCart1.getCode()).isEqualTo(clonedCartCode);

		// remove the current cart  before scheduling
		cartService.removeSessionCart();
		Assert.assertTrue(modelService.isRemoved(sessionCart));
		sessionCart = null;

		final CartToOrderCronJobModel cronJobModel = scheduleOrderService.createOrderFromCartCronJob(clonedCart1, address, null,
				null, Collections.singletonList(inactiveTrigger));
		//get the cronjob's job done:
		cartToOrderJob.perform(cronJobModel);

		Assertions.assertThat(cronJobModel.getCart()).isSameAs(clonedCart1);

		Collection<CartToOrderCronJobModel> cronjobs1 = findCronJobsByCart(clonedCart1);
		Assertions.assertThat(cronjobs1).containsOnly(cronJobModel);

		final String clonedCartCode2 = "clonedCart2";
		final CartModel clonedCart2 = cartService.clone(typeService.getComposedTypeForClass(CartModel.class),
				typeService.getComposedTypeForClass(CartEntryModel.class), clonedCart1, clonedCartCode2);
		modelService.save(clonedCart2);

		Assertions.assertThat(clonedCart2).isNotSameAs(clonedCart1);
		Assertions.assertThat(clonedCart2.getCode()).isEqualTo(clonedCartCode2);

		modelService.refresh(clonedCart1);
		modelService.refresh(cronJobModel);

		//After cloning the cronjob should have not null cart relation:
		Assertions.assertThat(cronJobModel.getCart()).isNotNull();
		//.. and it should be the same cart the cronjob was scheduled for.
		Assertions.assertThat(cronJobModel.getCart()).isSameAs(clonedCart1);

		cronjobs1 = findCronJobsByCart(clonedCart1);

		//After cloning, for the original cart the relation to  cartToOrderCronJob should be remained.
		Assertions.assertThat(cronjobs1).containsOnly(cronJobModel);

		final Collection<CartToOrderCronJobModel> cronjobs2 = findCronJobsByCart(clonedCart2);

		//After cloning, the cloned cart should not have relation to any cartToOrderCronJob, as it was not scheduled for order yet.
		Assertions.assertThat(cronjobs2).isEmpty();
	}

	/**
	 * Tests the {@link CloneAbstractOrderStrategy} in case of 1:N non part of relations cloning - the root problem of
	 * PLA-11094
	 */
	@Test
	public void testOneToManyRelationCloning()
	{

		final CartToOrderCronJobModel cronJobModel = scheduleOrderService.createOrderFromCartCronJob(sessionCart, address, null,
				null, Collections.singletonList(inactiveTrigger));

		Assertions.assertThat(cronJobModel.getCart()).isSameAs(sessionCart);

		Collection<CartToOrderCronJobModel> cronjobs = findCronJobsByCart(sessionCart);
		Assertions.assertThat(cronjobs).containsOnly(cronJobModel);

		final CartModel clonedCart = cloneAbstractOrderStrategy.clone(typeService.getComposedTypeForClass(CartModel.class),
				typeService.getComposedTypeForClass(CartEntryModel.class), sessionCart, "clonedCart", CartModel.class,
				CartEntryModel.class);

		modelService.save(clonedCart);
		modelService.refresh(sessionCart);
		modelService.refresh(cronJobModel);

		//Cart <--> CartToOrderCronJob Relation is 1:N, non part of relation.
		Assert.assertFalse(typeService.getAttributeDescriptor(CartModel._TYPECODE, CartModel.CARTTOORDERCRONJOB).getPartOf()
				.booleanValue());

		//.. so original cart should keep relation to cronjob
		cronjobs = findCronJobsByCart(sessionCart);
		Assertions.assertThat(cronjobs).containsOnly(cronJobModel);
		//.. whereas cloned cart should not have any relation to cronjobs yet (not scheduled).
		cronjobs = findCronJobsByCart(clonedCart);
		Assertions.assertThat(cronjobs).isNullOrEmpty();

		//check the cronjob
		Assertions.assertThat(cronJobModel.getCart()).isNotNull();
		Assertions.assertThat(cronJobModel.getCart()).isSameAs(sessionCart);
	}

	private Collection<CartToOrderCronJobModel> findCronJobsByCart(final CartModel cart)
	{

		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("cart", cart);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {soj:pk} FROM { ").append(CartToOrderCronJobModel._TYPECODE).append(" as soj JOIN ")
				.append(CartModel._TYPECODE).append(" as c ON {soj.cart} = {c:pk} } ").append(" WHERE {c:pk} = ?cart");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<CartToOrderCronJobModel> result = flexibleSearchService.search(query);
		return result.getResult();

	}

}
