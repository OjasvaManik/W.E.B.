package net.ojm.web_backend.repo.article

import net.ojm.web_backend.domain.entity.article.ArticleEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ArticleRepo : JpaRepository<ArticleEntity, UUID> {

    fun existsByArticleId(articleId: UUID): Boolean

    @EntityGraph(attributePaths = ["userId", "categories"])
    fun findByCategoriesCategoryId(categoryId: UUID): List<ArticleEntity>

    fun findByArticleTitleContainingIgnoreCase(title: String): List<ArticleEntity>

}