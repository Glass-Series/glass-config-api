package net.glasslauncher.mods.gcapi3test.impl.example;

public enum ExampleConfigEnum {
    YAY("Yay!"),
    NAY("Nay."),
    WHY("WHYY?!?!");

    final String stringValue;

    ExampleConfigEnum(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
