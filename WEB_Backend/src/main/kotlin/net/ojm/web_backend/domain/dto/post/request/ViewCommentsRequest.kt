package net.ojm.web_backend.domain.dto.post.request

import net.ojm.web_backend.domain.entity.post.PostEntity
import java.util.UUID

data class ViewCommentsRequest(

    val post: PostEntity

)
