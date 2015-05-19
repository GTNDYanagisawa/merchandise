package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.io.InputStream;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.core.ep.feed.FeedMetadataImpl;
import org.apache.olingo.odata2.core.ep.feed.ODataDeltaFeedImpl;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelDataProviderService;

public class DefaultMultichannelDataProviderServiceMockData extends
DefaultCarDataProviderServiceMockData implements MultichannelDataProviderService{
	
		/*
		 * 
		 * (non-Javadoc)
		 * @see de.hybris.platform.sap.sapcarintegration.services.impl.DefaultCarDataProviderService#readMultiChannelTransactionsFeed(java.lang.String, java.lang.String)
		 */
		public ODataFeed readMultiChannelTransactionsFeed(String customerNumber, PaginationData paginationData) {
	
			ODataFeed feed;
	
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/services/serviceMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/data/multichannelOrders.json");
	
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("MultiChannelPurchasesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				throw new RuntimeException("Error while reading MC sales Feed", e);//NOPMD
			}
	
			// to comply withestraction process
			FeedMetadataImpl feedMetadata = new FeedMetadataImpl();
			feedMetadata.setInlineCount(10);
			
			return new ODataDeltaFeedImpl(feed.getEntries(), feedMetadata);
		}
		
		
		public ODataFeed readSalesDocumentHeaderFeed(String customerNumber, String transactionNumber){
			ODataFeed feed;
			
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/services/serviceMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/data/multichannelOrder.json");
	
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("MultiChannelPurchasesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Multi Channel Sales Header Feed", e);//NOPMD
			}
	
			return feed;
		}
		
		public ODataFeed readSalesDocumentItemFeed(String customerNumber, String transactionNumber){
			ODataFeed feed;
			
			try {
	
				final InputStream edmContent = getClass().getResourceAsStream(
						"/test/services/serviceMetadata.xml");
				Edm edm = EntityProvider.readMetadata(edmContent, false);
	
				final EdmEntityContainer entityContainer = edm
						.getDefaultEntityContainer();
	
				InputStream content = getClass().getResourceAsStream(
						"/test/data/multichannelOrderItems.json");
	
				feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
						entityContainer.getEntitySet("MultiChannelPurchasesQueryResults"),
						content, EntityProviderReadProperties.init().build());
	
			} catch (Exception e) {
				throw new RuntimeException("Error while reading Multi Channel Sales Item Feed" , e);//NOPMD
			}
	
			return feed;
		}
	
}
