package com.urbanreport.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
}