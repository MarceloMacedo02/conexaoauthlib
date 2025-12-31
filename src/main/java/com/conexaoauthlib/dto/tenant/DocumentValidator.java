package com.conexaoauthlib.dto.tenant;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validação customizada para CPF e CNPJ.
 *
 * <p>Valida documentos brasileiros verificando os dígitos verificadores
 * para CPF (11 dígitos) e CNPJ (14 dígitos).</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
@Documented
@Constraint(validatedBy = DocumentValidatorValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentValidator {

    String message() default "Documento deve ser um CPF ou CNPJ válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
