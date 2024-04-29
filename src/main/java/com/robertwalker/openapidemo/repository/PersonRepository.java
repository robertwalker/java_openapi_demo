package com.robertwalker.openapidemo.repository;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.robertwalker.openapidemo.exception.AzureConnectionFailure;
import com.robertwalker.openapidemo.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class PersonRepository implements PersonRepositoryApi {
    public static final String AZURE_CONNECTION_STRING = "DefaultEndpointsProtocol=http;" +
            "AccountName=devstoreaccount1;" +
            "AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;" +
            "TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;";

    public static final String PEOPLE_TABLE = "people";

    public static final String ID_PROPERTY = "id";
    public static final String FIRST_NAME_PROPERTY = "firstName";
    public static final String MIDDLE_NAME_PROPERTY = "middleName";
    public static final String LAST_NAME_PROPERTY = "lastName";

    private static TableClient sharedPeopleTableClient;

    /**
     * Saves a person to Azure Table Storage.
     *
     * @param person the person to save
     * @return the saved person
     */
    public Person savePerson(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("The person is required");
        }

        Person newPerson = person.getId() == null ? person.withId(UUID.randomUUID()): person;
        TableEntity entity = createPeopleTableEntity(newPerson);
        return personFromEntity(entity);
    }

    /**
     * Finds a person in Azure Table Storage by partitionKey (lastName) and rowKey (id).
     *
     * @param person the person to find
     * @return the found person
     */
    public Person findPerson(Person person) {
        if (person == null || person.getId() == null) {
            throw new IllegalArgumentException("A person is required with a valid ID");
        }

        TableEntity entity = findPeopleTableEntity(person);
        return personFromEntity(entity);
    }

    /**
     * Find all people by first name.
     *
     * @param lastName a person's last name
     * @return a list of people with given last name
     */
    @Override
    public List<Person> findByLastName(String lastName) {
        List<Person> people = new ArrayList<>();
        PagedIterable<TableEntity> entities = listEntities(lastName);
        entities.forEach(entity -> people.add(personFromEntity(entity)));
        return people;
    }

    /**
     * Drops the people table from Azure Table list.
     */
    @Override
    public void dropPeopleTable() {
        dropPeopleTableClient();
    }

    private Person personFromEntity(TableEntity entity) {
        UUID id = entity.getProperty(ID_PROPERTY) != null
                ? UUID.fromString((String) entity.getProperty(ID_PROPERTY))
                : null;
        return Person.builder()
                .id(id)
                .firstName((String) entity.getProperty(FIRST_NAME_PROPERTY))
                .middleName((String) entity.getProperty(MIDDLE_NAME_PROPERTY))
                .lastName((String) entity.getProperty(LAST_NAME_PROPERTY))
                .build();
    }

    private String getPartitionKey(String lastName) {
        return StringUtils.left(lastName, 2);
    }

    private String getPartitionKey(Person person) {
        return getPartitionKey(person.getLastName());
    }

    private String getRowKey(Person person) {
        return person.getId() != null ? person.getId().toString() : UUID.randomUUID().toString();
    }

    /**
     * Lazy load or create the "persons" table in Azure Table Storage.
     *
     * @return the Azure table client for the people table
     */
    private static TableClient getPeopleTableClient() {
        TableServiceClient tableServiceClient = new TableServiceClientBuilder()
                .connectionString(AZURE_CONNECTION_STRING)
                .buildClient();
        if (sharedPeopleTableClient == null) {
            sharedPeopleTableClient = tableServiceClient.createTableIfNotExists(PEOPLE_TABLE);
            if (sharedPeopleTableClient == null) {
                sharedPeopleTableClient = tableServiceClient.getTableClient(PEOPLE_TABLE);
            }
        }
        return sharedPeopleTableClient;
    }

    private static void dropPeopleTableClient() {
        getPeopleTableClient().deleteTable();
        sharedPeopleTableClient = null;
    }

    private TableEntity createPeopleTableEntity(Person person) {
        if (person.getLastName() == null) {
            throw new IllegalArgumentException("The person is required have a last name");
        }

        TableEntity entity;
        try {
            TableClient tableClient = getPeopleTableClient();
            entity = new TableEntity(getPartitionKey(person), getRowKey(person))
                    .addProperty(ID_PROPERTY, person.getId().toString())
                    .addProperty(FIRST_NAME_PROPERTY, person.getFirstName())
                    .addProperty(MIDDLE_NAME_PROPERTY, person.getMiddleName())
                    .addProperty(LAST_NAME_PROPERTY, person.getLastName());
            tableClient.createEntity(entity);
        } catch (Exception e) {
            throw new AzureConnectionFailure("Failed to connect to Azure Table Storage", e);
        }
        return entity;
    }

    private TableEntity findPeopleTableEntity(Person person) {
        TableClient tableClient = getPeopleTableClient();
        return tableClient.getEntity(getPartitionKey(person), getRowKey(person));
    }

    private PagedIterable<TableEntity> listEntities(String lastName) {
        TableClient tableClient = getPeopleTableClient();
        List<String> propertiesToSelect = List.of(
                ID_PROPERTY,
                FIRST_NAME_PROPERTY,
                MIDDLE_NAME_PROPERTY,
                LAST_NAME_PROPERTY
        );

        // OData supported filter query
        String query = String.format("PartitionKey eq '%s'", getPartitionKey(lastName));
        ListEntitiesOptions options = new ListEntitiesOptions()
                .setFilter(query)
                .setSelect(propertiesToSelect);

        return tableClient.listEntities(options, null, null);
    }
}
