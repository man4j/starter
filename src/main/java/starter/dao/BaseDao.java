package starter.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

abstract public class BaseDao<T> {
    @PersistenceContext
    protected EntityManager em;
    
    private Class<T> entityClass;
    
    @SuppressWarnings("unchecked")
    public BaseDao() {
        TypeFactory tf = TypeFactory.defaultInstance();
        
        JavaType type = tf.findTypeParameters(tf.constructType(this.getClass()), BaseDao.class) [0];
        
        entityClass = (Class<T>) type.getRawClass();
    }
    
    /**
     * This method always generate SQL INSERT statement.
     */
    public void insert(T entity) {
        em.persist(entity);
    }
    
    /**
     * This method generate SQL SELECT statement if entity not found in first-level cache or second-level cache;
     */
    public T get(Serializable id) {
        return em.find(entityClass, id);
    }
    
    /**
     * This method generate SQL SELECT for fetch and compare detached object with persistent state and generate 
     * SQL update if detached object not equals persistent state.
     */
    public T checkAndUpdate(T detachedEntity) {
        return em.merge(detachedEntity);
    }
    
    /**
     * This method always generate SQL update.
     */
    public T update(T detachedEntity) {
        em.unwrap(Session.class).update(detachedEntity);
        
        return detachedEntity;
    }
    
    public void delete(T entity) {
        em.remove(entity);
    }
}
