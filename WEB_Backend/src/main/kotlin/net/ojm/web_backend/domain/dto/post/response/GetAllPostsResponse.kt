package net.ojm.web_backend.domain.dto.post.response

import java.util.UUID

data class GetAllPostsResponse(

    val postId: UUID,
    val postTitle: String,
    val postContent: String,
    val userId: UUID,
    val userName: String,
    val commentCount: Int,
    val upVotes: Int,
    val downVotes: Int,

)
