package net.ojm.web_backend.service.user.impl

import net.ojm.web_backend.repo.user.UserRepo
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

//@Service
//class CustomUserDetailsServiceImpl (private val userRepo: UserRepo) : UserDetailsService {
//
//    override fun loadUserByUsername(userName: String): UserDetails {
//        return userRepo.findByUserName(userName)
//            ?: throw UsernameNotFoundException("User not found with username: $userName");
//    }
//
//}

@Service
class CustomUserDetailsServiceImpl(
    private val userRepo: UserRepo
) : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails {
        val user = userRepo.findByUserName(userName)
            ?: throw UsernameNotFoundException("User not found with username: $userName")
        return CustomUserDetails(user)
    }
}
