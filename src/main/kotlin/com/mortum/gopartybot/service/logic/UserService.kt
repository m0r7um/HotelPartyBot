package com.mortum.gopartybot.service.logic

import com.mortum.gopartybot.persistance.model.Party
import com.mortum.gopartybot.persistance.model.User
import com.mortum.gopartybot.persistance.repository.UserRepository
import com.mortum.gopartybot.steps.Step
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val repository: UserRepository
) {
    fun addUser(user: User) {
        repository.save(user)
    }

    fun existsById(id: Long): Boolean {
        return repository.existsById(id)
    }

    @Transactional
    fun updateStepById(id: Long, step: Step) {
        repository.updateUserStepById(id, step)
    }

    fun findById(id: Long): Optional<User> {
        return repository.findById(id)
    }

    @Transactional
    fun updateUserNumberById(id: Long, number: Int) {
        repository.updateUserNumberById(id, number)
    }

    @Transactional
    fun updateUserPartyById(id: Long, party: Party) {
        repository.updateUserPartyById(id, party)
    }
}