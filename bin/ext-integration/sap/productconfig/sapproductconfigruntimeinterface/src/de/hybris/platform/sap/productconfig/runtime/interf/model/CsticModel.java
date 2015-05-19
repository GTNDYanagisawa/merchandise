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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import java.util.List;


/**
 * Represents the characteristic model.
 */
public interface CsticModel extends BaseModel
{

	public final static int TYPE_UNDEFINED = -1;
	/** Value type for String */
	public final static int TYPE_STRING = 0;
	/** Value type for integer */
	public final static int TYPE_INTEGER = 1;
	/** Value type for float */
	public final static int TYPE_FLOAT = 2;
	/** Value type for boolean */
	public final static int TYPE_BOOLEAN = 3;
	/** Value type for date */
	public final static int TYPE_DATE = 4;
	/** Value type for time */
	public final static int TYPE_TIME = 5;
	/** Value type for currency */
	public final static int TYPE_CURRENCY = 6;
	/** Value type for object class (materials) */
	public final static int TYPE_CLASS = 7;

	public final static String AUTHOR_SYSTEM = "S";

	public final static String AUTHOR_USER = "U";

	public final static String AUTHOR_DEFAULT = "D";

	public final static String AUTHOR_NOAUTHOR = "N";

	/**
	 * @return the characteristic name
	 */
	public String getName();

	/**
	 * @param name
	 *           characteristic name
	 */
	public void setName(String name);

	/**
	 * @return the characteristic language dependent name
	 */
	public String getLanguageDependentName();

	/**
	 * @param languageDependentName
	 *           characteristic language dependent name
	 */
	public void setLanguageDependentName(String languageDependentName);

	/**
	 * Get the long text description for a cstic, which will be displayed under the cstic name in the UI
	 *
	 * @return The long text value
	 */
	public String getLongText();

	/**
	 * Set the long text, which will be displayed under the Cstic name in the UI
	 *
	 * @param longText
	 *           Description for the cstic
	 */
	public void setLongText(String longText);

	/**
	 * @return an unmodifiable list of all assigned values
	 */
	public List<CsticValueModel> getAssignedValues();

	/**
	 * sets assigned value without to check whether the characteristic was changed
	 *
	 * @param assignedValues
	 *           list of all assigned values
	 */
	public void setAssignedValuesWithoutCheckForChange(List<CsticValueModel> assignedValues);

	/**
	 * @param assignedValues
	 *           list of all assigned values
	 */
	public void setAssignedValues(List<CsticValueModel> assignedValues);

	/**
	 * @return an unmodifiable list of all assignable values
	 */
	public List<CsticValueModel> getAssignableValues();

	/**
	 * @param assignableValues
	 *           list of all assignable values
	 */
	public void setAssignableValues(List<CsticValueModel> assignableValues);


	/**
	 * @return the value type
	 */
	public int getValueType();

	/**
	 * @param valueType
	 *           value type
	 */
	public void setValueType(int valueType);

	/**
	 * @return the length of the characteristic value type
	 */
	public int getTypeLength();

	/**
	 * @param typeLength
	 *           length of the characteristic value type
	 */
	public void setTypeLength(int typeLength);

	/**
	 * @return the number scale
	 */
	public int getNumberScale();

	/**
	 * @param numberScale
	 *           the number scale
	 */
	public void setNumberScale(int numberScale);

	/**
	 * @return true if the characteristic is visible
	 */
	public boolean isVisible();

	/**
	 * @param visble
	 *           flag indicating whether the characteristic is visible
	 */
	public void setVisible(boolean visble);

	/**
	 * @return true if the characteristic is consistent
	 */
	public boolean isConsistent();

	/**
	 * @param consistent
	 *           flag indicating whether the characteristic is consistent
	 */
	public void setConsistent(boolean consistent);

	/**
	 * @return true if the characteristic is complete
	 */
	public boolean isComplete();

	/**
	 * @param complete
	 *           flag indicating whether the characteristic is complete
	 */
	public void setComplete(boolean complete);

	/**
	 * @return true if the characteristic is read only
	 */
	public boolean isReadonly();

	/**
	 * @param readonly
	 *           flag indicating whether the characteristic is read only
	 */
	public void setReadonly(boolean readonly);

	/**
	 * @return true if the characteristic is required
	 */
	public boolean isRequired();

	/**
	 * @param required
	 *           flag indicating whether the characteristic is required
	 */
	public void setRequired(boolean required);

	/**
	 * @return true if the characteristic is multivalued
	 */
	public boolean isMultivalued();

	/**
	 * @param multivalued
	 *           flag indicating whether the characteristic is multivalued
	 */
	public void setMultivalued(boolean multivalued);

	/**
	 * @return true if the characteristic is changed by front end
	 */
	public boolean isChangedByFrontend();

	/**
	 * @param changedByFrontend
	 *           flag indicating whether the characteristic is changed by front end
	 */
	public void setChangedByFrontend(boolean changedByFrontend);

	/**
	 * @return cloned <code>CsticModel</code>
	 */
	@Override
	public CsticModel clone();

	/**
	 * @return the characteristic author
	 */
	public String getAuthor();

	/**
	 * @param author
	 *           haracteristic author
	 */
	public void setAuthor(String author);

	/**
	 * Assigns the given value to the characteristic, overwriting any previous value assignments.<br>
	 * This is a typical operation for single valued characteristics.
	 *
	 * @param valueName
	 *           the value to set
	 */
	public void setSingleValue(String valueName);


	/**
	 * Assigns the given value to the characteristic, while keeping any previous value assignments.<br>
	 * This is a typical operation for multi valued characteristics.
	 *
	 * @param valueName
	 *           the value to add
	 */
	public void addValue(String valueName);

	/**
	 * Remove the given value from the assigned values, while keeping the other value assignments.<br>
	 * This is a typical operation for multi valued characteristics.
	 *
	 * @param valueName
	 *           the value to add
	 */
	public void removeValue(String valueName);

	/**
	 * Gets the first value of the assigned Values if existing, or null otherwise. This is a typical operation for single
	 * valued characteristics.
	 *
	 * @return first value of assigned values
	 */
	public String getSingleValue();

	/**
	 * clears all assigned Values, same as setting an empty List as assigned value list
	 */
	public void clearValues();

	/**
	 * @param booleanValue
	 *           <code>true</code>, only if this characteristic allow additional values
	 */
	public void setAllowsAdditionalValues(boolean booleanValue);


	/**
	 * @return the characteristic entry field mask for user input
	 */
	public String getEntryFieldMask();

	/**
	 * @return true only if this characteristic allow additional values
	 */
	public boolean isAllowsAdditionalValues();

	/**
	 * @param csticEntryFieldMask
	 *           characteristic entry field mask for user input
	 */
	public void setEntryFieldMask(String csticEntryFieldMask);


	/**
	 * @return true if the characteristic values are intervals in domain
	 */
	public boolean isIntervalInDomain();


	/**
	 * @param intervalInDomain
	 *           flag indicating whether the characteristic values are intervals in domain
	 */
	public void setIntervalInDomain(boolean intervalInDomain);

	/**
	 * @return true if the characteristic has conflicts
	 */
	public boolean hasConflicts();

	/**
	 * @param conflict
	 *           to assign
	 */
	public void addConflict(ConflictModel conflict);

	/**
	 * @return the list of characteristic conflicts
	 */
	public List<ConflictModel> getConflicts();

	/**
	 * Clears all existing conflicts
	 */
	public void clearConflicts();

	/**
	 * @return true if the characteristic is constrained
	 */
	public boolean isConstrained();

	/**
	 * @param constrained
	 *           flag indicating whether the characteristic is constrained
	 */
	public void setConstrained(boolean constrained);

	/**
	 * @return the length of the characteristic static domain
	 */
	public int getStaticDomainLength();

	/**
	 * @param staticDomainLength
	 *           length of the characteristic static domain
	 */
	public void setStaticDomainLength(final int staticDomainLength);
}
