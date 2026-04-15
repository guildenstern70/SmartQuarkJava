/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import net.littlelite.model.Person;

import java.util.List;

@ApplicationScoped
class PersonDAO implements PanacheRepositoryBase<Person, Integer>
{
    public List<Person> findByName(String name)
    {
        return this.list("name = ?1", name);
    }

    public List<Person> findByNameAndSurname(String name, String surname)
    {
        return this.list("name = ?1 and surname = ?2", name, surname);
    }

    public List<Person> findByEmail(String email)
    {
        return this.list("email = ?1", email);
    }

    public List<Person> findBySurname(String surname)
    {
        return this.list("surname = ?1", surname);
    }

    public List<Person> findByAgeGreaterThan(int age)
    {
        return this.list("age > ?1", age);
    }

}