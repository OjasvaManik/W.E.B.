package net.ojm.web_backend.domain.dto.user.response

import java.util.*

data class ListUserResponse(

    val userId: UUID,
    val userName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val profileImageUrl: String? = null,
    val isBanned: Boolean

)
