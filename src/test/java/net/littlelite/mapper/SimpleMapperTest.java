/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.mapper;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.littlelite.dto.PersonDTO;
import net.littlelite.dto.PhoneDTO;
import net.littlelite.model.Person;
import net.littlelite.model.Phone;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SimpleMapperTest
{
    @Inject
    SimpleMapper simpleMapper;

    @Test
    public void testPersonToPersonDTO()
    {
        Person person = new Person();
        person.setName("John");
        person.setSurname("Doe");
        person.setAge(30);

        Phone phone = new Phone();
        phone.setPrefix("+39");
        phone.setNumber("123456789");
        phone.setPerson(person);

        person.setPhoneNumbers(Set.of(phone));

        PersonDTO dto = simpleMapper.personToPersonDTO(person);

        assertNotNull(dto);
        assertEquals(person.getName(), dto.getName());
        assertEquals(person.getSurname(), dto.getSurname());
        assertEquals(person.getAge(), dto.getAge());
        assertEquals(1, dto.getPhones().size());

        PhoneDTO phoneDTO = dto.getPhones().iterator().next();
        assertEquals(phone.getPrefix(), phoneDTO.getPrefix());
        assertEquals(phone.getNumber(), phoneDTO.getNumber());
    }

    @Test
    public void testPersonDTOToPerson()
    {
        PersonDTO dto = new PersonDTO();
        dto.setName("Jane");
        dto.setSurname("Smith");
        dto.setAge(25);

        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setPrefix("+1");
        phoneDTO.setNumber("987654321");
        dto.setPhones(Set.of(phoneDTO));

        Person person = simpleMapper.personDTOToPerson(dto);

        assertNotNull(person);
        assertEquals(dto.getName(), person.getName());
        assertEquals(dto.getSurname(), person.getSurname());
        assertEquals(dto.getAge(), person.getAge());
        assertEquals(1, person.getPhoneNumbers().size());

        Phone phone = person.getPhoneNumbers().iterator().next();
        assertEquals(phoneDTO.getPrefix(), phone.getPrefix());
        assertEquals(phoneDTO.getNumber(), phone.getNumber());
    }

    @Test
    public void testPhoneToPhoneDTO()
    {
        Phone phone = new Phone();
        phone.setPrefix("+44");
        phone.setNumber("555555");

        PhoneDTO dto = simpleMapper.phoneToPhoneDTO(phone);

        assertNotNull(dto);
        assertEquals(phone.getPrefix(), dto.getPrefix());
        assertEquals(phone.getNumber(), dto.getNumber());
    }

    @Test
    public void testPhoneDTOToPhone()
    {
        PhoneDTO dto = new PhoneDTO();
        dto.setPrefix("+33");
        dto.setNumber("444444");

        Phone phone = simpleMapper.phoneDTOToPhone(dto);

        assertNotNull(phone);
        assertEquals(dto.getPrefix(), phone.getPrefix());
        assertEquals(dto.getNumber(), phone.getNumber());
    }
}
