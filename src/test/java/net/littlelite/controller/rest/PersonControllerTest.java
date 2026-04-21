/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.controller.rest;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import net.littlelite.dto.PersonDTO;
import net.littlelite.dto.PhoneDTO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class PersonControllerTest
{
    private static final String BASE_PATH = "/api/persons";

    @Test
    public void testFindAllReturnsListOfPersons()
    {
        given()
                .when()
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue())
                .body("[0].surname", notNullValue());
    }

    @Test
    @TestTransaction
    public void testCreatePersonWithoutPhonesReturnsCreatedPerson()
    {
        PersonDTO newPerson = new PersonDTO();
        newPerson.setName("TestJohn");
        newPerson.setSurname("TestDoe");
        newPerson.setAge(30);

        given()
                .contentType(ContentType.JSON)
                .body(newPerson)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo("TestJohn"))
                .body("surname", equalTo("TestDoe"))
                .body("age", equalTo(30));
    }

    @Test
    @TestTransaction
    public void testCreatePersonWithPhonesReturnsCreatedPersonWithPhones()
    {
        PhoneDTO phone1 = new PhoneDTO();
        phone1.setPrefix("+39");
        phone1.setNumber("9991234567");

        PhoneDTO phone2 = new PhoneDTO();
        phone2.setPrefix("+39");
        phone2.setNumber("9990987654");

        PersonDTO newPerson = new PersonDTO();
        newPerson.setName("TestJane");
        newPerson.setSurname("TestSmith");
        newPerson.setAge(25);
        newPerson.setPhones(Set.of(phone1, phone2));

        given()
                .contentType(ContentType.JSON)
                .body(newPerson)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo("TestJane"))
                .body("surname", equalTo("TestSmith"))
                .body("age", equalTo(25))
                .body("phones", hasSize(2));
    }

    @Test
    @TestTransaction
    public void testUpdatePersonModifiesExistingPerson()
    {
        PersonDTO newPerson = new PersonDTO();
        newPerson.setName("TestOriginal");
        newPerson.setSurname("TestName");
        newPerson.setAge(40);

        Integer createdId = given()
                .contentType(ContentType.JSON)
                .body(newPerson)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        PersonDTO updateDTO = new PersonDTO();
        updateDTO.setId(createdId.longValue());
        updateDTO.setName("TestUpdated");
        updateDTO.setSurname("TestPerson");
        updateDTO.setAge(41);

        given()
                .contentType(ContentType.JSON)
                .body(updateDTO)
                .when()
                .put(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(createdId.intValue()))
                .body("name", equalTo("TestUpdated"))
                .body("surname", equalTo("TestPerson"))
                .body("age", equalTo(41));
    }

    @Test
    public void testUpdateNonExistentPersonReturnsNotFound()
    {
        PersonDTO nonExistent = new PersonDTO();
        nonExistent.setId(Long.MAX_VALUE);
        nonExistent.setName("Ghost");
        nonExistent.setSurname("Person");
        nonExistent.setAge(99);

        given()
                .contentType(ContentType.JSON)
                .body(nonExistent)
                .when()
                .put(BASE_PATH)
                .then()
                .statusCode(404);
    }

    @Test
    @TestTransaction
    public void testDeletePersonRemovesExistingPerson()
    {
        PersonDTO newPerson = new PersonDTO();
        newPerson.setName("TestToDelete");
        newPerson.setSurname("TestPerson");
        newPerson.setAge(50);

        Integer createdId = given()
                .contentType(ContentType.JSON)
                .body(newPerson)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        PersonDTO deleteDTO = new PersonDTO();
        deleteDTO.setId(createdId.longValue());

        given()
                .contentType(ContentType.JSON)
                .body(deleteDTO)
                .when()
                .delete(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(createdId.intValue()))
                .body("name", equalTo("TestToDelete"))
                .body("surname", equalTo("TestPerson"));
    }

    @Test
    public void testDeleteNonExistentPersonReturnsNotFound()
    {
        PersonDTO nonExistent = new PersonDTO();
        nonExistent.setId(Long.MAX_VALUE);

        given()
                .contentType(ContentType.JSON)
                .body(nonExistent)
                .when()
                .delete(BASE_PATH)
                .then()
                .statusCode(404);
    }

    @Test
    @TestTransaction
    public void testFindByIdReturnsExistingPerson()
    {
        PersonDTO newPerson = new PersonDTO();
        newPerson.setName("TestFindMe");
        newPerson.setSurname("TestById");
        newPerson.setAge(35);

        Integer createdId = given()
                .contentType(ContentType.JSON)
                .body(newPerson)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        PersonDTO searchDTO = new PersonDTO();
        searchDTO.setId(createdId.longValue());

        given()
                .contentType(ContentType.JSON)
                .body(searchDTO)
                .when()
                .post(BASE_PATH + "/by-id")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(createdId.intValue()))
                .body("name", equalTo("TestFindMe"))
                .body("surname", equalTo("TestById"))
                .body("age", equalTo(35));
    }

    @Test
    public void testFindByIdWithNonExistentIdReturnsNotFound()
    {
        PersonDTO searchDTO = new PersonDTO();
        searchDTO.setId(Long.MAX_VALUE);

        given()
                .contentType(ContentType.JSON)
                .body(searchDTO)
                .when()
                .post(BASE_PATH + "/by-id")
                .then()
                .statusCode(404);
    }

    @Test
    @TestTransaction
    public void testCreateUpdateDeleteWorkflowIsIdempotent()
    {
        PersonDTO newPerson = new PersonDTO();
        newPerson.setName("TestWorkflow");
        newPerson.setSurname("TestComplete");
        newPerson.setAge(28);

        Integer createdId = given()
                .contentType(ContentType.JSON)
                .body(newPerson)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        PersonDTO updateDTO = new PersonDTO();
        updateDTO.setId(createdId.longValue());
        updateDTO.setName("TestWorkflow");
        updateDTO.setSurname("TestComplete");
        updateDTO.setAge(29);

        given()
                .contentType(ContentType.JSON)
                .body(updateDTO)
                .when()
                .put(BASE_PATH)
                .then()
                .statusCode(200)
                .body("age", equalTo(29));

        PersonDTO deleteDTO = new PersonDTO();
        deleteDTO.setId(createdId.longValue());

        given()
                .contentType(ContentType.JSON)
                .body(deleteDTO)
                .when()
                .delete(BASE_PATH)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .body(deleteDTO)
                .when()
                .post(BASE_PATH + "/by-id")
                .then()
                .statusCode(404);
    }
}