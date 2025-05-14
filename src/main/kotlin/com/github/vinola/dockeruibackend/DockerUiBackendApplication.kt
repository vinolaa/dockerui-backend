package com.github.vinola.dockeruibackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DockerUiBackendApplication

fun main(args: Array<String>) {
    runApplication<DockerUiBackendApplication>(*args)
}
