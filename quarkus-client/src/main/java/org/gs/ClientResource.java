package org.gs;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

// Access the resources via "client/client" address
@Path("/client")
public class ClientResource {

    /*
        Inject Configuration properties to the app; they are stored in the application.properties file. These shall be used during the connection between the server and client
        These include: - URL to be used to contact the server
                       - The keystore and keystore password
                       - The truststore and truststore password
    */
    @ConfigProperty(name = "url")
    URL serverURL;

    @ConfigProperty(name = "keyStore")
    String keyStoreFile;

    @ConfigProperty(name = "keyStorePassword")
    String keyStoreFilePassword;

    @ConfigProperty(name = "trustStore")
    String trustStoreFile;

    @ConfigProperty(name = "trustStorePassword")
    String trustStoreFilePassword;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Client\n";
    }

    /*
     * Annotate that the application is a REST_CLIENT
     * Inject the client interface into the application. This will be used as an interface between the server and client
     */
    @RestClient
    @Inject
    Client client;

    /*
     * Create a simple GET request that will use the configurations entered to contact the server
     */
    @GET
    @Path("client")
    @Produces(MediaType.TEXT_PLAIN)
    public String callWithClient() {
        return client.call();
    }

    /*
     * Create a GET request using a builder that will use the configurations entered to contact the server
     */
    @GET
    @Path("clientBuilder")
    @Produces(MediaType.TEXT_PLAIN)
    /*
    * Create a method to call the server using a Client_Builder
    * Some errors it may give include:      - KeyStoreException error
                                            - CertificateException error
                                            - NoSuchAlgorithmException error
                                            - IOException error
    */
    public String callWithClientBuilder() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        /*
        * Get the KeyStore file as a stream and store it in a keystore object for use during the server call
        * On load, use the Keystore and Keystore file password
        */
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream inputStreamKeyStore = this.getClass()
                .getClassLoader()
                .getResourceAsStream(keyStoreFile);
        keyStore.load(inputStreamKeyStore, keyStoreFilePassword.toCharArray());

        /*
         * Get the TrustStore file as a stream and store it in a keystore object for use during the server call
         * On load, use the TrustStore and TrustStore file password
         * The TrustStore is of a KeyStore type
         */
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream inputStreamTrustStore = this.getClass()
                .getClassLoader()
                .getResourceAsStream(trustStoreFile);
        trustStore.load(inputStreamTrustStore, trustStoreFilePassword.toCharArray());

        /*
         * Set the url, keyStore, and trustStore during the build
         * Make the server call at the end of the build function
         */
        Client clientBuild = RestClientBuilder.newBuilder()
                .baseUrl(serverURL)
                .keyStore(keyStore, keyStoreFilePassword)
                .trustStore(trustStore)
                .build(Client.class);
        return clientBuild.call();

    }
}
