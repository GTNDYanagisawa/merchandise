package de.hybris.platform.sap.sapmodel.exceptions;

public class SAPModelRuntimeException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SAPModelRuntimeException(Exception e)
	{
		super(e);		
	}
	public SAPModelRuntimeException(String msg)
	{
		super(msg);		
	}

}
