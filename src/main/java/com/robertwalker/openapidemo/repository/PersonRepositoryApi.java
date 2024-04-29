package com.robertwalker.openapidemo.repository;

import com.robertwalker.openapidemo.model.Person;

import java.util.List;

public interface PersonRepositoryApi {
    /**
     * Saves a person to Azure Table Storage.
     *
     * @param person the person to save
     * @return the saved person
     */
    Person savePerson(Person person);

    /**
     * Finds a person in Azure Table Storage by partitionKey (lastName) and rowKey (id).
     *
     * @param person the person to find
     * @return the found person
     */
    Person findPerson(Person person);

    /**
     * Find all people by first name.
     *
     * @param lastName a person's last name
     * @return a list of people with given last name
     */
    List<Person> findByLastName(String lastName);

    /**
     * Drops the people table from Azure Table list.
     */
    void dropPeopleTable();
}
