package eu.arrowhead.application.house.service;

import org.springframework.stereotype.Component;

import eu.arrowhead.application.common.payload.EPayload;

@Component
public class HouseService{
    
    //=================================================================================================
	// members
    private float battery;

    private float money;

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
    public EPayload simulate(){
        return null;
    }

    //-------------------------------------------------------------------------------------------------
    public float resetBattery(){
        float current = battery;
        this.battery = 0;
        return current;
    }

    //-------------------------------------------------------------------------------------------------

    public void setBattery(float electricity){
        this.battery = electricity;
    }

    public void setMoney(float money){
        this.money = money;
    }

    public float getBattery(){
        return this.battery;
    }

    public float getMoney(){
        return this.money;
    }
}
