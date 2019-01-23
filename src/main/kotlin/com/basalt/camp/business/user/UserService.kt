package com.basalt.camp.business.user

import com.basalt.camp.business.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository, private val cacheManager: CacheRepository) {
    companion object {
        private val LOG = LoggerFactory.getLogger(UserService::class.java)
    }

    fun save(user: User): User {
        val savedUser = userRepository.save(user)
        cacheManager.add("USER_${user.email}", savedUser)
        return savedUser
    }

    fun findByEmail(email: String): User? {
        val userKey = "USER_$email"
        val cachedUser = cacheManager.get(userKey, User::class.java)
        return if (cachedUser != null) {
            LOG.info("User {} from cache", email)
            cachedUser
        } else {
            LOG.info("Finding user {}", email)
            val user = userRepository.findOneByEmail(email)
            user?.let { cacheManager.add(userKey, it) }
            user
        }
    }
}