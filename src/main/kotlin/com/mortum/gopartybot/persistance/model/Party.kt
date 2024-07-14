package com.mortum.gopartybot.persistance.model

import jakarta.persistence.*

@Entity
@Table(name = "parties")
class Party(
    @Id
    val id: Long = 0,

    @OneToOne
    var owner: User? = null,

    @Column(name = "name")
    val name: String? = null,
) {

    @OneToMany(fetch = FetchType.EAGER)
    var users: MutableSet<User> = mutableSetOf()

    var finished: Boolean = false

    fun addUserToParty(user: User?) {
        if (user != null) users.add(user)
    }
}