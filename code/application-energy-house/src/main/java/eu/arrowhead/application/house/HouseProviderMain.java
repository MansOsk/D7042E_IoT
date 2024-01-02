package eu.arrowhead.application.house;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EConstants.BASE_PACKAGE})
public class HouseProviderMain implements ApplicationRunner{
	//=================================================================================================
	// members
	@Autowired
	public ArrowheadService arrowheadService;
    
    @Autowired
	public SSLProperties sslProperties;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(HouseProviderMain.class, args);
	}
	
	@Override
	public void run(final ApplicationArguments args) throws Exception {
    	System.out.println("\n" + "HOUSE GUI");    	
    	final Scanner scaner = new Scanner(System.in);    	
    	gui(scaner);    	
    	scaner.close();
    	System.out.println("\n" + "Energy Forecast Consumer has been terminated.");
    }

	private void gui(Scanner scaner){
		while(true){
			
		}
	}
}
