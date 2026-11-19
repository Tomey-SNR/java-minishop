package com.codecool.minishop.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final List<String> REQUIRED_PROPERTIES = List.of(
            "DB_URL",
            "DB_USERNAME",
            "DB_PASSWORD",
            "ADMIN_USERNAME",
            "ADMIN_EMAIL",
            "ADMIN_PASSWORD"
    );

    @Override
    public void postProcessEnvironment(
            @NonNull ConfigurableEnvironment environment,
            @NonNull SpringApplication application) {

        Path envFile = Path.of(".env").toAbsolutePath().normalize();

        if (!Files.isRegularFile(envFile)) {
            throw new IllegalStateException(
                    "MiniShop cannot start: .env file was not found at "
                            + envFile
                            + ". Copy .env.example to .env and configure your settings."
            );
        }

        Dotenv dotenv = Dotenv.configure()
                .directory(envFile.getParent().toString())
                .filename(".env")
                .load();

        for (String property : REQUIRED_PROPERTIES) {
            String value = dotenv.get(property);

            if (value == null || value.isBlank()) {
                throw new IllegalStateException(
                        "MiniShop cannot start: missing or empty "
                                + property
                                + " in .env file."
                );
            }
        }

        Map<String, Object> properties = new HashMap<>();

        dotenv.entries().forEach(entry ->
                properties.put(entry.getKey(), entry.getValue())
        );

        environment.getPropertySources().addFirst(
                new MapPropertySource("dotenv", properties)
        );
    }
}
