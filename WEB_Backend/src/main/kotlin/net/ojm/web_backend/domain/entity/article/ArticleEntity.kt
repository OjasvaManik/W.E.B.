package net.ojm.web_backend.domain.entity.article

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import net.ojm.web_backend.domain.entity.user.UserEntity
import java.util.UUID

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "articles")
data class ArticleEntity(

    @Id
    @GeneratedValue
    @Column(name = "article_id", nullable = false, unique = true)
    val articleId: UUID = UUID.randomUUID(),

    @field:NotBlank(message = "Title cannot be blank")
    @field:Size(min = 2, max = 500, message = "Title must be between 2 and 500 characters")
    @Column(name = "article_title", nullable = false)
    val articleTitle: String,

    @field:NotBlank(message = "Content cannot be blank")
//    @field:Size(min = 2, max = 50000, message = "Content must be between 2 and 50000 characters")
    @Column(name = "article_content", nullable = false, columnDefinition = "LONGTEXT")
    val articleContent: String,

    @field:NotBlank(message = "Source cannot be blank")
    @field:Size(min = 2, max = 1000, message = "Source must be between 2 and 1000 characters")
    @Column(name = "article_source", nullable = false)
    val articleSource: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [(CascadeType.PERSIST)])
    @JoinColumn(name = "user_id", nullable = false)
    val userId: UserEntity,

    @ManyToMany(mappedBy = "articles")
    val categories: MutableSet<CategoryEntity> = mutableSetOf(),

    @OneToOne(mappedBy = "article", cascade = [CascadeType.ALL])
    var status: StatusEntity? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArticleEntity) return false
        return articleId == other.articleId
    }

    override fun hashCode(): Int {
        return articleId.hashCode()
    }
}
