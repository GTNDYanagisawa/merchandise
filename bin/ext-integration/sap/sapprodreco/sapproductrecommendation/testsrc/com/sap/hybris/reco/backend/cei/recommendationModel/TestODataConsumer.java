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
package com.sap.hybris.reco.backend.cei.recommendationModel;

import java.util.List;

import junit.framework.TestCase;

import com.sap.hybris.reco.dao.SAPRecommendationModelType;
import com.sap.hybris.reco.be.RecommendationModelTypeManager;
import com.sap.hybris.reco.be.cei.RecommendationModelTypeManagerCEI;
import com.sap.hybris.reco.common.util.ODataClientService;
import com.sap.hybris.reco.bo.SAPRecommendationModelTypeReader;
import com.sap.hybris.reco.common.util.HMCConfigurationReader;
/**
 *
 */
public class TestODataConsumer extends TestCase
{
	public void testSearchAll()
	{
		final SAPRecommendationModelTypeReader modelReader = new SAPRecommendationModelTypeReader();
		RecommendationModelTypeManager modelMgr = new RecommendationModelTypeManagerCEI();
		modelMgr.setClientService(new ODataClientService());
		modelMgr.setConfiguration(new HMCConfigurationReader());
		modelReader.setAccessBE(modelMgr);
		
      try
      {
		final List<SAPRecommendationModelType> recommendationModels = modelReader.getAllRecommendationModelTypes();
		assertNotNull(recommendationModels);
		System.out.println(recommendationModels.size());
		assertEquals(true, recommendationModels.size() > 0);
		System.out.println(recommendationModels);
      }
      catch (Exception e)
      {
    
      }

	}


	//	public void testRead()
	//	{
	//		final SAPRecommendationModelTypeReader modelReader = new SAPRecommendationModelTypeReader();
	//		modelReader.setConsumerBuilder(new ODataConsumerBuilder());
	//		modelReader.setConfiguration(new DefaultCEIoDataConfigurationImpl());
	//
	//		final SAPRecommendationModelType recommendationModel = modelReader.getRecommendationModel("ZTEST");
	//		assertNotNull(recommendationModel);
	//		System.out.println(recommendationModel);
	//
	//	}


	//	public void testSearch()
	//	{
	//		final SAPRecommendationModelTypeReader modelReader = new SAPRecommendationModelTypeReader();
	//		modelReader.setConsumerBuilder(new ODataConsumerBuilder());
	//		modelReader.setConfiguration(new DefaultCEIoDataConfigurationImpl());
	//
	//		final List<SAPRecommendationModelType> recommendationModels = modelReader.searchRecommendationModel("est");
	//		assertNotNull(recommendationModels);
	//		System.out.println(recommendationModels.size());
	//		assertEquals(true, recommendationModels.size() > 0);
	//		System.out.println(recommendationModels);
	//
	//	}
	//
	//	public void testSearch2()
	//	{
	//		final SAPTargetGroupReader targetGroupReader = new SAPTargetGroupReader();
	//		targetGroupReader.setConsumerBuilder(new ODataConsumerBuilder());
	//		targetGroupReader.setConfiguration(new DefaultADToDataConfigurationImpl());
	//
	//		final List<SAPTargetGroup> targetGroups = targetGroupReader.searchTargetGroups("abc");
	//		assertNotNull(targetGroups);
	//		System.out.println(targetGroups);
	//		assertEquals(true, targetGroups.size() > 0);
	//
	//	}
	//
	//	public void testBPSearch()
	//	{
	//		final SAPTargetGroupReader targetGroupReader = new SAPTargetGroupReader();
	//		targetGroupReader.setConsumerBuilder(new ODataConsumerBuilder());
	//		targetGroupReader.setConfiguration(new DefaultADToDataConfigurationImpl());
	//
	//		final List<SAPTargetGroup> targetGroups = targetGroupReader.searchTargetGroupsForBP("wec_b2b");
	//		assertNotNull(targetGroups);
	//		System.out.println(targetGroups.size() + " target groups found for wec_b2b");
	//		assertEquals(true, targetGroups.size() > 0);
	//		System.out.println(targetGroups);
	//
	//	}
	//




}
