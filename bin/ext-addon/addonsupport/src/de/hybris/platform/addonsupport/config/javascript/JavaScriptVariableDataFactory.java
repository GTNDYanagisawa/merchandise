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
package de.hybris.platform.addonsupport.config.javascript;

import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.ui.ModelMap;


public class JavaScriptVariableDataFactory
{

	public static final String CREATE_FROM_OBJ_ERROR = "Error creating JavaScriptVariableData for given args! Details: ";

	public static JavaScriptVariableData create(final String key, final String value)
	{
		final JavaScriptVariableData variable = new JavaScriptVariableData();
		variable.setQualifier(key);
		variable.setValue(value);
		return variable;
	}

	public static List<JavaScriptVariableData> createFromMap(final Map<String, String> variables)
	{
		final List<JavaScriptVariableData> variablesList = new ArrayList<JavaScriptVariableData>();
		for (final Entry<String, String> entry : variables.entrySet())
		{
			variablesList.add(create(entry.getKey(), entry.getValue()));
		}
		return variablesList;
	}

    public static List<JavaScriptVariableData> getVariables(final ModelMap model)
    {
        List<JavaScriptVariableData> variables = (List<JavaScriptVariableData>) model.get("jsVariables");
        if (variables == null)
        {
            variables = new LinkedList<JavaScriptVariableData>();
            model.put("jsVariables", variables);
        }
        return variables;
    }


	/**
	 * Creating JSON from object
	 * 
	 * @return JavaScriptVariableData
	 */
	public static JavaScriptVariableData createJSONFromObject(final String key, final Map<String, String> variables)
			throws IllegalArgumentException
	{
		try
		{
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writeValueAsString(variables);
			return create(key, json);
		}
		catch (final JsonGenerationException e)
		{
			throw new IllegalArgumentException(CREATE_FROM_OBJ_ERROR + e.getMessage());
		}
		catch (final JsonMappingException e)
		{
			throw new IllegalArgumentException(CREATE_FROM_OBJ_ERROR + e.getMessage());
		}
		catch (final IOException e)
		{
			throw new IllegalArgumentException(CREATE_FROM_OBJ_ERROR + e.getMessage());
		}
	}

}
