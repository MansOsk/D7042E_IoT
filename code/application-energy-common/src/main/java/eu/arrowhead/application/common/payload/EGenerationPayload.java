package eu.arrowhead.application.common.payload;

import java.io.Serializable;

public class EGenerationPayload implements Serializable{

    //=================================================================================================
	// members
	
    private float electricity;

    //-------------------------------------------------------------------------------------------------	
     private String type;

    //-------------------------------------------------------------------------------------------------	
    private long hours;

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------	
    public EGenerationPayload(){}

    //-------------------------------------------------------------------------------------------------	
    public EGenerationPayload(final float electricity, final String type, final long hours){
        this.electricity = electricity;
        this.type = type;
            this.hours = hours;
    }
    //--------------------------------------------GET--------------------------------------------------
    public float getElectricity(){
        return this.electricity;
    }
    public String getType(){
        return this.type;
    }
    public long getHours(){
        return this.hours;
    }

    //--------------------------------------------SET--------------------------------------------------
    public void setElectricity(final float electricity){
        this.electricity = electricity;
    }
    public void setType(String type){
        this.type = type;
    }
     public void setHours(final int hours){
        this.hours = hours;
    }  
}
