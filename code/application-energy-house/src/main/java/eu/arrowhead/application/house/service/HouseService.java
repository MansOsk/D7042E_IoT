package eu.arrowhead.application.house.service;

import org.springframework.stereotype.Component;

import eu.arrowhead.application.common.payload.EGenerationPayload;

@Component
public class HouseService{
    
    //=================================================================================================
	// members
    private float battery;

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
    public EGenerationPayload simulate(){
        return null;
    }

    public void setBattery(float electricity){
        this.battery = electricity;
    }

    public float getBattery(){
        return this.battery;
    }

}
