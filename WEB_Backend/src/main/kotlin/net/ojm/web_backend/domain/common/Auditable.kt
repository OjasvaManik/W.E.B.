package net.ojm.web_backend.domain.common

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null

}