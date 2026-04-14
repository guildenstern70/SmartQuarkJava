/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "smartquark")
public interface SmartQuarkConfig
{
    @WithDefault("unknown")
    String version();

    @WithDefault("SmartQuark Java Edition")
    String name();
}
