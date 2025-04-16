package org.esprit.gestion_user.Template;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset_password"); // Added string value here



    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}
