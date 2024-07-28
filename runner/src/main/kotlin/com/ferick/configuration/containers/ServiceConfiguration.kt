package com.ferick.configuration.containers

import com.ferick.containers.RunnerKafkaContainer
import com.ferick.containers.RunnerMongoContainer
import com.ferick.containers.VisualizerContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.Network
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths

@Configuration
class ServiceConfiguration(
    private val network: Network,
    private val kafkaContainer: RunnerKafkaContainer
) {

    private val rootPath = Paths.get("").toAbsolutePath().parent

    @Bean
    fun visualizer(mongoContainer: RunnerMongoContainer): VisualizerContainer = VisualizerContainer(
        ImageFromDockerfile("local-visualizer:run-v1").withDockerfile(
            rootPath.resolve("visualizer/build/docker/Dockerfile")
        ),
        network,
        kafkaContainer,
        mongoContainer
    )
}
