package net.ojm.web_backend.domain.dto.article.request

import java.util.UUID

data class CreateArticleRequest(

    val articleTitle: String,
    val articleContent: String,
    val articleSource: String,
    val userId: UUID,
    val categoryIds: List<UUID>,

)
