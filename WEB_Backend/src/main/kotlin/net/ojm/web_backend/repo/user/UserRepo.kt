package net.ojm.web_backend.repo.user

import net.ojm.web_backend.domain.entity.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepo : JpaRepository<UserEntity, UUID> {

    fun existsByEmail(email: String): Boolean;

    fun existsByUserName(username: String): Boolean;

    fun findByUserNameContainingIgnoreCase(userName: String): MutableList<UserEntity>;

    fun findByEmail(email: String): UserEntity?;

    fun findByUserName(userName: String): UserEntity?;

    fun existsByEmailIn(emails: List<String>): Boolean

    fun existsByUserNameIn(usernames: List<String>): Boolean


}