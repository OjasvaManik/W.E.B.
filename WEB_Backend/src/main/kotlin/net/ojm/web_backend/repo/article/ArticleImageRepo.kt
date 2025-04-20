package net.ojm.web_backend.repo.article

import net.ojm.web_backend.domain.entity.article.ArticleImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ArticleImageRepo : JpaRepository<ArticleImageEntity, UUID> {

    fun findByArticle_ArticleIdAndArticleImageName(articleId: UUID, imageName: String): ArticleImageEntity?

    fun findArticleImageNameByArticle_ArticleId(articleId: UUID): ArticleImageEntity?

}