package com.mortum.gopartybot.persistance.model

import com.mortum.gopartybot.steps.Step
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.telegram.telegrambots.meta.api.objects.User

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
class User(
    @Id
    val id: Long = 0,

    private val tag: String? = null,

    @Column(name = "user_name")
    private val userName: String? = null,

    var step: Step = Step.COMMON_MESSAGE,

    @Column(name = "number", unique = true)
    val number: Int? = null,
    val chatId: Long? = 0
) {

    constructor(user: User, chatId: Long?) : this(
        user.id,
        user.userName,
        "${user.firstName} ${user.lastName ?: ""}".trim(),
        Step.COMMON_MESSAGE,
        chatId = chatId
    )

    @ManyToOne
    var party: Party? = null

}