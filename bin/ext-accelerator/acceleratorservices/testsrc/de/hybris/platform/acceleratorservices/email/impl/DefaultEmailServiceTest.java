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
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.email.strategy.EmailAddressFetchStrategy;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.DataInputStream;
import java.util.Collections;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultEmailServiceTest
{
	private DefaultEmailService emailService;

	@Mock
	private EmailAddressDao emailAddressDao; //NOPMD
	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private EmailAddressFetchStrategy emailAddressFetchStrategy;
	@Mock
	private CatalogService catalogService;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private HtmlEmail email;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		emailService = new DefaultEmailService()
		{
			@Override
			protected HtmlEmail getPerConfiguredEmail() throws EmailException
			{
				return email;
			}

			@Override
			protected void validateEmailAddress(final String address, final String type)
			{
				// empty
			}
		};
		emailService.setMediaService(mediaService);
		emailService.setModelService(modelService);
		emailService.setConfigurationService(configurationService);
		emailService.setEmailAttachmentsMediaFolderName("EmailAttachments");
		emailService.setEmailAddressFetchStrategy(emailAddressFetchStrategy);
		emailService.setCatalogService(catalogService);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(catalogService.getDefaultCatalog()).willReturn(catalogModel);
		given(catalogModel.getActiveCatalogVersion()).willReturn(catalogVersionModel);
		given(Boolean.valueOf(configuration.getBoolean(DefaultEmailService.EMAILSERVICE_SEND_ENABLED_CONFIG_KEY, true)))
				.willReturn(Boolean.TRUE);
	}

	@Test
	public void testCreateEmailAttachment()
	{
		final DataInputStream inputStream = mock(DataInputStream.class);

		final EmailAttachmentModel emailAttachmentModel = mock(EmailAttachmentModel.class);
		given(modelService.create(EmailAttachmentModel.class)).willReturn(emailAttachmentModel);
		final MediaFolderModel folderModel = mock(MediaFolderModel.class);
		given(mediaService.getFolder(any(String.class))).willReturn(folderModel);
		final EmailAttachmentModel attachment = emailService.createEmailAttachment(inputStream, "test", "image/jpeg");

		verify(modelService, times(1)).create(EmailAttachmentModel.class);
		verify(mediaService, times(1)).setStreamForMedia(any(EmailAttachmentModel.class), any(DataInputStream.class),
				any(String.class), any(String.class), any(MediaFolderModel.class));
		Assert.assertEquals(emailAttachmentModel, attachment);
	}

	@Test
	public void testCreateEmailMessage()
	{
		final EmailAddressModel toAddress = mock(EmailAddressModel.class);
		final EmailAddressModel fromAddress = mock(EmailAddressModel.class);
		final EmailMessageModel emailMessageModel = mock(EmailMessageModel.class);

		given(modelService.create(EmailMessageModel.class)).willReturn(emailMessageModel);

		final EmailMessageModel message = emailService.createEmailMessage(Collections.singletonList(toAddress), null, null,
				fromAddress, "reply@hybris.com", "subject", "body", null);

		verify(modelService, times(1)).create(EmailMessageModel.class);
		Assert.assertEquals(emailMessageModel, message);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendIllegalArgumentException()
	{
		final EmailMessageModel emailMessageModel = mock(EmailMessageModel.class);
		final boolean result = emailService.send(emailMessageModel);
		Assert.assertTrue(result);
	}

	@Test
	public void testSend()
	{
		final EmailAddressModel toAddress = mock(EmailAddressModel.class);
		final EmailAddressModel fromAddress = mock(EmailAddressModel.class);
		final EmailMessageModel emailMessageModel = mock(EmailMessageModel.class);
		given(emailMessageModel.getToAddresses()).willReturn(Collections.singletonList(toAddress));
		given(emailMessageModel.getFromAddress()).willReturn(fromAddress);
		given(toAddress.getEmailAddress()).willReturn("ramana@neoworks.com");
		given(toAddress.getDisplayName()).willReturn("ramana ulluri");
		given(fromAddress.getEmailAddress()).willReturn("customerservices@hybris.com");
		given(fromAddress.getDisplayName()).willReturn("Customer Services");
		given(emailMessageModel.getSubject()).willReturn("subject - test");
		given(emailMessageModel.getBody()).willReturn(
				"body - This is a test email from CommerceServices.DefaultEmailServiceTest.testSend()");

		try
		{
			when(email.send()).thenReturn("messageId");
		}
		catch (final EmailException e)
		{
			Assert.fail("EmailException was thrown");
		}

		final boolean result = emailService.send(emailMessageModel);

		verify(modelService, times(1)).save(emailMessageModel);
		Assert.assertTrue(result);
	}

	@Test
	public void testSendWithAttachments()
	{
		final EmailAddressModel toAddress = mock(EmailAddressModel.class);
		final EmailAddressModel fromAddress = mock(EmailAddressModel.class);
		final EmailMessageModel emailMessageModel = mock(EmailMessageModel.class);
		final HtmlEmail email = mock(HtmlEmail.class);
		given(emailMessageModel.getToAddresses()).willReturn(Collections.singletonList(toAddress));
		given(emailMessageModel.getFromAddress()).willReturn(fromAddress);
		given(toAddress.getEmailAddress()).willReturn("ramana@neoworks.com");
		given(toAddress.getDisplayName()).willReturn("ramana ulluri");
		given(fromAddress.getEmailAddress()).willReturn("customerservices@hybris.com");
		given(fromAddress.getDisplayName()).willReturn("Customer Services");
		given(emailMessageModel.getSubject()).willReturn("subject - test");
		given(emailMessageModel.getBody())
				.willReturn(
						"body - This is a test email with dummy attachment from CommerceServices.DefaultEmailServiceTest.testSendWithAttachments()");

		final EmailAttachmentModel attachment = mock(EmailAttachmentModel.class);
		given(mediaService.getDataFromMedia(attachment)).willReturn(new byte[] {});
		given(emailMessageModel.getAttachments()).willReturn(Collections.singletonList(attachment));
		given(attachment.getMime()).willReturn("image/jpeg");
		given(attachment.getRealFileName()).willReturn("test");

		try
		{
			when(email.send()).thenReturn("messageId");
		}
		catch (final EmailException e)
		{
			Assert.fail("EmailException was thrown");
		}

		final boolean result = emailService.send(emailMessageModel);

		verify(attachment, times(1)).getMime();
		verify(modelService, times(1)).save(emailMessageModel);

		Assert.assertTrue(result);
	}
}
