package main;

public enum Responses {
    OK ("./ok"), FAIL ("./fail");

    private String responseStr;

    Responses(String responseStr) {
        this.responseStr = responseStr;
    }

    public String getString() {
        return responseStr + Cloud2ServerStarter.END_COMMAND_CHAR;
    }
}
