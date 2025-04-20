package net.ojm.web_backend.domain.dto.post.response

import java.util.*

data class ViewCommentsResponse(

    val commentId: UUID,
    val userId: UUID,
    val userName: String,
    val commentContent: String,
    val upVotes: Int,
    val downVotes: Int,

)
