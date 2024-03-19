package fr.enzomallard.springsse.data

import fr.enzomallard.springsse.data.emit.DataSseEmitter
import fr.enzomallard.springsse.data.retrieve.DataRetrievalService
import fr.enzomallard.springsse.data.retrieve.FakeDataRetrievalService
import fr.enzomallard.springsse.model.Data
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/**
 * The DataRetrievalManagerService class manages data retrievers.
 * The class maintains a map of DataRetrievalService instances, each representing a data retriever.
 */
@Service
class DataManagerService {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DataManagerService::class.java)
    }

    private val dataRetrievers: MutableMap<String, DataRetrievalService<Data>> = mutableMapOf()

    /**
     * Start a data retriever
     * @param id the id of the data retriever
     * @param username the username of the user that starts the data retriever
     * @return false if the data retriever already exists, true otherwise
     */
    fun startDataEmit(id: String, username: String): Boolean {
        if(id in dataRetrievers) return false
        // Create a thread that will create some data
        val service = FakeDataRetrievalService(id, username)
        dataRetrievers[id] = service
        service.flux.doOnComplete {
            LOGGER.info("DataRetrievalService $id $username completed")
            dataRetrievers.remove(id)
        }.subscribe()
        service.start()
        return true
    }

    /**
     * Stop a data retriever
     * @param id the id of the data retriever
     * @return true if the data retriever was stopped,
     * false if the data retriever does not exist or is already stopped/finished
     */
    fun stopDataEmit(@PathVariable id: String) = dataRetrievers[id]
        ?.takeUnless { !it.isAlive || it.isInterrupted }
        ?.apply { interrupt() } != null

    /**
     * Register a listener, each listener will receive
     * the data emitted by the data retriever
     */
    fun registerListener(id: String, name: String) = dataRetrievers[id]
        ?.let(::DataSseEmitter)
        // If the data retriever does not exist, just return a completed emitter
        ?: SseEmitter().apply { complete() }

    /**
     * Check if the user is the initiator of the data retriever
     * @param id the id of the data retriever
     * @param username the username of user to check
     * @return true if the user is the initiator of the data retriever
     */
    fun isInitiator(id: String, username: String) =
        dataRetrievers[id]?.initiator == username
}
