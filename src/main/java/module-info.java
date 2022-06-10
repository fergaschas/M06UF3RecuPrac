module com.fgascon.m06uf3recuprac {
    requires javafx.controls;
    requires javafx.fxml;
    requires mongo.java.driver;


    opens com.fgascon.m06uf3recuprac to javafx.fxml;
    exports com.fgascon.m06uf3recuprac;
    exports com.fgascon.m06uf3recuprac.views;
    opens com.fgascon.m06uf3recuprac.views to javafx.fxml;
}