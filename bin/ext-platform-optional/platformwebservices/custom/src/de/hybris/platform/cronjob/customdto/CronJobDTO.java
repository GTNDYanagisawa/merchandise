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
package de.hybris.platform.cronjob.customdto;

import de.hybris.platform.core.dto.ItemDTO;
import de.hybris.platform.core.dto.c2l.CurrencyDTO;
import de.hybris.platform.core.dto.c2l.LanguageDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.cronjob.dto.JobDTO;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.HybrisEnumValueToStringConverter;
import de.hybris.platform.webservices.objectgraphtransformer.StringToHybrisEnumValueConverter;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@GraphNode(target = CronJobModel.class, factory = GenericNodeFactory.class, uidProperties = "code")
@XmlRootElement(name = "cronjob")
public class CronJobDTO extends ItemDTO
{

	protected final Set<String> modifiedPropsSet = new HashSet<String>();

	// unique attributes
	private String code = null;

	// primitive attributes
	private Boolean logToDatabase = null;
	private Boolean logToFile = null;
	private String emailAddress = null;
	private String timeTable = null;
	private Boolean requestAbort = null;
	private Boolean changeRecordingEnabled = null;
	private Integer nodeID = null;
	private Boolean singleExecutable = null;
	private Boolean removeOnExit = null;
	private String logText = null;
	private Boolean requestAbortStep = null;
	private Integer priority = null;
	private Boolean active = null;

	// references
	private UserDTO sessionUser = null;
	private CurrencyDTO sessionCurrency = null;
	private LanguageDTO sessionLanguage = null;

	// refers string
	private String status = null; //CronJobStatus
	private String logLevelFile = null; //JobLogLevel
	private String result = null; //CronJobResult
	//private String job = null; //JobModel
	private JobDTO job = null; //JobModel
	private String logLevelDatabase = null; //JobLogLevel
	private String errorMode = null; // ErrorMode

	@XmlAttribute
	public String getCode()
	{
		return code;
	}


	public Boolean getLogToDatabase()
	{
		return logToDatabase;
	}

	public Boolean getLogToFile()
	{
		return logToFile;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public String getTimeTable()
	{
		return timeTable;
	}

	public Boolean getRequestAbort()
	{
		return requestAbort;
	}

	public Boolean getChangeRecordingEnabled()
	{
		return changeRecordingEnabled;
	}

	public Integer getNodeID()
	{
		return nodeID;
	}

	public Boolean getSingleExecutable()
	{
		return singleExecutable;
	}

	public Boolean getRemoveOnExit()
	{
		return removeOnExit;
	}

	public String getLogText()
	{
		return logText;
	}

	public Boolean getRequestAbortStep()
	{
		return requestAbortStep;
	}

	public Integer getPriority()
	{
		return priority;
	}

	public Boolean getActive()
	{
		return active;
	}

	public UserDTO getSessionUser()
	{
		return sessionUser;
	}

	public CurrencyDTO getSessionCurrency()
	{
		return sessionCurrency;
	}

	public LanguageDTO getSessionLanguage()
	{
		return sessionLanguage;
	}

	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getStatus()
	{
		return status;
	}

	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getLogLevelFile()
	{
		return logLevelFile;
	}

	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getResult()
	{
		return result;
	}

	//	@GraphProperty(interceptor = StringToJobModelConverter.class)
	//	public String getJob()
	//	{
	//		return job;
	//	}

	public JobDTO getJob()
	{
		return this.job;
	}

	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getLogLevelDatabase()
	{
		return logLevelDatabase;
	}

	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getErrorMode()
	{
		return errorMode;
	}

	public void setCode(final String code)
	{
		this.modifiedPropsSet.add("code");
		this.code = code;
	}


	public void setLogToDatabase(final Boolean logToDatabase)
	{
		this.modifiedPropsSet.add("logToDatabase");
		this.logToDatabase = logToDatabase;
	}

	public void setLogToFile(final Boolean logToFile)
	{
		this.modifiedPropsSet.add("logToFile");
		this.logToFile = logToFile;
	}

	public void setEmailAddress(final String emailAddress)
	{
		this.modifiedPropsSet.add("emailAddress");
		this.emailAddress = emailAddress;
	}

	public void setTimeTable(final String timeTable)
	{
		this.modifiedPropsSet.add("timeTable");
		this.timeTable = timeTable;
	}

	public void setRequestAbort(final Boolean requestAbort)
	{
		this.modifiedPropsSet.add("requestAbort");
		this.requestAbort = requestAbort;
	}

	public void setChangeRecordingEnabled(final Boolean changeRecordingEnabled)
	{
		this.modifiedPropsSet.add("changeRecordingEnabled");
		this.changeRecordingEnabled = changeRecordingEnabled;
	}

	public void setNodeID(final Integer nodeID)
	{
		this.modifiedPropsSet.add("nodeID");
		this.nodeID = nodeID;
	}

	public void setSingleExecutable(final Boolean singleExecutable)
	{
		this.modifiedPropsSet.add("singleExecutable");
		this.singleExecutable = singleExecutable;
	}

	public void setRemoveOnExit(final Boolean removeOnExit)
	{
		this.modifiedPropsSet.add("removeOnExit");
		this.removeOnExit = removeOnExit;
	}

	public void setLogText(final String logText)
	{
		this.modifiedPropsSet.add("logText");
		this.logText = logText;
	}

	public void setRequestAbortStep(final Boolean requestAbortStep)
	{
		this.modifiedPropsSet.add("requestAbortStep");
		this.requestAbortStep = requestAbortStep;
	}

	public void setPriority(final Integer priority)
	{
		this.modifiedPropsSet.add("priority");
		this.priority = priority;
	}

	public void setActive(final Boolean active)
	{
		this.modifiedPropsSet.add("active");
		this.active = active;
	}

	public void setSessionUser(final UserDTO sessionUser)
	{
		this.modifiedPropsSet.add("sessionUser");
		this.sessionUser = sessionUser;
	}

	public void setSessionCurrency(final CurrencyDTO sessionCurrency)
	{
		this.modifiedPropsSet.add("sessionCurrency");
		this.sessionCurrency = sessionCurrency;
	}

	public void setSessionLanguage(final LanguageDTO sessionLanguage)
	{
		this.modifiedPropsSet.add("sessionLanguage");
		this.sessionLanguage = sessionLanguage;
	}

	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setStatus(final String status)
	{
		this.modifiedPropsSet.add("status");
		this.status = status;
	}

	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setResult(final String result)
	{
		this.modifiedPropsSet.add("result");
		this.result = result;
	}

	//	@GraphProperty(interceptor = JobModelToStringConverter.class)
	//	public void setJob(final String job)
	//	{
	//		this.modifiedPropsSet.add("job");
	//		this.job = job;
	//	}

	//@GraphProperty(interceptor = JobModelToStringConverter.class)
	public void setJob(final JobDTO job)
	{
		this.modifiedPropsSet.add("job");
		this.job = job;
	}


	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setLogLevelFile(final String logLevelFile)
	{
		this.modifiedPropsSet.add("logLevelFile");
		this.logLevelFile = logLevelFile;
	}

	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setLogLevelDatabase(final String logLevelDatabase)
	{
		this.modifiedPropsSet.add("logLevelDatabase");
		this.logLevelDatabase = logLevelDatabase;
	}

	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setErrorMode(final String errorMode)
	{
		this.modifiedPropsSet.add("errorMode");
		this.errorMode = errorMode;
	}

	@Override
	public Set<String> getModifiedProperties()
	{
		return this.modifiedPropsSet;
	}

}
