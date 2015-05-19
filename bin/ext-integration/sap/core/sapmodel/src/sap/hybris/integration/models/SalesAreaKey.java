/**
 * 
 */
package sap.hybris.integration.models;



/**
 * @author Administrator
 * 
 */
public class SalesAreaKey implements Comparable<SalesAreaKey>
{
	private String salesOrganization = "";
	private String materialDistributionChannel = "";
	private String conditionDistributionChannel = "";



	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final SalesAreaKey o)
	{
		if (!getSalesOrganization().equals(o.getSalesOrganization()))
		{
			return getSalesOrganization().compareTo(o.getSalesOrganization());
		}

		if (getSalesOrganization().equals(o.getSalesOrganization()))
		{
			if (!getMaterialDistributionChannel().equals(o.getMaterialDistributionChannel()))
			{
				return getMaterialDistributionChannel().compareTo(o.getMaterialDistributionChannel());

			}
			//first 2 parts are a match, compare the last part
			else
			{
				return getConditionDistributionChannel().compareTo(o.getConditionDistributionChannel());
			}


		}

		//should never get executed
		return -1;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conditionDistributionChannel == null) ? 0 : conditionDistributionChannel.hashCode());
		result = prime * result + ((materialDistributionChannel == null) ? 0 : materialDistributionChannel.hashCode());
		result = prime * result + ((salesOrganization == null) ? 0 : salesOrganization.hashCode());
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final SalesAreaKey other = (SalesAreaKey) obj;
		if (conditionDistributionChannel == null)
		{
			if (other.conditionDistributionChannel != null)
			{
				return false;
			}
		}
		else if (!conditionDistributionChannel.equals(other.conditionDistributionChannel))
		{
			return false;
		}
		if (materialDistributionChannel == null)
		{
			if (other.materialDistributionChannel != null)
			{
				return false;
			}
		}
		else if (!materialDistributionChannel.equals(other.materialDistributionChannel))
		{
			return false;
		}
		if (salesOrganization == null)
		{
			if (other.salesOrganization != null)
			{
				return false;
			}
		}
		else if (!salesOrganization.equals(other.salesOrganization))
		{
			return false;
		}
		return true;
	}


	/**
	 * @return the salesOrganization
	 */
	public String getSalesOrganization()
	{
		return salesOrganization;
	}



	/**
	 * @param salesOrganization
	 *           the salesOrganization to set
	 */
	public void setSalesOrganization(final String salesOrganization)
	{
		this.salesOrganization = salesOrganization;
	}



	/**
	 * @return the materialDistributionChannel
	 */
	public String getMaterialDistributionChannel()
	{
		return materialDistributionChannel;
	}



	/**
	 * @param materialDistributionChannel
	 *           the materialDistributionChannel to set
	 */
	public void setMaterialDistributionChannel(final String materialDistributionChannel)
	{
		this.materialDistributionChannel = materialDistributionChannel;
	}



	/**
	 * @return the conditionDistributionChannel
	 */
	public String getConditionDistributionChannel()
	{
		return conditionDistributionChannel;
	}



	/**
	 * @param conditionDistributionChannel
	 *           the conditionDistributionChannel to set
	 */
	public void setConditionDistributionChannel(final String conditionDistributionChannel)
	{
		this.conditionDistributionChannel = conditionDistributionChannel;
	}



}
