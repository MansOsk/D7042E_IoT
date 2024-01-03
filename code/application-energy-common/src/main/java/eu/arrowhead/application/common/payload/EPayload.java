package eu.arrowhead.application.common.payload;

import java.io.Serializable;

public class EPayload implements Serializable{

    //=================================================================================================
	// members
	
    private float electricity;

    //-------------------------------------------------------------------------------------------------	
     private String type;

    //-------------------------------------------------------------------------------------------------	
    private long days;

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------	
    public EPayload(){}

    //-------------------------------------------------------------------------------------------------	
    public EPayload(final float electricity, final String type, final long days){
        this.electricity = electricity;
        this.type = type;
            this.days = days;
    }
    //--------------------------------------------GET--------------------------------------------------
    public float getElectricity(){
        return this.electricity;
    }
    public String getType(){
        return this.type;
    }
    public long getDays(){
        return this.days;
    }

    //--------------------------------------------SET--------------------------------------------------
    public void setElectricity(final float electricity){
        this.electricity = electricity;
    }
    public void setType(String type){
        this.type = type;
    }
     public void setDays(final int hours){
        this.days = hours;
    }  
}
