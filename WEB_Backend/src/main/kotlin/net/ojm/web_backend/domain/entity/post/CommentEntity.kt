package net.ojm.web_backend.domain.entity.post

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import net.ojm.web_backend.domain.entity.user.UserEntity
import java.util.UUID

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "comments")
data class CommentEntity(

    @Id
    @GeneratedValue
    val commentId : UUID = UUID.randomUUID(),

    @Column(name = "comment_content")
    val commentContent: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: PostEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @OneToMany(mappedBy = "comment", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val vote: List<CommentVoteEntity> = mutableListOf()

)
