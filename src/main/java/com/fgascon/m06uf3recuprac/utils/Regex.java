package com.fgascon.m06uf3recuprac.utils;

public class Regex {
    /**
     * Genera un regex que comprueba si la expresión es un separador para todas las
     * plataformas
     * @return regex de un separador de rutas
     */
    public static String pathSeparator(){
        return "\\\\|/";
    }
}
