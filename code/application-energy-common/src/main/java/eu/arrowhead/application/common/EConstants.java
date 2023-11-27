package eu.arrowhead.application.common;

public class EConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String ELECTRICITY_GENERATION_SERVICE = "electricity-generation-details";
	public static final String ELECTRICITY_GENERATION_SERVICE_URI = "/electricity-generation";

	public static final String REQUEST_PARAM_HOURS = "hours-elapsed";

	public static final String INTERFACE_SECURE = "HTTP-SECURE-XML";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-XML";
	public static final String HTTP_METHOD = "http-method";

	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private EConstants() {
		throw new UnsupportedOperationException();
	}
}
