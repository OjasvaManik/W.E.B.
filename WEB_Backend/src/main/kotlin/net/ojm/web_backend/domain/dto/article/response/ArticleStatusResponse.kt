package net.ojm.web_backend.domain.dto.article.response

import net.ojm.web_backend.domain.entity.article.StatusTypeEnum
import java.util.*

data class ArticleStatusResponse (

    val articleId: UUID,
    val status: StatusTypeEnum

)