package com.mortum.gopartybot.service.logic

import com.mortum.gopartybot.persistance.model.Party
import com.mortum.gopartybot.persistance.model.User
import com.mortum.gopartybot.persistance.repository.PartyRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class PartyService(
    private val partyRepository: PartyRepository
) {
    fun addParty(party: Party) {
        partyRepository.save(party)
    }

    fun findById(id: Long): Optional<Party> {
        return partyRepository.findById(id)
    }

    fun existsByOwner(user: User): Boolean {
        return partyRepository.existsByOwner(user)
    }

    fun findPartiesByFinished(finished: Boolean): MutableList<Party> {
        return partyRepository.findPartiesByFinished(finished)
    }

    @Transactional
    fun updatePartyFinishedById(id: Long, finished: Boolean) {
        return partyRepository.updatePartyFinishedById(id, finished)
    }
}