/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.dto;

import lombok.Data;

@Data
public class PhoneDTO
{
    protected Long id;

    protected String prefix;
    protected String number;
    protected PersonDTO person;
}
