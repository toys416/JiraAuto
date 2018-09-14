package com.oracle.bugjirabridge.jira.helper;

public enum AutomationStatusEnum {
    Automated(10700);
    private final int value;

    AutomationStatusEnum(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}