/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.controller.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.littlelite.dto.PersonDTO;
import net.littlelite.service.PersonService;

import java.util.List;

@Path("/api/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonController
{
    private final PersonService personService;

    @Inject
    public PersonController(PersonService personService)
    {
        this.personService = personService;
    }

    @GET
    public List<PersonDTO> findAll()
    {
        return this.personService.findAll();
    }

    @POST
    public PersonDTO create(PersonDTO personDTO)
    {
        return this.personService.create(personDTO);
    }

    @PUT
    public PersonDTO update(PersonDTO personDTO)
    {
        return this.personService.update(personDTO);
    }

    @DELETE
    public PersonDTO delete(PersonDTO personDTO)
    {
        return this.personService.delete(personDTO);
    }

    @POST
    @Path("/by-id")
    public PersonDTO findById(PersonDTO personDTO)
    {
        return this.personService.findById(personDTO);
    }
}
