package net.ojm.web_backend.exceptions.user.manage

// When user deletion fails
class UserDeletionException(
    email: String,
    message: String = "Failed to delete user with email $email",
    cause: Throwable? = null
) : RuntimeException(message, cause)

// When credentials are invalid during deletion
class InvalidDeletionCredentialsException(
    message: String = "Invalid credentials for account deletion"
) : SecurityException(message)