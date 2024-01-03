package eu.arrowhead.application.consumer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.common.debug.Debug;
import eu.arrowhead.application.consumer.service.ElectricityConsumptionConstants;
import eu.arrowhead.application.consumer.service.ElectricityConsumptionService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EConstants.BASE_PACKAGE})
public class ElectricityConsumerMain implements ApplicationRunner{

	//=================================================================================================
	// members

	@Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;

	private final Logger logger = LogManager.getLogger(ElectricityConsumerMain.class);

	OrchestrationResultDTO orchestrationResult;

	ElectricityConsumptionService service;

	private float previousResponse;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(ElectricityConsumerMain.class, args);
	}	

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run(final ApplicationArguments args) throws Exception {
		setup();
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
    	while(true){
			TimeUnit.SECONDS.sleep(1);
			float consumption = service.simulate();
			String[] queryParamElectricity = {orchestrationResult.getMetadata().get(EConstants.REQUEST_PARAM_KEY_ELECTRICITY), String.valueOf(consumption)};	

			float response = arrowheadService.consumeServiceHTTP(float.class, 
				HttpMethod.valueOf(orchestrationResult.getMetadata().get(EConstants.HTTP_METHOD)), 
				orchestrationResult.getProvider().getAddress(), 
				orchestrationResult.getProvider().getPort(), 
				orchestrationResult.getServiceUri(),
				getInterface(),
				token,
				null,
				queryParamElectricity
			);
			Debug.debug("CONSUMPTION= ", consumption + " " + ElectricityConsumptionConstants.TYPE);
			Debug.debug("BATTERY= ", response + " " + ElectricityConsumptionConstants.TYPE);
			Debug.debug("NET_CHANGE= ", response-previousResponse + " " + ElectricityConsumptionConstants.TYPE);
			Debug.debug("#####","######");
			previousResponse = response;
		}
    }

	//-------------------------------------------------------------------------------------------------
	public void setup(){
		orchestrationResult = orchestrate(EConstants.PUT_ELECTRICITY_HOUSE_SERVICE_DEFINTION);
		Debug.debug("Orchestration result: ", orchestrationResult);

		service = new ElectricityConsumptionService();
	}

	//-------------------------------------------------------------------------------------------------
	private OrchestrationResultDTO orchestrate(final String serviceDefinition) {
		logger.info("Orchestration request for " + serviceDefinition + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(serviceDefinition)
    			.interfaces(getInterface())
    			.build();
    	
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
    	final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
    			.flag(Flag.MATCHMAKING, true)
    			.flag(Flag.OVERRIDE_STORE, true)
    			.build();

		Debug.printOut(orchestrationFormRequest);

    	final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);

		Debug.printOut(orchestrationResponse);

    	if (orchestrationResponse == null) {
    		logger.info("No orchestration response received");
    	} else if (orchestrationResponse.getResponse().isEmpty()) {
    		logger.info("No provider found during the orchestration");
    	} else {
    		final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
    		validateOrchestrationResult(orchestrationResult, serviceDefinition);
    		return orchestrationResult;
    	}
    	throw new ArrowheadException("Unsuccessful orchestration: " + serviceDefinition);
    }

	private String getInterface() {
    	return sslProperties.isSslEnabled() ? EConstants.INTERFACE_SECURE : EConstants.INTERFACE_INSECURE;
    }

	private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}
    	
    	boolean hasValidInterface = false;
    	for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
			if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
				hasValidInterface = true;
				break;
			}
		}
    	if (!hasValidInterface) {
    		throw new InvalidParameterException("Requested and orchestrated interface do not match");
		}
    }
}
