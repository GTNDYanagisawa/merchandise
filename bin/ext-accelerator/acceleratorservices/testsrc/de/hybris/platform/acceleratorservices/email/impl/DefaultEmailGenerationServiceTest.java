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
package de.hybris.platform.acceleratorservices.email.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.acceleratorservices.process.email.context.EmailContextFactory;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 */
@UnitTest
public class DefaultEmailGenerationServiceTest
{
	private DefaultEmailGenerationService emailGenerationService;

	@Mock
	private EmailService emailService;
	@Mock
	private RendererService rendererService;
	@Mock
	private EmailContextFactory emailContextFactory;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		emailGenerationService = new DefaultEmailGenerationService();
		emailGenerationService.setEmailService(emailService);
		emailGenerationService.setRendererService(rendererService);
		emailGenerationService.setEmailContextFactory(emailContextFactory);
		emailGenerationService.setModelService(modelService);
	}

	@Test
	public void testGenerate()
	{
		final EmailMessageModel emailMessageModel = mock(EmailMessageModel.class);

		final EmailPageModel emailPageModel = mock(EmailPageModel.class);
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);
		final EmailPageTemplateModel emailPageTemplateModel = mock(EmailPageTemplateModel.class);
		final RendererTemplateModel renderTemplate = mock(RendererTemplateModel.class);
		final AbstractEmailContext emailContext = mock(AbstractEmailContext.class);
		final RendererTemplateModel subjectRenderTemplate = mock(RendererTemplateModel.class);
		final EmailAddressModel address = mock(EmailAddressModel.class);

		given(emailPageModel.getMasterTemplate()).willReturn(emailPageTemplateModel);
		given(emailPageTemplateModel.getHtmlTemplate()).willReturn(renderTemplate);
		given(emailContextFactory.create(businessProcessModel, emailPageModel, renderTemplate)).willReturn(emailContext);
		given(emailPageTemplateModel.getSubject()).willReturn(subjectRenderTemplate);
		given(renderTemplate.getContextClass()).willReturn("TestEmailContext");
		given(emailContext.getToEmail()).willReturn("a@b.com");
		given(emailContext.getToDisplayName()).willReturn("a b");
		given(emailContext.getFromEmail()).willReturn("customerservices@hybris.com");
		given(emailContext.getFromDisplayName()).willReturn("Customer Services");
		given(emailService.getOrCreateEmailAddressForEmail(any(String.class), any(String.class))).willReturn(address);
		given(
				emailService.createEmailMessage(Mockito.any(List.class), Mockito.any(List.class), Mockito.any(List.class),
						any(EmailAddressModel.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
						Mockito.any(List.class))).willReturn(emailMessageModel);

		EmailMessageModel result = null;
		try
		{
			result = emailGenerationService.generate(businessProcessModel, emailPageModel);
		}
		catch (final RuntimeException e)
		{
			Assert.fail("RuntimeException was thrown");
		}
		verify(rendererService, times(2)).render(any(RendererTemplateModel.class), any(Map.class), any(StringWriter.class));
		Assert.assertEquals(emailMessageModel, result);
	}
}
