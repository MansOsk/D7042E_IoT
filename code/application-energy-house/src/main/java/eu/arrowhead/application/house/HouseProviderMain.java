package eu.arrowhead.application.house;

import java.sql.Time;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.common.debug.Debug;
import eu.arrowhead.application.house.service.HouseService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EConstants.BASE_PACKAGE})
public class HouseProviderMain implements ApplicationRunner{
	//=================================================================================================
	// members
	@Autowired
	public ArrowheadService arrowheadService;
    
    @Autowired
	public SSLProperties sslProperties;

	@Autowired
	HouseService houseService;

	OrchestrationResultDTO gridOrchestration;
	OrchestrationResultDTO productionStartOrchestration;
	OrchestrationResultDTO productionStopOrchestration;

	private Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(HouseProviderMain.class, args);
	}
	
	@Override
	public void run(final ApplicationArguments args) throws Exception {
		setup();
    	System.out.println("\n" + "---HOUSE GUI---");    	
    	Scanner scaner = new Scanner(System.in);    	
    	gui(scaner);    	
    	scaner.close();
    	System.out.println("\n" + "HOUSE IS TERMINATED");
    }

	//-------------------------------------------------------------------------------------------------
	private void setup(){
		gridOrchestration = orchestrate(EConstants.POST_ELECTRICITY_GRID_SERVICE_DEFINTION);
		productionStartOrchestration = orchestrate(EConstants.ELECTRICITY_GENERATION_START);
		productionStopOrchestration = orchestrate(EConstants.ELECTRICITY_GENERATION_STOP);
	}

	//-------------------------------------------------------------------------------------------------
	private void gui(Scanner scaner) throws InterruptedException{
		while(true){
			logger.info("BATTERY= " + houseService.getBattery() + " kW/h");
			logger.info("BALANCE= " + houseService.getMoney() + " $");

			String command = scaner.nextLine();

			if(command.equals("SELL")){
				logger.info("NOW SELLING");
				float percent = scaner.nextFloat();
				if(percent < 0 || percent > 100){
					invalidCommand(String.valueOf(percent));
				}else{
					percent = percent / 100;

					float battery = houseService.getBattery();
					battery = battery * percent;
					
					houseService.setBattery(houseService.getBattery() - battery);
					postToGrid(battery);
				}
			}else if(command.equals("START")){
				startProduction();
			}else if(command.equals("STOP")){
				stopProduction();
			}
			else if(command.equals("WIPE")){
				houseService.setBattery(0);
				houseService.setMoney(0);
			}else if(command.equals("")){
				logger.info("");
			}else{
				invalidCommand(command);
			}
	
			update(); // Last action before next user input.
		}
	}

	private void invalidCommand(String cmd){
		logger.info("Invalid command " + cmd);
	}

	//-------------------------------------------------------------------------------------------------

	/**
	 * Updates all relevant parameters/variables after user action. 
	 */
	private void update(){
		if(houseService.getBattery() < 0){ // Pay for negative rest and reset battery to 0. 
			postToGrid(houseService.resetBattery());
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void postToGrid(float electricity){
		String[] queryParamElectricity = {gridOrchestration.getMetadata().get(EConstants.REQUEST_PARAM_KEY_ELECTRICITY), String.valueOf(electricity)};	
		final String token = gridOrchestration.getAuthorizationTokens() == null ? null : gridOrchestration.getAuthorizationTokens().get(getInterface());

		float response = arrowheadService.consumeServiceHTTP(float.class, 
			HttpMethod.valueOf(gridOrchestration.getMetadata().get(EConstants.HTTP_METHOD)), 
			gridOrchestration.getProvider().getAddress(), 
			gridOrchestration.getProvider().getPort(), 
			gridOrchestration.getServiceUri(),
			getInterface(),
			token,
			null,
			queryParamElectricity
		);

		float current = houseService.getMoney();
		houseService.setMoney(current + response);
	}

	//-------------------------------------------------------------------------------------------------
	private void stopProduction(){
		final String token = gridOrchestration.getAuthorizationTokens() == null ? null : productionStopOrchestration.getAuthorizationTokens().get(getInterface());

		String response = arrowheadService.consumeServiceHTTP(String.class, 
			HttpMethod.valueOf(productionStopOrchestration.getMetadata().get(EConstants.HTTP_METHOD)), 
			productionStopOrchestration.getProvider().getAddress(), 
			productionStopOrchestration.getProvider().getPort(), 
			productionStopOrchestration.getServiceUri(),
			getInterface(),
			token,
			null
		);
		Debug.debug(response);
	}

	//-------------------------------------------------------------------------------------------------
	private void startProduction(){
		final String token = gridOrchestration.getAuthorizationTokens() == null ? null : productionStartOrchestration.getAuthorizationTokens().get(getInterface());

		String response = arrowheadService.consumeServiceHTTP(String.class, 
			HttpMethod.valueOf(productionStartOrchestration.getMetadata().get(EConstants.HTTP_METHOD)), 
			productionStartOrchestration.getProvider().getAddress(), 
			productionStartOrchestration.getProvider().getPort(), 
			productionStartOrchestration.getServiceUri(),
			getInterface(),
			token,
			null
		);
		Debug.debug(response);
	}

	//-------------------------------------------------------------------------------------------------
	public OrchestrationResultDTO orchestrate(final String serviceDefinition) {
		logger.info("Orchestration request for " + serviceDefinition + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(serviceDefinition)
    			.interfaces(getInterface())
    			.build();
    	
    	final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
    	final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
    			.flag(Flag.MATCHMAKING, true)
    			.flag(Flag.OVERRIDE_STORE, true)
    			.build();

		Debug.printOut(orchestrationFormRequest);

    	final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);

		Debug.printOut(orchestrationResponse);

    	if (orchestrationResponse == null) {
    		logger.info("No orchestration response received");
    	} else if (orchestrationResponse.getResponse().isEmpty()) {
    		logger.info("No provider found during the orchestration");
    	} else {
    		final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
    		validateOrchestrationResult(orchestrationResult, serviceDefinition);
    		return orchestrationResult;
    	}
    	throw new ArrowheadException("Unsuccessful orchestration: " + serviceDefinition);
    }

	private String getInterface() {
    	return sslProperties.isSslEnabled() ? EConstants.INTERFACE_SECURE : EConstants.INTERFACE_INSECURE;
    }

	private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}
    	
    	boolean hasValidInterface = false;
    	for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
			if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
				hasValidInterface = true;
				break;
			}
		}
    	if (!hasValidInterface) {
    		throw new InvalidParameterException("Requested and orchestrated interface do not match");
		}
    }
}
