package net.ojm.web_backend.exceptions.user.profile_image

import java.util.*

class ProfileImageUploadException(
    userId: UUID,
    message: String = "Failed to upload profile image for user $userId",
    cause: Throwable? = null
) : RuntimeException(message, cause)

// When image is invalid (wrong format, size, etc.)
class InvalidImageException(
    message: String = "Invalid image file",
    cause: Throwable? = null
) : IllegalArgumentException(message, cause)