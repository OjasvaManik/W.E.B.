package net.ojm.web_backend.extensions.article

import net.ojm.web_backend.domain.dto.article.request.CreateArticleRequest
import net.ojm.web_backend.domain.dto.article.request.CreateCategoryRequest
import net.ojm.web_backend.domain.dto.article.response.*
import net.ojm.web_backend.domain.entity.article.ArticleEntity
import net.ojm.web_backend.domain.entity.article.CategoryEntity
import net.ojm.web_backend.domain.entity.article.StatusEntity
import net.ojm.web_backend.domain.entity.user.UserEntity

fun CreateArticleRequest.toArticleEntity(user: UserEntity) = ArticleEntity(

    articleTitle = this.articleTitle,
    articleContent = this.articleContent,
    articleSource = this.articleSource,
    userId = user,

)

fun CreateCategoryRequest.toCategoryEntity() = CategoryEntity(

    categoryName = this.categoryName,
    categoryDescription = this.categoryDescription,

)

fun CategoryEntity.toListCategoryResponse() = ListCategoryResponse(

    categoryName = this.categoryName,
    categoryDescription = this.categoryDescription,
    categoryId = this.categoryId,

)

//fun ArticleEntity.toCreateArticleResponse() = CreateArticleResponse(
//    articleId = this.articleId,
//    articleTitle = this.articleTitle,
//    articleContent = this.articleContent,
//    articleSource = this.articleSource,
//    userId = this.userId.userId,
//    categoryIds =
//)

//fun ArticleEntity.toListArticleResponse(): ListArticleResponse {
//    // Access the categories through the relationship in CategoryEntity
//    val categoryNames = this.categories.map { it.categoryName } // Make sure you're eager fetching categories in your query
//
//
//    return ListArticleResponse(
//        articleId = this.articleId,
//        articleTitle = this.articleTitle,
//        articleContent = this.articleContent,
//        articleSource = this.articleSource,
//        userId = this.userId.userId,
//        categoryName = categoryNames,
//        articleStatus = ,
//    )
//}


fun StatusEntity.toStatusResponse() = StatusResponse(

    status = this.status

)

fun StatusEntity.toArticleStatusResponse() = ArticleStatusResponse(

    articleId = article.articleId,
    status = this.status

)