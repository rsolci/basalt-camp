package com.basalt.camp.mocks.reservation

import com.basalt.camp.business.reservation.ReservationRepository
import com.basalt.camp.business.reservation.ReservationService
import com.basalt.camp.business.user.UserService
import com.basalt.camp.mocks.cache.CacheRepositoryMock
import com.basalt.camp.mocks.user.UserServiceMock
import org.mockito.Mockito

class ReservationServiceMock(userService: UserService = UserServiceMock(),
                             reservationRepository: ReservationRepository = Mockito.mock(ReservationRepository::class.java)) :
    ReservationService(userService, reservationRepository, CacheRepositoryMock())