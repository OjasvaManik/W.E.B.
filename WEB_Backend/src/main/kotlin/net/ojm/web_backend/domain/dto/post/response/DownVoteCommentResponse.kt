package net.ojm.web_backend.domain.dto.post.response

import java.util.*

data class DownVoteCommentResponse(

    val commentId: UUID,
    val downVote: Int

)
