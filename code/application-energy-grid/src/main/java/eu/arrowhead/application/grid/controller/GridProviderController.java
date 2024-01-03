package eu.arrowhead.application.grid.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.common.EUtilities;
import eu.arrowhead.application.common.debug.Debug;
import eu.arrowhead.application.common.payload.EPayload;
import eu.arrowhead.application.grid.service.GridConstants;
import eu.arrowhead.application.grid.service.GridService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
public class GridProviderController {
	
	//=================================================================================================
	// members

	@Autowired
	GridService gridService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	@PostMapping(path = EConstants.ELECTRICITY_GRID_URI)	
	@ResponseBody public float money(
		@RequestParam(name = EConstants.REQUEST_PARAM_ELECTRICITY) final String electricity) 
		throws IOException, URISyntaxException {
			float total = Float.parseFloat(electricity);
			total = gridService.simulate(total);

			Debug.debug("TOTAL= ", total + " " + GridConstants.TYPE);
			Debug.debug("#####","######");

			return total;
	}
}
