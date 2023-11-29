package eu.arrowhead.application.common;

public class EConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	public static final boolean DEBUG = true;			  //IMPORTANT, ENABLE PRINTOUTS OBJECTS AND DATA 

	public static final String ELECTRICITY_GENERATION_SERVICE = "electricity-generation";
	public static final String ELECTRICITY_GENERATION_SERVICE_URI = "/electricity-generation";

	public static final String ELECTRICITY_GENERATION_SERVICE_START = "electricity-generation-start";
	public static final String ELECTRICITY_GENERATION_SERVICE_START_URI = "/electricity-generation/start";

	public static final String ELECTRICITY_GENERATION_SERVICE_START_TIME = "electricity-generation-start-time";
	public static final String ELECTRICITY_GENERATION_SERVICE_START_TIME_URI = "/electricity-generation/start/{time}";

	public static final String ELECTRICITY_GENERATION_SERVICE_STOP = "electricity-generation-stop";
	public static final String ELECTRICITY_GENERATION_SERVICE_STOP_URI = "/electricity-generation/stop";

	public static final String REQUEST_PARAM_TIME = "time";

	public static final long DELAY_TIME_SIM = 10l;

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
