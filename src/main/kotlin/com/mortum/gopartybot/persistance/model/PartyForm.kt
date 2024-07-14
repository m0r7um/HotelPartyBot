package com.mortum.gopartybot.persistance.model;

import jakarta.persistence.*

@Entity
@Table(name = "party_form")
class PartyForm(
    @OneToOne
    val owner: User? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0
}
