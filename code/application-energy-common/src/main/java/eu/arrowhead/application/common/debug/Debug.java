package eu.arrowhead.application.common.debug;

import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.common.Utilities;

public class Debug {
    public static final void debug(){
        if(EConstants.DEBUG){
            System.out.print("##### [DEBUG] #####   ");
        }
    }

    public static final void debug(Object object){
        if(EConstants.DEBUG){
            debug();
            System.out.println(object.getClass().toString() + " " + object.toString());
            endFormat();
        }
    }
    public static final void debug(String tag,Object object){
        if(EConstants.DEBUG){
            debug();
            System.out.println(tag + " " + object.toString());
            endFormat();
        }
    }

    public static void printOut(final Object object) {
        if(EConstants.DEBUG){
            debug();
            System.out.println("{ARROWHEAD QUERY TO JSON}");
            System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
        }
    }

    private static final void endFormat(){
        /*
         * IF ANY END FORMATTING OF DEBUG PROMPT IS NEEDED
         */
    }
    
}
