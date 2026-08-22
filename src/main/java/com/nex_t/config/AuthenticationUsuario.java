package com.nex_t.config;

import java.lang.annotation.*;

/**
 * Injeta o Usuario autenticado (extraído do JWT) diretamente no parâmetro
 * do controller. Ex: void metodo(@AuthenticationUsuario Usuario usuario)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationUsuario {
}
