package com.basalt.camp.business.user

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "applicationuser")
class User(@Id val id: UUID, val email: String, val name: String)