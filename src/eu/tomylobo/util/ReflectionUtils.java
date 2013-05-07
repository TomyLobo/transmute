package eu.tomylobo.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class ReflectionUtils {
	public static <T,U extends T> void cloneFieldByField(Class<T> cls, T oldInstance, U newInstance) {
		if (cls == null)
			return;

		if (cls == Object.class)
			return;

		try {
			final Field[] declaredFields = cls.getDeclaredFields();
			for (int fieldIndex = 0; fieldIndex < declaredFields.length; ++fieldIndex) {
				final Field field = declaredFields[fieldIndex];
				if ((field.getModifiers() & Modifier.STATIC) != 0)
					continue;

				final boolean accessible = field.isAccessible();

				field.setAccessible(true);

				final Object value = field.get(oldInstance);
				ReflectionHelper.setPrivateValue(cls, newInstance, value, fieldIndex);

				field.setAccessible(accessible);
			}
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		cloneFieldByField(cls.getSuperclass(), oldInstance, newInstance);
	}
}
