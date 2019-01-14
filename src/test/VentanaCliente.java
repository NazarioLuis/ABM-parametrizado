package test;

import com.autoabm.abm.AutoABM;

@SuppressWarnings("serial")
public class VentanaCliente extends AutoABM<Cliente> {
	
	public static void main(String[] args) {
		VentanaCliente ventanaCliente = new VentanaCliente();
		ventanaCliente.setVisible(true);
	}
	
	@Override
	public Class<Cliente> getClase() {
		return Cliente.class;
	}

	@Override
	protected String getTitulo() {
		return "Registro de Clietnes";
	}
	
	@Override
	protected String getCabeceraFormulario() {
		return "Datos del cliente";
	}

	@Override
	protected int getAnchoEnPorcentaje() {
		return 80;
	}
	
	@Override
	protected int getAltoEnPorcentaje() {
		return 90;
	}

	@Override
	protected int getColumnasFormulario() {
		return 2;
	}

}
