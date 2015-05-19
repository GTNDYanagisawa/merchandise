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
package de.hybris.platform.omsorders.services.query.daos.impl;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integration.oms.order.model.OmsZoneDeliveryModeValueModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test case for {@link DefaultOmsZoneDeliveryModeValueDao}
 */
public class DefaultOmsZoneDeliveryModeValueTest extends ServicelayerTransactionalTest
{
    @Resource
    private DefaultOmsCountryZoneDeliveryModeDao countryZoneDeliveryModeDao;
    @Resource
    private CartService cartService;
    @Resource
    private ModelService modelService;
    @Resource
    private CommonI18NService commonI18NService;
    @Resource
    private BaseSiteService baseSiteService;
    @Resource
    private BaseStoreService baseStoreService; //NOPMD
    @Resource
    private DeliveryModeService deliveryModeService;
    @Resource
    private DefaultOmsZoneDeliveryModeValueDao omsZoneDeliveryModeValueDao;

    private CartModel cartModel;

    @Before
    public void setUp() throws ImpExException
    {
        importCsv("/omsorders/test/defaultOmsCountryZoneDeliveryModeDaoTest.impex", "utf-8");
        cartModel = cartService.getSessionCart();
        cartModel.setCurrency(commonI18NService.getCurrency("USD"));
        final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID("testSite");
        baseSiteService.setCurrentBaseSite(baseSiteForUID, false);
        cartModel.setStore(baseStoreService.getBaseStoreForUid("testStore"));
        modelService.save(cartModel);
    }

    @Test
    public void testFindDeliveryModesForUSToCAGross()
    {
        cartModel.setDeliveryAddress(createAddressModelForCountry("CA", cartModel));
        cartModel.setDeliveryFromAddress(createAddressModelForCountry("US", cartModel));
        modelService.save(cartModel);
        modelService.refresh(cartModel);
        final Collection<DeliveryModeModel> deliveryModes = countryZoneDeliveryModeDao.findDeliveryModes(cartModel);
        assertNotNull(deliveryModes);
        assertThat(
                deliveryModes,
                hasItems(deliveryModeService.getDeliveryModeForCode("premium-gross"),
                        deliveryModeService.getDeliveryModeForCode("standard-gross")));
        final OmsZoneDeliveryModeValueModel premium = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("premium-gross"));
        final OmsZoneDeliveryModeValueModel std = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("standard-gross"));
        assertEquals("08", premium.getSpecificCarrierShippingMethod());
        assertEquals("11", std.getSpecificCarrierShippingMethod());
        assertEquals(Double.valueOf(30.99), premium.getValue());
        assertEquals(Double.valueOf(16.99), std.getValue());
    }

    @Test
    public void testFindDeliveryModesForUSToCANet()
    {
        cartModel.setDeliveryAddress(createAddressModelForCountry("CA", cartModel));
        cartModel.setDeliveryFromAddress(createAddressModelForCountry("US", cartModel));
        cartModel.setNet(Boolean.TRUE);
        modelService.save(cartModel);
        modelService.refresh(cartModel);
        final Collection<DeliveryModeModel> deliveryModes = countryZoneDeliveryModeDao.findDeliveryModes(cartModel);
        assertNotNull(deliveryModes);
        assertThat(deliveryModes, hasItems(deliveryModeService.getDeliveryModeForCode("premium-net")));
        final OmsZoneDeliveryModeValueModel premium = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("premium-net"));
        final OmsZoneDeliveryModeValueModel std = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("standard-net"));
        assertEquals("08", premium.getSpecificCarrierShippingMethod());
        assertEquals("11", std.getSpecificCarrierShippingMethod());
        assertEquals(Double.valueOf(34.99), premium.getValue());
        assertEquals(Double.valueOf(20.99), std.getValue());
    }

    @Test
    public void testFindDeliveryModesForDomesticGross()
    {
        final AddressModel addressModel = createAddressModelForCountry("US", cartModel);
        cartModel.setDeliveryAddress(addressModel);
        cartModel.setDeliveryFromAddress(addressModel);
        modelService.save(cartModel);
        modelService.refresh(cartModel);
        final Collection<DeliveryModeModel> deliveryModes = countryZoneDeliveryModeDao.findDeliveryModes(cartModel);
        assertNotNull(deliveryModes);
        assertThat(
                deliveryModes,
                hasItems(deliveryModeService.getDeliveryModeForCode("premium-gross"),
                        deliveryModeService.getDeliveryModeForCode("standard-gross")));
        final OmsZoneDeliveryModeValueModel premium = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("premium-gross"));
        final OmsZoneDeliveryModeValueModel std = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("standard-gross"));
        assertEquals("02", premium.getSpecificCarrierShippingMethod());
        assertEquals("02", std.getSpecificCarrierShippingMethod());
        assertEquals(Double.valueOf(12.99), premium.getValue());
        assertEquals(Double.valueOf(6.99), std.getValue());
    }

    @Test
    public void testFindDeliveryModesForDomesticNet()
    {
        final AddressModel addressModel = createAddressModelForCountry("US", cartModel);
        cartModel.setDeliveryAddress(addressModel);
        cartModel.setDeliveryFromAddress(addressModel);
        cartModel.setNet(Boolean.TRUE);
        modelService.save(cartModel);
        modelService.refresh(cartModel);
        final Collection<DeliveryModeModel> deliveryModes = countryZoneDeliveryModeDao.findDeliveryModes(cartModel);
        assertNotNull(deliveryModes);
        assertThat(deliveryModes, hasItems(deliveryModeService.getDeliveryModeForCode("premium-net")));
        final OmsZoneDeliveryModeValueModel premium = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("premium-net"));
        final OmsZoneDeliveryModeValueModel std = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("standard-net"));
        assertEquals("02", premium.getSpecificCarrierShippingMethod());
        assertEquals("02", std.getSpecificCarrierShippingMethod());
        assertEquals(Double.valueOf(14.99), premium.getValue());
        assertEquals(Double.valueOf(8.99), std.getValue());
    }

    @Test
    public void testFindDeliveryModesCAtoUSTieredLessThan100()
    {
        cartModel.setSubtotal(Double.valueOf(60));
        cartModel.setDeliveryAddress(createAddressModelForCountry("CA", cartModel));
        cartModel.setDeliveryFromAddress(createAddressModelForCountry("CA", cartModel));
        cartModel.setNet(Boolean.TRUE);
        modelService.save(cartModel);
        modelService.refresh(cartModel);
        final Collection<DeliveryModeModel> deliveryModes = countryZoneDeliveryModeDao.findDeliveryModes(cartModel);
        assertNotNull(deliveryModes);
        assertThat(deliveryModes, hasItems(deliveryModeService.getDeliveryModeForCode("standard-net")));
        final OmsZoneDeliveryModeValueModel std = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("standard-net"));
        assertEquals("02", std.getSpecificCarrierShippingMethod());
        assertEquals(Double.valueOf(12.99), std.getValue());
    }

    @Test
    public void testFindDeliveryModesCAtoUSTieredOver100()
    {
        cartModel.setSubtotal(Double.valueOf(140));
        cartModel.setDeliveryAddress(createAddressModelForCountry("CA", cartModel));
        cartModel.setDeliveryFromAddress(createAddressModelForCountry("CA", cartModel));
        cartModel.setNet(Boolean.TRUE);
        modelService.save(cartModel);
        modelService.refresh(cartModel);
        final Collection<DeliveryModeModel> deliveryModes = countryZoneDeliveryModeDao.findDeliveryModes(cartModel);
        assertNotNull(deliveryModes);
        assertThat(deliveryModes, hasItems(deliveryModeService.getDeliveryModeForCode("standard-net")));
        final OmsZoneDeliveryModeValueModel std = omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("standard-net"));
        assertEquals("02", std.getSpecificCarrierShippingMethod());
        assertEquals(Double.valueOf(8.99), std.getValue());
    }


    @Test(expected = ModelNotFoundException.class)
    public void testFindDeliveryModesCAtoUSNotFound()
    {
        cartModel.setDeliveryAddress(createAddressModelForCountry("US", cartModel));
        cartModel.setDeliveryFromAddress(createAddressModelForCountry("CA", cartModel));
        cartModel.setCurrency(commonI18NService.getCurrency("USD"));
        cartModel.setNet(Boolean.TRUE);
        modelService.save(cartModel);
        modelService.refresh(cartModel);
        omsZoneDeliveryModeValueDao.findOmsZoneDeliveryModeValue(cartModel,
                deliveryModeService.getDeliveryModeForCode("standard-net"));
    }

    private AddressModel createAddressModelForCountry(final String countryIsoCode, final CartModel cartModel)
    {
        final AddressModel address = this.modelService.create(AddressModel.class);
        address.setCountry(commonI18NService.getCountry(countryIsoCode));
        address.setOwner(cartModel);
        modelService.save(address);
        return address;
    }
}
