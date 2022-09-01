package com.alivassopoli.adapter.ewelink;

import com.github.realzimboguy.ewelink.api.EweLink;
import com.github.realzimboguy.ewelink.api.wss.WssResponse;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;

//DOC -> http://ewelink-api-java.co.zw
public class EwelinkWebSocketClientApplication implements QuarkusApplication {
    private static final Logger LOG = Logger.getLogger(EwelinkWebSocketClientApplication.class);

    private final String ewelinkEmail;
    private final String ewelinkPassword;
    private final WssResponse wssResponse;

    public EwelinkWebSocketClientApplication(@ConfigProperty(name = "ewelink-websocket-client.ewelink.email") final String ewelinkEmail,
                                             @ConfigProperty(name = "ewelink-websocket-client.ewelink.password") final String ewelinkPassword,
                                             final WssResponse wssResponse) {
        this.ewelinkEmail = ewelinkEmail;
        this.ewelinkPassword = ewelinkPassword;
        this.wssResponse = wssResponse;
    }

    void onStart(@Observes final StartupEvent ev) {
        LOG.info("The application is starting...");
    }

    void onStop(@Observes final ShutdownEvent ev) {
        LOG.info("The application is stopping...");
    }

    @Override
    public int run(String... args) {
        final EweLink eweLink = new EweLink("us", ewelinkEmail, ewelinkPassword, 60);

        try {
            eweLink.login();
            eweLink.getWebSocket(wssResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Quarkus.waitForExit();
        return 0;
    }
}
