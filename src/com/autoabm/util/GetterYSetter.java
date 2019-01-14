package com.autoabm.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class GetterYSetter {
	
	public static void llamarSetter(Object obj, Field atributo, Object valor) throws Exception {
		PropertyDescriptor pd;
		pd = new PropertyDescriptor(atributo.getName(), obj.getClass());
		pd.getWriteMethod().invoke(obj, valor);
	}
	public static Object llamarGetter(Object obj, Field atributo) throws Exception {
		PropertyDescriptor pd;
		pd = new PropertyDescriptor(atributo.getName(), obj.getClass());
		return pd.getReadMethod().invoke(obj);
	}
}
