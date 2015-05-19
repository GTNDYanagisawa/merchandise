package de.hybris.platform.sap.sapcarintegration.services.impl;

import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;

public class DefaultCarConfigurationServiceMock implements CarConfigurationService{

	@Override
	public String getSapClient() {
		return "800";
	}
	
	@Override
	public String getRootUrl() {
		
		return "http://ldcird1.wdf.sap.corp:8002";
	};
	
	@Override
	public String getUsername() {
	return "anzeiger";
	}
	
	@Override
	public String getPassword() {
		return "display";
	}
	
	@Override
	public String getServiceName() {
		
		return "/sap/is/retail/car/int/odata/CARServices.xsodata";
	}

	@Override
	public String getTransactionType()
	{
		return "TA";
	}

	@Override
	public String getSalesOrganization()
	{
		return "1000";
	}

	@Override
	public String getDistributionChannel()
	{
		return "10";
	}

	@Override
	public String getDivision()
	{
		return "00";
	}
	
}
