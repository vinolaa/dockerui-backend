package com.github.vinola.dockeruibackend.dto

data class ContainerDTO(
    val id: String,
    val name: String,
    val image: String,
    val state: String,
    val status: String,
    val ports: List<PortBinding>
)