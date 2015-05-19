/**
 * 
 */
package de.hybris.platform.commercewebservicescommons.mapping;

import de.hybris.platform.commercewebservicescommons.mapping.impl.FieldSetBuilderContext;

import java.util.Set;


/**
 * Interface for field set builder which create set of field names based on string configuration
 * 
 */
public interface FieldSetBuilder
{
	/**
	 * Method converts configuration string to set of field names
	 * 
	 * @param clazz
	 *           - class of object for which field set is created
	 * @param fieldPrefix
	 *           - prefix which should be added to field name
	 * @param configuration
	 *           - string describing properties which should be added to the set
	 * @return set of fully qualified field names
	 */
	Set<String> createFieldSet(final Class clazz, final String fieldPrefix, String configuration);

	/**
	 * Method converts configuration string to set of field names
	 * 
	 * @param clazz
	 *           - class of object for which field set is created
	 * @param fieldPrefix
	 *           - prefix which should be added to field name
	 * @param configuration
	 *           - string describing properties which should be added to the set
	 * @param context
	 *           - object storing additional information like :<br/>
	 *           <b>typeVariableMap</b> - map containing information about types used in generic class <br/>
	 *           e.g. if we have type class like ProductSearchPageData<STATE, RESULT> we should give map like
	 *           {STATE=SearchStateData.class, RESULT=ProductData.class}<br/>
	 *           <b>recurrencyLevel</b> - define how many recurrency level builder should support (it is case when object
	 *           have it's own type field e.g. VariantMatrixElementData have elements which are also
	 *           VariantMatrixElementData type)
	 * 
	 * @return set of fully qualified field names
	 */
	Set<String> createFieldSet(final Class clazz, final String fieldPrefix, String configuration, FieldSetBuilderContext context);

}
