package net.ojm.web_backend.domain.dto.post.response

import java.util.*

data class ViewPostResponse(

    val postId: UUID,
    val userId: UUID,
    val userName: String,
    val postTitle: String,
    val postContent: String,
    val comments: List<ViewCommentsResponse>,
    val upVotes: Int,
    val downVotes: Int,

)