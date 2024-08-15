package com.ferick.configuration.containers

import com.ferick.containers.RunnerKafkaContainer
import com.ferick.containers.RunnerMongoContainer
import com.ferick.containers.ServiceContainer
import com.ferick.containers.VisualizerContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths

@Configuration
class ServiceConfiguration(
    private val network: Network,
    private val kafkaContainer: RunnerKafkaContainer
) {

    private val rootPath = Paths.get("").toAbsolutePath()

    @Bean
    fun visualizer(mongoContainer: RunnerMongoContainer): VisualizerContainer = VisualizerContainer(
        ImageFromDockerfile("local-visualizer:run-v1").withDockerfile(
            rootPath.resolve("visualizer/build/docker/Dockerfile")
        ),
        network,
        kafkaContainer,
        mongoContainer
    ).also { it.start() }

    @Bean
    fun receiver(transformer: ServiceContainer): ServiceContainer = ServiceContainer(
        ImageFromDockerfile("local-receiver:run-v1").withDockerfile(
            rootPath.resolve("receiver/build/docker/Dockerfile")
        ),
        network,
        "receiver",
        kafkaContainer
    )
        .dependsOn(transformer)
        .also { it.start() }

    @Bean
    fun transformer(visualizer: VisualizerContainer): ServiceContainer = ServiceContainer(
        ImageFromDockerfile("local-transformer:run-v1").withDockerfile(
            rootPath.resolve("transformer/build/docker/Dockerfile")
        ),
        network,
        "transformer",
        kafkaContainer
    )
        .dependsOn(visualizer)
        .waitingFor(Wait.forLogMessage(".*Adding newly assigned partitions: input-topic-0.*", 1))
        .also { it.start() }
}
