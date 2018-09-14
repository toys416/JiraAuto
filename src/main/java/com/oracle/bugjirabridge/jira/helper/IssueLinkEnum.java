package com.oracle.bugjirabridge.jira.helper;

public enum IssueLinkEnum {
    Test(10700);
    private final int value;

    IssueLinkEnum(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}