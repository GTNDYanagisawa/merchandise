package de.hybris.platform.sap.sapcarintegration.services;

public interface CarConfigurationService {

	String getSapClient();

	String getRootUrl();

	String getServiceName();

	String getUsername();

	String getPassword();
	
	String getTransactionType();
	
	String getSalesOrganization();
	
	String getDistributionChannel();
	
	String getDivision();

}
