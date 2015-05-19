/**
 * 
 */
package de.hybris.platform.atddengine.keywords;

import de.hybris.platform.atddengine.xml.XmlAssertions;
import de.hybris.platform.atddengine.xml.XmlUtils;


/**
 * Keyword library that provides XML and XPath verification as well as XML transformation capabilities.
 */
public class XmlKeywordLibrary
{
	/**
	 * Applies a transformation to an XML String.
	 * 
	 * @param xml
	 *           the XML to transform
	 * @param transformation
	 *           the transformation to apply (path classpath resource)
	 * @return the transformation result
	 */
	public String transformXml(final String xml, final String transformation)
	{
		return XmlUtils.applyTransformationResource(xml, transformation);
	}

	/**
	 * Verifies that two XML snippets are equal to each other after applying the given transformation to both of them
	 * 
	 * @param message
	 *           failure message
	 * @param expectedXml
	 *           the expected XML
	 * @param actualXml
	 *           the actual XML
	 * @param transformation
	 *           the transformation to apply (path classpath resource)
	 */
	public void verifyXmlEqual(final String message, final String expectedXml, final String actualXml, final String transformation)
	{
		XmlAssertions.assertXmlEqual(message, expectedXml, actualXml, transformation);
	}

	/**
	 * Verifies that two XML snippets are equal to each other.
	 * 
	 * @param message
	 *           failure message
	 * @param expectedXml
	 *           the expected XML
	 * @param actualXml
	 *           the actual XML
	 */
	public void verifyXmlEqual(final String message, final String expectedXml, final String actualXml)
	{
		XmlAssertions.assertXmlEqual(message, expectedXml, actualXml);
	}

	/**
	 * Verifies that two XML snippets are equal to each other.
	 * 
	 * @param expectedXml
	 *           the expected XML
	 * @param actualXml
	 *           the actual XML
	 */
	public void verifyXmlEqual(final String expectedXml, final String actualXml)
	{
		XmlAssertions.assertXmlEqual(expectedXml, actualXml);
	}
}
