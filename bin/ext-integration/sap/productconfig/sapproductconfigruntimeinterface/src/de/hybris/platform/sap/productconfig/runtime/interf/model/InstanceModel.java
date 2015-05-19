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

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;

import java.util.List;


/**
 * Represents the instance value model.
 */
public interface InstanceModel extends BaseModel
{
	/**
	 * General group, used for all cstic, which are not assigned to another group
	 */
	public static final String GENERAL_GROUP_NAME = "SAP_GENERAL_GROUP_NAME";

	/**
	 * @return the instance id
	 */
	public String getId();

	/**
	 * @param id
	 *           instance id
	 */
	public void setId(String id);

	/**
	 * @return the instance name
	 */
	public String getName();

	/**
	 * @param name
	 *           instance name
	 */
	public void setName(String name);

	/**
	 * @return the instance language dependent name
	 */
	public String getLanguageDependentName();

	/**
	 * @param languageDependentName
	 *           instance language dependent name
	 */
	public void setLanguageDependentName(String languageDependentName);

	/**
	 * @param csticName
	 *           characteristic name
	 * @return the characteristic model for the given characteristic name
	 */
	public CsticModel getCstic(String csticName);

	/**
	 * @param cstic
	 *           characteristic model
	 */
	public void setCstic(CsticModel cstic);

	/**
	 * @return the list of characteristic models of this instance
	 */
	public List<CsticModel> getCstics();

	/**
	 * @param cstic
	 *           list of characteristic models
	 */
	public void setCstics(List<CsticModel> cstic);

	/**
	 * @param subInstanceId
	 *           subinstance id
	 * @return the subinstance model for the given subinstance id
	 */
	public InstanceModel getSubInstance(String subInstanceId);

	/**
	 * removes the subinstance from the subinstance list for the given subinstance id
	 * 
	 * @param subInstanceId
	 *           subinstance id
	 * @return removed subinstance model
	 */
	public InstanceModel removeSubInstance(String subInstanceId);

	/**
	 * @param subInstance
	 *           subinstance model
	 */
	public void setSubInstance(InstanceModel subInstance);

	/**
	 * @return the list of subinstance models of this instance
	 */
	public List<InstanceModel> getSubInstances();

	/**
	 * @param subInstances
	 *           list of subinstance models
	 */
	public void setSubInstances(List<InstanceModel> subInstances);

	/**
	 * @return true if this instance is consistent
	 */
	public boolean isConsistent();

	/**
	 * @param consistent
	 *           flag indicating whether this instance is consistent
	 */
	public void setConsistent(boolean consistent);

	/**
	 * @return true if this instance is complete
	 */
	public boolean isComplete();

	/**
	 * @param complete
	 *           flag indicating whether this instance is complete
	 */
	public void setComplete(boolean complete);

	/**
	 * @return true if this instance is a root instance
	 */
	boolean isRootInstance();

	/**
	 * @param rootInstance
	 *           flag indicating whether this instance is a root instance
	 */
	void setRootInstance(boolean rootInstance);

	/**
	 * @param csticGroups
	 *           list of characteristic group models
	 */
	public void setCsticGroups(List<CsticGroupModel> csticGroups);

	/**
	 * @return the list of characteristic group models from this instance
	 */
	public List<CsticGroupModel> getCsticGroups();

	/**
	 * @return the list of <code>CsticGroup</code> from this instance
	 */
	public List<CsticGroup> retrieveCsticGroupsWithCstics();

	/**
	 * @return the BOM position of this instance
	 */
	public String getPosition();

	/**
	 * @param position
	 *           BOM position of this instance
	 */
	public void setPosition(String position);


	/**
	 * @return cloned <code>InstanceModel</code>
	 */
	@Override
	public InstanceModel clone();

}
