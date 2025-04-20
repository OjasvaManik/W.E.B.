package net.ojm.web_backend.service.user

import org.springframework.web.multipart.MultipartFile
import java.util.UUID


interface UserProfileImageService {

    fun uploadProfileImage(image: MultipartFile, userId: UUID) : Map<String, String>;

    fun downloadProfileImage(filename: String, userId: UUID): ByteArray?;

}