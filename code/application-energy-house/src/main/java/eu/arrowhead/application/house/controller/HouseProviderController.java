package eu.arrowhead.application.house.controller;

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
import eu.arrowhead.application.common.payload.EGenerationPayload;
import eu.arrowhead.application.house.service.HouseService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
public class HouseProviderController {
	
	//=================================================================================================
	// members

	@Autowired
	HouseService houseService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	/* @PostMapping(path = EConstants.ELECTRICITY_GENERATION_SERVICE_START_URI)		
	@ResponseBody public String startGeneratedElectricity() 
		throws IOException, URISyntaxException {
			long currentTime = EUtilities.nowSeconds();
			if(! electricityGenerationService.isStarted()){
				electricityGenerationService.setStartTime(currentTime);
				return "Solar panels started!";
			}else{
				throw new BadPayloadException("Solar panels have already been started!");
			}
	} */
}
