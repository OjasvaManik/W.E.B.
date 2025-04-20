package net.ojm.web_backend.domain.dto.post.response

import java.util.UUID

data class DownVotePostResponse(

    val postId: UUID,
    val downVote: Int

)
