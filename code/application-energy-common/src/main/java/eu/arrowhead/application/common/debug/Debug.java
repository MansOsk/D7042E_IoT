package eu.arrowhead.application.common.debug;

import eu.arrowhead.application.common.EConstants;

public class Debug {
    public static final void debug(){
        if(EConstants.DEBUG){
            System.out.println("");
        }
    }

    public static final void debug(Object object){
        if(EConstants.DEBUG){
            System.out.println(object.getClass().toString() + " " + object.toString());
            debug();
        }
    }
    public static final void debug(String tag,Object object){
        if(EConstants.DEBUG){
            System.out.println(tag + " " + object.toString());
            debug();
        }
    }
    
}
