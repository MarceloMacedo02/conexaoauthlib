/**
 * Provides Data Transfer Objects (DTOs) for OAuth2 authentication operations.
 *
 * <p>This package contains request and response DTOs for OAuth2 token operations
 * including token creation, introspection, and revocation.</p>
 *
 * <p>Key DTOs:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.dto.oauth2.TokenRequestDTO} - Token request with grant type and credentials</li>
 *   <li>{@link com.conexaoauthlib.dto.oauth2.TokenResponseDTO} - Token response with access token and metadata</li>
 *   <li>{@link com.conexaoauthlib.dto.oauth2.IntrospectRequestDTO} - Token introspection request</li>
 *   <li>{@link com.conexaoauthlib.dto.oauth2.IntrospectResponseDTO} - Token introspection response with claims</li>
 *   <li>{@link com.conexaoauthlib.dto.oauth2.RevokeRequestDTO} - Token revocation request</li>
 *   <li>{@link com.conexaoauthlib.dto.oauth2.ErrorResponseDTO} - Standardized error response</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * TokenRequestDTO request = TokenRequestDTO.builder()
 *     .grantType("client_credentials")
 *     .clientId("client-id")
 *     .clientSecret("client-secret")
 *     .scope("read write")
 *     .build();
 *
 * TokenResponseDTO response = oauth2Client.createToken(request);
 * String accessToken = response.getAccessToken();
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.dto.oauth2;
