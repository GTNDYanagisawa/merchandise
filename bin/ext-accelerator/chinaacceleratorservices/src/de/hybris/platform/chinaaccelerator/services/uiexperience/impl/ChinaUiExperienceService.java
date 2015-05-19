/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.uiexperience.impl;

//import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.impl.DefaultUiExperienceService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;


public class ChinaUiExperienceService extends DefaultUiExperienceService
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.acceleratorservices.uiexperience.impl.DefaultUiExperienceService#getDetectedUiExperienceLevel()
	 */
	@Override
	public UiExperienceLevel getDetectedUiExperienceLevel()
	{
		return UiExperienceLevel.DESKTOP;
	}

}
