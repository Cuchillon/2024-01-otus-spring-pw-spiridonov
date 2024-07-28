package com.ferick.containers

interface ContainerUrl {
    fun getLocalUrl(): String
    fun getDockerNetworkUrl(): String
}
