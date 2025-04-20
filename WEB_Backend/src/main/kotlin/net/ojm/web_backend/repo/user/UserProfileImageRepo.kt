package net.ojm.web_backend.repo.user

import net.ojm.web_backend.domain.entity.user.UserEntity
import net.ojm.web_backend.domain.entity.user.UserProfileImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserProfileImageRepo : JpaRepository<UserProfileImageEntity, UUID> {

    fun findTopByUserIdOrderByProfileImageIdDesc(userEntity: UserEntity): UserProfileImageEntity?;

    fun findByProfileImageName(profileImageName: String): UserProfileImageEntity?;

    fun findByProfileImageNameAndUserId(name: String, userId: UserEntity): UserProfileImageEntity?

}