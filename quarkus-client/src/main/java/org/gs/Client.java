package org.gs;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RegisterRestClient
@Path("/")
public interface Client {

    // Access the server when all conditions required are met
    @GET
    @Path("/server")
    @Produces(MediaType.TEXT_PLAIN)
    String call();

}
