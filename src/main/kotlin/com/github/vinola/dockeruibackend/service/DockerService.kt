package com.github.vinola.dockeruibackend.service

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallbackTemplate
import com.github.dockerjava.api.model.Frame
import com.github.vinola.dockeruibackend.dto.ContainerDTO
import com.github.vinola.dockeruibackend.dto.PortBinding
import org.springframework.stereotype.Service
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import java.nio.charset.StandardCharsets

@Service
class DockerService{

    private val dockerClient: DockerClient

    init {
        val standard = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build()

        val httpClient = ApacheDockerHttpClient.Builder()
            .dockerHost(standard.dockerHost)
            .sslConfig(standard.sslConfig)
            .build()

        dockerClient = DockerClientImpl.getInstance(standard, httpClient)
    }

    fun listContainers(): List<ContainerDTO> {
        return try {
            dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .map { container ->
                    ContainerDTO(
                        id = container.id,
                        name = container.names.firstOrNull() ?: "N/A",
                        image = container.image,
                        state = container.state,
                        status = container.status,
                        ports = container.ports.map {
                            PortBinding(
                                ip = it.ip,
                                privatePort = it.privatePort,
                                publicPort = it.publicPort,
                                type = it.type
                            )
                        }
                    )
                }
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao listar containers: ${ex.message}", ex)
        }
    }

    fun startContainer(containerId: String) {
        try {
            dockerClient.startContainerCmd(containerId).exec()
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao iniciar container: ${ex.message}", ex)
        }
    }

    fun stopContainer(containerId: String) {
        try {
            dockerClient.stopContainerCmd(containerId).exec()
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao parar container: ${ex.message}", ex)
        }
    }

    fun renameContainer(containerId: String, newName: String) {
        try {
            if (newName.length < 3) {
                throw IllegalArgumentException("O novo nome deve ter pelo menos 3 caracteres.")
            }
            dockerClient.renameContainerCmd(containerId)
                .withName(newName)
                .exec()
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao renomear container: ${ex.message}", ex)
        }
    }

    fun restartContainer(containerId: String) {
        try {
            dockerClient.restartContainerCmd(containerId).exec()
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao reiniciar container: ${ex.message}", ex)
        }
    }

    fun getContainerLogs(containerId: String): String {
        return try {
            val logBuilder = StringBuilder()

            val callback = object : ResultCallbackTemplate<Nothing, Frame>() {
                override fun onNext(frame: Frame) {
                    logBuilder.append(String(frame.payload, StandardCharsets.UTF_8))
                }
            }

            dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withTimestamps(true)
                .withTailAll()
                .exec(callback)
                .awaitCompletion()

            if (logBuilder.isEmpty()) {
                "Sem logs disponíveis."
            } else {
                logBuilder.toString()
            }
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao obter logs do container: ${ex.message}", ex)
        }
    }

}
