package eu.arrowhead.application.house.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.common.debug.Debug;
import eu.arrowhead.application.common.payload.EGenerationPayload;
import eu.arrowhead.application.house.ProviderApplicationInitListener;
import eu.arrowhead.application.house.service.HouseService;
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

@RestController
public class HouseProviderController {
	
	//=================================================================================================
	// members

	@Autowired
	HouseService houseService;

	@Autowired
	public SSLProperties sslProperties;

	@Autowired
	public ArrowheadService arrowheadService;

	private Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);

	OrchestrationResultDTO orchestrationResult;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	@GetMapping(path = EConstants.ELECTRICITY_HOUSE_URI)
	@ResponseBody public float getBattery() 
		throws IOException, URISyntaxException {
			return houseService.getBattery();
	}

	//-------------------------------------------------------------------------------------------------	
	@PostMapping(path = EConstants.ELECTRICITY_HOUSE_URI)	
	@ResponseBody public float setBattery(
		@RequestParam(name = EConstants.REQUEST_PARAM_ELECTRICITY) final String electricity) 
		throws IOException, URISyntaxException {
			float total = Float.parseFloat(electricity);
			houseService.setBattery(total);
			return houseService.getBattery();
	}

	//-------------------------------------------------------------------------------------------------	
	@PutMapping(path = EConstants.ELECTRICITY_HOUSE_URI)	
	@ResponseBody public float updateBattery(
		@RequestParam(name = EConstants.REQUEST_PARAM_ELECTRICITY) final String electricity)
		throws IOException, URISyntaxException {
			float total = Float.parseFloat(electricity);
			float currentCharge = houseService.getBattery();
			houseService.setBattery(currentCharge + total);
			Debug.debug("Current battery charge after consumption: ", houseService.getBattery());

			if(orchestrationResult == null){
				orchestrationResult = orchestrate(EConstants.ELECTRICITY_GENERATION);	
			}

			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());

			EGenerationPayload response = arrowheadService.consumeServiceHTTP(EGenerationPayload.class, 
				HttpMethod.valueOf(orchestrationResult.getMetadata().get(EConstants.HTTP_METHOD)), 
				orchestrationResult.getProvider().getAddress(), 
				orchestrationResult.getProvider().getPort(), 
				orchestrationResult.getServiceUri(),
				getInterface(),
				token,
				null
			);
			Debug.debug(response);

			currentCharge = houseService.getBattery();
			houseService.setBattery(currentCharge + response.getElectricity());
			
			Debug.debug("Current battery charge after provider: ", houseService.getBattery());

			return houseService.getBattery();
	}

	//-------------------------------------------------------------------------------------------------	
    public OrchestrationResultDTO orchestrate(final String serviceDefinition) {
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

	//-------------------------------------------------------------------------------------------------	
	private String getInterface() {
    	return sslProperties.isSslEnabled() ? EConstants.INTERFACE_SECURE : EConstants.INTERFACE_INSECURE;
    }

	//-------------------------------------------------------------------------------------------------	
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
