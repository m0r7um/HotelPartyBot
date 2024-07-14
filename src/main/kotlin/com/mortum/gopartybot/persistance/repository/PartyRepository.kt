package com.mortum.gopartybot.persistance.repository

import com.mortum.gopartybot.persistance.model.Party
import com.mortum.gopartybot.persistance.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PartyRepository : JpaRepository<Party, Long> {
    fun existsByOwner(user: User): Boolean

    fun findPartiesByFinished(boolean: Boolean): MutableList<Party>

    @Modifying
    @Query("UPDATE Party SET finished = :finished WHERE id = :id")
    fun updatePartyFinishedById(@Param("id") id: Long, @Param("finished") finished: Boolean)
}