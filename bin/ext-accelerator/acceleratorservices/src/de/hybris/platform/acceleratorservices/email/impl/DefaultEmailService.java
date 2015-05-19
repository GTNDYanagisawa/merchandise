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

import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.email.strategy.EmailAddressFetchStrategy;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.media.NoDataAvailableException;
import de.hybris.platform.util.mail.MailUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Service to create and send emails.
 */
public class DefaultEmailService extends AbstractBusinessService implements EmailService
{
	private static final Logger LOG = Logger.getLogger(DefaultEmailService.class);

	public static final String EMAILSERVICE_SEND_ENABLED_CONFIG_KEY = "emailservice.send.enabled";
	public static final int EMAIL_BODY_MAX_LENGTH = 4000;
	public static final String EMAIL_BODY_ENCODING = "UTF-8";
	public static final String EMAIL_BODY_MIME_TYPE = "text/html";//"text/plain";

	private String emailAttachmentsMediaFolderName;
	private String emailBodyMediaFolderName;
	private MediaService mediaService;
	private ConfigurationService configurationService;
	private CatalogVersionService catalogVersionService;
	private CatalogService catalogService;

	private EmailAddressFetchStrategy emailAddressFetchStrategy;

	@Override
	public EmailAddressModel getOrCreateEmailAddressForEmail(final String emailAddress, final String displayName)
	{
		return getEmailAddressFetchStrategy().fetch(emailAddress, displayName);
	}

	@Override
	public EmailAttachmentModel createEmailAttachment(final DataInputStream masterDataStream, final String filename,
			final String mimeType)
	{
		final EmailAttachmentModel attachment = getModelService().create(EmailAttachmentModel.class);
		attachment.setCode(filename);
		attachment.setMime(mimeType);
		attachment.setRealFileName(filename);
		attachment.setCatalogVersion(getCatalogVersion());
		getModelService().save(attachment);

		getMediaService().setStreamForMedia(attachment, masterDataStream, filename, mimeType, getEmailAttachmentsMediaFolder());
		return attachment;
	}

	protected MediaFolderModel getEmailAttachmentsMediaFolder()
	{
		return getMediaService().getFolder(getEmailAttachmentsMediaFolderName());
	}

	protected MediaFolderModel getEmailBodyMediaFolder()
	{
		return getMediaService().getFolder(getEmailBodyMediaFolderName());
	}

	@Override
	public EmailMessageModel createEmailMessage(final List<EmailAddressModel> toAddresses,
			final List<EmailAddressModel> ccAddresses, final List<EmailAddressModel> bccAddresses,
			final EmailAddressModel fromAddress, final String replyToAddress, final String subject, final String body,
			final List<EmailAttachmentModel> attachments)
	{
		// Do all validation now before creating the message
		if (toAddresses == null || toAddresses.isEmpty())
		{
			throw new IllegalArgumentException("toAddresses must not be empty");
		}
		if (fromAddress == null)
		{
			throw new IllegalArgumentException("fromAddress must not be null");
		}
		if (subject == null || subject.isEmpty())
		{
			throw new IllegalArgumentException("subject must not be empty");
		}
		if (body == null || body.isEmpty())
		{

			throw new IllegalArgumentException("body must not be empty");
		}
		validateEmailAddress(replyToAddress, "replyToAddress");

		final EmailMessageModel emailMessageModel = getModelService().create(EmailMessageModel.class);
		emailMessageModel.setToAddresses(toAddresses);
		emailMessageModel.setCcAddresses(ccAddresses);
		emailMessageModel.setBccAddresses(bccAddresses);
		emailMessageModel.setFromAddress(fromAddress);
		emailMessageModel.setReplyToAddress((replyToAddress != null && !replyToAddress.isEmpty()) ? replyToAddress : fromAddress
				.getEmailAddress());
		emailMessageModel.setSubject(subject);
		emailMessageModel.setAttachments(attachments);
		if (body.length() < EMAIL_BODY_MAX_LENGTH)
		{
			emailMessageModel.setBody(body);
			getModelService().save(emailMessageModel);
		}
		else
		{
			getModelService().save(emailMessageModel);
			final MediaModel bodyMedia = createBodyMedia("bodyMedia-" + emailMessageModel.getPk(), body);
			emailMessageModel.setBodyMedia(bodyMedia);
			getModelService().save(emailMessageModel);
		}

		return emailMessageModel;
	}

	/**
	 * Method creates MediaModel object for storing email body
	 * 
	 * @param bodyMediaName
	 *           - name for created object
	 * @param body
	 *           - content of email body
	 * @return created MediaModel object
	 */
	protected MediaModel createBodyMedia(final String bodyMediaName, final String body)
	{
		final MediaModel bodyMedia = getModelService().create(MediaModel.class);
		bodyMedia.setCatalogVersion(getCatalogVersion());
		bodyMedia.setCode(bodyMediaName);
		bodyMedia.setMime(EMAIL_BODY_MIME_TYPE);
		bodyMedia.setRealFileName(bodyMediaName);
		getModelService().save(bodyMedia);

		final MediaFolderModel mediaFolderModel = getEmailBodyMediaFolder();
		InputStream dataStream = null;
		try
		{
			try
			{
				dataStream = new ByteArrayInputStream(body.getBytes(EMAIL_BODY_ENCODING));
			}
			catch (final UnsupportedEncodingException e)
			{
				dataStream = new ByteArrayInputStream(body.getBytes());
				LOG.warn("emailBody - UnsupportedEncodingException", e);
			}
			mediaService.setStreamForMedia(bodyMedia, dataStream, bodyMediaName, EMAIL_BODY_MIME_TYPE, mediaFolderModel);
		}
		finally
		{
			try
			{
				if (dataStream != null)
				{
					dataStream.close();
				}
			}
			catch (final IOException e)
			{
				// ignored
			}
		}
		return bodyMedia;
	}

	protected CatalogVersionModel getCatalogVersion()
	{
		CatalogVersionModel catalogVersion = getCatalogService().getDefaultCatalog() == null ? null : getCatalogService()
				.getDefaultCatalog().getActiveCatalogVersion();
		if (catalogVersion == null)
		{
			final Collection<CatalogVersionModel> catalogs = getCatalogVersionService().getSessionCatalogVersions();
			for (final CatalogVersionModel cvm : catalogs)
			{
				if (cvm.getCatalog() instanceof ContentCatalogModel)
				{
					catalogVersion = cvm;
					break;
				}
			}
		}

		return catalogVersion;
	}

	protected void validateEmailAddress(final String address, final String type)
	{
		try
		{
			if (address != null && !address.isEmpty())
			{
				MailUtils.validateEmailAddress(address, type);
			}
		}
		catch (final EmailException ex)
		{
			throw new IllegalArgumentException(type, ex);
		}
	}

	@Override
	public boolean send(final EmailMessageModel message)
	{
		if (message == null)
		{
			throw new IllegalArgumentException("message must not be null");
		}

		final boolean sendEnabled = getConfigurationService().getConfiguration().getBoolean(EMAILSERVICE_SEND_ENABLED_CONFIG_KEY,
				true);
		if (sendEnabled)
		{
			try
			{
				final HtmlEmail email = getPerConfiguredEmail();
				email.setCharset("UTF-8");

				final List<EmailAddressModel> toAddresses = message.getToAddresses();
				if (CollectionUtils.isNotEmpty(toAddresses))
				{
					email.setTo(getAddresses(toAddresses));
				}
				else
				{
					throw new IllegalArgumentException("message has no To addresses");
				}

				final List<EmailAddressModel> ccAddresses = message.getCcAddresses();
				if (ccAddresses != null && !ccAddresses.isEmpty())
				{
					email.setCc(getAddresses(ccAddresses));
				}

				final List<EmailAddressModel> bccAddresses = message.getBccAddresses();
				if (bccAddresses != null && !bccAddresses.isEmpty())
				{
					email.setBcc(getAddresses(bccAddresses));
				}

				final EmailAddressModel fromAddress = message.getFromAddress();
				email.setFrom(fromAddress.getEmailAddress(), nullifyEmpty(fromAddress.getDisplayName()));

				// Add the reply to if specified
				final String replyToAddress = message.getReplyToAddress();
				if (replyToAddress != null && !replyToAddress.isEmpty())
				{
					email.setReplyTo(Collections.singletonList(createInternetAddress(replyToAddress, null)));
				}

				email.setSubject(message.getSubject());
				email.setHtmlMsg(getBody(message));

				// To support plain text parts use email.setTextMsg()

				final List<EmailAttachmentModel> attachments = message.getAttachments();
				if (attachments != null && !attachments.isEmpty())
				{
					for (final EmailAttachmentModel attachment : attachments)
					{
						try
						{
							final DataSource dataSource = new ByteArrayDataSource(getMediaService().getDataFromMedia(attachment),
									attachment.getMime());
							email.attach(dataSource, attachment.getRealFileName(), attachment.getAltText());
						}
						catch (final EmailException ex)
						{
							LOG.error("Failed to load attachment data into data source [" + attachment + "]", ex);
							return false;
						}
					}
				}

				// Important to log all emails sent out
				LOG.info("Sending Email [" + message.getPk() + "] To [" + convertToStrings(toAddresses) + "] From ["
						+ fromAddress.getEmailAddress() + "] Subject [" + email.getSubject() + "]");

				// Send the email and capture the message ID
				final String messageID = email.send();

				message.setSent(true);
				message.setSentMessageID(messageID);
				message.setSentDate(new Date());
				getModelService().save(message);

				return true;
			}
			catch (final EmailException e)
			{
				LOG.warn("Could not send e-mail pk [" + message.getPk() + "] subject [" + message.getSubject() + "] cause: "
						+ e.getMessage());
				if (LOG.isDebugEnabled())
				{
					LOG.debug(e);
				}
			}
		}
		else
		{
			LOG.warn("Could not send e-mail pk [" + message.getPk() + "] subject [" + message.getSubject() + "]");
			LOG.info("Email sending has been disabled. Check the config property 'emailservice.send.enabled'");
			return true;
		}

		return false;
	}

	protected HtmlEmail getPerConfiguredEmail() throws EmailException
	{
		return (HtmlEmail) MailUtils.getPreConfiguredEmail();
	}

	/**
	 * Method checks which attribute is storing email body (bodyMedia or body) and return its content as string
	 * 
	 * @param message
	 *           - email message object
	 * @return email body
	 */
	protected String getBody(final EmailMessageModel message)
	{
		if (message.getBodyMedia() != null)
		{
			final MediaModel media = message.getBodyMedia();
			String body;
			try
			{
				body = new String(mediaService.getDataFromMedia(media), EMAIL_BODY_ENCODING);
			}
			catch (final NoDataAvailableException e)
			{
				return message.getBody();
			}
			catch (final UnsupportedEncodingException e)
			{
				body = new String(mediaService.getDataFromMedia(media));
			}
			return body;
		}
		else
		{
			return message.getBody();
		}
	}

	protected String nullifyEmpty(final String str)
	{
		if (str != null && str.isEmpty())
		{
			return null;
		}
		return str;
	}

	protected Collection<InternetAddress> getAddresses(final List<EmailAddressModel> emailAddresses)
	{
		final Collection<InternetAddress> internetAddresses = new ArrayList<InternetAddress>();

		for (final EmailAddressModel emailAddress : emailAddresses)
		{
			try
			{
				internetAddresses.add(createInternetAddress(emailAddress.getEmailAddress(), emailAddress.getDisplayName()));
			}
			catch (final EmailException e)
			{
				LOG.warn("Failed to lookup to address [" + emailAddress + "]", e);
			}
		}
		return internetAddresses;
	}

	protected InternetAddress createInternetAddress(final String emailAddress, final String displayName) throws EmailException
	{
		try
		{
			final InternetAddress address = new InternetAddress(emailAddress);
			address.setPersonal(StringUtils.isNotBlank(displayName) ? displayName : emailAddress);
			address.validate();
			return address;
		}
		catch (final AddressException e)
		{
			throw new EmailException(e);
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new EmailException(e);
		}
	}

	protected List<String> convertToStrings(final List<EmailAddressModel> addresses)
	{
		final List<String> strings = new ArrayList<String>();
		if (addresses != null && !addresses.isEmpty())
		{
			for (final EmailAddressModel item : addresses)
			{
				strings.add(item.getEmailAddress());
			}
		}
		return strings;
	}

	public EmailAddressFetchStrategy getEmailAddressFetchStrategy()
	{
		return emailAddressFetchStrategy;
	}

	@Required
	public void setEmailAddressFetchStrategy(final EmailAddressFetchStrategy emailAddressFetchStrategy)
	{
		this.emailAddressFetchStrategy = emailAddressFetchStrategy;
	}

	protected String getEmailAttachmentsMediaFolderName()
	{
		return emailAttachmentsMediaFolderName;
	}

	@Required
	public void setEmailAttachmentsMediaFolderName(final String emailAttachmentsMediaFolderName)
	{
		this.emailAttachmentsMediaFolderName = emailAttachmentsMediaFolderName;
	}

	protected String getEmailBodyMediaFolderName()
	{
		return emailBodyMediaFolderName;
	}

	@Required
	public void setEmailBodyMediaFolderName(final String emailBodyMediaFolderName)
	{
		this.emailBodyMediaFolderName = emailBodyMediaFolderName;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected CatalogService getCatalogService()
	{
		return catalogService;
	}

	@Required
	public void setCatalogService(final CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}
}
