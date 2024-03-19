package fr.enzomallard.springsse.model

import java.util.*

data class Data(
    val value: String,
    val id: String,
    val initiator: String,
    val nextWait: Long,
    val uuid: UUID = UUID.randomUUID()
)
