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
package de.hybris.platform.webservices;

import de.hybris.platform.core.PK.PKException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservices.cache.impl.AbstractCachingStrategy;
import de.hybris.platform.webservices.paging.PagingStrategy;
import de.hybris.platform.webservices.processchain.ConfigurableRequestProcessChain;
import de.hybris.platform.webservices.processchain.ConfigurableRequestProcessChain.RequestExecution;
import de.hybris.platform.webservices.processor.RequestProcessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.apache.log4j.Logger;

import com.sun.jersey.api.core.ResourceContext;


@Produces(
{ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public abstract class AbstractResource<RESOURCE> implements RestResource<RESOURCE>, CrudActionNotifable<RESOURCE>
{
	private static final Logger LOG = Logger.getLogger(AbstractResource.class);

	private static final String QUERYPARAM_REQUESTED_CMD = "cmd";

	/**
	 * Webservice/context specific properties managed and injected by Jersey.
	 */

	/** The resource context is used for obtaining a new instance of a sub- resource. */
	@Context
	protected ResourceContext resourceCtx;

	/** The SecurityContext. */
	@Context
	protected SecurityContext securityCtx;

	/** The URI request context. */
	@Context
	protected UriInfo uriInfo;

	/** The HTTP header information. */
	@Context
	protected HttpHeaders httpHeaders;

	@Context
	protected Providers providers;

	@Context
	protected Request request;

	/** serviceLocator: a locator for available services **/
	protected ServiceLocator serviceLocator;

	private ResponseBuilder response = null;
	private String resourceId = null;
	private RESOURCE resrcEntity = null;

	private boolean resrcEntityResolved = false;

	/** Will be injected by spring during resource creation. */
	private CommandHandler postCommandHandler = null;
	private CommandHandler putCommandHandler = null;

	// http-method handler
	private Class<?> httpGetBuilder = HttpGetResponseBuilder.class;
	private Class<?> httpPutBuilder = HttpPutResponseBuilder.class;
	private Class<?> httpPostBuilder = HttpPostResponseBuilder.class;
	private Class<?> httpDeleteBuilder = HttpDeleteResponseBuilder.class;

	private SecurityStrategy securityStrategy;
	private PagingStrategy pagingStrategy;

	private ConfigurableRequestProcessChain<RESOURCE> requestProcessChain;

	/**
	 * factory bean for creating webservice action events on CRUD methods
	 */
	private EventActionFactory eventActionFactory;

	/**
	 * parent resource instance for recognizing resource being called as subresource in context of other root resource -
	 * in such case parentResourceEntity is being set up
	 */
	private AbstractResource parentResource = null;

	@Override
	public SecurityContext getSecurityContext()
	{
		return this.securityCtx;
	}

	@Override
	public Request getRequest()
	{
		return this.request;
	}

	/**
	 * Returns the {@link RestResource} which was executed as parent of current resource.
	 * 
	 * @return
	 */
	public AbstractResource getParentResource()
	{
		return parentResource;
	}

	/**
	 * @param parentResource
	 *           the parentResource to set
	 */
	public void setParentResource(final AbstractResource parentResource)
	{
		this.parentResource = parentResource;
	}

	/**
	 * @param postCommandHandler
	 *           the postCommandHandler to set
	 */
	public void setPostCommandHandler(final CommandHandler postCommandHandler)
	{
		this.postCommandHandler = postCommandHandler;
	}

	/**
	 * @return the postCommandHandler
	 */
	public CommandHandler getPostCommandHandler()
	{
		return postCommandHandler;
	}

	/**
	 * @param putCommandHandler
	 *           the putCommandHandler to set
	 */
	public void setPutCommandHandler(final CommandHandler putCommandHandler)
	{
		this.putCommandHandler = putCommandHandler;
	}

	@Override
	public CommandHandler getPutCommandHandler()
	{
		return putCommandHandler;
	}

	/**
	 * 
	 * @deprecated since 4.2.2, configure {@link RequestProcessor} instead
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	public void setCachingStrategy(@SuppressWarnings("unused") final AbstractCachingStrategy<RESOURCE> cachingStrategy)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("This is depracted API setCachingStrategy please use setProcessFilter ");
		}
	}


	public SecurityStrategy getSecurityStrategy()
	{
		return securityStrategy;
	}

	public void setSecurityStrategy(final SecurityStrategy securityStrategy)
	{
		this.securityStrategy = securityStrategy;
	}

	/**
	 * @param pagingStrategy
	 *           the pagingStrategy to set
	 */
	public void setPagingStrategy(final PagingStrategy pagingStrategy)
	{
		this.pagingStrategy = pagingStrategy;
	}

	/**
	 * @return the pagingStrategy
	 */
	public PagingStrategy getPagingStrategy()
	{
		return pagingStrategy;
	}

	/**
	 * Sets the ID for this resource.
	 * 
	 * @param resourceId
	 *           the resourceId to set
	 */
	public void setResourceId(final String resourceId)
	{
		this.resourceId = resourceId;
	}

	public String getResourceId()
	{
		return this.resourceId;
	}

	@Override
	public ServiceLocator getServiceLocator()
	{
		return serviceLocator;
	}

	public void setServiceLocator(final ServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}

	public void setEventActionFactory(final EventActionFactory eventActionFactory)
	{
		this.eventActionFactory = eventActionFactory;
	}

	protected EventActionFactory getEventActionFactory()
	{
		return eventActionFactory;
	}

	protected ResponseBuilder getResponse()
	{
		if (this.response == null)
		{
			this.response = Response.ok();
		}
		return this.response;
	}

	/**
	 * Security check for requested resource.
	 * 
	 * @return the response
	 */
	protected boolean isAccessGranted()
	{
		boolean isAllowed = true;
		if (this.securityStrategy != null)
		{
			final boolean isNew;
			if (this instanceof AbstractCollectionResource)
			{
				//POST: isNew flag is always true;
				//GET: the value of 'isNew' will not be taken into account at a later stage
				isNew = true;
			}
			else
			{
				isNew = (getResourceValue() == null);
			}
			final String operation = ((AbstractSecurityStrategy) this.securityStrategy)
					.getOperation(getRequest().getMethod(), isNew);
			isAllowed = this.securityStrategy.isResourceOperationAllowed(this, operation);
		}
		return isAllowed;
	}

	public boolean isAccessGrantedExternal()
	{
		final boolean isAllowed = this.isAccessGranted();

		if (!isAllowed)
		{
			getResponse().type("text/plain");
			getResponse().status(Response.Status.FORBIDDEN);
			getResponse().entity(
					"You do not have permission to request this resource " + "(" + this.getClass().getSimpleName() + ") using "
							+ request.getMethod() + " method.");
		}
		return isAllowed;
	}




	public void setGetMethod(final String className) throws ClassNotFoundException
	{
		this.httpGetBuilder = Thread.currentThread().getContextClassLoader().loadClass(className);
	}

	public void setPutMethod(final String className) throws ClassNotFoundException
	{
		this.httpPutBuilder = Thread.currentThread().getContextClassLoader().loadClass(className);
	}

	public void setPostMethod(final String className) throws ClassNotFoundException
	{
		this.httpPostBuilder = Thread.currentThread().getContextClassLoader().loadClass(className);
	}

	public void setDeleteMethod(final String className) throws ClassNotFoundException
	{
		this.httpDeleteBuilder = Thread.currentThread().getContextClassLoader().loadClass(className);
	}




	/**
	 * Creates and returns a default {@link YResponseBuilder} for a HTTP GET. In addition, method checks whether the
	 * requested user can perform the HTTP GET operation.
	 * 
	 * @return {@link YResponseBuilder}
	 */
	final protected YResponseBuilder createGetResponse()
	{

		final AbstractResponseBuilder result = this.createResponseBuilder(this.httpGetBuilder, null);

		final RequestExecution execution = new RequestExecution()
		{

			@Override
			public void execute(final AbstractResponseBuilder result)
			{
				result.processRequest();
			}
		};


		requestProcessChain.configure(RequestProcessor.RequestType.GET, null, result, execution);
		requestProcessChain.doProcess();
		return result;

	}

	/**
	 * Creates and returns a default {@link YResponseBuilder} for a HTTP DELETE. In addition, method checks whether the
	 * requested user can perform the HTTP DELETE operation.
	 * 
	 * @return {@link YResponseBuilder}
	 */
	protected YResponseBuilder createDeleteResponse()
	{

		final AbstractResponseBuilder result = this.createResponseBuilder(this.httpDeleteBuilder, null);

		final RequestExecution execution = new RequestExecution()
		{

			@Override
			public void execute(final AbstractResponseBuilder result)
			{
				result.processRequest();
			}
		};

		requestProcessChain.configure(RequestProcessor.RequestType.DELETE, null, result, execution);
		requestProcessChain.doProcess();
		return result;
	}

	/**
	 * Creates and returns a default {@link YResponseBuilder} for a HTTP POST. In addition, method checks whether the
	 * requested user can perform the HTTP POST operation.
	 * 
	 * @return {@link YResponseBuilder}
	 */
	protected YResponseBuilder createPostResponse(final Object dto)
	{
		final CommandHandler cmdHandler = getPostCommandHandler();
		final AbstractCommand cmd = AbstractResource.this.findCommand(cmdHandler);
		if (cmd != null)
		{
			cmd.setResource(AbstractResource.this);
			return createPostResponseAsCommandInner(cmd, dto);
		}
		else
		{
			return createPostResponseInner(this.createResponseBuilder(this.httpPostBuilder, dto), dto);
		}
	}

	/**
	 * Internal call for POST processing in case of command call {@link AbstractCommand}.
	 */
	private YResponseBuilder createPostResponseAsCommandInner(final AbstractCommand cmd, final Object dto)
	{
		final RequestExecution execution = new RequestExecution()
		{

			@Override
			public void execute(final AbstractResponseBuilder cmd)
			{
				try
				{
					cmd.setResource(AbstractResource.this);
					cmd.setRequestValue(dto);
					cmd.processRequest();
				}
				catch (final Exception e)
				{
					cmd.processException("Error processing POST command request (" + ((AbstractCommand) cmd).getName() + ")", e);
				}
			}
		};

		requestProcessChain.configure(RequestProcessor.RequestType.POST, dto, cmd, execution);
		requestProcessChain.doProcess();
		return cmd;

	}

	/**
	 * Internal call for POST processing in case of regular {@link AbstractResponseBuilder}.
	 */
	private YResponseBuilder createPostResponseInner(final AbstractResponseBuilder result, final Object dto)
	{
		final RequestExecution execution = new RequestExecution()
		{

			@Override
			public void execute(final AbstractResponseBuilder result)
			{

				try
				{
					result.processRequest(true);
				}
				catch (final Exception e)
				{
					result.processException("Error processing POST request", e);
				}
			}
		};

		requestProcessChain.configure(RequestProcessor.RequestType.POST, dto, result, execution);
		requestProcessChain.doProcess();
		return result;

	}

	protected YResponseBuilder createPutResponse(final Object dto)
	{
		return createPutResponse(dto, true);
	}

	/**
	 * Generic PUT.
	 * <p/>
	 * Merges passed DTO into value of this resource and sets that value as response entity. Id property parameter
	 * indicates the name of the property whose value must correspond with this resource id. If value of id-property is
	 * null, resource id gets injected. If value is not null and doesn't equals resource-id value than, depending on
	 * whether strict is enabled, a BAD_REQUEST is returned. In addition, method checks whether the requested user can
	 * perform the HTTP PUT operation.
	 * 
	 * @param dto
	 * @param strict
	 * @return {@link Response}
	 */
	protected YResponseBuilder createPutResponse(final Object dto, final boolean strict)
	{
		final CommandHandler cmdHandler = getPutCommandHandler();
		final AbstractCommand cmd = AbstractResource.this.findCommand(cmdHandler);

		if (cmd != null)
		{
			cmd.setResource(AbstractResource.this);
			return createPutResponseAsCommandInner(cmd, dto, strict);
		}
		else
		{
			return createPutResponseInner(AbstractResource.this.createResponseBuilder(AbstractResource.this.httpPutBuilder, dto),
					dto, strict);
		}


	}

	/**
	 * Internal call for PUT processing in case of regular {@link AbstractResponseBuilder}.
	 */
	private YResponseBuilder createPutResponseInner(final AbstractResponseBuilder result, final Object dto, final boolean strict)
	{

		final RequestExecution execution = new RequestExecution()
		{
			@Override
			public void execute(final AbstractResponseBuilder result)
			{
				// validate DTO (dto-id <-> resource-id)
				try
				{
					AbstractResource.this.processDtoId(dto, AbstractResource.this.resourceId, strict);
				}
				catch (final Exception e)
				{
					processException("Error processing PUT request", e);
				}
				result.processRequest(true);
			}
		};

		requestProcessChain.configure(RequestProcessor.RequestType.PUT, dto, result, execution);
		requestProcessChain.doProcess();
		return result;
	}

	/**
	 * Internal call for PUT processing in case of command call {@link AbstractCommand}.
	 */
	private YResponseBuilder createPutResponseAsCommandInner(final AbstractCommand result, final Object dto, final boolean strict)
	{

		final RequestExecution execution = new RequestExecution()
		{
			@Override
			public void execute(final AbstractResponseBuilder result)
			{
				// validate DTO (dto-id <-> resource-id)
				try
				{
					AbstractResource.this.processDtoId(dto, AbstractResource.this.resourceId, strict);
				}
				catch (final Exception e)
				{
					processException("Error processing PUT request", e);
				}

				try
				{
					result.setResource(AbstractResource.this);
					result.setRequestValue(dto);
					result.processRequest();
				}
				catch (final Exception e)
				{
					result.processException("Error processing PUT command request (" + ((AbstractCommand) result).getName() + ")", e);
				}
			}

		};

		requestProcessChain.configure(RequestProcessor.RequestType.PUT, dto, result, execution);
		requestProcessChain.doProcess();
		return result;
	}

	@Override
	public UriInfo getUriInfo()
	{
		return this.uriInfo;
	}


	/**
	 * Internal helper method.
	 * <p/>
	 * Compares resource id with value of passed dto property for equality. If property value is null, dto property gets
	 * set resource id. If property value is not null then, based on 'strict' parameter an error response is created.
	 * 
	 * @param dto
	 *           dto to validate
	 * @return true when DTO is valid for this resource
	 */
	protected boolean processDtoId(final Object dto, @SuppressWarnings("unused") final String expPropValueLiteral,
			@SuppressWarnings("unused") final boolean strict)
	{
		return true;
	}


	/**
	 * Checks whether command execution is requested and if so returns an appropriate {@link Command}.
	 * <p/>
	 * Searches current http-query params for presence of 'cmd' key. Uses value of 'cmd' key as 'command id' and asks
	 * {@link CommandHandler} for a {@link Command} with that id.
	 * 
	 * @param cmdHandler
	 *           {@link CommandHandler} which used
	 * @return
	 */

	private AbstractCommand findCommand(final CommandHandler cmdHandler)
	{
		AbstractCommand result = null;
		if (cmdHandler != null)
		{
			// ... try to lookup whether a command is requested
			final String cmdKey = uriInfo.getQueryParameters().getFirst(QUERYPARAM_REQUESTED_CMD);
			if (cmdKey != null)
			{
				result = (AbstractCommand) cmdHandler.getCommand(cmdKey.toLowerCase());
			}
		}

		return result;
	}


	/**
	 * Sets the value/entity for this resource.
	 * <p/>
	 * This is the value, which gets applied current CRUD operation.
	 * 
	 * @param resourceValue
	 */
	public void setResourceValue(final RESOURCE resourceValue)
	{
		this.resrcEntity = resourceValue;
		this.resrcEntityResolved = true;
	}



	// CODE ACTUALLY IS DOUBLED IN HTTPPOSTRESPONSEBUILDER!
	@Override
	public RESOURCE getResourceValue()
	{
		if (!resrcEntityResolved)
		{
			try
			{
				final String resourceId = getResourceId();
				this.resrcEntity = this.readResource(resourceId);
				this.resrcEntityResolved = true;
			}
			catch (final Exception e)
			{
				// these are nasty exceptions which are needed, to control further flow
				// we must swallow them
				// be careful: not every possible exception (e.g. AmbigiousIdentifierException) must be swallowed
				final Set<Class> handled = new HashSet<Class>(Arrays.asList( //
						UnknownIdentifierException.class, //
						PKException.class, //
						ModelLoadingException.class, //
						JaloItemNotFoundException.class, //
						ClassCastException.class, //
						ModelNotFoundException.class, //for modelService.getByExample()
						IllegalArgumentException.class));

				if (handled.contains(e.getClass()))
				{
					LOG.debug(e.getMessage());
				}
				else
				{
					// e.g. AmbigiousIdentifierException
					processException("Error reading resource value", new InternalServerErrorException(e));
				}
			}
		}

		return resrcEntity;
	}


	// RESOURCE BASED CALLBACKS/HOOKS
	// Instead of implementing custom logic into appropriate responsebuilders, it can be placed here too
	// generally there are two options (assuming that resource classes are getting generated)
	// a) put a custom Resource class into the custom folder and use the methods provided below
	// b) use the generated resource class and set only custom responsebuilders


	// GENERIC 'GET' HANDLING
	abstract protected RESOURCE readResource(String resourceId) throws Exception;


	protected void beforeDelete(@SuppressWarnings("unused") final RESOURCE respEntity)
	{
		// NOP
	}

	protected void deleteResource(@SuppressWarnings("unused") final RESOURCE respEntity)
	{
		// NOP
	}

	protected void afterDelete(@SuppressWarnings("unused") final RESOURCE deadEntity)
	{
		// NOP
	}



	protected void beforePut(@SuppressWarnings("unused") final Object reqEntity,
			@SuppressWarnings("unused") final RESOURCE resrcEntity)
	{
		// NOP
	}

	// GENERIC 'CREATE' HANDLING
	protected RESOURCE createResource(@SuppressWarnings("unused") final Object reqEntity)
	{
		return null;
	}

	// GENERIC 'UPDATE' HANDLING
	protected void createOrUpdateResource(@SuppressWarnings("unused") final Object reqEntity,
			@SuppressWarnings("unused") final RESOURCE resrcEntity, @SuppressWarnings("unused") final boolean mustBeCreated)
			throws Exception
	{
		// NOP
	}


	protected void afterPut(@SuppressWarnings("unused") final Object reqEntity,
			@SuppressWarnings("unused") final RESOURCE resrcEntity, @SuppressWarnings("unused") final boolean isNewlyCreated)
	{
		// NOP
	}



	// 'POST' CALLBACKS
	/**
	 * HTTP-POST-hook method for reading the POST resource value.
	 * <p/>
	 * Gets invoked by of {@link HttpPostResponseBuilder#readResource(Object)}.
	 * 
	 * @param <T>
	 *           type of response entity
	 * @param reqEntity
	 *           the passed request entity
	 * @return response entity
	 */
	protected <T> T readPostResource(final Object reqEntity)
	{
		return null;
	}

	/**
	 * HTTP-POST-listener which gets invoked before POST processing starts.
	 * <p/>
	 * Invoked by {@link HttpPostResponseBuilder#beforeProcessing(Object, Object)}
	 * 
	 * @param reqEntity
	 *           request entity (dto)
	 * @param resrcEntity
	 *           response entity (model)
	 */
	protected void beforePost(final Object reqEntity, final Object resrcEntity)
	{
		//NOP
	}

	/**
	 * HTTP-POST-hook method for creating the POST resource value.
	 * <p/>
	 * Invoked by {@link HttpPostResponseBuilder#createResource(Object)}. Method gets called before request entity is
	 * merged into response entity.
	 * 
	 * @param <T>
	 *           type of response entity
	 * @param reqEntity
	 *           request entity (dto)
	 * @return created response entity (model)
	 */
	protected <T> T createPostResource(final Object reqEntity)
	{
		return null;
	}

	/**
	 * HTTP-POST-hook method for updating (create/update) the POST resource value
	 * <p/>
	 * Invoked by {@link HttpPostResponseBuilder#createOrUpdateResource(Object, Object, boolean)}. Method gets called
	 * after request entity is merged into response entity.
	 * 
	 * @param reqEntity
	 *           request entity (dto)
	 * @param resrcEntity
	 *           resource entity (model)
	 * @param mustBeCreated
	 *           true when response entity must be newly created (updated only otherwise)
	 * @throws Exception
	 *            is allowed to throw any kind of exception
	 */
	protected void createOrUpdatePostResource(final Object reqEntity, final Object resrcEntity, final boolean mustBeCreated)
			throws Exception
	{
		// NOP
	}


	/**
	 * HTTP-POST-listener which gets invoked after POST processing is finished.
	 * 
	 * @param reqEntity
	 *           request entity (dto)
	 * @param resrcEntity
	 *           resource entity (model)
	 * @param isNewlyCreated
	 *           true when response entity was newly created (false when only updated)
	 */
	protected void afterPost(final Object reqEntity, final Object resrcEntity, final boolean isNewlyCreated)
	{
		// NOP
	}


	/**
	 * Internal.
	 * <p/>
	 * Creates an instance of requested {@link YResponseBuilder} and sets some initial values.
	 * 
	 * @param clazz
	 * @param requestEntity
	 * @return
	 */
	private AbstractYResponseBuilder createResponseBuilder(final Class<?> clazz, final Object requestEntity)
	{
		AbstractYResponseBuilder result = null;
		try
		{
			result = (AbstractYResponseBuilder) clazz.newInstance();
		}
		catch (final Exception e)
		{
			throw new YWebservicesException("Error instantiating " + clazz, e);
		}
		// set a Resource (this) (alternatively the appropriate Constructor could be chosen)
		// setting a resource is mandatory before doing anything else
		result.setResource(this);
		result.setRequestValue(requestEntity);
		return result;
	}


	/**
	 * Throws a {@link WebApplicationException} which interrupts any current processing and gives full control to jersey.
	 * But before passed {@link Exception} gets evaluated and used to produce nice client output and notify loggers.
	 * 
	 * @param msg
	 *           a message which shall be given to client as headline
	 * @param exception
	 *           the exception which is taken to produce a short client error message
	 * @throws WebApplicationException
	 */
	protected void processException(final String msg, Exception exception) throws WebApplicationException
	{
		// do not process WebApplicationException types...
		if (exception instanceof WebApplicationException)
		{
			//... leave here and give control to jersey
			throw (WebApplicationException) exception;
		}

		// any non YWebserviceException gets wrapped into such one
		// (that type of exception gives some additional web-specific operations)
		if (!(exception instanceof YWebservicesException))
		{
			exception = new YWebservicesException(msg, exception);
		}

		// safe cast
		final YWebservicesException yWsException = (YWebservicesException) exception;
		// get http-status (depends on exception cause)
		final int status = yWsException.getResponseStatus();

		// log full stacktrace
		yWsException.notifyLogger(LOG);

		// client only gets a short error message (reduced stacktrace) and http-status
		final ResponseBuilder responseBuilder = getResponse();
		responseBuilder.status(status);
		responseBuilder.entity(yWsException.getShortStacktrace());

		// now anything else is done by jersey
		throw new WebApplicationException(exception, responseBuilder.build());
	}



	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor} instead.
	 */
	@Deprecated
	@Override
	public void notifyAfterGet()
	{
		//
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.GET,//
				AbstractWebserviceActionEvent.TRIGGER.AFTER//
				));
	}

	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor} instead.
	 */
	@Deprecated
	@Override
	public void notifyAfterPut()
	{
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.PUT,//
				AbstractWebserviceActionEvent.TRIGGER.AFTER//
				));
	}

	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor}instead.
	 */
	@Deprecated
	@Override
	public void notifyAfterPost()
	{
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.POST,//
				AbstractWebserviceActionEvent.TRIGGER.AFTER//
				));
	}

	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor} instead.
	 */
	@Deprecated
	@Override
	public void notifyAfterDelete()
	{
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.DELETE,//
				AbstractWebserviceActionEvent.TRIGGER.AFTER//
				));
	}

	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor} instead.
	 */
	@Deprecated
	@Override
	public void notifyBeforeGet()
	{
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.GET,//
				AbstractWebserviceActionEvent.TRIGGER.BEFORE//
				));
	}

	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor} instead.
	 */
	@Deprecated
	@Override
	public void notifyBeforeDelete()
	{
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.DELETE,//
				AbstractWebserviceActionEvent.TRIGGER.BEFORE//
				));

	}

	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor} instead.
	 */
	@Deprecated
	@Override
	public void notifyBeforePost()
	{
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.POST,//
				AbstractWebserviceActionEvent.TRIGGER.BEFORE//
				));

	}

	/**
	 * 
	 * 
	 * @deprecated Configure {@link de.hybris.platform.webservices.processor.impl.NotificationRequestProcessor} instead.
	 */
	@Deprecated
	@Override
	public void notifyBeforePut()
	{
		serviceLocator.getEventService().publishEvent(getEventActionFactory().createEventAction(//
				this,//
				AbstractWebserviceActionEvent.CRUD_METHOD.PUT,//
				AbstractWebserviceActionEvent.TRIGGER.BEFORE//
				));
	}

	public void setRequestProcessChain(final ConfigurableRequestProcessChain<RESOURCE> requestProcessChain)
	{
		this.requestProcessChain = requestProcessChain;
	}


}
