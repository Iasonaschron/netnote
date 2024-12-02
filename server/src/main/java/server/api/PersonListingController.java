/**
 * Copyright 2024 Sebastian Proksch
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package server.api;

import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import commons.Person;

@RestController
@RequestMapping("/api/people")
public class PersonListingController {

    private List<Person> people = new LinkedList<>();

    /**
     * Constructor initializing the controller with a predefined list of people.
     * Adds two sample people to the list: Mickey Mouse and Donald Duck.
     */
    public PersonListingController() {
        people.add(new Person("Mickey", "Mouse"));
        people.add(new Person("Donald", "Duck"));
    }

    /**
     * Handles GET requests to retrieve the list of people.
     *
     * @return a list of all people
     */
    @GetMapping("/")
    public List<Person> list() {
        return people;
    }

    /**
     * Handles POST requests to add a new person to the list.
     * If the person does not already exist in the list, they will be added.
     *
     * @param p the person to add to the list
     * @return the updated list of people
     */
    @PostMapping("/")
    public List<Person> add(@RequestBody Person p) {
        if (!people.contains(p)) {
            people.add(p);
        }
        return people;
    }
}