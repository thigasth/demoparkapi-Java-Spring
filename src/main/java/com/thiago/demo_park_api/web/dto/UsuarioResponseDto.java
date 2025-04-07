package com.thiago.demo_park_api.web.dto;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UsuarioResponseDto {

    private long id;

    private String Username;

    private String role;

}
