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
 */

package de.hybris.platform.entitlementservices.interceptor.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.entitlementservices.enums.EntitlementTimeUnit;
import de.hybris.platform.entitlementservices.model.EntitlementModel;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.entitlementservices.model.UsageUnitModel;
import de.hybris.platform.entitlementservices.model.impl.DefaultParentChildModifierService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.util.localization.Localization;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({Localization.class})
public class ProductEntitlementValidatorTest
{

    @Mock
    private InterceptorContext ctx;

    private ProductEntitlementValidateInterceptor validator = new ProductEntitlementValidateInterceptor();
    private ProductEntitlementModel modelWithUU;
    private ProductEntitlementModel modelWithoutUU;
    private EntitlementModel entitlement;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Localization.class);

        validator.setParentChildModifierService(new DefaultParentChildModifierService());

        modelWithUU = new ProductEntitlementModel();
        entitlement = new EntitlementModel();
        entitlement.setUsageUnit(new UsageUnitModel());
        modelWithUU.setEntitlement(entitlement);

        modelWithoutUU = new ProductEntitlementModel();
        modelWithoutUU.setEntitlement(new EntitlementModel());

    }

    @Ignore("NON-470")
    @Test(expected = InterceptorException.class)
    public void testProductEntitlementValidatorQuantityIsNull() throws InterceptorException
    {
        validator.onValidate(modelWithUU, ctx);
    }

    @Ignore("NON-470")
    @Test(expected = InterceptorException.class)
    public void shouldRejectQuantityWithoutUsageUnit() throws InterceptorException
    {
        final EntitlementModel ent = new EntitlementModel();
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(ent);
        productEntitlement.setQuantity(1);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test
    public void testProductEntitlementValidatorQuantityIsPositive() throws InterceptorException
    {
        modelWithUU.setQuantity(5);
        validator.onValidate(modelWithUU, ctx);
    }

    @Test
    public void testProductEntitlementValidatorQuantityIsZero() throws InterceptorException
    {
        modelWithUU.setQuantity(0);
        validator.onValidate(modelWithUU, ctx);
    }

    @Test
    public void testProductEntitlementValidatorQuantityIsMinusOne() throws InterceptorException
    {
        modelWithUU.setQuantity(-1);
        validator.onValidate(modelWithUU, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void testProductEntitlementValidatorQuantityIsNegative() throws InterceptorException
    {
        modelWithUU.setQuantity(-5);
        validator.onValidate(modelWithUU, ctx);
    }

    @Test
    public void testProductEntitlementValidatorWithoutUsageUnit() throws InterceptorException
    {
        validator.onValidate(modelWithoutUU, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectTimeUnitWithoutStartDate() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
        productEntitlement.setTimeUnitDuration(1);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectDurationWithoutStartDate() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
        productEntitlement.setTimeUnitDuration(1);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test
    public void shouldAcceptZeroDuration() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(new EntitlementModel());
        productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
        productEntitlement.setTimeUnitStart(1);
        productEntitlement.setTimeUnitDuration(0);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test
    public void shouldAcceptUnlimitedDuration() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(new EntitlementModel());
        productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
        productEntitlement.setTimeUnitStart(1);
        productEntitlement.setTimeUnitDuration(0);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectPathEndingWithSeparator() throws InterceptorException
    {
        final ProductEntitlementModel model = new ProductEntitlementModel();
        model.setEntitlement(new EntitlementModel());
        model.setConditionPath("root/");
        validator.onValidate(model, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectNegativeDuration() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
        productEntitlement.setTimeUnitStart(1);
        productEntitlement.setTimeUnitDuration(-10);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectZeroStartTime() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
        productEntitlement.setTimeUnitStart(0);
        productEntitlement.setTimeUnitDuration(1);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectNegativeStartTime() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
        productEntitlement.setTimeUnitStart(-1);
        productEntitlement.setTimeUnitDuration(1);
        validator.onValidate(productEntitlement, ctx);
    }

    @Ignore("NON-470")
    @Test(expected = InterceptorException.class)
    public void shouldRejectStartTimeWithoutTimeUnit() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setTimeUnitStart(1);
        productEntitlement.setTimeUnitDuration(1);
        validator.onValidate(productEntitlement, ctx);
    }

    @Ignore("NON-470")
    @Test(expected = InterceptorException.class)
    public void shouldRejectTimeUnitWithoutStartTime() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setTimeUnit(EntitlementTimeUnit.MONTH);
        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectCommasInGeoPath() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setConditionGeo(Arrays.asList("Germany,Russia"));

        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void shouldRejectTrailingSlash() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setConditionGeo(Arrays.asList("Deutschland/"));

        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void locationWithTrailingSlash() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        final String location = "Deutschland/Bayern/";

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setQuantity(0);
        productEntitlement.setConditionGeo(Arrays.asList(location));

        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void locationWithTooManyLevels() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        final String location = "1/2/3/4";

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setQuantity(0);
        productEntitlement.setConditionGeo(Arrays.asList(location));

        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void locationWithMissingRegion() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        final String location = "Deutschland//MÃ¼nchen";

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setQuantity(0);
        productEntitlement.setConditionGeo(Arrays.asList(location));

        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void locationWithLeadingSlash() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        final String location = "/Deutschland";

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setQuantity(0);
        productEntitlement.setConditionGeo(Arrays.asList(location));

        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void locationWithComma() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
        final String location = "Deutschland,Great Britain";

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setQuantity(0);
        productEntitlement.setConditionGeo(Arrays.asList(location));

        validator.onValidate(productEntitlement, ctx);
    }

    @Test(expected = InterceptorException.class)
    public void pathWithTrailingSlash() throws InterceptorException
    {
        final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();

        productEntitlement.setEntitlement(entitlement);
        productEntitlement.setQuantity(0);
        productEntitlement.setConditionPath("/video/");

        validator.onValidate(productEntitlement, ctx);
    }

}
