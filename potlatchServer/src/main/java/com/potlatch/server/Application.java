package com.potlatch.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.MultipartConfigElement;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
//import org.magnum.mobilecloud.video.json.ResourcesMapper;
//import org.magnum.mobilecloud.video.auth.OAuth2SecurityConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;


import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import retrofit.mime.TypedFile;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.potlatch.server.auth.OAuth2SecurityConfiguration;
import com.potlatch.server.repository.GiftRepository;
import com.potlatch.server.repository.UserInfoRepository;
import com.potlatch.server.repository.UserEmotionRepository;



//Tell Spring to automatically inject any dependencies that are marked in
//our classes with @Autowired
@EnableAutoConfiguration

@EnableJpaRepositories(basePackageClasses={GiftRepository.class, UserInfoRepository.class, UserEmotionRepository.class})

// Tell Spring to turn on WebMVC (e.g., it should enable the DispatcherServlet
// so that requests can be routed to our Controllers)
@EnableWebMvc
// Tell Spring that this object represents a Configuration for the
// application
@Configuration
// Tell Spring to go and scan our controller package (and all sub packages) to
// find any Controllers or other components that are part of our applciation.
// Any class in this package that is annotated with @Controller is going to be
// automatically discovered and connected to the DispatcherServlet.
@ComponentScan

@Import(OAuth2SecurityConfiguration.class)


public class Application extends RepositoryRestMvcConfiguration {

	// The app now requires that you pass the location of the keystore and
	// the password for your private key that you would like to setup HTTPS
	// with. In Eclipse, you can set these options by going to:
	//    1. Run->Run Configurations
	//    2. Under Java Applications, select your run configuration for this app
	//    3. Open the Arguments tab
	//    4. In VM Arguments, provide the following information to use the
	//       default keystore provided with the sample code:
	//
	//       -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit
	//
	//    5. Note, this keystore is highly insecure! If you want more securtiy, you 
	//       should obtain a real SSL certificate:
	//
	//       http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
	//
	// Tell Spring to launch our app!
	

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	
    // This version uses the Tomcat web container and configures it to
	// support HTTPS. The code below performs the configuration of Tomcat
	// for HTTPS. Each web container has a different API for configuring
	// HTTPS. 
	//
	// The app now requires that you pass the location of the keystore and
	// the password for your private key that you would like to setup HTTPS
	// with. In Eclipse, you can set these options by going to:
	//    1. Run->Run Configurations
	//    2. Under Java Applications, select your run configuration for this app
	//    3. Open the Arguments tab
	//    4. In VM Arguments, provide the following information to use the
	//       default keystore provided with the sample code:
	//
	//       -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit
	//
	//    5. Note, this keystore is highly insecure! If you want more securtiy, you 
	//       should obtain a real SSL certificate:
	//
	//       http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
	//
    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer(
            @Value("${keystore.file:src/main/resources/private/keystore}") String keystoreFile,
            @Value("${keystore.pass:changeit}") final String keystorePass) throws Exception {

		// If you were going to reuse this class in another
		// application, this is one of the key sections that you
		// would want to change
    	
        final String absoluteKeystoreFile = new File(keystoreFile).getAbsolutePath();

        return new EmbeddedServletContainerCustomizer () {

			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
		            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
		            tomcat.addConnectorCustomizers(
		                    new TomcatConnectorCustomizer() {
								@Override
								public void customize(Connector connector) {
									connector.setPort(8443);
			                        connector.setSecure(true);
			                        connector.setScheme("https");

			                        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
			                        proto.setSSLEnabled(true);
			                        proto.setKeystoreFile(absoluteKeystoreFile);
			                        proto.setKeystorePass(keystorePass);
			                        proto.setKeystoreType("JKS");
			                        proto.setKeyAlias("tomcat"); 
			                        
								}
		                    });
		    
			}
        };
    }
	
	private static final String MAX_REQUEST_SIZE = "150MB";
	// This configuration element adds the ability to accept multipart
	// requests to the web container.
	@Bean
    public MultipartConfigElement multipartConfigElement() {
		// Setup the application container to be accept multipart requests
		final MultiPartConfigFactory factory = new MultiPartConfigFactory();
		// Place upper bounds on the size of the requests to ensure that
		// clients don't abuse the web container by sending huge requests
		factory.setMaxFileSize(MAX_REQUEST_SIZE);
		factory.setMaxRequestSize(MAX_REQUEST_SIZE);
		factory.setFileSizeThreshold(1048576);
		// Return the configuration to setup multipart in the container


		return factory.createMultipartConfig();
	}
	
	@Override
    protected void configureJacksonObjectMapper(ObjectMapper objectMapper) {
	    super.configureJacksonObjectMapper(objectMapper);
	   
	    @SuppressWarnings("rawtypes")
	    JsonSerializer<TypedFile> serializer = new JsonSerializer<TypedFile>() {
	
		    // We are going to register this class to handle all instances of type
		    // Resources
		    @Override
		    public Class<TypedFile> handledType() {
		    return TypedFile.class;
		    }
		
		    @Override
		    public void serialize(TypedFile value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
		    JsonProcessingException {
		    // Extracted the actual data inside of the Resources object
		    // that we care about (e.g., the list of Video objects)
		    	
		    Object content = value;
		    // Instead of all of the Resources member variables, etc.
		    // Just mashall the actual content (Videos) into the JSON
		    JsonSerializer<Object> s = provider.findValueSerializer(
		    content.getClass(), null);

		    s.serialize(content, jgen, provider);
		    }


	    };
	   
	   
		SimpleModule module = new SimpleModule();
		module.addSerializer(TypedFile.class, serializer);
	    objectMapper.registerModule(module);
   
    }


}
