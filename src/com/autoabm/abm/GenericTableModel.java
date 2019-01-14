package com.autoabm.abm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.autoabm.anotaciones.CampoABM;
import com.autoabm.anotaciones.IdABM;
import com.autoabm.util.GetterYSetter;
import com.autoabm.util.TextoUtil;
import com.autoabm.util.WraperUtil;

/**
 * Clase que permite generar un modelo de tabla de manera dinámica
 * 
 * @author luis y cristian
 *
 * @param <T>
 */
class GenericTableModel<T> extends AbstractTableModel{
	
	private static final long serialVersionUID = 5623346388561641297L;

	private List<String> nombresColumnas = new ArrayList<>();
	
	private List<T> lista = new ArrayList<>();

	private List<Field> atributos = new ArrayList<>();
	
	/**
	 * Contructor que recibe los atributos del objeto
	 * @param atributos
	 */
	public GenericTableModel(Field[] atributos) {
		//recorre todos lo atributos y determina cuales mostrar en la tabla
		for (Field atributo:atributos) {
			CampoABM campoABM = atributo.getAnnotation(CampoABM.class);
			IdABM idABM = atributo.getAnnotation(IdABM.class);
			boolean esId = idABM != null;
			boolean esColumna = (campoABM!=null&&!campoABM.ocultarEnTabla()&&!campoABM.textoLargo());
			if(esId || esColumna){
				this.atributos.add(atributo);
				if(esColumna && !campoABM.nombre().isEmpty()){
					this.nombresColumnas.add(campoABM.nombre().toUpperCase());
				}else if (esId && !idABM.nombre().isEmpty()){
					this.nombresColumnas.add(idABM.nombre().toUpperCase());
				}else {
					this.nombresColumnas.add(TextoUtil.normalizarNombreCampo(atributo.getName()));
				}
			}
		}
	}
	
	public void setLista(List<T> lista) {
		this.lista = lista;
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int i) {
		return nombresColumnas.get(i);
	}
	
	@Override
	public int getRowCount() {
		return lista.size();
	}

	@Override
	public int getColumnCount() {
		return nombresColumnas.size();
	}
	
	@Override
	public Object getValueAt(int r, int c) {
		return getValue(r,c);
	}

	/**
	 * Metodo que carga los valores en la tabla
	 * @param r
	 * @param c
	 * @return
	 */
	private Object getValue(int r, int c) {
		try {
			//recupera de manera dinámica los dato del atributo correspondiente
			return GetterYSetter.llamarGetter(lista.get(r), atributos.get(c));
		} catch (Exception e) {
			e.printStackTrace();
			return new Object();
		}
	}

	/**
	 * determina el tipo de dato de la columna para formatearla
	 * @param c
	 * @return Class<?> Tipo de dato
	 */
	@Override
	public Class<?> getColumnClass(int c) {
		try {
			//determina de manera dinámica el tipo de dato
			return WraperUtil.wrap(atributos.get(c).getType());
		} catch (Exception e) {
			return Object.class;
		}
	}
	
}
