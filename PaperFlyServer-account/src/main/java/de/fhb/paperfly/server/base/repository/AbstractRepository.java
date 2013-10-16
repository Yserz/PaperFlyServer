package de.fhb.paperfly.server.base.repository;

import de.fhb.paperfly.server.logging.interceptor.RepositoryLoggerInterceptor;
import java.util.List;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * This class is a Facade-pattern and provides methods for standard database
 * operations like create, read, update and delete for all types of
 * domainobjects.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Interceptors(RepositoryLoggerInterceptor.class)
public abstract class AbstractRepository<T> {

	private Class<T> entityClass;

	public AbstractRepository(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public abstract EntityManager getEntityManager();

	public abstract void setEntityManager(EntityManager em);

	/**
	 * @see EntityManager#flush()
	 */
	public void flush() {
		getEntityManager().flush();
	}

	/**
	 * @see EntityManager#contains(java.lang.Object)
	 */
	public boolean contains(T entity) {
		return getEntityManager().contains(entity);
	}

	/**
	 * @see EntityManager#refresh(java.lang.Object)
	 */
	public void refresh(T entity) {
		getEntityManager().refresh(entity);
	}

	/**
	 * @see EntityManager#persist(java.lang.Object)
	 */
	public T create(T entity) {
		getEntityManager().persist(entity);
		getEntityManager().flush();
		return this.edit(entity);
	}

	/**
	 * @see EntityManager#merge(java.lang.Object)
	 */
	public T edit(T entity) {
		return getEntityManager().merge(entity);
	}

	/**
	 * @see EntityManager#remove(java.lang.Object)
	 */
	public void remove(T entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	/**
	 * @see EntityManager#find(java.lang.Class, java.lang.Object)
	 */
	public T find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<T> findRange(int[] range) {
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		Query q = getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0]);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public int count() {
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		Query q = getEntityManager().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
