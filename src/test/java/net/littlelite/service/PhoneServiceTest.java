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
import net.littlelite.model.Phone;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class PhoneServiceTest
{
    @Inject
    PhoneService phoneService;

    @Test
    public void testUpdateThrowsNotFoundWhenPhoneDoesNotExist()
    {
        PhoneDTO input = new PhoneDTO();
        input.setId(Long.MAX_VALUE);
        input.setPrefix("+39");
        input.setNumber("000000000");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> phoneService.update(input));

        assertThat(exception.getMessage(), is(equalTo("Phone not found: " + Long.MAX_VALUE)));
    }

    @Test
    public void testDeleteThrowsNotFoundWhenPhoneDoesNotExist()
    {
        PhoneDTO input = new PhoneDTO();
        input.setId(Long.MAX_VALUE);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> phoneService.delete(input));

        assertThat(exception.getMessage(), is(equalTo("Phone not found: " + Long.MAX_VALUE)));
    }

    @Test
    @TestTransaction
    public void testCreateAndFindByIdPersistsPhoneAndLinksPerson()
    {
        Long personId = getAnyExistingPersonId();

        PhoneDTO input = new PhoneDTO();
        input.setPrefix("+39");
        input.setNumber("555555555");
        input.setPerson(buildPersonDTO(personId));

        PhoneDTO created = phoneService.create(input);

        assertThat(created, is(notNullValue()));
        assertThat(created.getId(), is(notNullValue()));
        assertThat(created.getPrefix(), is(equalTo("+39")));
        assertThat(created.getNumber(), is(equalTo("555555555")));

        PhoneDTO lookup = new PhoneDTO();
        lookup.setId(created.getId());
        PhoneDTO found = phoneService.findById(lookup);

        assertThat(found, is(notNullValue()));
        assertThat(found.getId(), is(equalTo(created.getId())));
        assertThat(found.getPrefix(), is(equalTo(created.getPrefix())));
        assertThat(found.getNumber(), is(equalTo(created.getNumber())));

        Phone persisted = Phone.findById(created.getId());
        assertThat(persisted, is(notNullValue()));
        assertThat(persisted.getPerson(), is(notNullValue()));
        assertThat(persisted.getPerson().getId(), is(equalTo(personId)));
    }

    @Test
    @TestTransaction
    public void testFindAllContainsCreatedPhone()
    {
        Long personId = getAnyExistingPersonId();

        PhoneDTO input = new PhoneDTO();
        input.setPrefix("+39");
        input.setNumber("666666666");
        input.setPerson(buildPersonDTO(personId));

        PhoneDTO created = phoneService.create(input);

        List<PhoneDTO> phones = phoneService.findAll();

        assertThat(phones.stream().map(PhoneDTO::getId).toList(), hasItem(created.getId()));
    }

    @Test
    @TestTransaction
    public void testUpdateChangesPhoneFieldsAndPerson()
    {
        List<Person> persons = Person.listAll();
        assertThat(persons.size() >= 2, is(true));
        Long oldPersonId = persons.get(0).getId();
        Long newPersonId = persons.get(1).getId();

        PhoneDTO createInput = new PhoneDTO();
        createInput.setPrefix("+39");
        createInput.setNumber("777777777");
        createInput.setPerson(buildPersonDTO(oldPersonId));
        PhoneDTO created = phoneService.create(createInput);

        PhoneDTO updateInput = new PhoneDTO();
        updateInput.setId(created.getId());
        updateInput.setPrefix("+1");
        updateInput.setNumber("888888888");
        updateInput.setPerson(buildPersonDTO(newPersonId));

        PhoneDTO updated = phoneService.update(updateInput);

        assertThat(updated.getId(), is(equalTo(created.getId())));
        assertThat(updated.getPrefix(), is(equalTo("+1")));
        assertThat(updated.getNumber(), is(equalTo("888888888")));

        Phone persisted = Phone.findById(created.getId());
        assertThat(persisted, is(notNullValue()));
        assertThat(persisted.getPrefix(), is(equalTo("+1")));
        assertThat(persisted.getNumber(), is(equalTo("888888888")));
        assertThat(persisted.getPerson(), is(notNullValue()));
        assertThat(persisted.getPerson().getId(), is(equalTo(newPersonId)));
    }

    @Test
    @TestTransaction
    public void testDeleteRemovesPhone()
    {
        Long personId = getAnyExistingPersonId();

        PhoneDTO createInput = new PhoneDTO();
        createInput.setPrefix("+44");
        createInput.setNumber("999999999");
        createInput.setPerson(buildPersonDTO(personId));
        PhoneDTO created = phoneService.create(createInput);

        PhoneDTO deleteInput = new PhoneDTO();
        deleteInput.setId(created.getId());

        PhoneDTO deleted = phoneService.delete(deleteInput);

        assertThat(deleted, is(notNullValue()));
        assertThat(deleted.getId(), is(equalTo(created.getId())));
        assertThat(Phone.findById(created.getId()), is(nullValue()));
    }

    private Long getAnyExistingPersonId()
    {
        Person person = Person.find("order by id").firstResult();
        assertThat(person, is(notNullValue()));
        return person.getId();
    }

    private PersonDTO buildPersonDTO(Long personId)
    {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(personId);
        return personDTO;
    }
}





