/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */


package net.littlelite.service;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.littlelite.config.SmartQuarkConfig;
import net.littlelite.utils.TitleLogger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
@Slf4j
public class StartupService
{
    private final SmartQuarkConfig config;
    private final DBInitializer dbInitializer;

    @ConfigProperty(name = "quarkus.http.port")
    int port;

    @Inject
    public StartupService(SmartQuarkConfig config,
                          DBInitializer dbInitializer)
    {
        this.config = config;
        this.dbInitializer = dbInitializer;
    }

    void onStart(@Observes StartupEvent ev)
    {
        List<String> info = List.of(
                "SmartQuark Java Edition v." + config.version(),
                "JVM: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"),
                "Listening on: http://localhost:" + port
        );
        TitleLogger.getInstance().logTitle(log, info);
        this.dbInitializer.populateDB();
    }

    void onStop(@Observes ShutdownEvent ev)
    {
        log.info("UIBM Common API is stopping...");
    }
}
