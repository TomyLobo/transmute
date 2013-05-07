package eu.tomylobo.util;

public class StringUtils {
	public static String join(String[] parts, String delimiter) {
		if (parts.length == 0)
			return "";

		StringBuilder sb = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; ++i) {
			sb.append(delimiter);
			sb.append(parts[i]);
		}

		return sb.toString();
	}
}
