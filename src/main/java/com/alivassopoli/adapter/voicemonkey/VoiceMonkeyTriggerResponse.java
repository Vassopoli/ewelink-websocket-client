package com.alivassopoli.adapter.voicemonkey;

public class VoiceMonkeyTriggerResponse {

    public final String status;
    public final String message;

    public VoiceMonkeyTriggerResponse(final String status, final String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "VoiceMonkeyTriggerResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
