package org.unitils.reflectionassert;

// START SNIPPET: user
public class User {

    private long id;
    private String first;
    private String last;
    // END SNIPPET: user
    private Address address;
    private String email;
// START SNIPPET: user

    public User(long id, String first, String last) {
        this.id = id;
        this.first = first;
        this.last = last;
    }
// END SNIPPET: user

    public User(long id, String first, String last, Address address) {
        this.id = id;
        this.first = first;
        this.last = last;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // START SNIPPET: user
}
// END SNIPPET: user
