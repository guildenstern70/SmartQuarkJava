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
import net.littlelite.dto.PhoneDTO;
import net.littlelite.service.PhoneService;

import java.util.List;

@Path("/api/phones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PhoneController
{
    private final PhoneService phoneService;

    @Inject
    public PhoneController(PhoneService phoneService)
    {
        this.phoneService = phoneService;
    }

    @GET
    public List<PhoneDTO> findAll()
    {
        return this.phoneService.findAll();
    }

    @POST
    public PhoneDTO create(PhoneDTO phoneDTO)
    {
        return this.phoneService.create(phoneDTO);
    }

    @PUT
    public PhoneDTO update(PhoneDTO phoneDTO)
    {
        return this.phoneService.update(phoneDTO);
    }

    @DELETE
    public PhoneDTO delete(PhoneDTO phoneDTO)
    {
        return this.phoneService.delete(phoneDTO);
    }

    @POST
    @Path("/by-id")
    public PhoneDTO findById(PhoneDTO phoneDTO)
    {
        return this.phoneService.findById(phoneDTO);
    }
}