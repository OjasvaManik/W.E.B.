package net.ojm.web_backend.extensions.user

import net.ojm.web_backend.domain.dto.user.request.CreateUserRequest
import net.ojm.web_backend.domain.dto.user.request.LoginUserRequest
import net.ojm.web_backend.domain.dto.user.response.ArticleUserResponse
import net.ojm.web_backend.domain.entity.user.RoleTypeEnum
import net.ojm.web_backend.domain.entity.user.UserEntity

//fun UserEntity.toCreateUserRequest() = CreateUserRequest (
//
//    firstName = this.firstName,
//    lastName = this.lastName,
//    email = this.email,
//    password = this.password,
//    userName = this.userName
//
//)

fun CreateUserRequest.toUserEntity() = UserEntity(

    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    _password = this.password,
    role = RoleTypeEnum.USER,
    userName = this.userName,
    isBanned = false

)

fun UserEntity.toArticleUserResponse() = ArticleUserResponse(

    userId = this.userId,
    firstName = this.firstName,
    lastName = this.lastName,
    role = this.role,
    userName = this.userName,

)