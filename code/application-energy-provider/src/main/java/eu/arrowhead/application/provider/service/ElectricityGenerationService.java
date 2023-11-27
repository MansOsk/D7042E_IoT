package eu.arrowhead.application.provider.service;

import java.util.Random;

import org.springframework.stereotype.Component;

import eu.arrowhead.application.common.payload.ElectricityGenerationPayload;

@Component
public class ElectricityGenerationService {
    
    //=================================================================================================
	// members

    //-------------------------------------------------------------------------------------------------
    private ElectricityGenerationConstants constants;

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
    public ElectricityGenerationPayload simulate(final int hoursElapsed){
        float totalElectricity = constants.electricity * constants.nSolarPanels * hoursElapsed;
        totalElectricity *= randomizeEfficiency(hoursElapsed, totalElectricity);

        return new ElectricityGenerationPayload(hoursElapsed, totalElectricity);
    }

    //-------------------------------------------------------------------------------------------------
    private float randomizeEfficiency(float min, float max){
        Random rand = new Random();
        return min + (max - min) * rand.nextFloat();
    }


}
