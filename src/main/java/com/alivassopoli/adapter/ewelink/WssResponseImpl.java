package com.alivassopoli.adapter.ewelink;

import com.alivassopoli.adapter.voicemonkey.VoiceMonkeyService;
import com.alivassopoli.adapter.voicemonkey.VoiceMonkeyTriggerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.realzimboguy.ewelink.api.wss.WssResponse;
import com.github.realzimboguy.ewelink.api.wss.wssrsp.WssRspMsg;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class WssResponseImpl implements WssResponse  {
    private static final Logger LOG = Logger.getLogger(WssResponseImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final int ONE_CLICK_PRESSED = 0;
    private static final int DOUBLE_CLICK_PRESSED = 1;
    private static final int LONG_CLICK_PRESSED = 2;

    private final String switchLivingRoom;
    private final String voiceMonkeyAccessToken;
    private final String voiceMonkeySecretToken;
    private final VoiceMonkeyService voiceMonkeyService;

    public WssResponseImpl(@ConfigProperty(name = "ewelink-websocket-client.ewelink.switch-living-room") final String switchLivingRoom,
                           @ConfigProperty(name = "ewelink-websocket-client.voice-monkey.access-token") final String voiceMonkeyAccessToken,
                           @ConfigProperty(name = "ewelink-websocket-client.voice-monkey.secret-token") final String voiceMonkeySecretToken,
                           @RestClient final VoiceMonkeyService voiceMonkeyService) {
        this.switchLivingRoom = switchLivingRoom;
        this.voiceMonkeyAccessToken = voiceMonkeyAccessToken;
        this.voiceMonkeySecretToken = voiceMonkeySecretToken;
        this.voiceMonkeyService = voiceMonkeyService;
    }

    @Override
    public void onMessage(final String s) {

        try {
            final Map<String, Object> map = mapper.readValue(s, Map.class);
            LOG.info(map);

            if (switchLivingRoom.equals(map.get("deviceid"))) {

                final Map<String, Object> params = (Map) map.get("params");
                final Integer keyPressed = (Integer) params.get("key");

                final Optional<String> monkeyNameOptional = getMonkeyName(keyPressed);

                monkeyNameOptional.ifPresent(monkeyName -> {
                    final VoiceMonkeyTriggerResponse response = voiceMonkeyService.getById(voiceMonkeyAccessToken, voiceMonkeySecretToken, monkeyName);
                    LOG.info(response);
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageParsed(final WssRspMsg rsp) {

        if (rsp.getError() == null) {
            //normal scenario
            LOG.info(rsp);
        } else if (rsp.getError() == 0) {
            LOG.info("login success");
        } else if (rsp.getError() > 0) {
            LOG.errorf("login error: %s", rsp);
        }
    }

    @Override
    public void onError(final String error) {
        LOG.errorf("onError in test, this should never be called: %s", error);
    }

    private Optional<String> getMonkeyName(final Integer keyPressed) {
        if (ONE_CLICK_PRESSED == keyPressed) {
            return Optional.of("turn-on-the-living-room-lights");
        } else if (DOUBLE_CLICK_PRESSED == keyPressed) {
            return Optional.of("turn-off-the-living-room-lights");
        } else if (LONG_CLICK_PRESSED == keyPressed) {
            return Optional.of("exiting-home");
        }
        return Optional.empty();
    }
}
