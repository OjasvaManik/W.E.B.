package net.ojm.web_backend.domain.dto.post.request

import java.util.UUID

data class VoteCommentRequest(

    val commentId: UUID,
    val userId: UUID,

)
