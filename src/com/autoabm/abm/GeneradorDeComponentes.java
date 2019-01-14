package com.autoabm.abm;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.autoabm.anotaciones.CampoABM;
import com.autoabm.componentes.TextPrompt;
import com.autoabm.opciones.OpcionesComponentes;
import com.autoabm.util.TextoUtil;
import com.autoabm.util.WraperUtil;

/**
 * Clase que construye de manera genérica el abm
 * 
 * @author luis y cristian
 *
 * @param <T>
 */
class GeneradorDeComponentes<T> {
	
	private AutoABM<T> abm;
	private Field[] atributos;
	private GridBagLayout gbl_formulario;
	private GenericTableModel<T> modeloTabla;

	/**
	 * Contructor por pararámetro 
	 * @param abm Recibe la vista como parámetro
	 */
	public GeneradorDeComponentes(AutoABM<T> abm) {
		this.abm = abm;
		//se recupera los atributos de la calse
		this.atributos = abm.getClase().getDeclaredFields();
	}
	
	/**
	 * Metodo que agrega componentes de manera dinámica al formulatio
	 * en base a los atributos de la calse
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void construir() throws Exception {
		String busqueda = "";
		int fila = 0;
		int columna = 0;
		int cantColumnas = abm.getCantColumnas(); 
		for (Field atributo : atributos) {
			//se recupera la anotacion
		    CampoABM campoABM = atributo.getAnnotation(CampoABM.class);
		    
		    if (campoABM!=null) {//si tiene una anotacion se agrega al formulario	
		    	Class<?> oComp = null;
		    	
		    	if (campoABM.datos().length>0) {//si tiene un array de datos utiliza un combo
		    		oComp = JComboBox.class;
				}else if (campoABM.textoLargo()) {//si es un texto largo usa un textarea
		    		if(atributo.getType()==String.class) oComp = JTextArea.class;
		    		else{
		    			throw new Exception("Solo se puede usar el atributo textoLargo con un String verificar las entidades");
		    		}
				}else {//si no es niguno de los ateriores
					//se recupera el componente asociado al tipo de dato del atributo
					oComp = OpcionesComponentes.valueOf(
							WraperUtil.wrap(atributo.getType()).getSimpleName()).getComponente();
				}
		    	
		    	//se crea y se estiliza el label para el campo
		    	JLabel label = new JLabel();
		    	label.setBorder(new EmptyBorder(3,5,3,3));
			    label.setHorizontalAlignment(SwingConstants.RIGHT);
		    	
		    	if (campoABM.nombre().isEmpty()) {
		    		//si no se especifa un nombre en la anotacion se usa el nombre del atributo
					label.setText(TextoUtil.normalizarNombreCampo(atributo.getName()));
				}else{//si se espcifica un nombre en la anotacion se usa ese nombre
					label.setText(campoABM.nombre().toUpperCase());
				}
		    	
		    	//si es string se agraga como campo de busqueda
		    	if(busqueda.isEmpty() && atributo.getType()==String.class) busqueda = label.getText();
		    	else if(atributo.getType()==String.class) busqueda+=", "+label.getText();
		    	
		    	//si el se indico validar como true se agrega (*) al label
		    	if (campoABM.validar()) label.setText(label.getText()+"(*)");
			    
		    	//se instancial el componente y se le asigna un nombre equibalente al nombr del atributo
			    JComponent campo = (JComponent) oComp.newInstance();
			    campo.setName(atributo.getName());
			    
			    if (oComp == JTextArea.class){//si es un textarea necesita siertas configuraciones
			    	fila++;
			    	columna=0;
			    	((JTextArea) campo).setRows(3);
			    	((JTextArea) campo).setLineWrap(true);
			    	JScrollPane sc = new JScrollPane(campo);
			    	campo = sc;
			    }else if (oComp == JComboBox.class) {//se es combobox se deben agregar los items
			    	for (int i = 0; i < campoABM.datos().length; i++) {
						((JComboBox<Object>) campo).addItem(campoABM.datos()[i]);
					}
				}
			    
			    //se agrega el label al formulario
			    agregarCampo(fila, columna, 0,1, label);
		    	columna++;
		    	//si es un campo largo ocupa todo el ancho del formulario y sino solo un espaico
		    	int ancho = (campoABM.textoLargo())?cantColumnas-1:1;
		    	//se agreaga el componte ya se un textfiel, un combo, etc
		    	agregarCampo(fila, columna, 1,ancho, campo);
		    	columna++;
			    
		    	//una ves completas las columnas se pasa a la siguiente fila
			    if(columna == cantColumnas){
			    	fila++;
			    	columna=0;
			    }
			}
		}
		
		//se instancia el modelo de la tabla que permite visualizar lod dato obtenidos de la bd
		modeloTabla = new GenericTableModel<T>(atributos);
		abm.getTable().setModel(modeloTabla);
		
		//agregar placeholder al buscador
		agregarPlaceholderBuscador(busqueda);
	}
	
	/**
	 * metodo que agrega un placeholder al campo de busqueda
	 * @param busqueda
	 */
	private void agregarPlaceholderBuscador(String busqueda) {
		TextPrompt placeholder = new TextPrompt("Buscar por "+busqueda, abm.getBuscador());
	    placeholder.changeAlpha(0.75f);
	    placeholder.changeStyle(Font.ITALIC);
	}

	/**
	 * Metodo que agrega un componente al formulario en dispocicion de grid (filas y columnas)
	 * @param fila 
	 * @param columna
	 * @param ancho
	 * @param cantColumnas
	 * @param componente
	 */
	public void agregarCampo(int fila, int columna, double ancho, int cantColumnas, Component componente) {
		if (gbl_formulario == null) {
			gbl_formulario = new GridBagLayout();
			this.abm.getFormulario().setLayout(gbl_formulario);
		}
		
		double[] rw = new double[fila + 1];
		for (int i = 0; i <= fila; i++) {
			rw[i] = (i < fila) ? 0 : Double.MIN_VALUE;
		}
		gbl_formulario.rowWeights = rw;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = columna;
		gbc.gridy = fila;
		gbc.gridwidth = cantColumnas;
		gbc.gridheight = 1;
		gbc.weightx = ancho;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl_formulario.setConstraints(componente, gbc);
		abm.getFormulario().add(componente);
	}

}
