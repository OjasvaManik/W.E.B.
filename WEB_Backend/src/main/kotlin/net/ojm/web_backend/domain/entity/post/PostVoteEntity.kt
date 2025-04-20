package net.ojm.web_backend.domain.entity.post

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import net.ojm.web_backend.domain.entity.user.UserEntity
import java.util.UUID

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@Table(name = "post_votes", uniqueConstraints = [UniqueConstraint(columnNames = ["post_id", "user_id"])])
data class PostVoteEntity(

    @Id
    @GeneratedValue
    @Column(name = "vote_id", nullable = false, unique = true)
    val voteId: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = true)
    var voteType: VoteTypeEnum? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    val post: PostEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

)

