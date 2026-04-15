/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */


package net.littlelite.mapper;

import net.littlelite.dto.PersonDTO;
import net.littlelite.dto.PhoneDTO;
import net.littlelite.model.Person;
import net.littlelite.model.Phone;
import org.mapstruct.Mapper;

@Mapper
public interface SimpleMapper {
    PersonDTO personToPersonDTO(Person person);
    PhoneDTO phoneToPhoneDTO(Phone phone);
}
