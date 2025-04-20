package net.ojm.web_backend.domain.dto.user.response

import net.ojm.web_backend.domain.entity.user.RoleTypeEnum
import java.util.UUID

data class ArticleUserResponse(

    val userId: UUID,
    val firstName: String,
    val lastName: String,
    val role: RoleTypeEnum,
    val userName: String,

)
