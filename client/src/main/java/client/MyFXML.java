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
package client;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

public class MyFXML {

    private final Injector injector;

    /**
     * Constructs a new MyFXML instance with the given Guice injector.
     *
     * @param injector the Guice injector used for dependency injection
     */
    public MyFXML(Injector injector) {
        this.injector = injector;
    }

    public <T> Pair<T, Parent> load(Class<T> c, String... parts) {
        return load(c, null, parts);
    }

    public <T> Pair<T, Parent> load(Class<T> c, ResourceBundle resourceBundle, String... parts) {
        try {
            var loader = new FXMLLoader(getLocation(parts), resourceBundle, null, new MyFactory(), StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a URL for the location of the FXML file based on the provided
     * parts.
     *
     * @param parts the parts of the path to the FXML file
     * @return the URL of the FXML file
     */
    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return MyFXML.class.getClassLoader().getResource(path);
    }

    /**
     * A custom factory for creating controller instances and resolving
     * dependencies.
     */
    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {

        /**
         * Returns a Builder for the specified type that injects dependencies using
         * Guice.
         *
         * @param type the class type of the controller
         * @return a Builder that can create the controller
         */
        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        /**
         * Returns a new instance of the specified type using Guice.
         *
         * @param type the class type of the object to create
         * @return a new instance of the specified type
         */
        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}