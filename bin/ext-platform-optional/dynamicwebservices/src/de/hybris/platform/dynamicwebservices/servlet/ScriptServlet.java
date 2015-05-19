/**
 * 
 */
package de.hybris.platform.dynamicwebservices.servlet;

import de.hybris.platform.core.Registry;
import de.hybris.platform.dynamicwebservices.model.DynamicWebServiceModel;
import de.hybris.platform.dynamicwebservices.service.DynamicWebservicesService;
import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import groovy.servlet.AbstractHttpServlet;
import groovy.util.ResourceException;


/**
 * Servlet delegating HTTP requests to scripts using {@link ScriptingLanguagesService}.
 * 
 * @author ag
 */
public class ScriptServlet extends AbstractHttpServlet
{
	private static final Logger LOG = Logger.getLogger(ScriptServlet.class.getName());

	private ScriptingLanguagesService scriptingLanguagesService;
	private DynamicWebservicesService dynamicWebservicesService;
	private String domain;

	@Override
	public void init(final ServletConfig config) throws ServletException
	{
		super.init(config);

		this.domain = config.getInitParameter("domain");
		if (StringUtils.isBlank(domain))
		{
			this.domain = config.getServletName();
		}

		final ApplicationContext ctx = Registry.getApplicationContext();
		this.scriptingLanguagesService = ctx.getBean("scriptingLanguagesService", ScriptingLanguagesService.class);
		this.dynamicWebservicesService = ctx.getBean("defaultDynamicWebservicesService", DynamicWebservicesService.class);
	}

	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		setDefaultResponseValues(request, response);

		String scriptURI = null;
		try
		{
			final String[] scriptURIAndTokens = splitRequestPath(request);
			scriptURI = scriptURIAndTokens[0];
			final DynamicWebServiceModel ws = dynamicWebservicesService.findEnabledWebService(scriptURI, domain);

			final String[] remainingPath = Arrays.copyOfRange(scriptURIAndTokens, 1, scriptURIAndTokens.length);
			executeScript(ws, createBinding(ws, scriptURI, remainingPath, request, response));
		}
		catch (final ModelNotFoundException e)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Service " + scriptURI + " not found within domain " + domain);
		}
		catch (final RuntimeException e)
		{
			handleError(response, scriptURI, e);
		}
	}

	protected void setDefaultResponseValues(final HttpServletRequest request, final HttpServletResponse response)
	{
		response.setContentType("text/html; charset=" + encoding);
	}

	protected void executeScript(final DynamicWebServiceModel ws, final ScriptServletBinding binding)
	{
		final ScriptExecutable executable = scriptingLanguagesService.getExecutableByURI(ws.getScriptURI());
		executable.execute(binding.context, binding.out, binding.err);
	}

	// returns [ scriptURI, remaining token 0, remaining token 1, ... ]
	protected String[] splitRequestPath(final HttpServletRequest request)
	{
		// TODO allow folders etc
		String path = request.getPathInfo();
		if (path.startsWith("/"))
		{
			path = path.substring(1);
		}
		final String[] pathTokens = path.split("/");
		return pathTokens;
	}

	protected ScriptServletBinding createBinding(final DynamicWebServiceModel ws, final String scriptURI,
			final String[] remainingPathTokens, final HttpServletRequest request, final HttpServletResponse response)
	{
		final Map<String, Object> context = new HashMap<>();

		context.put("request", request);
		context.put("response", response);
		context.put("context", servletContext);
		context.put("session", request.getSession(false));

		context.put("service", ws);
		context.put("serviceURI", scriptURI);
		context.put("pathTokens", remainingPathTokens);

		context.put("headers", getLazyLoadingHeadersWrapper(request));
		context.put("params", getLazyLoadingParamsWrapper(request));

		final String parametersFromPathPattern = ws.getPathParameterPattern();
		if (StringUtils.isNotBlank(parametersFromPathPattern))
		{
			addPathParamsToContext(remainingPathTokens, parametersFromPathPattern, context);
		}

		appendGroovyContext(context, ws, scriptURI, remainingPathTokens, request, response);

		final ScriptServletBinding binding = new ScriptServletBinding();
		binding.context = context;
		binding.out = new PrintWriter(System.out);
		binding.err = new PrintWriter(System.err);
		return binding;
	}

	protected void appendGroovyContext(final Map<String, Object> context, final DynamicWebServiceModel ws, final String scriptURI,
			final String[] remainingPathTokens, final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			final Class jsonBuilderClass = this.getClass().getClassLoader().loadClass("groovy.json.StreamingJsonBuilder");
			final Constructor writerConstructor = jsonBuilderClass.getConstructor(Writer.class);

			context.put("json", writerConstructor.newInstance(response.getWriter()));
		}
		catch (final Throwable t)
		{
			t.printStackTrace();
		}
	}

	protected Object getLazyLoadingParamsWrapper(final HttpServletRequest request)
	{
		// TODO lazy loading still missing !!!
		final Map<String, String[]> paramsRaw = request.getParameterMap();
		final Map params = new LinkedHashMap(paramsRaw.size() * 2);
		for (final Map.Entry<String, String[]> e : paramsRaw.entrySet())
		{
			if (e.getValue().length == 1)
			{
				params.put(e.getKey(), e.getValue()[0]);
			}
			else
			{
				params.put(e.getKey(), e.getValue()[0]);
			}
		}
		return params;
	}

	protected Object getLazyLoadingHeadersWrapper(final HttpServletRequest request)
	{
		// TODO lazy loading still missing !!!
		final Map<String, String> headers = new LinkedHashMap<String, String>(20);
		for (final Enumeration names = request.getHeaderNames(); names.hasMoreElements();)
		{
			final String headerName = (String) names.nextElement();
			final String headerValue = request.getHeader(headerName);
			headers.put(headerName, headerValue);
		}
		return headers;
	}

	protected void addPathParamsToContext(final String[] remainingPathTokens, final String pathParamsPattern,
			final Map<String, Object> context)
	{
		if (StringUtils.isNotBlank(pathParamsPattern))
		{
			int i = 0;
			for (final String pathParamChunk : pathParamsPattern.split("/"))
			{
				// do we have path tokens left at all - otherwise break here
				if (i >= remainingPathTokens.length)
				{
					break;
				}
				if (pathParamChunk.startsWith(":"))
				{
					final String paramName = pathParamChunk.substring(1).trim();
					if (!paramName.isEmpty())
					{
						final Object prev = context.put(paramName, remainingPathTokens[i]);
						if (prev != null)
						{
							throw new IllegalArgumentException("Path parameter :" + paramName + "=" + remainingPathTokens[i]
									+ " clashed with existing request parameter " + prev);
						}
					}
				}
				i++;
			}
		}
	}

	static class ScriptServletBinding
	{
		Map context;
		Writer out;
		Writer err;
	}

	protected void handleError(final HttpServletResponse response, final String scriptUri, final RuntimeException runtimeException)
			throws IOException
	{
		final StringBuilder error = new StringBuilder("ScriptServlet error: script: '").append(scriptUri).append("': ");

		final Throwable e = runtimeException.getCause();
		if (e == null)
		{
			error.append(" Script processing failed.\n").append(runtimeException.getMessage());
			if (runtimeException.getStackTrace().length > 0)
			{
				error.append(runtimeException.getStackTrace()[0].toString());
			}
			servletContext.log(error.toString());
			System.err.println(error.toString());
			runtimeException.printStackTrace(System.err);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error.toString());
		}
		else
		{
			if (e instanceof ResourceException)
			{
				error.append(" Script not found, sending 404.");
				servletContext.log(error.toString());
				System.err.println(error.toString());
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			servletContext.log("An error occurred processing the request", runtimeException);
			error.append(e.getMessage());
			if (e.getStackTrace().length > 0)
			{
				error.append(e.getStackTrace()[0].toString());
			}
			servletContext.log(e.toString());
			System.err.println(e.toString());
			runtimeException.printStackTrace(System.err);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
		}
	}
}
