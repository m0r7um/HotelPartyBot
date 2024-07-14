package com.mortum.gopartybot.service.logic

import com.mortum.gopartybot.persistance.model.PartyForm
import com.mortum.gopartybot.persistance.model.User
import com.mortum.gopartybot.persistance.repository.PartyFormRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PartyFormService(
    private val partyFormRepository: PartyFormRepository
) {

    fun existsByOwner(user: User): Boolean {
        return partyFormRepository.existsByOwner(user)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun deleteByUser(user: User) {
        partyFormRepository.deleteByOwner(user)
    }

    fun addPartyForm(partyForm: PartyForm) {
        partyFormRepository.save(partyForm)
    }

    fun findByUser(user: User): Optional<PartyForm> {
        return partyFormRepository.findByOwner(user)
    }
}