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
package de.hybris.platform.cockpit.services.search.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.cockpit.model.meta.impl.ClassAttributePropertyDescriptor;
import de.hybris.platform.cockpit.model.search.Operator;
import de.hybris.platform.cockpit.model.search.SearchParameterDescriptor;
import de.hybris.platform.cockpit.model.search.SearchParameterValue;
import de.hybris.platform.cockpit.services.search.ConditionTranslatorContext;
import de.hybris.platform.cockpit.services.search.GenericQueryConditionTranslator;
import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test showing the range compare behavior
 */
@IntegrationTest
public class FeatureGenericQueryConditionTranslatorIntegrationTest extends ServicelayerTest
{

	private static final String FOCAL_DISTANCE = "focalDistance";
	private static final String HP_39_117 = "HW1240-1732";
	private static final String HP_36_180 = "HW1240-1733";

	private static final String SONY_38_114 = "HW1230-0200";
	private static final String SONY_27_128 = "HW1230-0001";

	private static final String PENTAX_38_114 = "HW1220-0011";

	private static final String CANON_39 = "HW1210-3422";
	private static final String CANON_37_111 = "HW1210-3412";
	private static final String CANON_35_105 = "HW1210-3411";
	private static final String CANON_27_88 = "HW1210-0400";


	private final static Logger LOG = Logger.getLogger(FeatureGenericQueryConditionTranslatorIntegrationTest.class);

	private GenericQueryConditionTranslator translator;

	@Mock
	private ConditionTranslatorContext ctx;

	private ClassAttributeAssignmentModel classAttrAssignModel;

	private ClassificationAttributeUnitModel milimeterUnit;
	private ClassificationAttributeUnitModel centymeterUnit;
	private ClassificationAttributeUnitModel meterUnit;

	private SearchParameterDescriptor descriptor;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ModelService modelService;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createHardwareCatalog();

		classAttrAssignModel = fetchClassAttributeAssignment(FOCAL_DISTANCE);

		milimeterUnit = getClassificationAttributeUnitModel("mm");
		centymeterUnit = getClassificationAttributeUnitModel("cm");
		meterUnit = getClassificationAttributeUnitModel("m");



		MockitoAnnotations.initMocks(this);
		translator = new FeatureGenericQueryConditionTranslator()
		{
			@Override
			protected de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment toClassAttributeAssignment(
					final ClassAttributeAssignmentModel assignmentModel)
			{
				return modelService.getSource(classAttrAssignModel);
			}
		};

		final ClassAttributePropertyDescriptor classAttrPropertyDescr = new ClassAttributePropertyDescriptor(
				"SampleClassification/1.0/photography.focalDistance");

		descriptor = new ClassAttributeSearchDescriptor(classAttrPropertyDescr)
		{

			@Override
			public ClassAttributeAssignmentModel getClassAttributeAssignment()
			{
				return classAttrAssignModel;
			}
		};
	}

	private ClassificationAttributeUnitModel getClassificationAttributeUnitModel(final String code)
	{
		final ClassificationAttributeUnitModel model = new ClassificationAttributeUnitModel();
		model.setCode(code);

		return flexibleSearchService.getModelByExample(model);
	}

	@Test
	public void testQueryNoLowBoundaryValueForRange()
	{
		List<ProductModel> result = null;
		result = getListForValueRange(null, Double.valueOf(10.0));
		assertContainsCode(result); //empty result

		result = getListForValueRange(null, Double.valueOf(100.0));
		assertContainsCode(result, CANON_27_88);

		result = getListForValueRange(null, Double.valueOf(113.0));
		assertContainsCode(result, CANON_27_88, CANON_37_111, CANON_35_105);

		result = getListForValueRange(null, Double.valueOf(114.0));
		assertContainsCode(result, CANON_27_88, CANON_37_111, PENTAX_38_114, SONY_38_114, CANON_35_105);

		result = getListForValueRange(null, Double.valueOf(115.0));
		assertContainsCode(result, CANON_27_88, CANON_37_111, PENTAX_38_114, SONY_38_114, CANON_35_105);

		result = getListForValueRange(null, Double.valueOf(500.0));
		assertContainsCode(result, CANON_27_88, CANON_35_105, CANON_37_111, HP_36_180, HP_39_117, PENTAX_38_114, SONY_27_128,
				SONY_38_114); //all withot one
	}

	@Test
	public void testQueryBothBoundariesForRange()
	{
		List<ProductModel> result = null;
		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(10.0));
		assertContainsCode(result); //empty result

		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(35.0));
		assertContainsCode(result); //empty result

		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(100.0));
		assertContainsCode(result); //empty result

		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(105.0));
		assertContainsCode(result, CANON_35_105); //empty result

		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(115.0));
		assertContainsCode(result, CANON_35_105, CANON_37_111, SONY_38_114, PENTAX_38_114);

		result = getListForValueRange(Double.valueOf(39.0), Double.valueOf(39.0));
		assertContainsCode(result); //empty result

		result = getListForValueRange(Double.valueOf(39.0), Double.valueOf(500.0));
		assertContainsCode(result, HP_39_117);
	}



	@Test
	public void testQueryBothBoundariesWithDifferentUnitsForRange()
	{
		List<ProductModel> result = null;

		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(10.5), milimeterUnit, centymeterUnit);
		assertContainsCode(result, CANON_35_105); //empty result

		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(10.5), null, centymeterUnit);
		assertContainsCode(result, CANON_35_105); //empty result

		result = getListForValueRange(Double.valueOf(35.0), Double.valueOf(0.105), milimeterUnit, meterUnit);
		assertContainsCode(result, CANON_35_105); //empty result

		result = getListForValueRange(Double.valueOf(3.5), Double.valueOf(0.115), centymeterUnit, meterUnit);
		assertContainsCode(result, CANON_35_105, CANON_37_111, SONY_38_114, PENTAX_38_114);

		result = getListForValueRange(Double.valueOf(0.035), Double.valueOf(0.115), meterUnit, meterUnit);
		assertContainsCode(result, CANON_35_105, CANON_37_111, SONY_38_114, PENTAX_38_114);

		result = getListForValueRange(Double.valueOf(0.035), Double.valueOf(115), meterUnit, milimeterUnit);
		assertContainsCode(result, CANON_35_105, CANON_37_111, SONY_38_114, PENTAX_38_114);

		result = getListForValueRange(Double.valueOf(0.035), Double.valueOf(115), meterUnit, null);
		assertContainsCode(result, CANON_35_105, CANON_37_111, SONY_38_114, PENTAX_38_114);

		result = getListForValueRange(Double.valueOf(3.9), Double.valueOf(39.0), centymeterUnit, milimeterUnit);
		assertContainsCode(result); //empty result

		result = getListForValueRange(Double.valueOf(3.9), Double.valueOf(39.0), centymeterUnit, null);
		assertContainsCode(result); //empty result

		result = getListForValueRange(Double.valueOf(0.039), Double.valueOf(39.0), meterUnit, milimeterUnit);
		assertContainsCode(result); //empty result

		result = getListForValueRange(Double.valueOf(0.039), Double.valueOf(39.0), meterUnit, null);
		assertContainsCode(result); //empty result

	}

	@Test
	public void testQueryNoUpperBoundaryValueForRange()
	{
		List<ProductModel> result = null;

		result = getListForValueRange(Double.valueOf(26.0), null);
		assertContainsCode(result, CANON_27_88, CANON_35_105, CANON_37_111, CANON_39, HP_36_180, HP_39_117, PENTAX_38_114,
				SONY_27_128, SONY_38_114);

		result = getListForValueRange(Double.valueOf(27.0), null);
		assertContainsCode(result, CANON_27_88, CANON_35_105, CANON_37_111, CANON_39, HP_36_180, HP_39_117, PENTAX_38_114,
				SONY_27_128, SONY_38_114);

		result = getListForValueRange(Double.valueOf(28.0), null);
		assertContainsCode(result, CANON_35_105, CANON_37_111, CANON_39, HP_36_180, HP_39_117, PENTAX_38_114, SONY_38_114);

		result = getListForValueRange(Double.valueOf(35.0), null);
		assertContainsCode(result, CANON_35_105, CANON_37_111, CANON_39, HP_36_180, HP_39_117, PENTAX_38_114, SONY_38_114);

		result = getListForValueRange(Double.valueOf(36.0), null);
		assertContainsCode(result, CANON_37_111, CANON_39, HP_36_180, HP_39_117, PENTAX_38_114, SONY_38_114);

		result = getListForValueRange(Double.valueOf(39.0), null);
		assertContainsCode(result, CANON_39, HP_39_117);

		result = getListForValueRange(Double.valueOf(40.0), null);
		assertContainsCode(result); //empty result
	}


	private List<ProductModel> getListForValueRange(final Double start, final Double end)
	{
		return getListForValueRange(start, end, null, null);
	}

	private List<ProductModel> getListForValueRange(final Double start, final Double end,
			final ClassificationAttributeUnitModel startUnit, final ClassificationAttributeUnitModel endUnit)
	{

		final FeatureValue firstValue = (start == null ? null : new FeatureValue(start, null, startUnit));
		final FeatureValue secondValue = (end == null ? null : new FeatureValue(end, null, endUnit));

		final List<FeatureValue> values = Arrays.asList(firstValue, secondValue);


		final SearchParameterValue paramValue = new SearchParameterValue(descriptor, values, Operator.BETWEEN);

		final GenericCondition condition = translator.translate(paramValue, ctx);


		final StringBuilder buffer = new StringBuilder(1000);
		buffer.append("SELECT {item." + ItemModel.PK + "} FROM {" + ProductModel._TYPECODE + " as item } WHERE \n");

		final Map<String, Object> valuesMap = new HashMap<String, Object>();
		final Map<String, String> typeIndexMap = new HashMap<String, String>();
		typeIndexMap.put(null, "item");//i can't believe this also 

		condition.toFlexibleSearch(buffer, typeIndexMap, valuesMap);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("query " + buffer.toString());
		}

		final SearchResult<ProductModel> result = flexibleSearchService.search(buffer.toString(), valuesMap);
		Assert.assertNotNull(result);
		return result.getResult();

	}

	private ClassAttributeAssignmentModel fetchClassAttributeAssignment(final String code)
	{

		final SearchResult<ClassAttributeAssignmentModel> result = flexibleSearchService.search("select {caa." + ItemModel.PK
				+ "} from {" + ClassAttributeAssignmentModel._TYPECODE + " as caa} , {" + ClassificationAttributeModel._TYPECODE
				+ " as ca} " + " where {ca." + ClassificationAttributeModel.CODE + "} = '" + code + "' AND  {caa."
				+ ClassAttributeAssignmentModel.CLASSIFICATIONATTRIBUTE + "}  = {ca." + ItemModel.PK + "}");
		Assert.assertTrue(result.getCount() == 1);
		return result.getResult().get(0);
	}

	public void assertContainsCode(final List<ProductModel> products, final String... codes)
	{
		if (codes == null || codes.length == 0)
		{
			Assert.assertTrue("Expected product list should be empty , but was " + products, products.isEmpty());
		}
		else
		{
			Assert.assertTrue(
					"Expected product list should have the same size , but was " + products + " of expected " + Arrays.asList(codes),
					products.size() == codes.length);
		}

		final List<String> codesLocal = new ArrayList<String>(Arrays.asList(codes));
		for (final ProductModel product : products)
		{
			if (codesLocal.remove(product.getCode()))
			{
				continue;
			}
		}

		if (!codesLocal.isEmpty())
		{
			Assert.fail(" Product list " + products + " does not contain a code(s) " + codesLocal);
		}
	}

}
