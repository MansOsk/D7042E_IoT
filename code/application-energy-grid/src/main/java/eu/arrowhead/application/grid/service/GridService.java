package eu.arrowhead.application.grid.service;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class GridService{
    
    //=================================================================================================
	// members

    //=================================================================================================
	// methods

    //-------------------------------------------------------------------------------------------------
    public float simulate(float electricity){
        float price = randomizeEfficiency(GridConstants.MIN_PRICE, GridConstants.MAX_PRICE);
        return price * electricity;
    }

    private float randomizeEfficiency(float min, float max){
        Random rand = new Random();
        return min + rand.nextFloat() * (max-min);
    }
}
