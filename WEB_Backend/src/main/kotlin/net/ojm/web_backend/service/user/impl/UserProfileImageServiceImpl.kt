package net.ojm.web_backend.service.user.impl

import net.ojm.web_backend.domain.entity.user.UserProfileImageEntity
import net.ojm.web_backend.repo.user.UserProfileImageRepo
import net.ojm.web_backend.repo.user.UserRepo
import net.ojm.web_backend.service.user.UserProfileImageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

const val PROFILE_IMAGE_FOLDER_PATH = "D:/Minor/Project/uploads/user/profile_photo/"

@Service
class UserProfileImageServiceImpl(
    private val userProfileImageRepo: UserProfileImageRepo,
    private val userRepo: UserRepo
) : UserProfileImageService {

    override fun uploadProfileImage(image: MultipartFile, userId: UUID): Map<String, String> {
        val user = userRepo.findById(userId).orElseThrow {
            IllegalArgumentException("User with ID $userId not found")
        }

        val fileName = image.originalFilename ?: throw IllegalArgumentException("Image must have a name")
        val contentType = image.contentType ?: "application/octet-stream"
        val imagePath = PROFILE_IMAGE_FOLDER_PATH + fileName

        val entity = UserProfileImageEntity(
            profileImageName = fileName,
            profileImageType = contentType,
            profileImageFilePath = imagePath,
            userId = user
        )

        userProfileImageRepo.save(entity)
        image.transferTo(File(imagePath))

        return mapOf(
            "name" to fileName,
            "type" to contentType,
            "path" to imagePath,
            "userId" to userId.toString()
        )
    }


    override fun downloadProfileImage(filename: String, userId: UUID): ByteArray {
        val user = userRepo.findById(userId).orElseThrow {
            IllegalArgumentException("User with ID $userId not found")
        }

        val imageData = userProfileImageRepo
            .findByProfileImageNameAndUserId(filename, user)
            ?: throw FileNotFoundException("Image $filename for user $userId not found")

        val path = Paths.get(imageData.profileImageFilePath)
        if (!Files.exists(path)) {
            throw FileNotFoundException("File not found on disk at path: $path")
        }

        return Files.readAllBytes(path)
    }




}