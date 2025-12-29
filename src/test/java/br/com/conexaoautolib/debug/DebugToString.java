package br.com.conexaoautolib.debug;

import br.com.conexaoautolib.model.request.TokenRequest;
import br.com.conexaoautolib.model.request.factories.TokenRequestFactory;

public class DebugToString {
    public static void main(String[] args) {
        TokenRequest request = TokenRequestFactory.createFullTokenRequest();
        System.out.println("DEBUG: toString output:");
        System.out.println(request.toString());
        
        System.out.println("\nDEBUG: Contains checks:");
        System.out.println("Contains 'test-client-secret': " + request.toString().contains("test-client-secret"));
        System.out.println("Contains '[REDACTED]': " + request.toString().contains("[REDACTED]"));
        
        // Check actual field values
        System.out.println("\nDEBUG: Field values:");
        System.out.println("grantType: " + request.getGrantType());
        System.out.println("clientId: " + request.getClientId());
        System.out.println("clientSecret: " + request.getClientSecret());
        System.out.println("username: " + request.getUsername());
        System.out.println("password: " + request.getPassword());
        System.out.println("refreshToken: " + request.getRefreshToken());
    }
}