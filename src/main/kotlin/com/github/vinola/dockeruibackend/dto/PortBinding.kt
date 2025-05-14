package com.github.vinola.dockeruibackend.dto

data class PortBinding(
    val ip: String?,
    val privatePort: Int?,
    val publicPort: Int?,
    val type: String?
)