package com.autoabm.opciones;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.autoabm.componentes.DecimalTextField;
import com.autoabm.componentes.EnteroTextField;
import com.autoabm.componentes.FechaTextField;

/**
 * Maneja las opciones para cada tipo de dato
 * @author luis y cristian
 *
 */
public enum OpcionesComponentes {
	String(JTextField.class) {
		@Override
		public Object getValorComponente() {
			return ((JTextComponent) campo).getText();
		}

		@Override
		public void setValorComponente() {
			((JTextComponent) campo).setText((java.lang.String) valor);
		}
	},
	Boolean(JCheckBox.class) {
		@Override
		public Object getValorComponente() {
			return ((JCheckBox) campo).isSelected();
		}

		@Override
		public void setValorComponente() {
			((JCheckBox) campo).setSelected((boolean) valor);
		}
	},
	Double(DecimalTextField.class) {
		@Override
		public Object getValorComponente() {
			return ((DecimalTextField) campo).getDoubleValue();
		}

		@Override
		public void setValorComponente() {
			((DecimalTextField) campo).setValue((double) valor);
			
		}
	},
	Float(DecimalTextField.class) {
		@Override
		public Object getValorComponente() {
			return ((DecimalTextField) campo).getFloatValue();
		}

		@Override
		public void setValorComponente() {
			((DecimalTextField) campo).setValue((float) valor);
		}
	},
	Integer(EnteroTextField.class) {
		@Override
		public Object getValorComponente() {
			return ((EnteroTextField) campo).getIntValue();
		}

		@Override
		public void setValorComponente() {
			((EnteroTextField) campo).setValue((int) valor);
		}
	},
	Long(EnteroTextField.class) {
		@Override
		public Object getValorComponente() {
			return ((EnteroTextField) campo).getLongValue();
		}

		@Override
		public void setValorComponente() {
			((EnteroTextField) campo).setValue((long) valor);
		}
	},
	Date(FechaTextField.class) {
		@Override
		public Object getValorComponente() {
			return ((FechaTextField) campo).getFecha();
		}

		@Override
		public void setValorComponente() {
			((FechaTextField) campo).setValue((java.util.Date) valor);
		}
	};
	
	private final Class<?> componente;
	protected Component campo;
	protected Object valor;

	OpcionesComponentes(Class<?> componentes) {
        this.componente = componentes;
    }
    
    public Class<?> getComponente() {
        return this.componente;
    }
    
    public abstract Object getValorComponente();
    
    public Object getValorComponente(Component campo){
    	this.campo = campo;
    	return getValorComponente();
    }
    
    public abstract void setValorComponente();

	public void setValorComponente(Component comp, Object valor) {
		if(valor!=null) {
			this.campo = comp;
			this.valor = valor;
			setValorComponente();;
		}
	}
}
