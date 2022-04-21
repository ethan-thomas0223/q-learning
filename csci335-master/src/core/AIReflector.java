package core;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@SuppressWarnings("rawtypes")
public class AIReflector<T> {
	private final static String suffix = ".class";

	private Map<String,Class<T>> name2type;

	private FilenameFilter filter = (dir, name) -> name.endsWith(suffix);

	public AIReflector(Class superType, String packageName, Class... paramTypes) {
		this.name2type = new TreeMap<>();

		String targetDirName = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String pathname = targetDirName + packageName.replace('.', File.separatorChar);
		pathname = pathname.replace("%20", " ");
		File targetDir = new File(pathname);
		if (!targetDir.isDirectory()) {throw new IllegalArgumentException(targetDir + " is not a directory");}
		for (File f: targetDir.listFiles(filter)) {
			testAndAdd(f.getName(), superType, packageName, paramTypes);
		}
	}

	@SuppressWarnings("unchecked")
	private void testAndAdd(String name, Class superType, String packageName, Class... paramTypes) {
		name = name.substring(0, name.length() - suffix.length());
		try {
			Class type = Class.forName(packageName + "." + name);
			if (paramTypes.length == 0) {
				type.getDeclaredConstructor().newInstance();
			} else {
				Constructor<T> constructor = type.getConstructor(paramTypes);
				if (constructor.getParameterTypes().length == paramTypes.length) {
					for (int i = 0; i < paramTypes.length; i++) {
						if (constructor.getParameterTypes()[i] != paramTypes[i]) {
							System.out.println("Bailing out; " + constructor.getParameterTypes()[i] + " does not match " + paramTypes[i]);
							return;
						}
					}
				}
			}
			if (superType.isAssignableFrom(type)) {
				name2type.put(name, type);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
			// If an exception is thrown, we omit the type.
			// Hence, ignore this exceptions.
			System.out.println("Bailing on " + name);
			System.out.println(e);
		}
	}

	public Constructor<T> constructorFor(String typeName, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		if (!name2type.containsKey(typeName)) {
			throw new IllegalArgumentException("Undefined type: " + typeName);
		}
		return name2type.get(typeName).getConstructor(parameterTypes);
	}

	public ArrayList<String> getTypeNames() {
		return new ArrayList<>(name2type.keySet());
	}

	public String toString() {
		StringBuilder result = new StringBuilder("Available:");
		for (String s: name2type.keySet()) {
			result.append(" ").append(s);
		}
		return result.toString();
	}

	public T newInstanceOf(String typeName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return name2type.get(typeName).getDeclaredConstructor().newInstance();
	}

	public Optional<T> optionalInstanceOf(String typeName) {
		try {
			return Optional.of(newInstanceOf(typeName));
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
