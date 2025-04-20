package net.ojm.web_backend.exceptions.user.manage

class UserNotFoundException(
    identifier: String,
    message: String = "User not found with identifier: $identifier"
) : NoSuchElementException(message)