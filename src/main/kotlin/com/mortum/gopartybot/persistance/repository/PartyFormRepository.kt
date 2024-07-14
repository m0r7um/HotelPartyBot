package com.mortum.gopartybot.persistance.repository

import com.mortum.gopartybot.persistance.model.PartyForm
import com.mortum.gopartybot.persistance.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PartyFormRepository : JpaRepository<PartyForm, Long> {
    fun existsByOwner(user: User): Boolean

    fun deleteByOwner(user: User)

    fun findByOwner(user: User): Optional<PartyForm>
}