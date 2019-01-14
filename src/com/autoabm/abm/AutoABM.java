package com.autoabm.abm;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Ventana abstracta y parametrizable
 * 
 * @author luis y cristian
 *
 * @param <T>
 */
public abstract class AutoABM<T> extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private JButton btnEliminar;
	private JButton btnModificar;
	private JButton btnNuevo;
	private JButton btnGuardar;
	private JButton btnCancelar;
	private JPanel formulario;
	private JPanel botoneraSecundario;
	private JPanel botoneraPrincipal;
	private JTextField buscador;

	public AutoABM() {
		prepararPlantilla();
		try {
			//se construye el formulario
			(new GeneradorDeComponentes<T>(this)).construir();
			//se prepara el controlador para realizar altas bajas y modificaciones
			new AutoABMController<T>(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepararPlantilla() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int ancho = (int) Math.round(screenSize.getWidth()*((double)getAnchoEnPorcentaje()/105));
		int alto = (int) Math.round(screenSize.getHeight()*((double)getAltoEnPorcentaje()/105));
		setBounds(100, 100, ancho, alto);
		
		setTitle(getTitulo());
		
		setLocationRelativeTo(this);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 10, 400, 0, 400, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 10, 0, 0, 0, 5, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		getContentPane().add(scrollPane, gbc_scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);

		botoneraPrincipal = new JPanel();
		GridBagConstraints gbc_botoneraPrincipal = new GridBagConstraints();
		gbc_botoneraPrincipal.insets = new Insets(0, 0, 5, 5);
		gbc_botoneraPrincipal.fill = GridBagConstraints.BOTH;
		gbc_botoneraPrincipal.gridx = 2;
		gbc_botoneraPrincipal.gridy = 1;
		getContentPane().add(botoneraPrincipal, gbc_botoneraPrincipal);
		botoneraPrincipal.setLayout(new BoxLayout(botoneraPrincipal, BoxLayout.PAGE_AXIS));

		btnNuevo = new JButton("NUEVO");
		btnNuevo.setMaximumSize(new Dimension(150, 40));
		botoneraPrincipal.add(btnNuevo);

		btnModificar = new JButton("MODIFICAR");
		btnModificar.setMaximumSize(new Dimension(150, 40));
		botoneraPrincipal.add(btnModificar);

		btnEliminar = new JButton("ELIMINAR");
		btnEliminar.setMaximumSize(new Dimension(150, 40));
		botoneraPrincipal.add(btnEliminar);

		formulario = new JPanel();
		formulario.setBorder(
				new TitledBorder(null, getCabeceraFormulario(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_formulario = new GridBagConstraints();
		gbc_formulario.insets = new Insets(0, 0, 5, 5);
		gbc_formulario.fill = GridBagConstraints.BOTH;
		gbc_formulario.gridx = 3;
		gbc_formulario.gridy = 1;
		getContentPane().add(formulario, gbc_formulario);
		
		buscador = new JTextField();
		GridBagConstraints gbc_buscador = new GridBagConstraints();
		gbc_buscador.insets = new Insets(0, 0, 5, 5);
		gbc_buscador.fill = GridBagConstraints.HORIZONTAL;
		gbc_buscador.gridx = 1;
		gbc_buscador.gridy = 2;
		getContentPane().add(buscador, gbc_buscador);
		buscador.setColumns(10);

		botoneraSecundario = new JPanel();
		GridBagConstraints gbc_botoneraSecundario = new GridBagConstraints();
		gbc_botoneraSecundario.fill = GridBagConstraints.VERTICAL;
		gbc_botoneraSecundario.insets = new Insets(0, 0, 5, 5);
		gbc_botoneraSecundario.gridx = 3;
		gbc_botoneraSecundario.gridy = 3;
		getContentPane().add(botoneraSecundario, gbc_botoneraSecundario);
		botoneraSecundario.setLayout(new BoxLayout(botoneraSecundario, BoxLayout.X_AXIS));

		btnGuardar = new JButton("GUARDAR");
		btnGuardar.setMinimumSize(new Dimension(120, 40));
		btnGuardar.setPreferredSize(new Dimension(120, 40));
		btnGuardar.setMaximumSize(new Dimension(120, 40));
		botoneraSecundario.add(btnGuardar);

		btnCancelar = new JButton("CANCELAR");
		btnCancelar.setMinimumSize(new Dimension(120, 40));
		btnCancelar.setPreferredSize(new Dimension(120, 40));
		btnCancelar.setMaximumSize(new Dimension(120, 40));
		botoneraSecundario.add(btnCancelar);
	}

	public abstract Class<T> getClase();

	protected abstract String getCabeceraFormulario();

	protected abstract String getTitulo();

	protected abstract int getAnchoEnPorcentaje();

	protected abstract int getAltoEnPorcentaje();
	
	protected abstract int getColumnasFormulario();
	
	public int getCantColumnas(){
		return getColumnasFormulario()*2;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public JButton getBtnEliminar() {
		return btnEliminar;
	}

	public void setBtnEliminar(JButton btnEliminar) {
		this.btnEliminar = btnEliminar;
	}

	public JButton getBtnModificar() {
		return btnModificar;
	}

	public void setBtnModificar(JButton btnModificar) {
		this.btnModificar = btnModificar;
	}

	public JButton getBtnNuevo() {
		return btnNuevo;
	}

	public void setBtnNuevo(JButton btnNuevo) {
		this.btnNuevo = btnNuevo;
	}

	public JButton getBtnGuardar() {
		return btnGuardar;
	}

	public void setBtnGuardar(JButton btnGuardar) {
		this.btnGuardar = btnGuardar;
	}

	public JButton getBtnCancelar() {
		return btnCancelar;
	}

	public void setBtnCancelar(JButton btnCancelar) {
		this.btnCancelar = btnCancelar;
	}

	public JPanel getFormulario() {
		return formulario;
	}

	public void setFormulario(JPanel formulario) {
		this.formulario = formulario;
	}

	public JPanel getBotoneraSecundario() {
		return botoneraSecundario;
	}

	public void setBotoneraSecundario(JPanel botoneraSecundario) {
		this.botoneraSecundario = botoneraSecundario;
	}

	public JPanel getBotoneraPrincipal() {
		return botoneraPrincipal;
	}

	public void setBotoneraPrincipal(JPanel botoneraPrincipal) {
		this.botoneraPrincipal = botoneraPrincipal;
	}
	
	public JTextField getBuscador() {
		return buscador;
	}

}
