package com.autoabm.util;

public class TextoUtil {
	public static String normalizarNombreCampo(String nombre) {
		String normalizado = null;
		String[] parts = nombre.split("(?=\\p{Upper})");
		for (int j = 0; j < parts.length; j++) {
			if(j==0) normalizado = parts[j].toUpperCase();
			else normalizado = normalizado+" "+parts[j].toUpperCase();
		}
		return normalizado;
	}
}
