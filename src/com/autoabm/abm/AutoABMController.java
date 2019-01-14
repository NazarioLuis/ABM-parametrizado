package com.autoabm.abm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.hibernate.exception.ConstraintViolationException;

import com.autoabm.anotaciones.CampoABM;
import com.autoabm.opciones.Eventos;
import com.autoabm.opciones.OpcionesComponentes;
import com.autoabm.util.FormularioUtil;
import com.autoabm.util.GetterYSetter;
import com.autoabm.util.MensajeUtil;
import com.autoabm.util.TextoUtil;
import com.autoabm.util.WraperUtil;

/**
 * Clase que permite insertar, actualizar y eliminar de manera generica
 * 
 * @author luis y cristian
 *
 * @param <T>
 */
class AutoABMController<T> implements ActionListener,MouseListener {
	private AutoABM<T> abm;
	private int evento;
	private T obj;
	private List<Component> campos;
	private AutoDao<T> dao;
	private List<T> lista;
	private GenericTableModel<T> modeloTabla;
	private List<Field> atributosFiltrables;

	/**
	 * Constructor recibe la vista, carga alguno valores y ejecuta metodos iniciales
	 * @param abm
	 */
	@SuppressWarnings("unchecked")
	public AutoABMController(AutoABM<T> abm) {
		this.abm = abm;
		//se recupera los componetes del formulario
		this.campos = getSoloCampos(abm.getFormulario().getComponents());
		//se instancia el dao
		this.dao = new AutoDao<T>(abm.getClase());
		//se recupera el modelo de la tabla
		this.modeloTabla = (GenericTableModel<T>) abm.getTable().getModel();
		//se recuperan los atributos que puedes ser filtrados
		atributosFiltrables = getAtributodFiltrables();

		agregarEventos();
		estadoInicial(true);
		recuperarPorFiltro();
	}

	/**
	 * Metodo que filtra los componentes y retorna solo los validos
	 * @param componentes
	 * @return List<Component> lista de componente sin incluir labels
	 */
	private List<Component> getSoloCampos(Component[] componentes) {
		List<Component> soloCampos = new ArrayList<>();
		for (int i = 0; i < componentes.length; i++) {
			if (componentes[i].getClass() == JScrollPane.class) {
				soloCampos.add(((JScrollPane) componentes[i]).getViewport().getView());
			} else if (componentes[i].getClass() != JLabel.class) {
				soloCampos.add(componentes[i]);
			}
		}
		return soloCampos;
	}

	/**
	 * Metodo que habilita y desabilita los componentes del abm 
	 * @param esInicial
	 */
	private void estadoInicial(boolean esInicial) {
		if (esInicial) {//si esta en estado inicial 
			FormularioUtil.habilitarCampos(abm.getFormulario(), false);
			FormularioUtil.habilitarCampos(abm.getBotoneraPrincipal(), true);
			FormularioUtil.habilitarCampos(abm.getBotoneraSecundario(), false);
		} else {//si esta en estado de edicion
			FormularioUtil.habilitarCampos(abm.getFormulario(), true);
			FormularioUtil.habilitarCampos(abm.getBotoneraPrincipal(), false);
			FormularioUtil.habilitarCampos(abm.getBotoneraSecundario(), true);
		}
	}

	/**
	 * Metodo que agrega evento a los componentes del abm
	 */
	private void agregarEventos() {
		FormularioUtil.pasarFocoConEnter(abm.getFormulario());
		abm.getBtnNuevo().addActionListener(this);
		abm.getBtnModificar().addActionListener(this);
		abm.getBtnEliminar().addActionListener(this);
		abm.getBtnGuardar().addActionListener(this);
		abm.getBtnCancelar().addActionListener(this);
		abm.getBuscador().addActionListener(this);
		abm.getTable().addMouseListener(this);
	}

	/**
	 * Metodo que habilita el formulario en indica que el evento es una ALTA
	 */
	private void nuevo() {
		estadoInicial(false);
		evento = Eventos.ALTA;
	}

	/**
	 * Metodo que habilita el formulario en indica que el evento es una BAJA
	 */
	private void modificar() {
		estadoInicial(false);
		evento = Eventos.MODIFICACION;
	}

	/**
	 * Metodo que elimina un registro selecionado
	 */
	private void eliminar() {
		//se recupera un objeto utilizando la seleccion de la tabla
		obj = lista.get(abm.getTable().getSelectedRow());
		
		//se pregunta al usuario si eta seguro de eliminar el registro
		int respuesta = MensajeUtil.pregunta("Estas seguro que deseas eliminar el registro seleccionado", abm);
		if (respuesta == JOptionPane.YES_OPTION) {
			//si la respuesta es afirmativa se elimina el registro
			try {
				dao.eliminar(obj);
				dao.commit();
				recuperarPorFiltro();
				resetear();
			} catch (Exception e) {
				MensajeUtil.error("No se puede eliminar el registro, es probable que se encuentre en uso", abm);
			}
		}
	}

	/**
	 * Metodo que inserta o actualiza un registro 
	 */
	private void guardar() {
		
		try {
			//se carga los atributos con los valores del formulario
			obj = cargarAtributos(obj);
		} catch (Exception e1) {
			if(e1.getClass()!=RuntimeException.class) e1.printStackTrace();
			return;
		}
		
		//se inserta y se actualizar la vista
		try {
			dao.guardar(obj);
			dao.commit();
			recuperarPorFiltro();
			resetear();
		} catch (Exception e) {
			Throwable exc = e.getCause();
			if (exc.getClass() == ConstraintViolationException.class) {
				MensajeUtil.error(exc.getCause().getMessage(), abm);
			}else{
				MensajeUtil.error("Se produjo un error al intentar gurdar", abm);
			}
			dao.rollback();
		}
	}
	
	/**
	 * Metodo que carga los datos del formulario a los atributos del objeto
	 * @param obj
	 * @return T se retorna el objeto con los atributos cagados 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private T cargarAtributos(T obj) throws Exception {
		
		//Si es una alta se crea un nuevo objeto
		if(evento==Eventos.ALTA) obj = abm.getClase().newInstance();
		
		//Se recorren los campos del formulario
		for (int i = 0; i < campos.size(); i++) {
			//se recupera el atributo que corresponde al campo, sus anotaciones y las opciones del componente
			Field atributo = abm.getClase().getDeclaredField(campos.get(i).getName());
			CampoABM campoABM = atributo.getAnnotation(CampoABM.class);
			OpcionesComponentes oComp = OpcionesComponentes.valueOf(WraperUtil.wrap(atributo.getType()).getSimpleName());
			
			//se determina el nombre del label asociado
			String etiqueta;
			if(campoABM.nombre().isEmpty()){
				//si no se asigno un nombre en la anotacion se usa el nombre del atributo
				etiqueta = TextoUtil.normalizarNombreCampo(atributo.getName());
			}else {//si se asigno se utiliza en nombre indicado
				etiqueta = campoABM.nombre().toUpperCase();
			}
			 
			//Si se indico como validar=true se verifica que el campo no este vacio
			if (campoABM.validar()&&FormularioUtil.campoVacio(campos.get(i))) {
				MensajeUtil.error(etiqueta+" es un campo obligatorio", abm);
				throw new RuntimeException();
			}
			
			Object valor = null;
			if (campos.get(i).getClass()==JComboBox.class) {//si es combo se optiene el valor asi
				valor = ((JComboBox<Object>)campos.get(i)).getSelectedItem();
			}else{//sino se utiliza las opciones del componente correspondiente al tipo de dato
				valor = oComp.getValorComponente(campos.get(i));
			}
			
			//se setea el valor al atributo
			GetterYSetter.llamarSetter(obj, atributo, valor);
			
		}
		return obj;
	}

	/**
	 * Metodo que resetea el abm a su estado inicial
	 */
	private void resetear() {
		estadoInicial(true);
		FormularioUtil.vaciarFormulario(abm.getFormulario());
	}

	/**
	 * Metodo que carga los valore de un objeto al formulario al selecionarlo de la tabla
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void cargarFormulario() throws Exception {
		//se recupera un objeto utilizando la seleccion de la tabla
		obj = lista.get(abm.getTable().getSelectedRow());
		
		//se recorren los campos del formulario
		for (int i = 0; i < campos.size(); i++) {
			//se recupera el atributo que corresponde al campo y las opciones del componete
			Field atributo = abm.getClase().getDeclaredField(campos.get(i).getName());
			OpcionesComponentes oComp = OpcionesComponentes.valueOf(WraperUtil.wrap(atributo.getType()).getSimpleName());
			
			//se recupera el valor del atributo con getter
			Object valor = GetterYSetter.llamarGetter(obj, atributo);
			
			if (campos.get(i).getClass()==JComboBox.class) {//se es combo se pasa el valor asi 
				((JComboBox<Object>)campos.get(i)).setSelectedItem(valor);
			}else{//sino se utiliza las opciones del componente correspondiente al tipo de dato 
				oComp.setValorComponente(campos.get(i),valor);
			}
		}
	}
	
	/**
	 * Metodo que recupera los registros de la bd por filtro
	 */
	private void recuperarPorFiltro() {
		lista = dao.recuperarPorFiltro(abm.getBuscador().getText(),atributosFiltrables);
		modeloTabla.setLista(lista);
	}
	
	/**
	 * recupera los atributos que son string y por lo tanto se pueden usar como filtro
	 * @return List<Field>
	 */
	private List<Field> getAtributodFiltrables() {
		Field[] atributos = abm.getClase().getDeclaredFields();
		List<Field> filtrables = new ArrayList<>();
		for (int i = 0; i < atributos.length; i++) {
			if(atributos[i].getType() == String.class) filtrables.add(atributos[i]);
		}
		return filtrables;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == abm.getBtnNuevo())
			nuevo();
		if (e.getSource() == abm.getBtnModificar())
			modificar();
		if (e.getSource() == abm.getBtnEliminar())
			eliminar();
		if (e.getSource() == abm.getBtnGuardar())
			guardar();
		if (e.getSource() == abm.getBtnCancelar())
			resetear();
		if (e.getSource() == abm.getBuscador())
			recuperarPorFiltro();
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		try {
			cargarFormulario();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {}
	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {}
	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {}
	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {}
}
