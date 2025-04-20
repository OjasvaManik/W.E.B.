package net.ojm.web_backend.domain.dto.post.response

import java.util.*

data class GetAllCommentsResponse(
    val commentId: UUID,
    val commentContent: String,
    val userId: UUID,
    val userName: String,
    val upVotes: Int,
    val downVotes: Int
)
