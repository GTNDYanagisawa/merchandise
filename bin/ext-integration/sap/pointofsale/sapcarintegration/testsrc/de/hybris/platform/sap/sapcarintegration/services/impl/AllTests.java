package de.hybris.platform.sap.sapcarintegration.services.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
{ 
	DefaultCarDataProviderServiceTest.class, 
	DefaultCarOrderHistoryExtractorServiceTest.class,
	DefaultCarOrderHistoryServiceTest.class,
	DefaultMultichannelDataProviderServiceTest.class, 
	DefaultMultichannelOrderHistoryExtractorServiceTest.class,
	DefaultMultichannelOrderHistoryServiceTest.class,
		
		 })
public class AllTests
{

}
