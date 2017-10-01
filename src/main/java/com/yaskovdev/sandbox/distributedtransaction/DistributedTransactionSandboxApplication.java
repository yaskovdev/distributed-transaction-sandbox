package com.yaskovdev.sandbox.distributedtransaction;

import io.nflow.engine.internal.config.EngineConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(EngineConfiguration.class)
public class DistributedTransactionSandboxApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DistributedTransactionSandboxApplication.class, args);
    }
}
