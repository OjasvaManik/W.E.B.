package net.ojm.web_backend.domain.dto.article.response

import java.util.UUID

data class ListArticleResponse(

    val articleId: UUID,
    val articleTitle: String,
    val articleContent: String,
    val articleSource: String,
    val userId: UUID,
    val categoryName: List<String>,
    val articleStatus: StatusResponse?,

)
