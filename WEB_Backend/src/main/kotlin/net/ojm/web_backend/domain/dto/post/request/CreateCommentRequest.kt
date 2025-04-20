package net.ojm.web_backend.domain.dto.post.request

import java.util.UUID

data class CreateCommentRequest(

    val commentContent: String,
    val postId: UUID,
    val userId: UUID,

)
