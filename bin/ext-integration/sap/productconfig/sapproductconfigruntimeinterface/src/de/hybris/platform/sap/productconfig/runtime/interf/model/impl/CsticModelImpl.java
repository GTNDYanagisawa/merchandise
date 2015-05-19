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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CsticModelImpl extends BaseModelImpl implements CsticModel
{

	private String name;
	private String languageDependentName;
	private String longText;

	private List<CsticValueModel> assignedValues = Collections.emptyList();
	private List<CsticValueModel> assignableValues = Collections.emptyList();

	private int valueType;
	private int typeLength;
	private int numberScale;

	private boolean complete;
	private boolean consistent;
	private boolean constrained;
	private boolean multivalued;
	private boolean readonly;
	private boolean required;
	private boolean visible;

	private boolean changedByFrontend = false;
	private boolean allowsAdditionalValues;
	private String entryFieldMask;
	private String author;
	private boolean intervalInDomain;

	private int staticDomainLength;

	private List<ConflictModel> conflicts;


	public CsticModelImpl()
	{
		super();
		conflicts = new ArrayList<ConflictModel>();
	}

	@Override
	public String getName()
	{
		return name;
	}


	@Override
	public void setName(final String name)
	{
		this.name = name;
	}


	@Override
	public String getLanguageDependentName()
	{
		return languageDependentName;
	}


	@Override
	public void setLanguageDependentName(final String languageDependentName)
	{
		this.languageDependentName = languageDependentName;
	}

	@Override
	public String getLongText()
	{
		return longText;
	}

	@Override
	public void setLongText(final String longText)
	{
		this.longText = longText;
	}

	@Override
	public List<CsticValueModel> getAssignableValues()
	{
		return Collections.unmodifiableList(assignableValues);
	}

	@Override
	public void setAssignableValues(final List<CsticValueModel> assignableValues)
	{
		this.assignableValues = assignableValues;
	}


	@Override
	public List<CsticValueModel> getAssignedValues()
	{
		return Collections.unmodifiableList(assignedValues);
	}


	@Override
	public void setAssignedValues(final List<CsticValueModel> assignedValues)
	{
		if (!this.assignedValues.equals(assignedValues))
		{
			changedByFrontend = true;
		}
		this.setAssignedValuesWithoutCheckForChange(assignedValues);

	}

	@Override
	public void setAssignedValuesWithoutCheckForChange(final List<CsticValueModel> assignedValues)
	{
		this.assignedValues = assignedValues;

	}


	@Override
	public int getValueType()
	{
		return valueType;
	}


	@Override
	public void setValueType(final int valueType)
	{
		this.valueType = valueType;
	}


	@Override
	public int getTypeLength()
	{
		return typeLength;
	}


	@Override
	public void setTypeLength(final int typeLength)
	{
		this.typeLength = typeLength;
	}


	@Override
	public int getNumberScale()
	{
		return numberScale;
	}


	@Override
	public void setNumberScale(final int numberScale)
	{
		this.numberScale = numberScale;
	}


	@Override
	public boolean isVisible()
	{
		return visible;
	}


	@Override
	public void setVisible(final boolean visible)
	{
		this.visible = visible;
	}


	@Override
	public boolean isConsistent()
	{
		return consistent;
	}


	@Override
	public void setConsistent(final boolean consistent)
	{
		this.consistent = consistent;
	}


	@Override
	public boolean isComplete()
	{
		return complete;
	}


	@Override
	public void setComplete(final boolean complete)
	{
		this.complete = complete;
	}


	@Override
	public boolean isReadonly()
	{
		return readonly;
	}


	@Override
	public void setReadonly(final boolean readonly)
	{
		this.readonly = readonly;
	}


	@Override
	public boolean isRequired()
	{
		return required;
	}


	@Override
	public void setRequired(final boolean required)
	{
		this.required = required;
	}


	@Override
	public boolean isMultivalued()
	{
		return multivalued;
	}



	@Override
	public void setMultivalued(final boolean multivalued)
	{
		this.multivalued = multivalued;
	}


	@Override
	public boolean isChangedByFrontend()
	{
		return changedByFrontend;
	}


	@Override
	public void setChangedByFrontend(final boolean changedByFrontend)
	{
		this.changedByFrontend = changedByFrontend;
	}


	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(200);
		builder.append("CsticModelImpl [name=");
		builder.append(name);
		builder.append(", languageDependentName=");
		builder.append(languageDependentName);
		builder.append(", assignedValues=");
		builder.append(assignedValues);
		builder.append(", assignableValues=");
		builder.append(assignableValues);
		builder.append(", valueType=");
		builder.append(valueType);
		builder.append(", typeLength=");
		builder.append(typeLength);
		builder.append(", numberScale=");
		builder.append(numberScale);
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", consistent=");
		builder.append(consistent);
		builder.append(", multivalued=");
		builder.append(multivalued);
		builder.append(", readonly=");
		builder.append(readonly);
		builder.append(", required=");
		builder.append(required);
		builder.append(", visible=");
		builder.append(visible);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public CsticModel clone()
	{
		CsticModelImpl clonedCstic = null;
		clonedCstic = (CsticModelImpl) super.clone();

		clonedCstic.changedByFrontend = false;

		final List<CsticValueModel> clonedAssignedValues = new ArrayList<CsticValueModel>(this.assignedValues.size());
		for (final CsticValueModel assignedValue : this.assignedValues)
		{
			final CsticValueModel clonedAssignedValue = assignedValue.clone();
			clonedAssignedValues.add(clonedAssignedValue);
		}
		clonedCstic.assignedValues = clonedAssignedValues;

		final List<CsticValueModel> clonedAssignableValues = new ArrayList<CsticValueModel>(this.assignableValues.size());
		for (final CsticValueModel assignableValue : this.assignableValues)
		{
			final CsticValueModel clonedAssignableValue = assignableValue.clone();
			clonedAssignableValues.add(clonedAssignableValue);
		}
		clonedCstic.assignableValues = clonedAssignableValues;

		clonedCstic.conflicts = new ArrayList<ConflictModel>();
		for (final ConflictModel conflict : this.conflicts)
		{
			final ConflictModel clonedConflict = conflict.clone();
			clonedCstic.addConflict(clonedConflict);
		}

		return clonedCstic;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((assignableValues == null) ? 0 : assignableValues.hashCode());
		result = prime * result + ((assignedValues == null) ? 0 : assignedValues.hashCode());
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + (consistent ? 1231 : 1237);
		result = prime * result + (constrained ? 1231 : 1237);
		result = prime * result + ((languageDependentName == null) ? 0 : languageDependentName.hashCode());
		result = prime * result + (multivalued ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((longText == null) ? 0 : longText.hashCode());
		result = prime * result + numberScale;
		result = prime * result + (readonly ? 1231 : 1237);
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + typeLength;
		result = prime * result + valueType;
		result = prime * result + (visible ? 1231 : 1237);
		result = prime * result + staticDomainLength;
		return result;
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CsticModelImpl other = (CsticModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}
		if (assignableValues == null)
		{
			if (other.assignableValues != null)
			{
				return false;
			}
		}
		else if (!assignableValues.equals(other.assignableValues))
		{
			return false;
		}
		if (assignedValues == null)
		{
			if (other.assignedValues != null)
			{
				return false;
			}
		}
		else if (!assignedValues.equals(other.assignedValues))
		{
			return false;
		}
		if (complete != other.complete)
		{
			return false;
		}
		if (consistent != other.consistent)
		{
			return false;
		}
		if (constrained != other.constrained)
		{
			return false;
		}
		if (languageDependentName == null)
		{
			if (other.languageDependentName != null)
			{
				return false;
			}
		}
		else if (!languageDependentName.equals(other.languageDependentName))
		{
			return false;
		}
		if (multivalued != other.multivalued)
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		if (longText == null)
		{
			if (other.longText != null)
			{
				return false;
			}
		}
		else if (!longText.equals(other.longText))
		{
			return false;
		}
		if (numberScale != other.numberScale)
		{
			return false;
		}
		if (readonly != other.readonly)
		{
			return false;
		}
		if (required != other.required)
		{
			return false;
		}
		if (typeLength != other.typeLength)
		{
			return false;
		}
		if (valueType != other.valueType)
		{
			return false;
		}
		if (visible != other.visible)
		{
			return false;
		}
		if (staticDomainLength != other.staticDomainLength)
		{
			return false;
		}
		return true;
	}


	@Override
	public void setSingleValue(final String value)
	{
		List<CsticValueModel> newList;
		if (value == null || value.isEmpty())
		{
			newList = Collections.emptyList();
		}
		else
		{
			final CsticValueModel newValueModel = getValueModelForValue(value);
			newList = Collections.singletonList(newValueModel);
		}
		this.setAssignedValues(newList);
	}

	protected CsticValueModel getValueModelForValue(final String valueName)
	{
		CsticValueModel newValueModel = null;
		for (final CsticValueModel assignableValue : assignableValues)
		{
			if (isValueNameMatching(valueName, assignableValue))
			{
				newValueModel = assignableValue;
			}
		}
		if (newValueModel == null)
		{
			newValueModel = new CsticValueModelImpl();
			newValueModel.setName(valueName);
		}
		return newValueModel;
	}


	protected boolean isValueNameMatching(final String value, final CsticValueModel valueModel)
	{
		return (value == null && valueModel.getName() == null) || valueModel.getName().equals(value);
	}



	@Override
	public void addValue(final String valueName)
	{
		final CsticValueModel newValueModel = getValueModelForValue(valueName);
		final List<CsticValueModel> newValues = new ArrayList<CsticValueModel>(this.assignedValues);
		if (!newValues.contains(newValueModel))
		{
			newValues.add(newValueModel);
		}
		this.setAssignedValues(newValues);
	}

	@Override
	public void removeValue(final String valueName)
	{
		final CsticValueModel newValueModel = getValueModelForValue(valueName);
		final List<CsticValueModel> newValues = new ArrayList<CsticValueModel>(this.assignedValues);
		newValues.remove(newValueModel);
		this.setAssignedValues(newValues);
	}



	@Override
	public String getSingleValue()
	{
		String value = null;
		if (!assignedValues.isEmpty())
		{
			value = assignedValues.get(0).getName();
		}
		return value;
	}



	@Override
	public void clearValues()
	{
		setAssignedValues(Collections.EMPTY_LIST);
	}



	@Override
	public boolean isAllowsAdditionalValues()
	{
		return allowsAdditionalValues;
	}



	@Override
	public String getEntryFieldMask()
	{
		return entryFieldMask;
	}


	@Override
	public boolean isIntervalInDomain()
	{
		return this.intervalInDomain;
	}



	@Override
	public void setAllowsAdditionalValues(final boolean allowsAdditionalValues)
	{
		this.allowsAdditionalValues = allowsAdditionalValues;

	}


	@Override
	public void setEntryFieldMask(final String entryFieldMask)
	{
		this.entryFieldMask = entryFieldMask;
	}


	@Override
	public void setIntervalInDomain(final boolean intervalInDomain)
	{
		this.intervalInDomain = intervalInDomain;
	}


	@Override
	public boolean hasConflicts()
	{
		return (!this.conflicts.isEmpty());
	}


	@Override
	public void addConflict(final ConflictModel conflict)
	{
		this.conflicts.add(conflict);

	}


	@Override
	public List<ConflictModel> getConflicts()
	{
		return Collections.unmodifiableList(conflicts);
	}

	@Override
	public void clearConflicts()
	{
		this.conflicts.clear();
	}

	@Override
	public String getAuthor()
	{
		return author;
	}

	@Override
	public void setAuthor(final String author)
	{
		this.author = author;
	}

	@Override
	public boolean isConstrained()
	{
		return constrained;
	}

	@Override
	public void setConstrained(final boolean constrained)
	{
		this.constrained = constrained;
	}

	@Override
	public int getStaticDomainLength()
	{
		return staticDomainLength;
	}

	@Override
	public void setStaticDomainLength(final int staticDomainLength)
	{
		this.staticDomainLength = staticDomainLength;
	}
}
