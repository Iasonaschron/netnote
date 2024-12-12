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
package server;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.api.CollectionController;

@Configuration
public class Config {

    private Collection defaultCollection;


    private final CollectionController collectionController;
    /**
     * Creates a Random bean
     *
     * @return A new instance of Random
     */
    @Bean
    public Random getRandom() {
        return new Random();
    }

    /**
     * Dependency Injection for collection Controller
     * @param collectionController the collection Controller
     */
    public Config(CollectionController collectionController){
        this.collectionController = collectionController;
    }

    /**
     * Pre-Construct method that reads a hardcoded default collection and sends it to the
     * server. If it already exists in the server repository then nothing happens and the
     * file is left as is.
     */
    @PostConstruct
    public void startup(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            File defaultConfig = new File("server/src/main/resources/defaultcollectionhardcoded.json");

            if(defaultConfig.exists()){
                defaultCollection = mapper.readValue(defaultConfig,Collection.class);
                collectionController.addCollection(defaultCollection);
                System.out.println("Collection added");
            }
            else{
                System.out.println("Default collection hardcoded.json not found");
            }
        }
        catch(IOException e){
            throw new RuntimeException("Something went wrong here",e);
        }
    }

    /**
     * Pre-Destroy Method that checks gets the default collection from the server
     * in which notes are being added to and hard saves it to a file that persists
     * across resets.
     */
    @PreDestroy
    public void shutdown(){
        File defFile = new File("server/src/main/resources/defaultcollectionhardcoded.json");
        ObjectMapper mapper = new ObjectMapper();

        try{
            Collection defColl =collectionController.getDefaultCollection();
            mapper.writeValue(defFile,defColl);
            System.out.println("Default Collection configuration has been saved");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}