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
package de.hybris.platform.cockpit.model.meta;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ClassAttrWithDotsTest extends ServicelayerTest
{

	private ClassificationClassModel classClassMean;
	private ClassAttributeAssignmentModel classPropertyAssignmentMean;
	private ProductModel product;
	private ClassificationSystemVersionModel classSystemVersionMean;
	private ClassificationAttributeModel classPropertyMean;

	@Resource
	private ModelService modelService;

	@Resource
	private ClassificationService classificationService;

	@Resource
	private ClassificationSystemService classificationSystemService;

	@Before
	public void setUp() throws Exception
	{
		final ClassificationSystemModel classSystemMean = modelService.create(ClassificationSystemModel.class);
		classSystemMean.setId("my.Classification/System");

		classSystemVersionMean = modelService.create(ClassificationSystemVersionModel.class);
		classSystemVersionMean.setCatalog(classSystemMean);
		classSystemVersionMean.setVersion("my.classifiaction/Version");

		classClassMean = modelService.create(ClassificationClassModel.class);
		classClassMean.setCatalogVersion(classSystemVersionMean);
		classClassMean.setCode("my/cool.class");

		classPropertyMean = new ClassificationAttributeModel();
		classPropertyMean.setCode("super.attribute/property");
		classPropertyMean.setSystemVersion(classSystemVersionMean);

		classPropertyAssignmentMean = modelService.create(ClassAttributeAssignmentModel.class);
		classPropertyAssignmentMean.setClassificationAttribute(classPropertyMean);
		classPropertyAssignmentMean.setClassificationClass(classClassMean);
		classPropertyAssignmentMean.setSystemVersion(classSystemVersionMean);


		modelService.saveAll(classSystemMean, classSystemVersionMean, classClassMean, classPropertyMean,
				classPropertyAssignmentMean);

		product = modelService.create(ProductModel.class);
		product.setCatalogVersion(classSystemVersionMean);
		product.setCode("myTestProduct");

		classClassMean.setProducts(Collections.singletonList(product));

		modelService.saveAll(product, classClassMean);

	}

	@Test
	public void testSetFeatureValue()
	{
		assertThat(classificationService.getFeatures(product)).isNotEmpty();

		final ProductFeatureModel productFeature = modelService.create(ProductFeatureModel.class);
		productFeature.setProduct(product);
		productFeature.setValue("foo_bar");
		productFeature.setClassificationAttributeAssignment(classPropertyAssignmentMean);
		productFeature.setValuePosition(Integer.valueOf(1));
		modelService.save(productFeature);

		assertThat(classificationService.getFeature(product, classPropertyAssignmentMean).getValue().getValue()).isEqualTo(
				"foo_bar");
	}

	@Test
	public void testGetClassificationClass()
	{
		assertThat(classificationSystemService.getClassForCode(classSystemVersionMean, "my/cool.class")).isEqualTo(classClassMean);
	}

	@Test
	public void testGetFeatureCode()
	{
		assertThat(classificationSystemService.getAttributeForCode(classSystemVersionMean, "super.attribute/property")).isEqualTo(
				classPropertyMean);
	}
}
