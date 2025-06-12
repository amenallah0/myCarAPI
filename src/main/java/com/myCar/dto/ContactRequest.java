package com.myCar.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ContactRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    private String phone;
    private String subject;
    
    @NotBlank(message = "Le message est obligatoire")
    private String message;
}
