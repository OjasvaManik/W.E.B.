package net.ojm.web_backend.domain.dto.article.response

import java.util.*

data class CreateArticleResponse(

    val articleId: UUID,
    val articleTitle: String,
    val articleContent: String,
    val articleSource: String,
    val categoryIds: List<UUID>,
    val userId: UUID,

)