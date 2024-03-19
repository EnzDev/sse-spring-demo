package fr.enzomallard.springsse.data.retrieve

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

/**
 * A service that generates random data and sends it to listeners
 */
abstract class DataRetrievalService<T>(val initiator: String) : Thread() {
    private val sink = Sinks.many().replay().all<T>()
    val flux: Flux<T> = sink.asFlux()

    override fun run() {
        try {
            retrieveData(sink)
        } catch (e: Exception) {
            LOGGER.error("An error occurred", e)
            sink.tryEmitError(e)
        } finally {
            sink.tryEmitComplete()
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DataRetrievalService::class.java)
    }

    abstract fun retrieveData(sink: Sinks.Many<T>)
}