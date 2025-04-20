package net.ojm.web_backend.domain.dto.article.request

import java.util.*

data class UpdateArticleRequest(

    val articleTitle: String,
    val articleContent: String,
    val articleSource: String,
    val categoryIds: List<UUID>,
    val userId: UUID,

)
