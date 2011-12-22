package org.unitils.mock.example2;

public class MyService {

    private MyDao myDao;

    public boolean doService() {
        return true;
    }

    public void setMyDao(MyDao myDao) {
        this.myDao = myDao;
    }
}
