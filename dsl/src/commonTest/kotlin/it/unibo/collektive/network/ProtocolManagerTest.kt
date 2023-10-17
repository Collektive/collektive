package it.unibo.collektive.network

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.ProtocolManager
import it.unibo.collektive.stack.Path

class ProtocolManagerTest : StringSpec({
    val localID = IntId()
    val recipientID = IntId()
    val protocolManager = ProtocolManager()

    "A protocol manager should be initialized empty" {
        val res = protocolManager.receiveMessage(localID)
        res shouldBe emptyMap()
    }

    "A protocol manager should add a message to the flow" {
        val path = Path(listOf("exchange.1"))
        val value = 1
        val message = Message(localID, recipientID, mapOf(path to value))
        protocolManager.sendMessage(message)
        protocolManager.receiveMessage(recipientID).size shouldBe 1
    }

    "After that a protocol retrieved messages for a recipient it should remove them from the buffer" {
        protocolManager.receiveMessage(recipientID).size shouldBe 0
    }
})
