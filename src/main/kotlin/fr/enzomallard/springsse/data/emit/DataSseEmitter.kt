package fr.enzomallard.springsse.data.emit

import fr.enzomallard.springsse.data.retrieve.DataRetrievalService
import fr.enzomallard.springsse.model.Data
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

/**
 * A SseEmitter that emits data from a DataRetrievalService
 */
class DataSseEmitter(dataEmitter: DataRetrievalService<Data>) : SseEmitter() {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DataSseEmitter::class.java)
    }

    init {
        val unsub = dataEmitter.flux
            .doOnError {
                LOGGER.error("An error occurred", it)
                completeWithError(it)
            }.doOnComplete {
                complete()
            }.subscribe { data ->
                val event: SseEventBuilder = event()
                    .data(data, MediaType.APPLICATION_JSON)
                    .name("ValueEmitted")
                try {
                    send(event)
                } catch (ioException: IOException) {
                    LOGGER.debug("Unable to send message")
                }
            }

        onCompletion {
            unsub.dispose()
        }
    }
}