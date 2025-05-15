package com.github.vinola.dockeruibackend.controller

import com.github.vinola.dockeruibackend.dto.ContainerDTO
import com.github.vinola.dockeruibackend.service.DockerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/docker")
class DockerController(
    private val dockerService: DockerService
) {

    @GetMapping("/containers")
    fun getContainers(): List<ContainerDTO> {
        return dockerService.listContainers()
    }

    @PostMapping("/{id}/start")
    fun startContainer(@PathVariable id: String): ResponseEntity<String> {
        return try {
            dockerService.startContainer(id)
            ResponseEntity.ok("Container started successfully")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error starting container: ${e.message}")
        }
    }

    @PostMapping("/{id}/stop")
    fun stopContainer(@PathVariable id: String) : ResponseEntity<String> {
        return try {
            dockerService.stopContainer(id)
            ResponseEntity.ok("Container stopped successfully")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error stopping container: ${e.message}")
        }
    }

    @PostMapping("/{id}/restart")
    fun restartContainer(@PathVariable id: String) : ResponseEntity<String> {
        return try {
            dockerService.restartContainer(id)
            ResponseEntity.ok("Container restarted successfully")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error restarting container: ${e.message}")
        }
    }

    @PostMapping("/{id}/rename/{newName}")
    fun renameContainer(@PathVariable id: String, @PathVariable newName: String) : ResponseEntity<String> {
        return try {
            dockerService.renameContainer(id, newName)
            ResponseEntity.ok("Container renamed successfully")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error renaming container: ${e.message}")
        }
    }

    @GetMapping("/{id}/logs")
    fun getContainerLogs(@PathVariable id: String): ResponseEntity<String> {
        return try {
            val logs = dockerService.getContainerLogs(id)
            ResponseEntity.ok(logs)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching logs: ${e.message}")
        }
    }

}
