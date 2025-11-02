module com.cincuentazo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cincuentazo to javafx.fxml;
    exports com.cincuentazo;
}