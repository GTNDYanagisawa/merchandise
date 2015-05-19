/**
 * 
 */
package de.hybris.platform.ycommercewebservices.jaxb;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.metadata.MetadataSourceAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.*;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;


/**
 * WsDTOGenericMetadataSourceAdapter is responsible for creating JAXB metadata used by Moxy implementation of JAXB. It
 * creates XmlBindings data structure for one class given by constructor parameter.
 */
public class WsDTOGenericMetadataSourceAdapter<T> extends MetadataSourceAdapter
{
	public final String DEFAULT_COLLECTION_ITEM_NAME = "item";
	public final String PREFIX_TO_FILTER = "WsDTO";

	private final Class<T> clazz;
	private final List<Class> typeAdapters;
	private final Boolean wrapCollections;

	public WsDTOGenericMetadataSourceAdapter(final Class<T> clazz, final List<Class> typeAdapters, final Boolean wrapCollections)
	{
		this.clazz = clazz;
		this.typeAdapters = typeAdapters;
		this.wrapCollections = wrapCollections;
	}

	@Override
	public XmlBindings getXmlBindings(final Map<String, ?> properties, final ClassLoader classLoader)
	{
		final String packageName = clazz.getPackage().getName();
		final ObjectFactory factory = new ObjectFactory();
		final XmlBindings xmlBindings = new XmlBindings();
		xmlBindings.setPackageName(packageName);
		xmlBindings.setJavaTypes(new JavaTypes());
		xmlBindings.setXmlJavaTypeAdapters(new XmlJavaTypeAdapters());

		final JavaType javaType = new JavaType();
		javaType.setName(clazz.getName());
		javaType.setJavaAttributes(new JavaAttributes());
		final XmlRootElement xre = new XmlRootElement();
		xre.setName(createNodeNameFromClass(clazz));
		javaType.setXmlRootElement(xre);

		// check for collections and set XmlElementWrapper and XmlElement for a collection fields in order to have wrappers
		if (wrapCollections != null && Boolean.TRUE.equals(wrapCollections))
		{
			final Field[] fields = clazz.getDeclaredFields();
			for (final Field field : fields)
			{
				final Class fieldClass = field.getType();
				if (Collection.class.isAssignableFrom(fieldClass))
				{
					final XmlElementWrapper xew = new XmlElementWrapper();
					xew.setName(field.getName());

					final XmlElement xe = new XmlElement();
					xe.setJavaAttribute(field.getName());
					xe.setName(createCollectionItemNameFromField(field));
					xe.setXmlElementWrapper(xew);

					javaType.getJavaAttributes().getJavaAttribute().add(factory.createXmlElement(xe));
				}
			}
		}

		xmlBindings.getJavaTypes().getJavaType().add(javaType);

		// set package level type adapters 
		for (final Class clazz : this.typeAdapters)
		{
			final XmlJavaTypeAdapter xjta = new XmlJavaTypeAdapter();
			xjta.setValue(clazz.getName());
			xjta.setType(Date.class.getName());
			xmlBindings.getXmlJavaTypeAdapters().getXmlJavaTypeAdapter().add(xjta);
		}

		return xmlBindings;
	}

	protected String createNodeNameFromClass(final Class clazz)
	{
		final String name = clazz.getSimpleName().replace(PREFIX_TO_FILTER, "");
		return decapitalizeFirstLetter(name);
	}

	protected String createCollectionItemNameFromField(final Field field)
	{
		final Class fieldClass = field.getType();
		if (Collection.class.isAssignableFrom(fieldClass))
		{
			final ParameterizedType pt = (ParameterizedType) field.getGenericType();
			final Type[] typesInside = pt.getActualTypeArguments();
			if (typesInside.length == 1)
			{
				return createNodeNameFromClass((Class) typesInside[0]);
			}
			else
			{
				return DEFAULT_COLLECTION_ITEM_NAME;
			}
		}
		else
		{
			return DEFAULT_COLLECTION_ITEM_NAME;
		}
	}

	private static String decapitalizeFirstLetter(final String original)
	{
		if (original.isEmpty())
		{
			return original;
		}
		return original.substring(0, 1).toLowerCase() + original.substring(1);
	}
}
