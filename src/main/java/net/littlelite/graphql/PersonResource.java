/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.graphql;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import jakarta.inject.Inject;
import net.littlelite.dto.PersonDTO;
import net.littlelite.service.PersonService;

import java.util.List;

@GraphQLApi
public class PersonResource
{
    @Inject
    PersonService service;

    @Query("allPersons")
    @Description("Get all persons")
    public List<PersonDTO> getAllPersons()
    {
        return this.service.findAll();
    }
}
