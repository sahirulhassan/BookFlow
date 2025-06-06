module pk.edu.uitu.bookflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql;
    requires java.desktop;


    opens pk.edu.uitu.bookflow to javafx.fxml;
    exports pk.edu.uitu.bookflow;
}