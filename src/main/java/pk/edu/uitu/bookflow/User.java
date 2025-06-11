package pk.edu.uitu.bookflow;

import javafx.beans.property.*;

import java.time.LocalDate;

public class User {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty password;
    private final StringProperty role;
    private final ObjectProperty<LocalDate> joined;

    public User(int id, String name, String email, String password, String role, LocalDate joined) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.role = new SimpleStringProperty(role);
        this.joined = new SimpleObjectProperty<>(joined);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
    }

    public LocalDate getJoined() {
        return joined.get();
    }

    public void setJoined(LocalDate joined) {
        this.joined.set(joined);
    }

    public ObjectProperty<LocalDate> joinedProperty() {
        return joined;
    }
}
