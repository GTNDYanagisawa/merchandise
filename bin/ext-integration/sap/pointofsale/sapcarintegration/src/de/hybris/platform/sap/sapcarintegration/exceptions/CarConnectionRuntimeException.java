package de.hybris.platform.sap.sapcarintegration.exceptions;

public class CarConnectionRuntimeException extends RuntimeException
{

	private static final long serialVersionUID = -6810533990715272730L;

	public CarConnectionRuntimeException(String msg, Exception e)
	{
		super(msg, e);
	}
	
	public CarConnectionRuntimeException(String msg)
	{
		super(msg);
	}
}
