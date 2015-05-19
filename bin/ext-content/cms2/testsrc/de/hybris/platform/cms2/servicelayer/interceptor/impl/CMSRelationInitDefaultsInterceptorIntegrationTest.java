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
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


public class CMSRelationInitDefaultsInterceptorIntegrationTest extends ServicelayerTest // NOPMD: Junit4 allows any name for test method
{
	@Resource
	ModelService modelService;

	@Test
	public void shouldCreateCMSRelationModelWithGeneratedUid()
	{
		// given
		final CMSRelationModel cmsRelation = modelService.create(CMSRelationModel.class);

		// when
		final String uid = cmsRelation.getUid();

		// then
		assertThat(uid).isNotNull();
		assertThat(uid).isNotEmpty();
	}

}
