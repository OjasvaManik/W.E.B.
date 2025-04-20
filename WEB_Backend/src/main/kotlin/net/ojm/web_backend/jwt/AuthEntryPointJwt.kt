package net.ojm.web_backend.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class AuthEntryPointJwt : AuthenticationEntryPoint {

    private val logger = LoggerFactory.getLogger(AuthEntryPointJwt::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error("Unauthorized error: ${authException.message}")

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val body: Map<String, Any> = mapOf(
            "status" to HttpServletResponse.SC_UNAUTHORIZED,
            "error" to "Unauthorized",
            "message" to (authException.message ?: "Unauthorized access"),
            "path" to request.servletPath
        )

        val mapper = ObjectMapper()
        mapper.writeValue(response.outputStream, body)
    }
}
