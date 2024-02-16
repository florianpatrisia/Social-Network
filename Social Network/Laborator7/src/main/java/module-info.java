module com.example.laborator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;

    opens com.example.laborator to javafx.fxml;
    opens com.example.laborator.domain;
    exports com.example.laborator;
    exports com.example.laborator.domain;
    exports com.example.laborator.domain.validators;
    exports com.example.laborator.repository;
    exports com.example.laborator.repository.paging;
    exports com.example.laborator.service;
    exports com.example.laborator.utils;
    exports com.example.laborator.utils.events;
    exports com.example.laborator.utils.observer;
    exports com.example.laborator.controller;

    opens com.example.laborator.controller to javafx.fxml;
}