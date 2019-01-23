package com.basalt.camp.business.user

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    companion object {
        private val LOG = LoggerFactory.getLogger(UserService::class.java)
    }

    fun save(user: User): User {
        return userRepository.save(user)
    }

    @Cacheable(value = ["user-by-email"])
    fun findByEmail(email: String): User? {
        LOG.info("Finding user {}", email)
        return userRepository.findOneByEmail(email)
    }
}