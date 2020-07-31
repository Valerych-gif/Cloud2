package main;

public enum Commands {
    UPLOAD ("./upload"), DOWNLOAD ("./download");

    String commandStr;

    Commands(String commandStr) {
        this.commandStr = commandStr;
    }

    public String getCommandStr() {
        return commandStr;
    }
}
