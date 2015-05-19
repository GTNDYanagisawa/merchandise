/**
 * 
 */
package com.sap.hybris.reco.addon.be.cei;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import com.sap.hybris.reco.CMSSAPRecommendationComponent;
import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;
import com.sap.hybris.reco.addon.be.ProductRecommendationManagerBackend;
import com.sap.hybris.reco.addon.bo.RecommendationContextProvider;
import com.sap.hybris.reco.addon.dao.ProductRecommendation;
import com.sap.hybris.reco.common.util.HMCConfigurationReader;
import com.sap.hybris.reco.constants.SapproductrecommendationConstants;


/**
 * @author Administrator
 * 
 */
@BackendType("CEI")
public class ProductRecommendationManagerCEI extends BackendBusinessObjectBaseJCo implements ProductRecommendationManagerBackend
{
	private final static Logger LOG = Logger.getLogger( ProductRecommendationManagerCEI.class.getName() );
	private static String JCO_STATELESS = "JCoStateless";
	private ProductService productService;
	protected HMCConfigurationReader configuration;
	private CMSSAPRecommendationComponentModel componentModel;

	@Override
	public List<ProductRecommendation> getProductRecommendation(final RecommendationContextProvider context)
	{
		final List<ProductRecommendation> result = new ArrayList<ProductRecommendation>();
      
		//build JCoConneciton
		JCoConnection jCoConnection = null;
		if (configuration.getRfcDestinationId() == null)
		{
			jCoConnection = getDefaultJCoConnection();
		}
		else
		{
			jCoConnection = getJCoConnection(JCO_STATELESS, configuration.getRfcDestinationId());
		}

		try
		{
			if (jCoConnection.isBackendAvailable() == false)
			{
				LOG.error("RFC - " + configuration.getRfcDestinationId() + " backend is not available");
				return result;
			}
		}
		catch (final BackendException e)
		{
			LOG.error("", e);
			return result;
		}
		
		final String engineId = context.getRecommendationModel();
		final String itemType= context.getItemDataSourceType();
		final String productId = context.getProductId();
		final String includeCart = context.getIncludeCart();
		try
		{
			final JCoFunction function = jCoConnection.getFunction("PROD_RECO_GET_RECOMMENDATIONS");

			final JCoParameterList importParameterList = function.getImportParameterList();
			final JCoTable recommenders = importParameterList.getTable("IT_RECOMMENDERS");
			recommenders.appendRow();
			recommenders.setValue("MODEL_TYPE", engineId);
			final JCoTable leadingObjects = recommenders.getTable("LEADING_OBJECTS");
			if ( productId != null && !productId.equals("") && !productId.equalsIgnoreCase("null"))
			{
				leadingObjects.appendRow();
				leadingObjects.setValue("ITEM_TYPE", itemType);
				leadingObjects.setValue("ITEM_ID", productId);
			}
			final JCoTable cartEntries = recommenders.getTable("BASKET_OBJECTS");
			
			for (final String cartItem : context.getCartItems())
			{
				cartEntries.appendRow();
				cartEntries.setValue("ITEM_ID", cartItem);
				cartEntries.setValue("ITEM_TYPE", itemType);
				if (includeCart.equalsIgnoreCase(Boolean.toString(Boolean.TRUE)))
				{
					leadingObjects.appendRow();
					leadingObjects.setValue("ITEM_TYPE", itemType);
					leadingObjects.setValue("ITEM_ID", cartItem);					
				}
			}

			importParameterList.setValue("IV_USER_ID", context.getUserId());
			importParameterList.setValue("IV_USER_TYPE", configuration.getUserType()); 

			jCoConnection.execute(function);

			final JCoParameterList exportParameterList = function.getExportParameterList();

			final JCoTable results = exportParameterList.getTable("ET_RESULTS");

			if (!results.isEmpty())
			{
				final int len = results.getNumRows();
				for (int i = 0; i < len; i++)
				{
					results.setRow(i);
					final String recommendationId = results.getString("ITEM_ID");
					final String recommendationType = results.getString("MODEL_TYPE");
					final ProductRecommendation productRecommendation = createProductRecommedation(recommendationId,
							recommendationType);
					if (productRecommendation != null)
					{
						result.add(productRecommendation);
					}
				}
			}
		}
		catch (final BackendException e)
		{
			LOG.error("", e);
		}
		finally
		{
		}

		return result;
	}

	/**
	 * @param result
	 */
	private ProductRecommendation createProductRecommedation(final String productId, final String type)
	{
		try
		{
			final ProductModel product = getProductService().getProductForCode(productId);
			final ProductRecommendation productRecommendation = new ProductRecommendation();
			productRecommendation.setProduct(product);
			return productRecommendation;
		}
		catch (final UnknownIdentifierException exception)
		{
			return null;
		}
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}


	public HMCConfigurationReader getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(final HMCConfigurationReader configuration)
	{
		this.configuration = configuration;
	}

}
