package net.ojm.web_backend.domain.dto.post.request

import java.util.UUID

data class ViewPostRequest(

    val postId: UUID,
    val userId: UUID,

)
