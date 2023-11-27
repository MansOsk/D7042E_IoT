package eu.arrowhead.application.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.bouncycastle.jcajce.provider.asymmetric.EC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.common.payload.ElectricityGenerationPayload;
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
	@ResponseBody public ElectricityGenerationPayload getGeneratedElectricity(@RequestParam(name = EConstants.REQUEST_PARAM_HOURS) final int hoursElapsed) 
		throws IOException, URISyntaxException {
			if (hoursElapsed <= 0) {
				throw new BadPayloadException("Hours are negative or equal to 0");
			}
			return electricityGenerationService.simulate(hoursElapsed);
	}

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}
}
