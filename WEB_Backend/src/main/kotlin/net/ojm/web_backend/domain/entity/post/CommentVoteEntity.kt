package net.ojm.web_backend.domain.entity.post

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import net.ojm.web_backend.domain.entity.user.UserEntity
import java.util.*

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "comment_votes", uniqueConstraints = [UniqueConstraint(columnNames = ["comment_id", "user_id"])])
data class CommentVoteEntity(

    @Id
    @GeneratedValue
    @Column(name = "vote_id", nullable = false, unique = true)
    val voteId: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = true)
    var voteType: VoteTypeEnum? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    val comment: CommentEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity
)
