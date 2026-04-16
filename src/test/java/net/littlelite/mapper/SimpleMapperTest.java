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

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

        assertThat(dto, is(notNullValue()));
        assertThat(dto.getName(), is(equalTo(person.getName())));
        assertThat(dto.getSurname(), is(equalTo(person.getSurname())));
        assertThat(dto.getAge(), is(equalTo(person.getAge())));
        assertThat(dto.getPhones(), hasSize(1));

        PhoneDTO phoneDTO = dto.getPhones().iterator().next();
        assertThat(phoneDTO.getPrefix(), is(equalTo(phone.getPrefix())));
        assertThat(phoneDTO.getNumber(), is(equalTo(phone.getNumber())));
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

        assertThat(person, is(notNullValue()));
        assertThat(person.getName(), is(equalTo(dto.getName())));
        assertThat(person.getSurname(), is(equalTo(dto.getSurname())));
        assertThat(person.getAge(), is(equalTo(dto.getAge())));
        assertThat(person.getPhoneNumbers(), hasSize(1));

        Phone phone = person.getPhoneNumbers().iterator().next();
        assertThat(phone.getPrefix(), is(equalTo(phoneDTO.getPrefix())));
        assertThat(phone.getNumber(), is(equalTo(phoneDTO.getNumber())));
    }

    @Test
    public void testPhoneToPhoneDTO()
    {
        Phone phone = new Phone();
        phone.setPrefix("+44");
        phone.setNumber("555555");

        PhoneDTO dto = simpleMapper.phoneToPhoneDTO(phone);

        assertThat(dto, is(notNullValue()));
        assertThat(dto.getPrefix(), is(equalTo(phone.getPrefix())));
        assertThat(dto.getNumber(), is(equalTo(phone.getNumber())));
    }

    @Test
    public void testPhoneDTOToPhone()
    {
        PhoneDTO dto = new PhoneDTO();
        dto.setPrefix("+33");
        dto.setNumber("444444");

        Phone phone = simpleMapper.phoneDTOToPhone(dto);

        assertThat(phone, is(notNullValue()));
        assertThat(phone.getPrefix(), is(equalTo(dto.getPrefix())));
        assertThat(phone.getNumber(), is(equalTo(dto.getNumber())));
    }
}
