package eu.arrowhead.application.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.common.EUtilities;
import eu.arrowhead.application.common.payload.EGenerationPayload;
import eu.arrowhead.application.provider.service.ElectricityGenerationService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
public class ElectricityProviderController {
	
	//=================================================================================================
	// members

	@Autowired
	ElectricityGenerationService electricityGenerationService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	@GetMapping(path = EConstants.ELECTRICITY_GENERATION_SERVICE_URI)
	@ResponseBody public EGenerationPayload getGeneratedElectricity() 
		throws IOException, URISyntaxException {
			if (! electricityGenerationService.isStarted()) {
				throw new BadPayloadException("Solar panels are stopped!");
			}
			return electricityGenerationService.simulate();
	}

	//-------------------------------------------------------------------------------------------------	
	@PostMapping(path = EConstants.ELECTRICITY_GENERATION_SERVICE_START_URI)
	@ResponseBody public String startGeneratedElectricity() 
		throws IOException, URISyntaxException {
			long currentTime = EUtilities.nowSeconds();
			if(! electricityGenerationService.isStarted()){
				electricityGenerationService.setStartTime(currentTime);
				return "Solar panels started!";
			}else{
				return "Solar panels have already been started!";
			}
	}
	@ResponseBody public String startParamGeneratedElectricity(
		@RequestParam(name = EConstants.REQUEST_PARAM_TIME) final long time) 
		throws IOException, URISyntaxException {
			long currentTime = EUtilities.nowSeconds();
			if (time < currentTime-EConstants.TIMEOUT && currentTime < time) {
				throw new BadPayloadException("Format of time need to be correct");
			}
			if(! electricityGenerationService.isStarted()){
				electricityGenerationService.setStartTime(time);
				return "Solar panels started!";
			}else{
				return "Solar panels have already been started!";
			}
	}

	//-------------------------------------------------------------------------------------------------	
	@PostMapping(path = EConstants.ELECTRICITY_GENERATION_SERVICE_STOP_URI)
	@ResponseBody public String stopGeneratedElectricity() 
		throws IOException, URISyntaxException {
			if(electricityGenerationService.isStarted()){
				electricityGenerationService.setStartTime(0);
				return "Solar panels stopped!";
			}else{
				return "Solar panels have already been stopped!";
			}
	}

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}
}
