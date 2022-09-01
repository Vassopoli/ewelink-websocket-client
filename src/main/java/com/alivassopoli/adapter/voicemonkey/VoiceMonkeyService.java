package com.alivassopoli.adapter.voicemonkey;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/trigger")
@RegisterRestClient
public interface VoiceMonkeyService {

    @GET
    VoiceMonkeyTriggerResponse getById(@QueryParam("access_token") final String accessToken,
                                       @QueryParam("secret_token") final String secretToken,
                                       @QueryParam("monkey") final String monkey);
}
