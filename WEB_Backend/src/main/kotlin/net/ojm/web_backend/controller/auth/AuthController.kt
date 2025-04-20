package net.ojm.web_backend.controller.auth

import jakarta.validation.Valid
import net.ojm.web_backend.domain.dto.user.request.CreateUserRequest
import net.ojm.web_backend.domain.dto.user.request.LoginUserRequest
import net.ojm.web_backend.domain.dto.user.response.LoginUserResponse
import net.ojm.web_backend.extensions.user.toUserEntity
import net.ojm.web_backend.jwt.JwtUtils
import net.ojm.web_backend.service.user.UserService
import net.ojm.web_backend.service.user.impl.CustomUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/v1/web/auth")
class AuthController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils
) {

    @PostMapping(path = ["/register"])
    fun createUser(@Valid @RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Void> {

        userService.createUser(createUserRequest.toUserEntity())
        return ResponseEntity.ok().build()

    }

    @PostMapping(path = ["/register/bulk"])
    fun createUsers(@Valid @RequestBody users: List<CreateUserRequest>): ResponseEntity<Void> {
        val userEntities = users.map { it.toUserEntity() }
        userService.createUsers(userEntities)
        return ResponseEntity.ok().build()
    }


    @PostMapping("/login")
    fun authenticateUser(@RequestBody loginRequest: LoginUserRequest): ResponseEntity<Any> {
        val authentication = try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.userName,
                    loginRequest._password
                )
            )
        } catch (ex: AuthenticationException) {
            val errorBody = mapOf(
                "message" to "Bad credentials",
                "status" to false
            )
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody)
        }

        SecurityContextHolder.getContext().authentication = authentication
        val userDetails = authentication.principal as CustomUserDetails

        val jwtToken = jwtUtils.generateTokenFromUsername(userDetails)

        val role = userDetails.authorities.first().authority // Returns String

        val response = LoginUserResponse(
            userId = userDetails.getUserId(),
            userName = userDetails.username,
            role = role,
            jwtToken = jwtToken
        )

        return ResponseEntity.ok(response)
    }

}