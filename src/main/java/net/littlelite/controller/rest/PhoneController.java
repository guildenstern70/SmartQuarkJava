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
import net.littlelite.dto.PhoneDTO;
import net.littlelite.service.PhoneService;

@Path("/api/phones")
public class PhoneController
{
    private final PhoneService phoneService;

    @Inject
    public PhoneController(PhoneService phoneService)
    {
        this.phoneService = phoneService;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findAll()
    {
        var phones = this.phoneService.findAll();
        return Response.ok(phones).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id)
    {
        var phone = this.phoneService.findById(id);
        if (phone == null)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(phone).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(PhoneDTO phoneDTO)
    {
        var created = this.phoneService.create(phoneDTO);
        return Response.ok(created).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(PhoneDTO phoneDTO)
    {
        var updated = this.phoneService.update(phoneDTO);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(PhoneDTO phoneDTO)
    {
        var deleted = this.phoneService.delete(phoneDTO.getId());
        return Response.ok(deleted).build();
    }

}