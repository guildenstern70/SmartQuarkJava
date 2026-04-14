/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */


package net.littlelite;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@QuarkusMain
@Slf4j
public class Main implements QuarkusApplication
{
    @Override
    public int run(String... args)
    {
        log.info("Running SmartQuark Java Edition");
        Quarkus.waitForExit();
        return 0;
    }
}