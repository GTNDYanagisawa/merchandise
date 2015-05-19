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
package de.hybris.platform.acceleratorservices.process.email.context.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.acceleratorservices.process.strategies.EmailTemplateTranslationStrategy;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.acceleratorservices.urlencoder.impl.DefaultUrlEncoderService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.commons.renderer.daos.RendererTemplateDao;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * 
 * 
 */
@UnitTest
public class DefaultEmailContextFactoryTest
{
	private DefaultEmailContextFactory emailContextFactory;

	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSComponentService cmsComponentService;
	@Mock
	private RendererTemplateDao rendererTemplateDao;
	@Mock
	private RendererService rendererService;
	@Mock
	private ModelService modelService;
	@Mock
	private TypeService typeService;
	@Mock
	private ProcessContextResolutionStrategy contextResolutionStrategy;

	@Mock
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	@Mock
	private CustomerEmailResolutionService customerEmailResolutionService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private EmailTemplateTranslationStrategy emailTemplateTranslationStrategy;
	@Mock
	private DefaultUrlEncoderService urlEncoderService;

	private final AbstractEmailContext emailContext = new TestEmailContext();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		emailContextFactory = Mockito.spy(new DefaultEmailContextFactory());
		emailContextFactory.setCmsPageService(cmsPageService);
		emailContextFactory.setCmsComponentService(cmsComponentService);
		emailContextFactory.setRendererService(rendererService);
		emailContextFactory.setRendererTemplateDao(rendererTemplateDao);
		emailContextFactory.setModelService(modelService);
		emailContextFactory.setTypeService(typeService);
		emailContextFactory.setContextResolutionStrategy(contextResolutionStrategy);
		emailContextFactory.setEmailTemplateTranslationStrategy(emailTemplateTranslationStrategy);

		emailContext.setSiteBaseUrlResolutionService(siteBaseUrlResolutionService);
		emailContext.setCustomerEmailResolutionService(customerEmailResolutionService);
		emailContext.setConfigurationService(configurationService);
		emailContext.setUrlEncoderService(urlEncoderService);
	}


	@Test
	public void testCreateCustomerEmailContext() throws ClassNotFoundException
	{
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);
		final EmailPageModel emailPageModel = mock(EmailPageModel.class);
		final RendererTemplateModel rendererTemplateModel = mock(RendererTemplateModel.class);
		final CMSSiteModel siteModel = mock(CMSSiteModel.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final Class emailContextClass = TestEmailContext.class;

		given(rendererTemplateModel.getContextClass()).willReturn(emailContextClass.getName());

		given(configurationService.getConfiguration()).willReturn(configuration);
		given(businessProcessModel.getSite()).willReturn(siteModel);
		given(siteModel.getUid()).willReturn("electronics");
		given(businessProcessModel.getCustomer()).willReturn(customerModel);
		given(customerModel.getDisplayName()).willReturn("a b");
		given(customerEmailResolutionService.getEmailForCustomer(customerModel)).willReturn("a@b.com");
		doReturn(emailContext).when(emailContextFactory).resolveEmailContext(rendererTemplateModel);

		final AbstractEmailContext result = emailContextFactory.create(businessProcessModel, emailPageModel, rendererTemplateModel);

		Assert.assertNotNull(result);

		Assert.assertEquals("a@b.com", result.getEmail());
		Assert.assertEquals("a b", result.getDisplayName());
	}

	@Test
	public void testCreateCustomerEmailContextWithCMSSlots()
	{
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		final EmailPageModel emailPageModel = mock(EmailPageModel.class);
		final RendererTemplateModel rendererTemplateModel = mock(RendererTemplateModel.class);
		final CMSSiteModel siteModel = mock(CMSSiteModel.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final Class emailContextClass = TestEmailContext.class;


		given(rendererTemplateModel.getContextClass()).willReturn(emailContextClass.getName());

		given(configurationService.getConfiguration()).willReturn(configuration);
		given(businessProcessModel.getSite()).willReturn(siteModel);
		given(siteModel.getUid()).willReturn("electronics");
		given(businessProcessModel.getCustomer()).willReturn(customerModel);
		given(customerModel.getDisplayName()).willReturn("a b");
		given(customerEmailResolutionService.getEmailForCustomer(customerModel)).willReturn("a@b.com");

		final ContentSlotData contentSlotData = mock(ContentSlotData.class);
		given(contentSlotData.getPosition()).willReturn("TopContent");
		given(cmsPageService.getContentSlotsForPage(emailPageModel)).willReturn(Collections.singleton(contentSlotData));
		final ContentSlotModel contentSlotModel = mock(ContentSlotModel.class);
		given(contentSlotData.getContentSlot()).willReturn(contentSlotModel);

		final AbstractCMSComponentModel paragraphComponentModel = mock(CMSParagraphComponentModel.class);
		given(paragraphComponentModel.getVisible()).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(cmsComponentService.isComponentContainer(any(String.class)))).willReturn(Boolean.FALSE);

		given(contentSlotModel.getCmsComponents()).willReturn(Collections.singletonList(paragraphComponentModel));
		final RendererTemplateModel compRendererTemplateModel = mock(RendererTemplateModel.class);
		given(rendererTemplateDao.findRendererTemplatesByCode(any(String.class))).willReturn(
				Collections.singletonList(compRendererTemplateModel));

		final String property = "content";
		given(cmsComponentService.getEditorProperties(paragraphComponentModel)).willReturn(Collections.singleton(property));
		given(modelService.getAttributeValue(paragraphComponentModel, property)).willReturn("This is a test paragraph");
		doReturn(emailContext).when(emailContextFactory).resolveEmailContext(rendererTemplateModel);

		given(typeService.getComposedTypeForClass(any(Class.class))).willReturn(composedTypeModel);

		final AbstractEmailContext result = emailContextFactory.create(businessProcessModel, emailPageModel, rendererTemplateModel);

		verify(rendererService, times(1)).render(any(RendererTemplateModel.class), any(Map.class), any(StringWriter.class));
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.getCmsSlotContents().size());
		Assert.assertEquals("a@b.com", result.getEmail());
		Assert.assertEquals("a b", result.getDisplayName());
	}

	@Test
	public void testCreateCustomerEmailContextWithVariables()
	{
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);
		final EmailPageModel emailPageModel = mock(EmailPageModel.class);
		final RendererTemplateModel rendererTemplateModel = mock(RendererTemplateModel.class);
		final CMSSiteModel siteModel = mock(CMSSiteModel.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final Class emailContextClass = TestEmailContext.class;

		given(rendererTemplateModel.getContextClass()).willReturn(emailContextClass.getName());

		given(configurationService.getConfiguration()).willReturn(configuration);
		given(businessProcessModel.getSite()).willReturn(siteModel);
		given(siteModel.getUid()).willReturn("electronics");
		given(businessProcessModel.getCustomer()).willReturn(customerModel);
		given(customerModel.getDisplayName()).willReturn("a b");
		given(customerEmailResolutionService.getEmailForCustomer(customerModel)).willReturn("a@b.com");

		final Map<String, String> map = new HashMap<String, String>();
		map.put("commonResourceUrl", "{baseUrl}/_ui/common");
		emailContextFactory.setEmailContextVariables(map);

		doReturn(emailContext).when(emailContextFactory).resolveEmailContext(rendererTemplateModel);

		final AbstractEmailContext result = emailContextFactory.create(businessProcessModel, emailPageModel, rendererTemplateModel);

		Assert.assertNotNull(result);
		Assert.assertEquals("a@b.com", result.getEmail());
		Assert.assertEquals("a b", result.getDisplayName());
		final Object baseUrl = result.get("baseUrl");
		final String commonResourceUrl = baseUrl != null ? baseUrl + "/_ui/common" : "/_ui/common";
		Assert.assertEquals(commonResourceUrl, result.get("commonResourceUrl"));
	}



	class TestEmailContext extends AbstractEmailContext
	{
		@Override
		public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel)
		{
			super.init(businessProcessModel, emailPageModel);
		}

		@Override
		protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel)
		{
			return ((StoreFrontCustomerProcessModel) businessProcessModel).getSite();
		}

		@Override
		protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel)
		{
			return ((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer();
		}

		@Override
		protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel)
		{
			return ((StoreFrontCustomerProcessModel) businessProcessModel).getLanguage();
		}
	}
}
