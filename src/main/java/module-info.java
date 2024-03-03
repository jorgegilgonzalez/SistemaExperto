module com.jorge.sistemaexperto {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jorge.sistemaexperto to javafx.fxml;
    exports com.jorge.sistemaexperto;
}