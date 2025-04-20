package net.ojm.web_backend.domain.entity.article

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.util.*

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "article_status")
data class StatusEntity(
    @Id
    val articleId: UUID? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "article_id")
    val article: ArticleEntity,

    @Enumerated(EnumType.STRING)
    val status: StatusTypeEnum = StatusTypeEnum.PENDING
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StatusEntity) return false
        return articleId == other.articleId
    }

    override fun hashCode(): Int {
        return articleId?.hashCode() ?: 0
    }
}
