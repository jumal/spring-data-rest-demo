package org.jumaland;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.hateoas.client.Traverson.getDefaultMessageConverters;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class PersonRESTAPITests {

    private static final URI ROOT_URI = URI.create("http://127.0.0.1:8080");
    private static final RestTemplate REST_TEMPLATE = new TestRestTemplate();
    static {
        REST_TEMPLATE.setMessageConverters(getDefaultMessageConverters(HAL_JSON));
    }
    private static final ParameterizedTypeReference<Resource<Person>> TYPE = new ParameterizedTypeReference<Resource<Person>>() {};
    private static final ParameterizedTypeReference<Resources<Person>> COLLECTION_TYPE = new ParameterizedTypeReference<Resources<Person>>() {};
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRESTAPITests.class);

    @Test
    public void test() {

        // Discover API URI
        String personsURI = new Traverson(ROOT_URI, HAL_JSON).follow("persons").asLink().getHref();

        // Create
        ResponseEntity<Resource<Person>> person = REST_TEMPLATE.exchange(personsURI, POST, entity("{\"firstName\":\"jp\",\"lastName\":\"c\"}"), TYPE);
        String personURI = person.getBody().getLink("self").getHref();

        // List
        ResponseEntity<Resources<Person>> persons = REST_TEMPLATE.exchange(personsURI, GET, null, COLLECTION_TYPE);
        assertThat(persons.getBody().getContent().size(), is(1));

        // Update
        REST_TEMPLATE.exchange(personURI, PUT, entity("{\"firstName\":\"jp\",\"lastName\":\"c2\"}"), TYPE);

        // Search
        Link searchLink = new Traverson(ROOT_URI, HAL_JSON).follow("persons", "search", "findByLastName").asTemplatedLink();
        searchLink.getVariables().stream().forEach(variable -> {
            LOGGER.info("name:" + variable.getName());
            LOGGER.info("description:" + variable.getDescription());
            LOGGER.info("type:" + variable.getType());
        });
        String searchURI = searchLink.expand("c2").getHref();
        persons = REST_TEMPLATE.exchange(searchURI, GET, null, COLLECTION_TYPE);
        assertThat(persons.getBody().getContent().size(), is(1));

        // Delete
        REST_TEMPLATE.delete(personURI);
    }

    private static HttpEntity<String> entity(final String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON_UTF8);
        return new HttpEntity<>(json, headers);
    }
}
