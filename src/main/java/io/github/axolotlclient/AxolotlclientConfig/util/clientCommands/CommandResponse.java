package io.github.axolotlclient.AxolotlclientConfig.util.clientCommands;

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
