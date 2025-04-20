package net.ojm.web_backend.domain.entity.article

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "article_images")
data class ArticleImageEntity(

    @Id
    @GeneratedValue
    @Column(name = "article_image_id", nullable = false, unique = true)
    val articleImageId: UUID = UUID.randomUUID(),

    @field:NotBlank(message = "Image name cannot be blank")
    @field:Size(max = 255, message = "Image name too long. Max 255 chars.")
    @Column(name = "article_image_name", nullable = false)
    val articleImageName: String,

    @field:NotBlank(message = "Image type cannot be blank")
    @field:Pattern(
        regexp = "image/(png|jpeg|jpg|webp|svg)",
        message = "File format not supported. Images can only be PNG, JPEG, JPG, WEBP, SVG"
    )
    @Column(name = "article_image_type", nullable = false)
    val articleImageType: String,

    @field:NotBlank(message = "File path cannot be blank")
    @field:Size(max = 512, message = "File path too long. Max 512 chars.")
    @Column(name = "article_image_file_path", nullable = false)
    val articleImageFilePath: String,

    @OneToOne(optional = false, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "article_id", nullable = false, unique = true)
    val article: ArticleEntity

)
