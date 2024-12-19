module org.example.projetjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;




    opens org.example.projetjava to javafx.fxml;
    exports org.example.projetjava;

    opens Controllers to javafx.fxml;


}