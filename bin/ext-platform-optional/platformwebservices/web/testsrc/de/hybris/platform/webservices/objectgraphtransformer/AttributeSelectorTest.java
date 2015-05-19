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
package de.hybris.platform.webservices.objectgraphtransformer;

import static junit.framework.Assert.assertEquals;

import de.hybris.platform.webservices.AbstractYResource;
import de.hybris.platform.webservices.WsUtilService;
import de.hybris.platform.webservices.impl.DefaultWsUtilService;
import de.hybris.platform.webservices.impl.ServiceLocatorImpl;
import de.hybris.platform.webservices.paging.impl.QueryPagingStrategy;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuAddressModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.usergraph.TuUserModel;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import com.sun.jersey.core.util.MultivaluedMapImpl;


/**
 * Basic test for attribute selector API.
 */
public class AttributeSelectorTest
{
	//
	// introduce new TuXmlUserModel class; same like TuUserDTO but with class annotation
	// 
	@GraphNode(target = TuXmlUserModel.class)
	@XmlRootElement(name = "tuuser")
	public static class TuXmlUserDTO extends TuUserDTO
	{
		// NOP
	}

	public static class TuXmlUserModel extends TuUserModel
	{

		public TuXmlUserModel(final String uid)
		{
			super(uid);
		}
	}

	public static class TuXmlUserResource extends AbstractYResource<TuXmlUserModel>
	{
		public TuXmlUserResource()
		{
			super("TuXmlUser");
		}

		@Override
		protected TuXmlUserModel readResource(final String resourceId) throws Exception
		{
			return null;
		}
	}





	/**
	 * Tests default behavior - i.e no configuration for attribute selection. By default all attributes should be copied.
	 */
	@Test
	public void testDefault()
	{
		// target -> source directly via target graph without attribute selection

		// prepare GraphTransformer and GraphContext
		final YObjectGraphTransformer ygt = new YObjectGraphTransformer(TuXmlUserDTO.class);
		final ServiceLocatorImpl serviceLocator = new ServiceLocatorImpl();
		serviceLocator.setWsUtilService(new WsUtilMockService(null));
		final YObjectGraphContext yCtx = (YObjectGraphContext) ygt.createGraphContext();
		yCtx.setRequestResource(new TuXmlUserResource());
		final QueryPagingStrategy wsPaging = new QueryPagingStrategy();
		yCtx.getRequestResource().setPagingStrategy(wsPaging);
		yCtx.setServices(serviceLocator);
		yCtx.setModelToDtoTransformation(true);
		//fake UriInfo because paging caused NPE, move this static method to this class
		yCtx.setUriInfo(prepareUriInfo(new HashMap()));


		// transform model -> dto
		final TuXmlUserDTO userDto = ygt.transform(yCtx, createTestUserModel());

		// Assert all attributes have passed
		assertEquals("uid is incorrect", "userModel", userDto.getUid());
		Assert.assertNotNull(userDto.getMainAddress());
		assertEquals("main adress firstname is incorrect", "mainFirst", userDto.getMainAddress().getFirstname());
		Assert.assertNotNull(userDto.getSecondAddress());
		assertEquals("second address lastname is incorrect", "secondLast", userDto.getSecondAddress().getLastname());
		assertEquals("user login is incorrect", "JohnSmith", userDto.getLogin());
		assertEquals("user password is incorrect", "1234", userDto.getPassword());
	}

	/**
	 * This tests attribute selection "filtering" i.e. excluding some Model attributes from being transformed into DTO
	 * values.
	 */
	@Test
	public void testAttributeFiltering()
	{
		//target -> source directly via target graph with attribute selection
		final Set<String> selectedAttributes = new HashSet<String>();
		selectedAttributes.add("uid");
		selectedAttributes.add("mainAddress");
		selectedAttributes.add("firstName");
		selectedAttributes.add("login");


		// prepare GraphTransformer and GraphContext
		final YObjectGraphTransformer ygt = new YObjectGraphTransformer(TuXmlUserDTO.class);
		final ServiceLocatorImpl serviceLocator = new ServiceLocatorImpl();
		serviceLocator.setWsUtilService(new WsUtilMockService(selectedAttributes));
		final YObjectGraphContext yCtx = (YObjectGraphContext) ygt.createGraphContext();
		yCtx.setRequestResource(new TuXmlUserResource());
		final QueryPagingStrategy wsPaging = new QueryPagingStrategy();
		yCtx.getRequestResource().setPagingStrategy(wsPaging);
		yCtx.setServices(serviceLocator);
		yCtx.setModelToDtoTransformation(true);
		//fake UriInfo because paging caused NPE
		yCtx.setUriInfo(prepareUriInfo(new HashMap()));

		// transform model -> dto
		final TuXmlUserDTO userDto = ygt.transform(yCtx, createTestUserModel());


		// Assert that not filtered attributes have passed
		assertEquals("uid is incorrect", "userModel", userDto.getUid());
		Assert.assertNotNull(userDto.getMainAddress());
		assertEquals("main address firstname is incorrect", "mainFirst", userDto.getMainAddress().getFirstname());
		assertEquals("user login in incorrect", "JohnSmith", userDto.getLogin());

		//Assert that filtered attributes have not passed (null)
		Assert.assertNull(userDto.getSecondAddress());


		//This simple property is not selected so it should be null here
		Assert.assertNull(userDto.getPassword());
	}

	/**
	 * Mocks attribute selector specific operations of {@link WsUtilService}
	 */
	private static class WsUtilMockService extends DefaultWsUtilService
	{
		Set<String> selectedAttribs;

		public WsUtilMockService(final Set<String> selectedAttributes)
		{
			super();
			this.selectedAttribs = selectedAttributes;
		}

		@Override
		public Set<String> getConfigurationForType(final int level, final Class<?> runtimeNodeType,
				final Map<String, List<String>> httpQueryParameters, final Set<String> allowedAttributesNames,
				final List<String> possibleConfigNodeNames)
		{
			return selectedAttribs;
		}

		//		//deactivation the attribute-level security (missing TuXmlUser jalo class causes exception)
		//		@Override
		//		public Collection<PropertyMapping> performAttributeSecurity(final Class<?> targetNodeClass,
		//				final Collection<PropertyMapping> selectedAttributes, final String userRightCode)
		//		{
		//			return selectedAttributes;
		//		}
	}


	/**
	 * Internal.
	 * <p/>
	 * Creates and prepares a test instance of {@link TuXmlUserModel}
	 */
	private TuXmlUserModel createTestUserModel()
	{
		final TuXmlUserModel userModel = new TuXmlUserModel("userModel");

		final TuAddressModel adrModelMain = new TuAddressModel("mainFirst", "mainLast");
		final TuAddressModel adrModelSecond = new TuAddressModel("secondFirst", "secondLast");
		userModel.setMainAddress(adrModelMain);
		userModel.setSecondAddress(adrModelSecond);
		userModel.setLogin("JohnSmith");
		userModel.setPassword("1234");

		return userModel;
	}


	public static void main(final String[] argc)
	{
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		//		Logger log = Logger.getLogger(ObjTree.class);
		//		log.setLevel(Level.DEBUG);
		//
		//		log = Logger.getLogger(DefaultObjTreeNodeCopier.class);
		//		log.setLevel(Level.DEBUG);

		Logger.getRootLogger().setLevel(Level.DEBUG);
		final AttributeSelectorTest test = new AttributeSelectorTest();
		test.testAttributeFiltering();
	}


	/**
	 * Creates "fake" UriInfo. The UriInfo is essential for passing query parameters inside YCollectionNodeProcessor.
	 * That's a lot of code, but at least it is dead simple. DynamicProxy would not help here..
	 * 
	 * @param params
	 *           a Map of "fake" query parameters.
	 * @return
	 */
	static public UriInfo prepareUriInfo(final Map<String, String> params)
	{
		final MultivaluedMap<String, String> _internalMap = new MultivaluedMapImpl();
		for (final String param : params.keySet())
		{
			final List<String> values = new ArrayList<String>();
			values.add(params.get(param));
			_internalMap.put(param, values);
		}

		return new UriInfo()
		{

			@Override
			public UriBuilder getRequestUriBuilder()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public URI getRequestUri()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(final boolean decode)
			{
				return _internalMap;
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters()
			{
				return getQueryParameters(true);
			}

			@Override
			public List<PathSegment> getPathSegments(final boolean decode)
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public List<PathSegment> getPathSegments()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(final boolean decode)
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public String getPath(final boolean decode)
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public String getPath()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public List<String> getMatchedURIs(final boolean decode)
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public List<String> getMatchedURIs()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public List<Object> getMatchedResources()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public UriBuilder getBaseUriBuilder()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public URI getBaseUri()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public UriBuilder getAbsolutePathBuilder()
			{
				throw new UnsupportedOperationException("not supported");
			}

			@Override
			public URI getAbsolutePath()
			{
				throw new UnsupportedOperationException("not supported");
			}
		};
	}
}
