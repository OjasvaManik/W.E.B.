package net.ojm.web_backend.domain.entity.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.proxy.HibernateProxy
import java.util.UUID

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "user_profile_image")
data class UserProfileImageEntity(

    @Id
    @GeneratedValue
    @Column(name = "profile_image_id", nullable = false, unique = true)
    val profileImageId: UUID = UUID.randomUUID(),

    @field:NotBlank(message = "Image name cannot be blank")
    @field:Size(max = 255, message = "Image name too long. Max 255 chars.")
    @Column(name = "profile_image_name", nullable = false)
    val profileImageName: String,

    @field:NotBlank(message = "Image type cannot be blank")
    @field:Pattern(
        regexp = "image/(png|jpeg|jpg|webp|svg)",
        message = "File format not supported. Images can only be PNG, JPEG, JPG, WEBP, SVG"
    )
    @Column(name = "profile_image_type", nullable = false)
    val profileImageType: String,

    @field:NotBlank(message = "File path cannot be blank")
    @field:Size(max = 512, message = "File path too long. Max 512 chars.")
    @Column(name = "profile_image_file_path", nullable = false)
    val profileImageFilePath: String,

    @ManyToOne(optional = false, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", nullable = false)
    val userId: UserEntity? = null

) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as UserProfileImageEntity

        return profileImageId != null && profileImageId == other.profileImageId
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}
