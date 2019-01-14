package com.autoabm.util;

import java.awt.Component;

import javax.swing.JOptionPane;

public class MensajeUtil {

	private static String mje;
	private static int tipo;
	private static String titulo;
	private static Component fuente;

	public static void error(String mje,Component fuente) {
		MensajeUtil.mje=mje;
		MensajeUtil.fuente=fuente;
		MensajeUtil.titulo="ERROR";
		MensajeUtil.tipo=JOptionPane.ERROR_MESSAGE;
		mostrar();
	}
	
	public static void atencion(String mje,Component fuente) {
		MensajeUtil.mje=mje;
		MensajeUtil.fuente=fuente;
		MensajeUtil.titulo="ATENCION";
		MensajeUtil.tipo=JOptionPane.WARNING_MESSAGE;
		mostrar();
	}
	
	public static void exito(String mje,Component fuente) {
		MensajeUtil.mje=mje;
		MensajeUtil.fuente=fuente;
		MensajeUtil.titulo="EXITO";
		MensajeUtil.tipo=JOptionPane.INFORMATION_MESSAGE;
		mostrar();
	}
	
	public static int pregunta(String mje,Component fuente) {
		return JOptionPane.showConfirmDialog(fuente, 
				mje,
				"ATENCION", JOptionPane.YES_NO_OPTION);
	}

	private static void mostrar() {
		JOptionPane.showMessageDialog(fuente, mje, titulo, tipo);
	}

}
