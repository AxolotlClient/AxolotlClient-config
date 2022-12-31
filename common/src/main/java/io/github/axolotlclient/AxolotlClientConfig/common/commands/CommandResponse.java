package io.github.axolotlclient.AxolotlClientConfig.common.commands;

/**
 * A class storing a basic command response and its states.
 */

public class CommandResponse {

    public boolean success;
    public String response;

    public CommandResponse(boolean success, String response){
        this.success = success;
        this.response = response;
    }

}
