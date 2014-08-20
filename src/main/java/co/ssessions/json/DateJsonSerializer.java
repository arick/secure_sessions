package co.ssessions.json;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateJsonSerializer implements JsonSerializer<Date> {

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {

		JsonPrimitive jsonPrimitive = null;
		if (src != null) {
			jsonPrimitive = new JsonPrimitive(src.getTime());
		}
		return jsonPrimitive;
	}

}
