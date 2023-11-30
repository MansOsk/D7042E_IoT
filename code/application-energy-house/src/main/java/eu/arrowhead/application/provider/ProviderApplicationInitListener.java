package eu.arrowhead.application.provider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.config.ApplicationInitListener;
import ai.aitia.arrowhead.application.library.util.ApplicationCommonConstants;
import eu.arrowhead.application.common.EConstants;
import eu.arrowhead.application.provider.security.ProviderSecurityConfig;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class ProviderApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;
	
	@Value(ApplicationCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;

	@Value(ApplicationCommonConstants.$APPLICATION_SYSTEM_NAME)
	private String mySystemName;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_ADDRESS_WD)
	private String mySystemAddress;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_PORT_WD)
	private int mySystemPort;
	
	private final Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		checkConfiguration();
		
		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICEREGISTRY);
		checkCoreSystemReachability(CoreSystem.ORCHESTRATOR);
		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		
			setTokenSecurityFilter();
		
		}else {
			logger.info("TokenSecurityFilter in not active");
		}		

		arrowheadService.updateCoreServiceURIs(CoreSystem.ORCHESTRATOR);
		
		// Only register if security enabled. 
		if (sslEnabled && tokenSecurityFilterEnabled) {
			//Register services into ServiceRegistry
			final ServiceRegistryRequestDTO energyService1 = createServiceRegistryRequest(
				EConstants.ELECTRICITY_GENERATION_SERVICE, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_URI, 
				HttpMethod.GET
			);
			arrowheadService.forceRegisterServiceToServiceRegistry(energyService1);
			logger.info("Service registered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE);

			final ServiceRegistryRequestDTO energyService2 = createServiceRegistryRequest(
				EConstants.ELECTRICITY_GENERATION_SERVICE_START, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_START_URI, 
				HttpMethod.POST
			);
			arrowheadService.forceRegisterServiceToServiceRegistry(energyService2);
			logger.info("Service registered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE_START);

			final ServiceRegistryRequestDTO energyService3 = createServiceRegistryRequest(
				EConstants.ELECTRICITY_GENERATION_SERVICE_START_TIME, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_START_TIME_URI, 
				HttpMethod.POST
			);
			arrowheadService.forceRegisterServiceToServiceRegistry(energyService3);
			logger.info("Service registered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE_START);

			final ServiceRegistryRequestDTO energyService4 = createServiceRegistryRequest(
				EConstants.ELECTRICITY_GENERATION_SERVICE_STOP, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_STOP_URI, 
				HttpMethod.POST
			);
			arrowheadService.forceRegisterServiceToServiceRegistry(energyService4);
			logger.info("Service registered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE_STOP);
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		if (sslEnabled && tokenSecurityFilterEnabled) {
			arrowheadService.unregisterServiceFromServiceRegistry(
				EConstants.ELECTRICITY_GENERATION_SERVICE, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_URI
			);
			logger.info("Service unregistered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE);

			arrowheadService.unregisterServiceFromServiceRegistry(
				EConstants.ELECTRICITY_GENERATION_SERVICE_START, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_START_URI
			);
			logger.info("Service unregistered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE_START);

			arrowheadService.unregisterServiceFromServiceRegistry(
				EConstants.ELECTRICITY_GENERATION_SERVICE_START_TIME, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_START_TIME_URI
			);
			logger.info("Service unregistered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE_START);

			arrowheadService.unregisterServiceFromServiceRegistry(
				EConstants.ELECTRICITY_GENERATION_SERVICE_STOP, 
				EConstants.ELECTRICITY_GENERATION_SERVICE_STOP_URI
			);
			logger.info("Service unregistered: {}", EConstants.ELECTRICITY_GENERATION_SERVICE_STOP);
		}
	}
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private void checkConfiguration() {
		if (!sslEnabled && tokenSecurityFilterEnabled) {			 
			logger.warn("Contradictory configuration:");
			logger.warn("token.security.filter.enabled=true while server.ssl.enabled=false");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void setTokenSecurityFilter() {
		final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();
		if (authorizationPublicKey == null) {
			throw new ArrowheadException("Authorization public key is null");
		}
		
		KeyStore keystore;
		try {
			keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
			keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
			throw new ArrowheadException(ex.getMessage());
		}			
		final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());
		
		providerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
		providerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);

	}

	private ServiceRegistryRequestDTO createServiceRegistryRequest(final String serviceDefinition, final String serviceUri, final HttpMethod httpMethod) {
		final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
		serviceRegistryRequest.setServiceDefinition(serviceDefinition);
		final SystemRequestDTO systemRequest = new SystemRequestDTO();
		systemRequest.setSystemName(mySystemName);
		systemRequest.setAddress(mySystemAddress);
		systemRequest.setPort(mySystemPort);		

		if (tokenSecurityFilterEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.TOKEN.name());

			List<String> myList = new ArrayList<String>();
			myList.add(EConstants.INTERFACE_SECURE);
			serviceRegistryRequest.setInterfaces(myList);
		} else if (sslEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.CERTIFICATE.name());

			List<String> myList = new ArrayList<String>();
			myList.add(EConstants.INTERFACE_SECURE);
			serviceRegistryRequest.setInterfaces(myList);
		} else {
			serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE.name());
			
			List<String> myList = new ArrayList<String>();
			myList.add(EConstants.INTERFACE_INSECURE);
			serviceRegistryRequest.setInterfaces(myList);
		}
		serviceRegistryRequest.setProviderSystem(systemRequest);
		serviceRegistryRequest.setServiceUri(serviceUri);
		serviceRegistryRequest.setMetadata(new HashMap<>());
		serviceRegistryRequest.getMetadata().put(EConstants.HTTP_METHOD, httpMethod.name());
		
		return serviceRegistryRequest;
	}
}
