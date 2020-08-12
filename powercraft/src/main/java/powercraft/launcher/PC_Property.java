package powercraft.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class PC_Property {

	private Object value = null;
	private String comment[] = null;

	private PC_Property(LineNumberReader lnr, String[] thisComment) {
		comment = thisComment;
		HashMap<String, PC_Property> hm = new HashMap<String, PC_Property>();
		value = hm;
		try {
			String line = lnr.readLine();
			String key = null;
			List<String> comment = new ArrayList<String>();
			while (line != null) {
				line = line.trim();
				if (line.startsWith("#")) {
					comment.add(line.substring(1));
				} else if (line.endsWith("}")) {
					break;
				} else if (line.endsWith("{") && key != null) {
					hm.put(key, new PC_Property(lnr, comment.toArray(new String[0])));
					key = null;
					comment.clear();
				} else if (!line.equals("")) {
					int peq = line.indexOf('=');
					if (peq == -1) {
						peq = line.indexOf('{');
						if (peq > 0) {
							key = line.substring(0, peq).trim();
							hm.put(key, new PC_Property(lnr, comment.toArray(new String[0])));
							key = null;
							comment.clear();
						} else {
							key = line;
						}
					} else if (peq > 0) {
						key = line.substring(0, peq).trim();
						String value = line.substring(peq + 1).trim();
						hm.put(key, new PC_Property(value, comment.toArray(new String[0])));
						key = null;
						comment.clear();
					}
				}

				line = lnr.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PC_Property(String s, String... desc) {
		comment = desc;
		if (s.startsWith("\"") && s.endsWith("\"")) {
			value = s.substring(1, s.length() - 1);
		} else if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("ok")) {
			value = true;
		} else if (s.equalsIgnoreCase("false")) {
			value = false;
		} else {
			try {
				if (s.contains(".")) {
					value = Float.parseFloat(s);
				} else {
					value = Integer.parseInt(s);
				}
			} catch (NumberFormatException e) {
				value = s;
			}
		}
	}

	public PC_Property(Object value, String... desc) {
		if (value == null)
			value = new HashMap<String, PC_Property>();
		this.value = value;
		comment = desc;
	}

	public PC_Property() {
		value = new HashMap<String, PC_Property>();
		comment = null;
	}

	public String getString() {
		return value.toString();
	}

	public int getInt() {
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		return 0;
	}

	public float getFloat() {
		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}
		return 0.0f;
	}

	public boolean getBoolean() {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		return false;
	}

	public HashMap<String, PC_Property> getPropertys() {
		if (value instanceof HashMap) {
			return (HashMap<String, PC_Property>) value;
		}
		return null;
	}

	public boolean hasChildren() {
		return value instanceof HashMap;
	}

	public void put(String key, PC_Property prop) {
		if (hasChildren()) {
			getPropertys().put(key, prop);
		}
	}

	private void save(OutputStreamWriter osw, String tabs) {
		if (hasChildren()) {
			HashMap<String, PC_Property> hm = (HashMap<String, PC_Property>) value;
			String[] keys = hm.keySet().toArray(new String[0]);
			Arrays.sort(keys);
			for (String key : keys) {
				try {
					PC_Property prop = hm.get(key);
					String[] comment = prop.comment;
					if (comment != null && comment.length > 0) {
						for (String c : comment) {
							osw.write(tabs + "#" + c + "\n");
						}
					}
					if (prop.hasChildren()) {
						osw.write(tabs + key + "{\n");
						prop.save(osw, tabs + "\t");
						osw.write(tabs + "}\n");
					} else {
						if (prop.value instanceof String) {
							osw.write(tabs + key + "=\"" + (String) prop.value + "\"\n");
						} else {
							osw.write(tabs + key + "=" + prop.value.toString() + "\n");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void save(OutputStream os) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
			save(osw, "");
			osw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static PC_Property loadFromFile(InputStream is) {
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		PC_Property prop = new PC_Property(lnr, null);
		try {
			lnr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	public String[] getComment() {
		return comment;
	}

	public void setValue(Object o, String[] comment) {
		value = o;
		this.comment = comment;
	}

	public void replaceWith(PC_Property prop) {
		if (hasChildren() && prop.hasChildren()) {
			HashMap<String, PC_Property> hm = prop.getPropertys();
			HashMap<String, PC_Property> thm = getPropertys();
			for (Entry<String, PC_Property> e : hm.entrySet()) {
				if (thm.containsKey(e.getKey())) {
					thm.get(e.getKey()).replaceWith(e.getValue());
				} else {
					thm.put(e.getKey(), e.getValue());
				}
			}
		} else {
			value = prop.value;
			comment = prop.comment;
		}
	}

	public PC_Property getProperty(String key, Object defaultValue, String[] comment) {
		String[] keys = key.split("\\.");
		PC_Property prop = this;
		for (int i = 0; i < keys.length; i++) {
			if (prop.hasChildren()) {
				HashMap<String, PC_Property> hm = prop.getPropertys();
				if (hm.containsKey(keys[i])) {
					prop = hm.get(keys[i]);
				} else {
					if (i == keys.length - 1) {
						hm.put(keys[i], prop = new PC_Property(defaultValue, comment));
					} else {
						hm.put(keys[i], prop = new PC_Property());
					}
				}
			} else {
				break;
			}
		}
		return prop;
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int defaultValue, String... comment) {
		return getProperty(key, defaultValue, comment).getInt();
	}

	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}

	public float getFloat(String key, float defaultValue, String... comment) {
		return getProperty(key, defaultValue, comment).getFloat();
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultValue, String... comment) {
		return getProperty(key, defaultValue, comment).getBoolean();
	}

	public String getString(String key) {
		return getString(key, "");
	}

	public String getString(String key, String defaultValue, String... comment) {
		return getProperty(key, defaultValue, comment).getString();
	}

	public void setInt(String key, int i) {
		PC_Property prop = getProperty(key, i, null);
		prop.setValue(i, prop.getComment());
	}

	public void setFloat(String key, float f) {
		PC_Property prop = getProperty(key, f, null);
		prop.setValue(f, prop.getComment());
	}

	public void setBoolean(String key, boolean b) {
		PC_Property prop = getProperty(key, b, null);
		prop.setValue(b, prop.getComment());
	}

	public void setString(String key, String s) {
		PC_Property prop = getProperty(key, s, null);
		prop.setValue(s, prop.getComment());
	}

}
