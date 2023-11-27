package eu.arrowhead.application.common.payload;

import java.io.Serializable;

public class ElectricityGenerationPayload implements Serializable{

    //=================================================================================================
	// members
	
     //-------------------------------------------------------------------------------------------------	
    private int hoursElapsed;

     //-------------------------------------------------------------------------------------------------	
    private float generatedElectricity;

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------	
    public ElectricityGenerationPayload(){}

    //-------------------------------------------------------------------------------------------------	
    public ElectricityGenerationPayload(final int hoursElapsed, final float generatedElectricity){
        this.hoursElapsed = hoursElapsed;
        this.generatedElectricity = generatedElectricity;
    }
    ///--------------------------------------------GET--------------------------------------------------
    public int getHoursElapsed(){
        return this.hoursElapsed;
    }
    public float getGeneratedElectricity(){
        return this.generatedElectricity;
    }

    ///--------------------------------------------SET--------------------------------------------------
    public void setHoursElapsed(final int hours){
        this.hoursElapsed = hours;
    }

    public void setGeneratedElectricity(final float generatedElectricity){
        this.generatedElectricity = generatedElectricity;
    }
    
}
