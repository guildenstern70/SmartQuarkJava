/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import net.littlelite.dto.PersonDTO;
import net.littlelite.mapper.SimpleMapper;
import net.littlelite.model.Person;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class PersonService
{
    private final SimpleMapper simpleMapper;

    @Inject
    public PersonService(SimpleMapper simpleMapper)
    {
        this.simpleMapper = simpleMapper;
    }

    public List<PersonDTO> findAll()
    {
        return Person.<Person>listAll()
                .stream()
                .map(simpleMapper::personToPersonDTO)
                .toList();
    }

    public PersonDTO findById(PersonDTO personDTO)
    {
        Long id = getRequiredId(personDTO);
        Person person = getRequiredPerson(id);
        return simpleMapper.personToPersonDTO(person);
    }

    @Transactional
    public PersonDTO create(PersonDTO personDTO)
    {
        Person person = simpleMapper.personDTOToPerson(personDTO);
        linkPhonesToPerson(person);
        person.persist();
        return simpleMapper.personToPersonDTO(person);
    }

    @Transactional
    public PersonDTO update(PersonDTO personDTO)
    {
        Long id = getRequiredId(personDTO);
        Person existing = getRequiredPerson(id);
        Person updated = simpleMapper.personDTOToPerson(personDTO);

        existing.setName(updated.getName());
        existing.setSurname(updated.getSurname());
        existing.setAge(updated.getAge());
        existing.setPhoneNumbers(updated.getPhoneNumbers() == null ? null : new HashSet<>(updated.getPhoneNumbers()));

        linkPhonesToPerson(existing);
        return simpleMapper.personToPersonDTO(existing);
    }

    @Transactional
    public PersonDTO delete(PersonDTO personDTO)
    {
        Long id = getRequiredId(personDTO);
        Person existing = getRequiredPerson(id);
        PersonDTO deleted = simpleMapper.personToPersonDTO(existing);
        existing.delete();
        return deleted;
    }

    private Long getRequiredId(PersonDTO personDTO)
    {
        if (personDTO == null || personDTO.getId() == null)
        {
            throw new IllegalArgumentException("PersonDTO id is required");
        }

        return personDTO.getId();
    }

    private Person getRequiredPerson(Long id)
    {
        return Person.findByIdOptional(id)
                .map(Person.class::cast)
                .orElseThrow(() -> new NotFoundException("Person not found: " + id));
    }

    private void linkPhonesToPerson(Person person)
    {
        if (person.getPhoneNumbers() == null)
        {
            return;
        }

        person.getPhoneNumbers().forEach(phone -> phone.setPerson(person));
    }
}

