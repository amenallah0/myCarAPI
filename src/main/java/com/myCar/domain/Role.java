package com.myCar.domain;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_EXPERT;
    
    // Cette méthode aide à convertir les rôles sans le préfixe "ROLE_"
    public static Role fromString(String role) {
        if (role != null) {
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            return Role.valueOf(role.toUpperCase());
        }
        return null;
    }
    
    // Cette méthode retourne le rôle sans le préfixe "ROLE_"
    public String withoutPrefix() {
        return this.name().substring(5);
    }
}