/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.controller.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.littlelite.config.SmartQuarkConfig;

@Path("/")
public class IndexController
{
    private final SmartQuarkConfig config;
    private final Template index;

    @Inject
    public IndexController(Template index,
                           SmartQuarkConfig config)
    {
        this.config = config;
        this.index = index;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get()
    {
        return this.index.instance()
                .data("version", this.config.version());
    }
}
