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
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface SimpleMapper {

    @Mapping(target = "phones", source = "phoneNumbers")
    PersonDTO personToPersonDTO(Person person);

    @Mapping(target = "phoneNumbers", source = "phones")
    @Mapping(target = "id", ignore = true)
    Person personDTOToPerson(PersonDTO personDTO);

    PhoneDTO phoneToPhoneDTO(Phone phone);
    @Mapping(target = "id", ignore = true)
    Phone phoneDTOToPhone(PhoneDTO phoneDTO);

}
