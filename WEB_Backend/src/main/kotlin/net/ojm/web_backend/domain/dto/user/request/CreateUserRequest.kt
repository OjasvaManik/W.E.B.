package net.ojm.web_backend.domain.dto.user.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateUserRequest(

    @field:NotBlank(message = "First name cannot be blank")
    @field:Size(min = 2, max = 50)
    val firstName: String,

    @field:NotBlank(message = "Last name cannot be blank")
    @field:Size(min = 2, max = 50)
    val lastName: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8)
    val password: String,

    @field:NotBlank(message = "Username cannot be blank")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]{3,30}$",
        message = "Username must be 3-30 characters and can only include letters, digits, underscores, or hyphens"
    )
    val userName: String

)
