/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Person person;

    private String quote;

    /**
     * Default constructor for object mapping frameworks like JPA.
     */
    @SuppressWarnings("unused")
    public Quote() {
        // for object mappers
    }

    /**
     * Constructs a new Quote with the specified person and quote.
     *
     * @param person the person who said the quote
     * @param quote the quote itself
     */
    public Quote(Person person, String quote) {
        this.person = person;
        this.quote = quote;
    }

    /**
     * Returns the ID of the quote
     *
     * @return The ID of the quote
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the person who said the quote.
     *
     * @return the person associated with the quote
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Returns the quote text.
     *
     * @return the quote text
     */
    public String getQuote() {
        return quote;
    }

    /**
     * Sets the ID of the quote
     *
     * @param id The new ID for the quote
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Compares this Quote object to another object for equality.
     * The comparison is based on the fields of the Quote.
     *
     * @param obj the object to compare this Quote to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Returns a hash code for this Quote.
     * The hash code is generated based on the fields of the Quote.
     *
     * @return the hash code for this Quote
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns a string representation of the Quote.
     * The string includes the fields of the Quote in a multi-line format.
     *
     * @return a string representation of the Quote
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}