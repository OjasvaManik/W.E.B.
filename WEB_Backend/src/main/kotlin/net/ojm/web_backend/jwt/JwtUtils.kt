package net.ojm.web_backend.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import javax.crypto.SecretKey
import java.security.Key
import java.util.Date

@Component
class JwtUtils {

    private val logger = LoggerFactory.getLogger(JwtUtils::class.java)

    @Value("\${spring.app.jwtSecret}")
    private lateinit var jwtSecret: String

    @Value("\${spring.app.jwtExpirationMs}")
    private var jwtExpirationMs: Int = 0

    fun getJwtFromHeader(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        logger.debug("Authorization Header: {}", bearerToken)
        return if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    fun generateTokenFromUsername(userDetails: UserDetails): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key())
            .compact()
    }

    fun getUserNameFromJwtToken(token: String): String {
        return Jwts.parser()
            .verifyWith(key() as SecretKey)
            .build()
            .parseSignedClaims(token)
            .payload.subject
    }

    fun validateJwtToken(authToken: String): Boolean {
        return try {
            println("Validate")
            Jwts.parser().verifyWith(key() as SecretKey).build().parseSignedClaims(authToken)
            true
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
            false
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
            false
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
            false
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
            false
        }
    }

    private fun key(): Key {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    }
}