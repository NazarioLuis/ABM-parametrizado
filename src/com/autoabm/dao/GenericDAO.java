package com.autoabm.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.autoabm.util.Connexion;

public abstract class GenericDAO <T>{
	protected Class<T> clase;
	
	public GenericDAO(Class<T> clase) {
		this.clase = clase;
	}
	
	public void guardar(T entity) throws Exception{
		openTransaction();
		getSession().saveOrUpdate(entity);
	}
	
	public void eliminar(T entity) throws Exception{
		openTransaction();
		getSession().delete(entity);
	}
	
	public int siguientetId(){
		openTransaction();
		@SuppressWarnings("unchecked")
		Query<Integer> query = getSession().createQuery("select max(id) from "+clase.getName());
		Integer result = query.list().get(0);
		if (result == null) {
			result = 0;
		}
		commit();
		return result+1;
	}
	
	public T recuperPorId(Serializable id){
		openTransaction();
		T result = getSession().get(clase, id);
		commit();
		return result;
	}
	
	public List<T> recuperarTodo(){
		openTransaction();
		@SuppressWarnings("unchecked")
		Query<T> query = getSession().createQuery("from "+clase.getName()+" order by id");
		List<T> lista = query.getResultList();
		commit();
		return lista;
	}
		
	public void commit(){
		getSession().flush();
		getSession().getTransaction().commit();
	}
	public void rollback(){
		if(getSession().getTransaction().isActive())
			getSession().getTransaction().rollback();
	}
	
	protected void openTransaction(){
		if (!getSession().getTransaction().isActive()) getSession().beginTransaction();
	}

	protected Session getSession() {
		return Connexion.getSessionFactory().getCurrentSession();
	}
	
	
}
