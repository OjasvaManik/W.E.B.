package net.ojm.web_backend.exceptions.user.profile_image

import java.io.FileNotFoundException

// When image file is not found
class ProfileImageNotFoundException(
    filename: String,
    message: String = "Profile image '$filename' not found"
) : FileNotFoundException(message)

// When image processing fails
class ImageProcessingException(
    message: String = "Failed to process image",
    cause: Throwable? = null
) : RuntimeException(message, cause)