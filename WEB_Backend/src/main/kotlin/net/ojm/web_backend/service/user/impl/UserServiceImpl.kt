package net.ojm.web_backend.service.user.impl

import jakarta.persistence.EntityNotFoundException
import net.ojm.web_backend.domain.dto.user.response.ListUserResponse
import net.ojm.web_backend.domain.entity.user.UserEntity
import net.ojm.web_backend.domain.entity.user.UserProfileImageEntity
import net.ojm.web_backend.repo.user.UserRepo
import net.ojm.web_backend.repo.user.UserProfileImageRepo
import net.ojm.web_backend.service.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserServiceImpl(
    private val userRepo: UserRepo,
    private val userProfileImageRepo: UserProfileImageRepo,
    private val passwordEncoder: PasswordEncoder,
): UserService {

    @Transactional
    override fun createUser(userEntity: UserEntity) {

        if (userRepo.existsByEmail(userEntity.email)) {
            throw IllegalArgumentException("Email already in use")
        }

        if (userRepo.existsByUserName(userEntity.userName)) {
            throw IllegalArgumentException("UserName already in use")
        }

        val encodedUser: UserEntity = userEntity.copy(_password = passwordEncoder.encode(userEntity.password))
        userRepo.save(encodedUser);
    }

    @Transactional
    override fun createUsers(userEntities: List<UserEntity>) {
        val emails = userEntities.map { it.email }
        val usernames = userEntities.map { it.userName }

        if (userRepo.existsByEmailIn(emails)) {
            throw IllegalArgumentException("One or more emails are already in use")
        }

        if (userRepo.existsByUserNameIn(usernames)) {
            throw IllegalArgumentException("One or more usernames are already in use")
        }

        val encodedUsers = userEntities.map { it.copy(_password = passwordEncoder.encode(it.password)) }
        userRepo.saveAll(encodedUsers)
    }


    override fun searchUser(userName: String?): List<ListUserResponse> {
        val users = if (!userName.isNullOrBlank()) {
            userRepo.findByUserNameContainingIgnoreCase(userName);
        } else {
            userRepo.findAll()
        }

        return users.map {
            val image = userProfileImageRepo.findTopByUserIdOrderByProfileImageIdDesc(it);

            ListUserResponse(
                userId = it.userId,
                userName = it.userName,
                email = it.email,
                firstName = it.firstName,
                lastName = it.lastName,
                role = it.role.name,
                profileImageUrl = image?.profileImageFilePath,
                isBanned = it.isBanned,
            )
        }
    }

    @Transactional
    override fun toggleBanStatus(userId: UUID): UserEntity {
        val user = userRepo.findById(userId)
            .orElseThrow { throw EntityNotFoundException("User not found with ID: $userId") }

        val updatedUser = user.copy(isBanned = !user.isBanned)
        return userRepo.save(updatedUser)
    }

    @Transactional
    override fun deleteUserByEmail(userEmail: String, password: String): ResponseEntity<String> {
        val user = userRepo.findByEmail(userEmail)
            ?: throw EntityNotFoundException("User not found with email: $userEmail");

        if (password.isBlank()) {
            throw IllegalArgumentException("Password must not be blank");
        }

        if (!passwordEncoder.matches(password, user._password)) {
            throw IllegalArgumentException("Passwords do not match")
        }

        userRepo.delete(user)
        return ResponseEntity.ok("User has been deleted");
    }


}