package com.ulysseprod.Email;

public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset_password");

    ;

    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}
