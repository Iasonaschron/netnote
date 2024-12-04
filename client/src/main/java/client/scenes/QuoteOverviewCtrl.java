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
package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Quote;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class QuoteOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<Quote> data;

    @FXML
    private TableView<Quote> table;
    @FXML
    private TableColumn<Quote, String> colFirstName;
    @FXML
    private TableColumn<Quote, String> colLastName;
    @FXML
    private TableColumn<Quote, String> colQuote;

    /**
     * Constructs a QuoteOverviewCtrl instance with the given dependencies.
     *
     * @param server   a ServerUtils object for making server requests
     * @param mainCtrl the MainCtrl object for controlling the main view
     */
    @Inject
    public QuoteOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the columns of the table to display the quote's person's first name,
     * last name, and the quote text.
     *
     * @param location  the location used to resolve relative paths for the root object,
     *                  or null if the location is not known
     * @param resources the resources used to localize the root object, or null if
     *                  no localization is required
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFirstName.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getPerson().getFirstName()));
        colLastName.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getPerson().getLastName()));
        colQuote.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getQuote()));
    }

    /**
     * Opens the "add quote" dialog by invoking the main controller.
     */
    public void addQuote() {
        mainCtrl.showAdd();
    }

    /**
     * Refreshes the quote list by fetching the latest quotes from the server
     * and updating the table.
     */
    public void refresh() {
        var quotes = server.getQuotes();
        data = FXCollections.observableList(quotes);
        table.setItems(data);
    }
}