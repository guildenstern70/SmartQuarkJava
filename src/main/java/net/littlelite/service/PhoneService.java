/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import net.littlelite.dto.PhoneDTO;
import net.littlelite.mapper.SimpleMapper;
import net.littlelite.model.Person;
import net.littlelite.model.Phone;

import java.util.List;

@ApplicationScoped
public class PhoneService
{
    private final SimpleMapper simpleMapper;
    private final EntityManager entityManager;

    @Inject
    public PhoneService(SimpleMapper simpleMapper,
                        EntityManager entityManager)
    {
        this.simpleMapper = simpleMapper;
        this.entityManager = entityManager;
    }

    public List<PhoneDTO> findAll()
    {
        return Phone.<Phone>listAll()
                .stream()
                .map(simpleMapper::phoneToPhoneDTO)
                .toList();
    }

    public PhoneDTO findById(PhoneDTO phoneDTO)
    {
        Long id = getRequiredId(phoneDTO);
        Phone phone = getRequiredPhone(id);
        return simpleMapper.phoneToPhoneDTO(phone);
    }

    @Transactional
    public PhoneDTO create(PhoneDTO phoneDTO)
    {
        Person person = resolvePerson(phoneDTO);
        Long nextId = getNextPhoneId();

        entityManager.createNativeQuery("""
                INSERT INTO phone (id, prefix, number, person_id)
                VALUES (?1, ?2, ?3, ?4)
                """)
                .setParameter(1, nextId)
                .setParameter(2, phoneDTO.getPrefix())
                .setParameter(3, phoneDTO.getNumber())
                .setParameter(4, person == null ? null : person.getId())
                .executeUpdate();

        Phone phone = getRequiredPhone(nextId);
        return simpleMapper.phoneToPhoneDTO(phone);
    }

    @Transactional
    public PhoneDTO update(PhoneDTO phoneDTO)
    {
        Long id = getRequiredId(phoneDTO);
        Phone existing = getRequiredPhone(id);
        Phone updated = simpleMapper.phoneDTOToPhone(phoneDTO);

        existing.setPrefix(updated.getPrefix());
        existing.setNumber(updated.getNumber());
        existing.setPerson(resolvePerson(phoneDTO));

        return simpleMapper.phoneToPhoneDTO(existing);
    }

    @Transactional
    public PhoneDTO delete(PhoneDTO phoneDTO)
    {
        Long id = getRequiredId(phoneDTO);
        Phone existing = getRequiredPhone(id);
        PhoneDTO deleted = simpleMapper.phoneToPhoneDTO(existing);
        existing.delete();
        return deleted;
    }

    private Long getRequiredId(PhoneDTO phoneDTO)
    {
        if (phoneDTO == null || phoneDTO.getId() == null)
        {
            throw new IllegalArgumentException("PhoneDTO id is required");
        }

        return phoneDTO.getId();
    }

    private Phone getRequiredPhone(Long id)
    {
        return Phone.findByIdOptional(id)
                .map(Phone.class::cast)
                .orElseThrow(() -> new NotFoundException("Phone not found: " + id));
    }

    private Person resolvePerson(PhoneDTO phoneDTO)
    {
        if (phoneDTO == null || phoneDTO.getPerson() == null || phoneDTO.getPerson().getId() == null)
        {
            return null;
        }

        Long personId = phoneDTO.getPerson().getId();
        return Person.findByIdOptional(personId)
                .map(Person.class::cast)
                .orElseThrow(() -> new NotFoundException("Person not found: " + personId));
    }

    private Long getNextPhoneId()
    {
        Number maxId = (Number) entityManager.createNativeQuery("SELECT COALESCE(MAX(id), 0) FROM phone")
                .getSingleResult();
        return maxId.longValue() + 1L;
    }
}




