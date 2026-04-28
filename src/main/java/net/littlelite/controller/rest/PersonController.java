/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.controller.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.littlelite.dto.PersonDTO;
import net.littlelite.service.PersonService;

import java.net.URI;
import java.util.List;

@Path("/api/persons")
public class PersonController
{
    private final PersonService personService;

    @Inject
    public PersonController(PersonService personService)
    {
        this.personService = personService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll()
    {
        return Response.ok(this.personService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id)
    {
        var found = this.personService.findById(id);
        if (found == null)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(found).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(PersonDTO personDTO)
    {
        var created = this.personService.create(personDTO);
        var location = URI.create(String.format("/api/persons/%d", created.getId()))    ;
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(PersonDTO personDTO)
    {
        var updated = this.personService.update(personDTO);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public PersonDTO delete(@PathParam("id") Long id)
    {
        return this.personService.delete(id);
    }

}
