package com.shailu.deposito_dental_pos.controller;

import org.springframework.stereotype.Component;

@Component
public class MainController {

    public void initialize() {
        //Crontolador conecta la UI con el negocio
        System.out.println("Pantalla principal cargada");
    }
}
