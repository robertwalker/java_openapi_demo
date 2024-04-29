package com.robertwalker.openapidemo.service;

import com.robertwalker.openapidemo.api.GreetingApiDelegate;
import com.robertwalker.openapidemo.model.Greeting;
import com.robertwalker.openapidemo.model.Person;
import com.robertwalker.openapidemo.repository.PersonRepositoryApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GreetingService implements GreetingApiDelegate {
    private final PersonRepositoryApi personRepository;

    @Override
    public ResponseEntity<Void> deletePeople() {
        personRepository.dropPeopleTable();
        log.info("Dropped table: people");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Greeting> getGreeting(String inputName) {
        Person person = createPersonFromFullName(inputName);
        log.info("Hello {}", person.getFullName());

        Person savedPerson = personRepository.savePerson(person);
        log.info("Saved: {}", savedPerson);

        Person foundPerson = personRepository.findPerson(savedPerson);
        log.info("Found: {}", foundPerson);

        List<Person> people = personRepository.findByLastName(foundPerson.getLastName());
        log.info("There are {} People with last name starting with {}: {}",
                people.size(), StringUtils.left(foundPerson.getLastName(), 2), people);

        Greeting greeting = new Greeting("Hello " + foundPerson.getFullName());
        return new ResponseEntity<>(greeting, HttpStatus.OK);
    }

    private Person createPersonFromFullName(String inputName) {
        String name = StringUtils.isNotBlank(inputName) ? inputName.trim() : "Stranger";
        String[] components = StringUtils.split(name, ' ');
        Person person = Person.builder().build();
        if (components.length == 3) {
            return person
                    .withFirstName(components[0])
                    .withMiddleName(components[1])
                    .withLastName(components[2]);
        }
        if (components.length == 2) {
            return person
                    .withFirstName(components[0])
                    .withLastName(components[1]);
        }
        if (components.length == 1) {
            return person
                    .withLastName(components[0]);
        }
        return person;
    }
}
