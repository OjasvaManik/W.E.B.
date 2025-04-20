package net.ojm.web_backend.domain.dto.user.response

import java.util.*


data class LoginUserResponse(

    val userId: UUID,
    val userName: String,
    val role: String,
    val jwtToken: String

)
