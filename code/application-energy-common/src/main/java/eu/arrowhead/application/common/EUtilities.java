package eu.arrowhead.application.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EUtilities {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static long nowUTCSeconds() {
		return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public EUtilities() {		
		throw new UnsupportedOperationException();
	}
}
