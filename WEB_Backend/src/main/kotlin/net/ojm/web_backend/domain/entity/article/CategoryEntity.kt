package net.ojm.web_backend.domain.entity.article

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "category")
data class CategoryEntity(

    @Id
    @Column(name = "category_id")
    @GeneratedValue
    val categoryId: UUID = UUID.randomUUID(),

    @Column(name = "category_name", nullable = false)
    @field:NotBlank(message = "Category Name cannot be blank")
    @field:Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    val categoryName: String,

    @Column(name = "category_title", nullable = false)
    @field:NotBlank(message = "Category description cannot be blank")
    @field:Size(min = 2, max = 150, message = "Category description must be between 2 and 150 characters")
    val categoryDescription: String,

    @ManyToMany(cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "article_category",
        joinColumns = [JoinColumn(name = "category_id")],
        inverseJoinColumns = [JoinColumn(name = "article_id")]
    )
    val articles: MutableSet<ArticleEntity> = mutableSetOf()

)
