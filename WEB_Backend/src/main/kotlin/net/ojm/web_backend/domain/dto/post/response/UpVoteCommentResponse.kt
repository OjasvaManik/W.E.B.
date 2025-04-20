package net.ojm.web_backend.domain.dto.post.response

import java.util.UUID

data class UpVoteCommentResponse(

    val commentId: UUID,
    val upVote: Int

)
