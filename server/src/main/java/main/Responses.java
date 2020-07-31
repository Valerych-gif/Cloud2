package main;

public enum Responses {
    OK ("./ok"), FAIL ("./fail");

    String responseStr;

    Responses(String responseStr) {
        this.responseStr = responseStr;
    }

    @Override
    public String toString() {
        return responseStr;
    }
}
