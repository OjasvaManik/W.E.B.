package net.ojm.web_backend.domain.entity.post

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import net.ojm.web_backend.domain.entity.user.UserEntity
import java.util.UUID

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "posts")
data class PostEntity(

    @Id
    @GeneratedValue
    @Column(name = "post_id", nullable = false, unique = true)
    val postId: UUID = UUID.randomUUID(),

    @Column(name = "post_title", nullable = false)
    val postTitle: String,

    @Column(name = "post_content", nullable = false)
    val postContent: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [(CascadeType.PERSIST)])
    @JoinColumn(name = "user_id", nullable = false)
    val userId: UserEntity,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: List<CommentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val vote: List<PostVoteEntity> = mutableListOf()

)
