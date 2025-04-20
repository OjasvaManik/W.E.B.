package net.ojm.web_backend.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.ojm.web_backend.service.user.impl.CustomUserDetailsServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class AuthTokenFilter(
    private val jwtUtils: JwtUtils,
    @Lazy private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(AuthTokenFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("AuthTokenFilter called for URI: ${request.requestURI}")
        try {
            val jwt = parseJwt(request)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                val username = jwtUtils.getUserNameFromJwtToken(jwt)
                val userDetails = userDetailsService.loadUserByUsername(username)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                logger.debug("Roles from JWT: ${userDetails.authorities}")
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication", e)
        }

        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        val jwt = jwtUtils.getJwtFromHeader(request)
        logger.debug("AuthTokenFilter.kt: $jwt")
        return jwt
    }
}
