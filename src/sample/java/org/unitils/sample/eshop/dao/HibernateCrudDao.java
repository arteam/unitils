package org.unitils.sample.eshop.dao;

/**
 * 
 */
public class HibernateCrudDao<T> extends HibernateDao {

    private Class<T> mappedClass;

    public HibernateCrudDao(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    public T findById(Long id) {
        return (T) getSession().get(mappedClass, id);
    }

    public void create (T object) {
        getSession().persist(object);
    }

    public void update(T object) {
        getSession().update(object);
    }

    public void delete(T object) {
        getSession().delete(object);
    }
}
