public enum Commands {
    UPLOAD ("./upload"),
    DOWNLOAD ("./download"),
    CLOSE_CONNECTION("./closeconnection"),
    GET_DIR_CONTENT("./getdircontent");

    private String commandStr;

    Commands(String commandStr) {
        this.commandStr = commandStr;
    }

    public String getString() {
        return commandStr;
    }
}