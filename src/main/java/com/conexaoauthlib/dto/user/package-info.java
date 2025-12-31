/**
 * Provides Data Transfer Objects (DTOs) for User management operations.
 *
 * <p>This package contains request and response DTOs for user CRUD operations,
 * including creation, retrieval, listing with pagination, status updates,
 * password changes, and authentication.</p>
 *
 * <p>Key DTOs:</p>
 * <ul>
 *   <li>{@link com.conexaoauthlib.dto.user.UserCreateRequestDTO} - User creation request</li>
 *   <li>{@link com.conexaoauthlib.dto.user.UserResponseDTO} - User response with details</li>
 *   <li>{@link com.conexaoauthlib.dto.user.UserUpdateRequestDTO} - Partial update request</li>
 *   <li>{@link com.conexaoauthlib.dto.user.UserStatusDTO} - Status update request</li>
 *   <li>{@link com.conexaoauthlib.dto.user.UserPasswordRequestDTO} - Password change request</li>
 *   <li>{@link com.conexaoauthlib.dto.user.UserFilterDTO} - Pagination and filter parameters</li>
 *   <li>{@link com.conexaoauthlib.dto.user.UserLoginRequestDTO} - Login request</li>
 * </ul>
 *
 * <p>Validation:</p>
 * <ul>
 *   <li>Email validation using @Email annotation</li>
 *   <li>Password strength validation (uppercase, lowercase, digit, special char)</li>
 *   <li>Name length validation (3-255 characters)</li>
 * </ul>
 *
 * <p>Security:</p>
 * <ul>
 *   <li>Passwords are never exposed in responses</li>
 *   <li>Password change requires current password</li>
 *   <li>Rate limiting should be applied to login attempts</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * UserCreateRequestDTO user = UserCreateRequestDTO.builder()
 *     .name("João da Silva")
 *     .email("joao.silva@empresa.com")
 *     .password("SenhaForte123!")
 *     .tenantId("tenant-123")
 *     .build();
 *
 * UserResponseDTO response = userClient.create(user);
 * }</pre>
 *
 * @since 1.0.0
 */
package com.conexaoauthlib.dto.user;
