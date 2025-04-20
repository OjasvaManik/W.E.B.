package net.ojm.web_backend.domain.dto.post.request

import net.ojm.web_backend.domain.entity.user.UserEntity
import java.util.UUID

data class CreatePostRequest(

    val postTitle: String,
    val postContent: String,
    val userId: UUID,

)
