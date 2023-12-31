package eu.arrowhead.application.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.common.EUtilities;
import eu.arrowhead.application.common.debug.Debug;
import eu.arrowhead.application.common.payload.EPayload;
import eu.arrowhead.application.provider.service.ElectricityGenerationService;
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
	@GetMapping(path = EConstants.ELECTRICITY_GENERATION_URI)
	@ResponseBody public EPayload getGeneratedElectricity() 
		throws IOException, URISyntaxException {
			if (! electricityGenerationService.isStarted()) {
				throw new BadPayloadException("Solar panels are stopped!");
			}
			EPayload generation = electricityGenerationService.simulate();

			Debug.debug("PRODUCTION= ", generation.getElectricity() + " " + generation.getType());
			Debug.debug("#####","######");

			return generation;
	}

	//-------------------------------------------------------------------------------------------------	
	@PostMapping(path = EConstants.ELECTRICITY_GENERATION_START_URI)		
	@ResponseBody public String startGeneratedElectricity() 
		throws IOException, URISyntaxException {
			long currentTime = EUtilities.nowSeconds();
			if(! electricityGenerationService.isStarted()){
				electricityGenerationService.setStartTime(currentTime);
				return "Solar panels started!";
			}else{
				throw new BadPayloadException("Solar panels have already been started!");
			}
	}
	@PostMapping(path = EConstants.ELECTRICITY_GENERATION_START_TIME_URI)	// From time.
	@ResponseBody public String startParamGeneratedElectricity(
		@PathVariable long time) 
		throws IOException, URISyntaxException {
			long currentTime = EUtilities.nowSeconds();
			if (time < currentTime-EConstants.DELAY_TIME_SIM || currentTime < time) {  // If format is bad.
				Debug.debug("Formatting of time is incorrect, USE:");
				Debug.debug("Current time in millis (in seconds): ", EUtilities.nowSeconds());
				throw new BadPayloadException("Format of time need to be correct");
			}
			if(! electricityGenerationService.isStarted()){
				electricityGenerationService.setStartTime(time);
				return "Solar panels started!";
			}else{
				throw new BadPayloadException("Solar panels have already been started!");
			}
	}

	//-------------------------------------------------------------------------------------------------	
	@PostMapping(path = EConstants.ELECTRICITY_GENERATION_STOP_URI)
	@ResponseBody public String stopGeneratedElectricity() 
		throws IOException, URISyntaxException {
			if(electricityGenerationService.isStarted()){
				electricityGenerationService.setStartTime(0);
				return "Solar panels stopped!";
			}else{
				throw new BadPayloadException("Solar panels have already been stopped!");
			}
	}
}
