package main;

public enum Commands {
    UPLOAD ("./upload"),
    DOWNLOAD ("./download"),
    CLOSE_CONNECTION("./closeconnection"),
    GET_DIR_CONTENT("./getdircontent"),
    GET_SHARED_DIR_CONTENT("./getshareddircontent"),
    AUTHORIZATION ("./authorization"),
    REGISTRATION ("./registration"),
    SHARE ("./share");

    private String commandStr;

    Commands(String commandStr) {
        this.commandStr = commandStr;
    }

    public String getString() {
        return commandStr;
    }
}
