package com.basalt.camp.mocks.user

import com.basalt.camp.business.cache.CacheRepository
import com.basalt.camp.business.user.UserRepository
import com.basalt.camp.business.user.UserService
import org.mockito.Mockito

class UserServiceMock(userRepository: UserRepository = Mockito.mock(UserRepository::class.java)) : UserService(userRepository, Mockito.mock(CacheRepository::class.java))