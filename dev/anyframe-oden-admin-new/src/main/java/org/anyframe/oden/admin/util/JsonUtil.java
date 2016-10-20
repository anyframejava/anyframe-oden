package org.anyframe.oden.admin.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Primitives;

public class JsonUtil {

	public static <T> T jsonToGeneric(String json, Class<T> target) throws Exception {
		if(json.startsWith("[{") && (json.endsWith("}]\n") || json.endsWith("}]"))){
			json = json.substring(1, json.lastIndexOf("]"));
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Object object = gson.fromJson(json, (Type) target);
		return Primitives.wrap(target).cast(object);
	}
}
