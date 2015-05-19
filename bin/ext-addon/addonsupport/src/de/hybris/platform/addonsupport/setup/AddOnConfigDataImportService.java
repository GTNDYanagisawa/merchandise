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
package de.hybris.platform.addonsupport.setup;

import de.hybris.platform.addonsupport.impex.AddonConfigDataImportType;
import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;

public interface AddOnConfigDataImportService
{
	boolean executeImport(final String extensionName, AddonConfigDataImportType importType, ImpexMacroParameterData macroParameters);
}
