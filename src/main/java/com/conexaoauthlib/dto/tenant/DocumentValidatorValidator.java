package com.conexaoauthlib.dto.tenant;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementação do validador de CPF/CNPJ.
 *
 * <p>Valida documentos brasileiros verificando os dígitos verificadores.
 * Remove automaticamente caracteres não numéricos antes da validação.</p>
 *
 * @author ConexãoAuthLib Team
 * @version 1.0.0
 */
public class DocumentValidatorValidator implements ConstraintValidator<DocumentValidator, String> {

    @Override
    public boolean isValid(String document, ConstraintValidatorContext context) {
        if (document == null || document.isBlank()) {
            return true; // @NotNull/@NotBlank deve ser usado para campos obrigatórios
        }

        String cleanDocument = document.replaceAll("[^0-9]", "");

        if (cleanDocument.length() == 11) {
            return validateCPF(cleanDocument);
        } else if (cleanDocument.length() == 14) {
            return validateCNPJ(cleanDocument);
        }

        return false;
    }

    /**
     * Valida um CPF verificando os dígitos verificadores.
     *
     * @param cpf CPF com 11 dígitos numéricos
     * @return true se válido, false caso contrário
     */
    private boolean validateCPF(String cpf) {
        // CPF com todos dígitos iguais é inválido
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validação do primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int digit1 = (sum * 10) % 11;
        if (digit1 == 10) {
            digit1 = 0;
        }

        // Validação do segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int digit2 = (sum * 10) % 11;
        if (digit2 == 10) {
            digit2 = 0;
        }

        return digit1 == Character.getNumericValue(cpf.charAt(9)) &&
               digit2 == Character.getNumericValue(cpf.charAt(10));
    }

    /**
     * Valida um CNPJ verificando os dígitos verificadores.
     *
     * @param cnpj CNPJ com 14 dígitos numéricos
     * @return true se válido, false caso contrário
     */
    private boolean validateCNPJ(String cnpj) {
        // CNPJ com todos dígitos iguais é inválido
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        // Validação do primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weights1[i];
        }
        int digit1 = sum % 11;
        digit1 = digit1 < 2 ? 0 : 11 - digit1;

        // Validação do segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * weights2[i];
        }
        int digit2 = sum % 11;
        digit2 = digit2 < 2 ? 0 : 11 - digit2;

        return digit1 == Character.getNumericValue(cnpj.charAt(12)) &&
               digit2 == Character.getNumericValue(cnpj.charAt(13));
    }
}
