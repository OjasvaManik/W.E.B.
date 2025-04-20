package net.ojm.web_backend.controller.user

import net.ojm.web_backend.domain.dto.user.request.DeleteUserRequest
import net.ojm.web_backend.domain.dto.user.request.ListUserRequest
import net.ojm.web_backend.domain.dto.user.response.ListUserResponse
import net.ojm.web_backend.service.user.UserService
import net.ojm.web_backend.service.user.impl.PROFILE_IMAGE_FOLDER_PATH
import net.ojm.web_backend.service.user.impl.UserProfileImageServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@RestController
@RequestMapping("/api/v1/web/user")
class UserController(
    private val userProfileImageServiceImpl: UserProfileImageServiceImpl,
    private val userService: UserService
) {

    @PostMapping("/profile-image/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfileImage(
        @RequestParam("image") image: MultipartFile,
        @RequestParam("userId") userId: UUID
    ): ResponseEntity<Map<String, String>> {
        return try {
            val imageDetails = userProfileImageServiceImpl.uploadProfileImage(image, userId)
            ResponseEntity.ok(imageDetails)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to upload image: ${e.message}"))
        }
    }



    @GetMapping("/profile-image/{userId}/{filename}")
    fun downloadProfileImage(
        @PathVariable userId: UUID,
        @PathVariable filename: String
    ): ResponseEntity<ByteArray> {
        return try {
            val imageData = userProfileImageServiceImpl.downloadProfileImage(filename, userId)
            val contentType = Files.probeContentType(Paths.get(PROFILE_IMAGE_FOLDER_PATH + filename))

            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType ?: "application/octet-stream"))
                .body(imageData)
        } catch (e: FileNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }


    @DeleteMapping(path = ["/delete-user"])
    fun deleteUser(@RequestBody request: DeleteUserRequest): ResponseEntity<String> {
        return userService.deleteUserByEmail(request.email, request.password);
    }

    @GetMapping(path = ["/all-users"])
    fun getUsers(@ModelAttribute request: ListUserRequest): ResponseEntity<List<ListUserResponse>> {
        return ResponseEntity.ok(userService.searchUser(request.userName))
    }

}