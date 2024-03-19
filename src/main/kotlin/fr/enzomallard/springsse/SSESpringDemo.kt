package fr.enzomallard.springsse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * This is the entry point of the application.
 *
 * The `DataRetrievalService.kt` file defines an abstract class `DataRetrievalService` that extends `Thread`.
 * This class is responsible for retrieving data.
 * It has methods to register and unregister listeners and stop listeners.
 * The `run` method is overridden to handle stop and error handling.
 * Implementations of this class must implement the `retrieveData` method, which is responsible for retrieving data can notify listeners.
 *
 * The `DataRetrievalManagerService.kt` file defines a service that manages instances of `DataRetrievalService`.
 * It maintains a map of `DataRetrievalService` instances, each representing a data retriever.
 * It has methods to start and stop data retrievers, register a listener, and check if a user is the initiator of a data retriever.
 *
 * The `startDataEmit` method in `DataRetrievalManagerService` creates
 * a new `FakeDataRetrievalService`, adds it to the map of data retrievers, and starts it.
 * It also registers response and stops listeners.
 *
 * The `stopDataEmit` method in `DataRetrievalManagerService` stops a data retriever by interrupting its thread.
 *
 * The `registerListener` method in `DataRetrievalManagerService` returns a `DataSSEEmitter` for a data retriever,
 * which maps data from the associated listener into Server-Sent Events (SSE) to the client.
 *
 * The `isInitiator` method in `DataRetrievalManagerService` checks if a user is the initiator of a data retriever.
 *
 * From the Web Rest Controller, the `DataRetrievalManagerService` methods can be called.
 * Any user can start a data retriever but stopping and listening are only allowed
 * for the initiator of the data retriever or to user with the `DATA_ADMIN` role.
 */
@SpringBootApplication
class SSESpringDemo

fun main(args: Array<String>) {
    runApplication<SSESpringDemo>(*args)
}
