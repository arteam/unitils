package org.unitils.reflectionassert;


public class UserWithEquals {

    private long id;
    private String first;
    private String last;

    public UserWithEquals(long id, String first, String last) {
        this.id = id;
        this.first = first;
        this.last = last;
    }

    // START SNIPPET: equals
    public boolean equals(Object object) {
        if (object instanceof UserWithEquals) {
            return id == ((UserWithEquals) object).id;
        }
        return false;
    }
    // END SNIPPET: equals

}
