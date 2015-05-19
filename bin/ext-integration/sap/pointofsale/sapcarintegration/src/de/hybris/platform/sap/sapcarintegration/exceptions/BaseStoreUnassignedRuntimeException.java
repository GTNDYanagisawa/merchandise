package de.hybris.platform.sap.sapcarintegration.exceptions;

public class BaseStoreUnassignedRuntimeException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseStoreUnassignedRuntimeException(String msg, Exception e)
	{
		super(msg, e);
	}
	
	public BaseStoreUnassignedRuntimeException(String msg)
	{
		super(msg);
	}
}
