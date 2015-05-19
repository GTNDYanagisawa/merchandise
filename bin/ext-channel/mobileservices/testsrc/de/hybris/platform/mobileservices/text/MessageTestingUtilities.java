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
package de.hybris.platform.mobileservices.text;

import de.hybris.platform.mobileservices.enums.MessageType;
import de.hybris.platform.mobileservices.enums.MobileMessageError;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.text.testimpl.BAM390DetectorTask;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;


/**
 * 
 */
@Ignore("BAM-390 should be abstract class, but some test instanciate this utility method. The ignore is fine here")
public class MessageTestingUtilities
{
	private static final Logger LOG = Logger.getLogger(MessageTestingUtilities.class.getName());

	private final ModelService modelService;
	private final BAM390DetectorTask bogusCatcher;

	public MessageTestingUtilities(final ModelService modelService, final BAM390DetectorTask bogusCatcher)
	{
		this.modelService = modelService;
		this.bogusCatcher = bogusCatcher;
	}

	public static boolean isTextServiceIllegalStateReplyFailure(final MobileMessageContextModel message)
	{

		final boolean failedForStatus = message != null && message.getStatus() == MobileMessageStatus.ERROR
				&& StringUtils.contains(message.getMessageErrorDescription(), "must be in state PROCESSING");
		final boolean failedForType = message != null && message.getStatus() == MobileMessageStatus.ERROR
				&& StringUtils.contains(message.getMessageErrorDescription(), "must be of type INCOMING");
		final boolean failedForPhone = message != null && message.getStatus() == MobileMessageStatus.ERROR
				&& StringUtils.contains(message.getMessageErrorDescription(), "has go no phone number");

		final boolean bam390 = failedForStatus || failedForType || failedForPhone;

		if (bam390)
		{
			final MobileMessageStatus beforeStatus = message.getStatus();
			final MobileMessageError beforeError = message.getMessageError();
			final MessageType beforeType = message.getType();
			final String beforeNormalizedPhoneNumber = message.getNormalizedPhoneNumber();

			LOG.error("BAM390(pk:" + message.getPk() + "){Status->" + beforeStatus + ":Type->" + beforeType + ":error->"
					+ beforeError + ":number->" + beforeNormalizedPhoneNumber + "}");

		}
		return bam390;
	}

	public boolean isModelServiceBogus(final MobileMessageContextModel message)
	{
		final int maxTaskWaitSeconds = 10;
		return _isModelServiceBogus(message, message.getStatus(), maxTaskWaitSeconds);
	}

	public boolean isModelServiceBogusStressTest(final MobileMessageContextModel message, final int maxTurns)
	{
		final int maxTaskWaitSeconds = 10;
		if (_isModelServiceBogus(message, message.getStatus(), maxTaskWaitSeconds))
		{
			return true;
		}

		for (int i = 0; i < maxTurns; i++)
		{
			if (changeStatusAndCheck(i, message, maxTaskWaitSeconds))
			{
				return true;
			}
		}
		return false;
	}

	private boolean changeStatusAndCheck(final int turn, final MobileMessageContextModel message, final int maxWaitSeconds)
	{
		modelService.refresh(message);
		final MobileMessageStatus oldStatus = message.getStatus();

		final MobileMessageStatus newStatus = turn % 2 == 0 ? MobileMessageStatus.VERIFYING : MobileMessageStatus.SCHEDULED;
		message.setStatus(newStatus); // a non final status so that it does not occur naturally on the message
		modelService.save(message);

		if (_isModelServiceBogus(message, newStatus, maxWaitSeconds))
		{
			return true;
		}
		// Restore the message
		modelService.refresh(message);
		message.setStatus(oldStatus);
		modelService.save(message);

		return false;
	}

	public static String toString(final Object object)
	{
		return object == null ? "" : object.toString();
	}

	private boolean _isModelServiceBogus(final MobileMessageContextModel message, final MobileMessageStatus expectedStatus,
			final int maxWaitSeconds)
	{
		final int waitPerTurnMs = 50;
		final int waitTurns = (maxWaitSeconds * 1000) / waitPerTurnMs;

		modelService.refresh(message);

		final String statusStr = toString(expectedStatus);
		final String typeStr = toString(message.getType());
		final String msgErrorStr = toString(message.getMessageError());
		final String textStr = toString(message.getOutgoingText());


		final TaskModel task = bogusCatcher.requestTestFor(message);
		// wait for task being removed -> that signals task engine has processed it
		for (int i = 0; i < waitTurns && !modelService.isRemoved(task); i++)
		{
			try
			{
				Thread.sleep(waitPerTurnMs);
			}
			catch (final InterruptedException e)
			{
				LOG.error("Error waiting for task execution", e);
			}
		}
		if (!modelService.isRemoved(task))
		{
			LOG.error("Message (pk:" + message.getPk() + ") task has not been removed yet after waiting for " + maxWaitSeconds);
			return false;
		}

		if (!bogusCatcher.testResultsAvailable(message))
		{
			LOG.error("Message (pk:" + message.getPk() + ") task has not recorded any data!?");
			return false;
		}

		// Check discrepancies
		final String testStatus = bogusCatcher.getTestResultFor(message, "status");
		if (!StringUtils.equals(testStatus, statusStr))
		{
			LOG.error("Message (pk:" + message.getPk() + ") field status. Model in Task contains "
					+ testStatus + " test suite reads " + statusStr);
			return true;
		}

		final String testType = bogusCatcher.getTestResultFor(message, "type");
		if (!StringUtils.equals(testType, typeStr))
		{
			LOG.error("Message (pk:" + message.getPk() + ") field type. Model in Task contains "
					+ testType + " test suite reads " + typeStr);
			return true;
		}

		final String testMsgError = bogusCatcher.getTestResultFor(message, "messageError");
		if (!StringUtils.equals(testMsgError, msgErrorStr))
		{
			LOG.error("Message (pk:" + message.getPk() + ") field messageError. Model in Task contains "
					+ testMsgError + " test suite reads " + msgErrorStr);
			return true;
		}

		final String testMsg = bogusCatcher.getTestResultFor(message, "outgoingText");
		if (!StringUtils.equals(testMsg, textStr))
		{
			LOG.error("Message (pk:" + message.getPk() + ") field outgoingText. Model in Task contains "
					+ testMsg + " test suite reads " + textStr);
			return true;
		}
		return false;
	}
}
