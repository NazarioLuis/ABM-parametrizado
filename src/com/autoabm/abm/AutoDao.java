package com.autoabm.abm;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.query.Query;

import com.autoabm.dao.GenericDAO;

/**
 * Esta clase permite construir sentencias sql dinamicamente
 * 
 * @author luis y cristian
 *
 * @param <T>
 */
class AutoDao<T> extends GenericDAO<T>{

	public AutoDao(Class<T> clase) {
		super(clase);
	}
	

	public List<T> recuperarPorFiltro(String filtro, List<Field> atributos) {
		openTransaction();
		
		String where = "";
		for (int i = 0; i < atributos.size(); i++) {
			if(where.isEmpty()) where = "where upper("+atributos.get(i).getName()+") like :filtro ";
			else where += "or upper("+atributos.get(i).getName()+") like :filtro ";
		}
		
		@SuppressWarnings("unchecked")
		Query<T> query = getSession().createQuery(
				"from "+clase.getName()
				+" "+where
				+" order by id desc"
		);
		
		query.setParameter("filtro", "%"+filtro.toUpperCase()+"%");
		List<T> lista = query.getResultList();
		commit();
		return lista;
	}


	
}
