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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;


public class ConfigModelFactoryImpl implements ConfigModelFactory
{

	@Override
	public ConfigModel createInstanceOfConfigModel()
	{
		return new ConfigModelImpl();
	}

	@Override
	public InstanceModel createInstanceOfInstanceModel()
	{
		return new InstanceModelImpl();
	}

	@Override
	public CsticModel createInstanceOfCsticModel()
	{
		return new CsticModelImpl();
	}

	@Override
	public CsticValueModel createInstanceOfCsticValueModel()
	{
		return new CsticValueModelImpl();
	}

	@Override
	public CsticGroupModel createInstanceOfCsticGroupModel()
	{
		return new CsticGroupModelImpl();
	}

	@Override
	public ConflictModel createInstanceOfConflictModel()
	{
		return new ConflictModelImpl();
	}

	@Override
	public PriceModel createInstanceOfPriceModel()
	{
		return new PriceModelImpl();
	}


	@Override
	public PriceModel getZeroPriceModel()
	{
		return PriceModel.NO_PRICE;
	}

}
