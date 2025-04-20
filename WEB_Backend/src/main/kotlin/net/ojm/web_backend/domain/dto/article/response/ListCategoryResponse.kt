package net.ojm.web_backend.domain.dto.article.response

import java.util.UUID

data class ListCategoryResponse(

    val categoryId: UUID,
    val categoryName: String,
    val categoryDescription: String,

)
