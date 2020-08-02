public enum Responses {
    OK ("./ok"),
    FAIL ("./fail"),
    END_OF_DIR_CONTENT("./endofdircontent");

    private String responseStr;

    Responses(String responseStr) {
        this.responseStr = responseStr;
    }

    public String getString() {
        return responseStr;
    }
}