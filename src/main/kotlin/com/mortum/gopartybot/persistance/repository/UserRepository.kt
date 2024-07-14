package com.mortum.gopartybot.persistance.repository

import com.mortum.gopartybot.persistance.model.Party
import com.mortum.gopartybot.persistance.model.User
import com.mortum.gopartybot.steps.Step
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    @Modifying
    @Query("UPDATE User SET step = :step WHERE id = :id")
    fun updateUserStepById(@Param("id") id: Long, @Param("step") step: Step)

    @Modifying
    @Query("UPDATE User SET number = :number WHERE id = :id")
    fun updateUserNumberById(@Param("id") id: Long, @Param("number") number: Int)

    @Modifying
    @Query("UPDATE User SET party = :party WHERE id = :id")
    fun updateUserPartyById(@Param("id") id: Long, @Param("party") party: Party)
}