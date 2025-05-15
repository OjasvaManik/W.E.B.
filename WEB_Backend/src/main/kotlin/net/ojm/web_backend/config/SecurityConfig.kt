package net.ojm.web_backend.config

import net.ojm.web_backend.jwt.AuthTokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val unauthorizedHandler: AuthenticationEntryPoint,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, authTokenFilter: AuthTokenFilter): SecurityFilterChain? {
        http
            .cors { } // ✅ enable CORS in the chain
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/api/v1/web/auth/**",
                        "/api/v1/web/wiki",
                        "/api/v1/web/wiki/view/{articleId}",
                        "/api/v1/web/wiki/category/{categoryId}",
                        "/api/v1/web/wiki/{articleId}/{filename}",
                        "/api/v1/web/forum",
                        "/api/v1/web/search"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic { }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling { it.authenticationEntryPoint(unauthorizedHandler) }
            .headers { it.frameOptions { it1 -> it1.sameOrigin() } }

        return http.build()
    }


    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS") // ✅ Add OPTIONS
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }


    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

}


//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val user1: UserDetails = User.withUsername("user1")
//            .password("{noop}password1")
//            .roles("USER")
//            .build();
//
//        val admin: UserDetails = User.withUsername("admin")
//            .password("{noop}adminPassword")
//            .roles("ADMIN")
//            .build();
//
//        return InMemoryUserDetailsManager(user1, admin);
//    }


//            .formLogin { form ->
//                form
//                    .loginPage("/login") // Your React login route
//                    .loginProcessingUrl("/api/v1/web/auth/login") // API endpoint for auth
//                    .defaultSuccessUrl("/dashboard") // Redirect after success
//                    .failureUrl("/login?error=true") // Redirect after failure
//                    .permitAll()
//            }
//            .logout { logout ->
//                logout
//                    .logoutUrl("/api/v1/web/auth/logout")
//                    .logoutSuccessUrl("/login?logout=true")
//                    .permitAll()
//            }