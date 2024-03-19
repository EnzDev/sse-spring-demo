package fr.enzomallard.springsse.data.retrieve

import fr.enzomallard.springsse.model.Data
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Sinks
import kotlin.random.Random

/**
 * A service that generates random data and sends it to listeners
 */
class FakeDataRetrievalService(
    private val id: String,
    initiator: String
) : DataRetrievalService<Data>(initiator) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(FakeDataRetrievalService::class.java)
    }

    override fun retrieveData(sink: Sinks.Many<Data>) {
        LOGGER.info("Starting DataRetrievalService $id $initiator")
        try {
            // Send between 5 and 100 messages at random intervals
            val max = Random.nextInt(5, 10) * id.length
            for (i in 1..max) {
                val nextWait = Random.nextLong(100, 1000)
                val myData = Data("Event $id: $i/$max", id, initiator, nextWait)
                LOGGER.debug("Sending message {} {}: {}", id, initiator, myData)
                sink.tryEmitNext(myData)
                    .orThrowWithCause(InterruptedException("Sink closed"))
                sleep(nextWait)
            }
        } catch (e: InterruptedException) {
            LOGGER.info("Early termination of thread $id $initiator")
        }
    }
}