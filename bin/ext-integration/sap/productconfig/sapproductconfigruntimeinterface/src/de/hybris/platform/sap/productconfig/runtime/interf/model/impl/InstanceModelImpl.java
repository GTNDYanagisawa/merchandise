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

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticGroupImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.ArrayList;
import java.util.List;


public class InstanceModelImpl extends BaseModelImpl implements InstanceModel
{

	private String id;
	private String name;
	private String languageDependentName;
	private String position;

	private List<InstanceModel> subInstances = new ArrayList<InstanceModel>();
	private List<CsticModel> cstics = new ArrayList<CsticModel>();

	private List<CsticGroupModel> csticGroups = new ArrayList<CsticGroupModel>();

	private boolean complete;
	private boolean consistent;
	private boolean rootInstance;

	@Override
	public String getId()
	{
		return id;
	}


	@Override
	public void setId(final String id)
	{
		this.id = id;
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
	public List<InstanceModel> getSubInstances()
	{
		return subInstances;
	}


	@Override
	public void setSubInstances(final List<InstanceModel> subInstances)
	{
		this.subInstances = subInstances;
	}


	@Override
	public List<CsticModel> getCstics()
	{
		return cstics;
	}


	@Override
	public void setCstics(final List<CsticModel> cstics)
	{
		this.cstics = cstics;
	}


	@Override
	public boolean isRootInstance()
	{
		return rootInstance;
	}

	@Override
	public void setRootInstance(final boolean rootInstance)
	{
		this.rootInstance = rootInstance;
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
	public CsticModel getCstic(final String csticName)
	{
		final List<CsticModel> cstics = getCstics();
		for (final CsticModel cstic : cstics)
		{
			if (cstic.getName().equalsIgnoreCase(csticName))
			{
				return cstic;
			}
		}
		return null;
	}

	@Override
	public void setCstic(final CsticModel cstic)
	{
		throw new IllegalStateException("Method not implemented");
	}

	@Override
	public InstanceModel getSubInstance(final String subInstanceId)
	{
		final List<InstanceModel> subInstances = getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			if (subInstance.getId().equalsIgnoreCase(subInstanceId))
			{
				return subInstance;
			}
		}
		return null;
	}

	@Override
	public InstanceModel removeSubInstance(final String subInstanceId)
	{
		final List<InstanceModel> subInstances = getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			if (subInstance.getId().equalsIgnoreCase(subInstanceId))
			{
				subInstances.remove(subInstance);
				return subInstance;
			}
		}
		return null;
	}



	@Override
	public void setSubInstance(final InstanceModel subInstance)
	{
		throw new IllegalStateException("Method not implemented");
	}

	@Override
	public void setCsticGroups(final List<CsticGroupModel> csticGroups)
	{
		this.csticGroups = csticGroups;
	}


	@Override
	public List<CsticGroupModel> getCsticGroups()
	{
		return csticGroups;
	}

	@Override
	public List<CsticGroup> retrieveCsticGroupsWithCstics()
	{
		final List<CsticGroup> csticGroupsWithCstics = new ArrayList<CsticGroup>();

		for (final CsticGroupModel csticModelGroup : getCsticGroups())
		{
			final CsticGroup csticGroup = new CsticGroupImpl();

			String langDepName = csticModelGroup.getDescription();
			final String name = csticModelGroup.getName();
			langDepName = getDisplayName(langDepName, name);
			csticGroup.setName(name);
			csticGroup.setDescription(langDepName);

			final List<CsticModel> cstics = getCsticsForGroup(csticModelGroup);
			csticGroup.setCstics(cstics);

			if (cstics.size() == 0)
			{
				continue;
			}

			csticGroupsWithCstics.add(csticGroup);
		}

		if (csticGroupsWithCstics.size() == 0)
		{
			final CsticGroup group = new CsticGroupImpl();
			group.setName(GENERAL_GROUP_NAME);
			group.setCstics(getCstics());
			csticGroupsWithCstics.add(group);
		}

		return csticGroupsWithCstics;
	}

	protected String getDisplayName(String langDepName, final String name)
	{
		if (langDepName == null || langDepName.isEmpty())
		{
			langDepName = "[" + name + "]";
		}
		return langDepName;
	}

	private List<CsticModel> getCsticsForGroup(final CsticGroupModel csticModelGroup)
	{
		final List<CsticModel> cstics = new ArrayList<>();
		final List<String> csticNames = csticModelGroup.getCsticNames();
		if (csticNames == null)
		{
			return cstics;
		}

		for (final String csticName : csticNames)
		{
			final CsticModel cstic = getCstic(csticName);
			if (cstic == null)
			{
				continue;
			}

			cstics.add(cstic);
		}
		return cstics;
	}

	@Override
	public String getPosition()
	{
		return position;
	}


	@Override
	public void setPosition(final String position)
	{
		this.position = position;
	}

	@Override
	public InstanceModel clone()
	{
		InstanceModel clonedInstanceModel;
		clonedInstanceModel = (InstanceModel) super.clone();

		final List<InstanceModel> clonedSubInstances = new ArrayList<InstanceModel>();
		for (final InstanceModel subInstance : this.subInstances)
		{
			final InstanceModel clonedSubInstance = subInstance.clone();
			clonedSubInstances.add(clonedSubInstance);
		}
		clonedInstanceModel.setSubInstances(clonedSubInstances);

		final List<CsticModel> clonedCstics = new ArrayList<CsticModel>();
		for (final CsticModel cstic : this.cstics)
		{
			final CsticModel clonedCstic = cstic.clone();
			clonedCstics.add(clonedCstic);
		}
		clonedInstanceModel.setCstics(clonedCstics);

		final List<CsticGroupModel> clonedCsticGroups = new ArrayList<CsticGroupModel>();
		for (final CsticGroupModel csticGroup : this.csticGroups)
		{
			final CsticGroupModel clonedCsticGroup = csticGroup.clone();
			clonedCsticGroups.add(clonedCsticGroup);
		}
		clonedInstanceModel.setCsticGroups(clonedCsticGroups);

		return clonedInstanceModel;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + (consistent ? 1231 : 1237);
		result = prime * result + ((cstics == null) ? 0 : cstics.hashCode());
		result = prime * result + ((csticGroups == null) ? 0 : csticGroups.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((languageDependentName == null) ? 0 : languageDependentName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + (rootInstance ? 1231 : 1237);
		result = prime * result + ((subInstances == null) ? 0 : subInstances.hashCode());
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
		final InstanceModelImpl other = (InstanceModelImpl) obj;
		if (!super.equals(other))
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
		if (cstics == null)
		{
			if (other.cstics != null)
			{
				return false;
			}
		}
		else if (!cstics.equals(other.cstics))
		{
			return false;
		}
		if (csticGroups == null)
		{
			if (other.csticGroups != null)
			{
				return false;
			}
		}
		else if (!csticGroups.equals(other.csticGroups))
		{
			return false;
		}
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
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
		if (position == null)
		{
			if (other.position != null)
			{
				return false;
			}
		}
		else if (!position.equals(other.position))
		{
			return false;
		}
		if (rootInstance != other.rootInstance)
		{
			return false;
		}
		if (subInstances == null)
		{
			if (other.subInstances != null)
			{
				return false;
			}
		}
		else if (!subInstances.equals(other.subInstances))
		{
			return false;
		}
		return true;
	}
}
