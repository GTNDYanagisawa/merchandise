/**
 * 
 */
package de.hybris.platform.ycommercewebservices.jaxb;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.springframework.util.ClassUtils;


public class Jaxb2HttpMessageConverter extends AbstractXmlHttpMessageConverter<Object>
{
	private JaxbContextFactory jaxbContextFactory;

	private Map<String, ?> marshallerProperties;
	private Map<String, ?> unmarshallerProperties;

	//JAXBContext is thread safe and can be buffered
	protected final ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();
	protected JAXBContext jaxbNullContext;


	@Override
	protected Object readFromSource(final Class<?> clazz, final HttpHeaders headers, final Source source) throws IOException
	{
		final Unmarshaller unmarshaller;
		final JAXBContext jaxbContext = getJaxbContext(clazz);

		try
		{
			unmarshaller = jaxbContext.createUnmarshaller();
			if (this.unmarshallerProperties != null)
			{
				for (final String name : this.unmarshallerProperties.keySet())
				{
					unmarshaller.setProperty(name, this.unmarshallerProperties.get(name));
				}
			}
		}
		catch (final JAXBException e)
		{
			throw new HttpMessageNotReadableException("Can't create unmarshaller: " + e.getMessage(), e);
		}

		try
		{
			final JAXBElement je = unmarshaller.unmarshal(source, clazz);
			return je.getValue();
		}
		catch (final JAXBException e)
		{
			throw new HttpMessageNotReadableException("Cant unmarshall input to class: " + clazz.getName(), e);
		}
	}

	@Override
	protected void writeToResult(final Object o, final HttpHeaders headers, final Result result) throws IOException
	{
		final Marshaller marshaller;
		final Class clazz = ClassUtils.getUserClass(o);
		final JAXBContext jaxbContext = getJaxbContext(clazz);

		try
		{
			marshaller = jaxbContext.createMarshaller();
			if (this.marshallerProperties != null)
			{
				for (final String name : this.marshallerProperties.keySet())
				{
					marshaller.setProperty(name, this.marshallerProperties.get(name));
				}
			}
		}
		catch (final JAXBException je)
		{
			throw new HttpMessageNotWritableException("Can't create marshaller: " + je.getMessage(), je);
		}

		try
		{
			marshaller.marshal(o, result);

		}
		catch (final JAXBException e)
		{
			throw new HttpMessageNotWritableException("Cant marshall class: " + clazz.getName(), e);
		}
	}

	@Override
	protected boolean supports(final Class<?> clazz)
	{
		return true;
	}


	protected final JAXBContext getJaxbContext(final Class clazz)
	{
		if (clazz == null)
		{
			if (jaxbNullContext == null)
			{
				try
				{
					jaxbNullContext = jaxbContextFactory.createJaxbContext();
				}
				catch (final JAXBException ex)
				{
					throw new HttpMessageConversionException("Could not instantiate JAXBContext" + ex.getMessage(), ex);
				}
			}
			return jaxbNullContext;
		}

		JAXBContext jaxbContext = this.jaxbContexts.get(clazz);
		if (jaxbContext == null)
		{
			try
			{
				jaxbContext = jaxbContextFactory.createJaxbContext(clazz);
				this.jaxbContexts.putIfAbsent(clazz, jaxbContext);
			}
			catch (final JAXBException ex)
			{
				throw new HttpMessageConversionException("Could not instantiate JAXBContext for class [" + clazz + "]: "
						+ ex.getMessage(), ex);
			}
		}
		return jaxbContext;
	}

	public JaxbContextFactory getJaxbContextFactory()
	{
		return jaxbContextFactory;
	}

	@Required
	public void setJaxbContextFactory(final JaxbContextFactory jaxbContextFactory)
	{
		this.jaxbContextFactory = jaxbContextFactory;
	}

	public Map<String, ?> getMarshallerProperties()
	{
		return marshallerProperties;
	}

	public void setMarshallerProperties(final Map<String, ?> marshallerProperties)
	{
		this.marshallerProperties = marshallerProperties;
	}

	public Map<String, ?> getUnmarshallerProperties()
	{
		return unmarshallerProperties;
	}

	public void setUnmarshallerProperties(final Map<String, ?> unmarshallerProperties)
	{
		this.unmarshallerProperties = unmarshallerProperties;
	}
}
