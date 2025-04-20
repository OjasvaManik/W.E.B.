package net.ojm.web_backend.domain.dto.article.response

import net.ojm.web_backend.domain.dto.user.response.ArticleUserResponse
import net.ojm.web_backend.domain.entity.article.StatusEntity
import java.util.*

data class ViewArticleResponse(

    val articleId: UUID,
    val articleImageUrl: String?,
    val articleTitle: String,
    val articleContent: String,
    val articleSource: String,
    val articleStatus: StatusResponse,
    val user: ArticleUserResponse

)
