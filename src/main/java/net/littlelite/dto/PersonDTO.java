/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PersonDTO
{
    protected Long id;

    protected String name;
    protected String surname;
    protected int age;
    protected Set<PhoneDTO> phones;
}
