package eu.arrowhead.application.consumer.service;

import java.util.Random;

public class ElectricityConsumptionService {

    public float simulate(){
        return -1*randomizeEfficiency(ElectricityConsumptionConstants.MIN_CONSUMPTION, ElectricityConsumptionConstants.MAX_CONSUMPTION);
    }

    private float randomizeEfficiency(float min, float max){
        Random rand = new Random();
        return min + rand.nextFloat() * (max-min);
    }
    
}
