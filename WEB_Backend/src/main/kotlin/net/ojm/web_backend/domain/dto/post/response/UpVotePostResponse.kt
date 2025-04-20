package net.ojm.web_backend.domain.dto.post.response

import java.util.*

data class UpVotePostResponse(

    val postId: UUID,
    val upVote: Int

)