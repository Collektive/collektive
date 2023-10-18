package it.unibo.collektive.network

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.NetworkManager
import it.unibo.collektive.stack.Path

class NetworkTest : StringSpec({
    val senderID1 = IntId()
    val senderID2 = IntId()
    val recipientID = IntId()
    val network = NetworkManager()

    "A network messages buffer should be initialized empty" {
        val res = network.receive(recipientID)
        res shouldBe emptyMap()
    }

    "A network should add a list of messages to the buffer" {
        val path = Path(listOf("exchange.1"))
        val path2 = Path(listOf("exchange.2"))
        val value = 1
        val message = Message(senderID1, recipientID, mapOf(path to value))
        val message2 = Message(senderID2, recipientID, mapOf(path2 to value))
        network.send(listOf(message, message2))
        network.receive(recipientID).size shouldBe 2
    }

    "After that the network retrieved messages for a recipient it should remove them from the buffer" {
        network.receive(recipientID) shouldBe emptyMap()
    }

    "A network should keep trace of connected device, at the beginning should be empty" {
        network.getDevices() shouldBe emptyList()
    }

    "A network should be able to add the devices to a list" {
        network.connectDevice(senderID1)
        network.connectDevice(senderID2)
        network.getDevices().size shouldBe 2
    }

    "A network should be able to remove devices from the list" {
        network.disconnectDevice(senderID1)
        network.getDevices().size shouldBe 1
    }
})
