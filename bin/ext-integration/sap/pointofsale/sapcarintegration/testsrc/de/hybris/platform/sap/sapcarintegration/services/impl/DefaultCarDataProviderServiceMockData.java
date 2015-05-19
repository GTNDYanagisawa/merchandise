package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.FeedMetadata;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.core.ep.feed.FeedMetadataImpl;
import org.apache.olingo.odata2.core.ep.feed.ODataDeltaFeedImpl;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;

public class DefaultCarDataProviderServiceMockData implements
		CarDataProviderService {

	
		// reads Header data
		@Override
		public ODataFeed readHeaderFeed(String customerNumber, String sortBy) {
	
			ODataFeed feed;
	
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/posServicesMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/POSSalesHeader.json");
	
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				
				throw new RuntimeException("Error while reading Header Feed", e); //NOPMD
			}
	
			// to comply withestraction process
			FeedMetadataImpl feedMetadata = new FeedMetadataImpl();
			feedMetadata.setInlineCount(10);
			
			return new ODataDeltaFeedImpl(feed.getEntries(), feedMetadata);
		}
		
		
		
		@Override
		public ODataFeed readHeaderFeed(Date businessDayDate, String storeId,
				Integer transactionIndex){

			ODataFeed feed;
			
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/posServicesMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/POSSalesHeaderWithKey.json");
	
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
			}
	
			return feed;
		}
		

		// reads Header data
		@Override
		public ODataFeed readItemFeed(Date businessDayDate, String storeId, Integer transactionIndex){

			ODataFeed feed;

			try {

				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/posServicesMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);

				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();

				InputStream content = getClass().getResourceAsStream(
						"/test/SalesQueryItem.json");
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
			}

			return feed;
		}
		

		@Override
		public ODataFeed readLocaltionFeed(String location) {

			ODataFeed feed;

			try {

				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/services/serviceMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);

				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();

				InputStream content = getClass().getResourceAsStream(
						"/test/data/storeLocation.json");

				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("RetailLocationQueryResults"),
						content, EntityProviderReadProperties.init().build());

			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
			}

			return feed;
		}

		
		@Override
		public ODataFeed readHeaderFeed(String customerNumber,
				PaginationData paginationData) {
			ODataFeed feed;
			
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/services/serviceMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/data/carOrders.json");
	
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				
				throw new RuntimeException("Error while reading Header Feed", e); //NOPMD
			}
	
			// to comply withestraction process
			FeedMetadataImpl feedMetadata = new FeedMetadataImpl();
			feedMetadata.setInlineCount(10);
			
			return new ODataDeltaFeedImpl(feed.getEntries(), feedMetadata);
		}
		
		
		@Override
		public ODataFeed readHeaderFeed(String businessDayDate, String storeId,
				Integer transactionIndex, String customerNumber) {
			
			ODataFeed feed;
			
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/services/serviceMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/data/carOrder.json");
	
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
				
				
	
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
			}
	
			return feed;
		}
		
		
		@Override
		public ODataFeed readItemFeed(String businessDayDate, String storeId,
				Integer transactionIndex, String customerNumber) {
			ODataFeed feed;

			try {

				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/services/serviceMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);

				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();

				InputStream content = getClass().getResourceAsStream(
						"/test/data/carOrderItems.json");
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("POSSalesQueryResults"),
						content, EntityProviderReadProperties.init().build());
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
			}

			return feed;
		}

		
		
	
}
