package net.ojm.web_backend.service.user

import net.ojm.web_backend.domain.dto.user.response.ListUserResponse
import net.ojm.web_backend.domain.entity.user.UserEntity
import org.springframework.http.ResponseEntity
import java.util.*

interface UserService {

    fun createUser(userEntity: UserEntity);

    fun createUsers(userEntities: List<UserEntity>)

    fun searchUser(userName: String?): List<ListUserResponse>;

    fun toggleBanStatus(userId: UUID): UserEntity;

    fun deleteUserByEmail(userEmail: String, password: String): ResponseEntity<String>;

}