package net.ojm.web_backend.domain.entity.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "users")
@Access(AccessType.FIELD)
data class UserEntity(

    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: UUID = UUID.randomUUID(),

    @field:NotBlank(message = "First name cannot be blank")
    @field:Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name")
    val firstName: String,

    @field:NotBlank(message = "Last name cannot be blank")
    @field:Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name")
    val lastName: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password", nullable = false)
    val _password: String,

    @field:NotNull(message = "Role can’t be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: RoleTypeEnum = RoleTypeEnum.USER,

    @field:NotBlank(message = "Username cannot be blank")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]{3,30}$",
        message = "Username must be 3-30 characters and can only include letters, digits, underscores, or hyphens"
    )
    @Column(name = "user_name", nullable = false, unique = true)
    val userName: String,

    @Column(name = "is_banned", nullable = false)
    val isBanned: Boolean = false,

    ) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    override fun getPassword(): String = _password

    override fun getUsername(): String = userName

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = !isBanned

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}
