package eu.arrowhead.application.common;

public class EUtilities {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static long nowSeconds() {
		return System.currentTimeMillis() / 1000;
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public EUtilities() {		
		throw new UnsupportedOperationException();
	}
}
