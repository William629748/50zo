module com.cincuentazo {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.media;


    opens com.cincuentazo.controller to javafx.fxml;
    exports com.cincuentazo;


}