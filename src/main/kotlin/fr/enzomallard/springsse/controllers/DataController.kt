package fr.enzomallard.springsse.controllers

import fr.enzomallard.springsse.data.DataManagerService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/data/")
class DataController(
    private val dataManagerService: DataManagerService
) {
    @PostMapping("{id}/start")
    fun start(@PathVariable id: String, principal: Principal): ResponseEntity<Any> =
        if(dataManagerService.startDataEmit(id, principal.name))
            ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()


    /**
     * Stop a data retriever
     * @param id the id of the data retriever
     * @param principal the connected user
     *
     */
    @PostMapping("{id}/stop")
    @PreAuthorize("hasRole('DATA_ADMIN') or @dataManagerService.isInitiator(#id, #principal.name)")
    fun stop(@PathVariable id: String, principal: Principal): ResponseEntity<Any> =
        if (dataManagerService.stopDataEmit(id))
            ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()

    @GetMapping("{id}/listen")
    @PreAuthorize("hasRole('DATA_ADMIN') or @dataManagerService.isInitiator(#id, #principal.name)")
    fun listen(@PathVariable id: String, principal: Principal) =
        dataManagerService.registerListener(id, principal.name)
}