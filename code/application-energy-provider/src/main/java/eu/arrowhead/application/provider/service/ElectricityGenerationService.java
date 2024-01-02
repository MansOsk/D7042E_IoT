package eu.arrowhead.application.provider.service;

import java.util.Random;

import org.springframework.stereotype.Component;

import eu.arrowhead.application.common.EUtilities;
import eu.arrowhead.application.common.payload.EGenerationPayload;

@Component
public class ElectricityGenerationService{
    
    //=================================================================================================
	// members

    private long startTime = EUtilities.nowSeconds();

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
    public EGenerationPayload simulate(){
        long hours = EUtilities.nowSeconds() - startTime;

        float electricity = ElectricityGenerationConstants.ELECTRICITY;
        int n_solarpanels = ElectricityGenerationConstants.N_SOLARPANELS;
        float min_efficiency = ElectricityGenerationConstants.MIN_EFFICIENCY;
        float max_efficiency = ElectricityGenerationConstants.MAX_EFFICIENCY;
        String type = ElectricityGenerationConstants.TYPE;

        electricity = electricity * n_solarpanels * hours;
        electricity *= randomizeEfficiency(min_efficiency, max_efficiency);

        EGenerationPayload payload = new EGenerationPayload(electricity, type, hours);

        startTime = EUtilities.nowSeconds();

        return payload;
    }

    //-------------------------------------------------------------------------------------------------
    private float randomizeEfficiency(float min, float max){
        Random rand = new Random();
        return min + rand.nextFloat() * (max-min);
    }

    //--------------------------------------GET&SET----------------------------------------------------

    public long getStartTime(){
        return this.startTime;
    }

    public void setStartTime(long startTime){
        this.startTime = startTime;
    }

    public boolean isStarted(){
        if(startTime == 0){
            return false;
        }else{
            return true;
        }
    }

}
