/**
 * 
 */
package de.hybris.platform.ycommercewebservices.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;


/**
 * MoxyJaxbContextFactoryImpl is a factory that creates JaxbContext using a Moxy implementation of JAXB. The context is
 * created for a given set of classes. The factory finally adds to this context also some global classes (provided in
 * otherClasses list).
 */
public class MoxyJaxbContextFactoryImpl implements JaxbContextFactory
{
	private List<Class> otherClasses = new ArrayList<>();
	private List<Class> typeAdapters = new ArrayList<>();
	private Boolean wrapCollections;

	@Override
	public JAXBContext createJaxbContext(final Class... classes) throws JAXBException
	{
		final Map<String, Object> properties = new HashMap<String, Object>();
		final List<MetadataSource> mappings = new ArrayList();
		final Map<Class, Object> allClasses = new HashMap<>();

		for (final Class clazz : classes)
		{
			final List<Class> classesInHierarchy = getAllSuperClasses(clazz);
			for (final Class clazzInHierarchy : classesInHierarchy)
			{
				allClasses.put(clazzInHierarchy, null);
			}
		}

		for (final Class clazz : allClasses.keySet())
		{
			final MetadataSource ms = new WsDTOGenericMetadataSourceAdapter<>(clazz, typeAdapters, wrapCollections);
			mappings.add(ms);
		}

		properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, mappings);

		Class[] otherClassesArray = new Class[otherClasses.size()];
		otherClassesArray = otherClasses.toArray(otherClassesArray);
		final JAXBContext jaxbContext = JAXBContextFactory.createContext(otherClassesArray, properties);

		return jaxbContext;
	}

	public List<Class> getOtherClasses()
	{
		return otherClasses;
	}

	public void setOtherClasses(final List<Class> otherClasses)
	{
		this.otherClasses = otherClasses;
	}

	public List<Class> getTypeAdapters()
	{
		return typeAdapters;
	}

	public void setTypeAdapters(final List<Class> typeAdapters)
	{
		this.typeAdapters = typeAdapters;
	}

	public Boolean getWrapCollections()
	{
		return wrapCollections;
	}

	public void setWrapCollections(final Boolean wrapCollections)
	{
		this.wrapCollections = wrapCollections;
	}

	protected static List<Class> getAllSuperClasses(final Class clazz)
	{
		final List<Class> classList = new ArrayList<Class>();

		Class currentClass = clazz;
		while (currentClass != null && !currentClass.equals(Object.class))
		{
			classList.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		return classList;
	}
}
