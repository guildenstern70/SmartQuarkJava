/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.service;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import net.littlelite.dto.PersonDTO;
import net.littlelite.dto.PhoneDTO;
import net.littlelite.model.Person;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class PersonServiceTest
{
    @Inject
    PersonService personService;

    @Test
    public void testUpdateThrowsNotFoundWhenPersonDoesNotExist()
    {
        PersonDTO input = new PersonDTO();
        input.setId(Long.MAX_VALUE);
        input.setName("Nobody");
        input.setSurname("NeverFound");
        input.setAge(99);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> personService.update(input));

        assertThat(exception.getMessage(), is(equalTo("Person not found: " + Long.MAX_VALUE)));
    }

    @Test
    public void testDeleteThrowsNotFoundWhenPersonDoesNotExist()
    {
        PersonDTO input = new PersonDTO();
        input.setId(Long.MAX_VALUE);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> personService.delete(input));

        assertThat(exception.getMessage(), is(equalTo("Person not found: " + Long.MAX_VALUE)));
    }

    @Test
    @TestTransaction
    public void testCreatePersistsAllThreePhoneNumbersAndLinksPhonesToPerson()
    {
        PersonDTO input = new PersonDTO();
        input.setName("ServiceTest");
        input.setSurname("ThreePhones");
        input.setAge(42);
        input.setPhones(Set.of(
                buildPhone("111111111"),
                buildPhone("222222222"),
                buildPhone("333333333")
        ));

        PersonDTO created = personService.create(input);

        assertThat(created, is(notNullValue()));
        assertThat(created.getId(), is(notNullValue()));
        assertThat(created.getPhones(), hasSize(3));

        Person persisted = Person.findById(created.getId());
        assertThat(persisted, is(notNullValue()));
        assertThat(persisted.getPhoneNumbers(), hasSize(3));

        Set<String> expectedPhones = Set.of(
                "+39-111111111",
                "+39-222222222",
                "+39-333333333"
        );
        Set<String> persistedPhones = persisted.getPhoneNumbers()
                .stream()
                .map(phone -> phone.getPrefix() + "-" + phone.getNumber())
                .collect(Collectors.toSet());

        assertThat(persistedPhones, is(equalTo(expectedPhones)));

        persisted.getPhoneNumbers().forEach(phone ->
        {
            assertThat(phone.getPerson(), is(notNullValue()));
            assertThat(phone.getPerson().getId(), is(equalTo(created.getId())));
        });
    }

    private PhoneDTO buildPhone(String number)
    {
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setPrefix("+39");
        phoneDTO.setNumber(number);
        return phoneDTO;
    }
}


