package com.basalt.camp.api.reservation

import com.basalt.camp.api.BasePayload

class VacancyPayload(val vacancyList: List<VacancyItem>): BasePayload()