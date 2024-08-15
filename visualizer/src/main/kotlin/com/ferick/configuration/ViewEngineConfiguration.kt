package com.ferick.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect

@Configuration
class ViewEngineConfiguration {

    @Bean
    fun conditionalCommentDialect(): IDialect {
        return Java8TimeDialect()
    }
}
