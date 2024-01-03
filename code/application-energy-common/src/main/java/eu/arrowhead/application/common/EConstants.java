package eu.arrowhead.application.common;

public class EConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	public static final boolean DEBUG = true;			  //IMPORTANT, ENABLE PRINTOUTS THROUGHOUT ALL.

	//-------------------------------------------------------------------------------------------------
	public static final String ELECTRICITY_GENERATION = "electricity-generation";
	public static final String ELECTRICITY_GENERATION_URI = "/electricity-generation";

	public static final String ELECTRICITY_GENERATION_START = "electricity-generation-start";
	public static final String ELECTRICITY_GENERATION_START_URI = "/electricity-generation/start";

	public static final String ELECTRICITY_GENERATION_START_TIME = "electricity-generation-start-time";
	public static final String ELECTRICITY_GENERATION_START_TIME_URI = "/electricity-generation/start/{time}";

	public static final String ELECTRICITY_GENERATION_STOP = "electricity-generation-stop";
	public static final String ELECTRICITY_GENERATION_STOP_URI = "/electricity-generation/stop";

	//-------------------------------------------------------------------------------------------------
	public static final String ELECTRICITY_HOUSE = "electricity-house";
	public static final String ELECTRICITY_HOUSE_URI = "/electricity-house";

	public static final String GET_ELECTRICITY_HOUSE_SERVICE_DEFINTION = "get-electricity-house";
	public static final String POST_ELECTRICITY_HOUSE_SERVICE_DEFINTION = "post-electricity-house";
	public static final String PUT_ELECTRICITY_HOUSE_SERVICE_DEFINTION = "put-electricity-houose";

	//-------------------------------------------------------------------------------------------------
	public static final String ELECTRICITY_GRID = "electricity-grid";
	public static final String ELECTRICITY_GRID_URI = "/electricity-grid";

	public static final String POST_ELECTRICITY_GRID_SERVICE_DEFINTION = "post-electricity-grid";

	//-------------------------------------------------------------------------------------------------
	public static final String REQUEST_PARAM_TIME = "time";
	public static final String REQUEST_PARAM_KEY_TIME = "request-param-time";

	public static final String REQUEST_PARAM_ELECTRICITY = "electricity";
	public static final String REQUEST_PARAM_KEY_ELECTRICITY = "request-param-electricity";

	//-------------------------------------------------------------------------------------------------
	public static final long DELAY_TIME_SIM = 10l;	// Just delaying electricity production upon start. 

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
