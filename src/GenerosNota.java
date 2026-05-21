import java.util.HashMap;

// preferencia por genero
public class GenerosNota {
	public HashMap<String,Float> notasGeneros = new HashMap<String,Float>();
	public GenerosNota() {
		notasGeneros.put("action",10.0f);
		notasGeneros.put("adventure",9.9f);
		notasGeneros.put("science",9.8f);
		notasGeneros.put("fiction",9.7f);
		notasGeneros.put("fantasy",9.6f);
		notasGeneros.put("crime",9.5f);
		notasGeneros.put("thriller",9.4f);
		notasGeneros.put("animation",9.3f);
		notasGeneros.put("family",9.2f);
		notasGeneros.put("comedy",9.0f);
		notasGeneros.put("romance",8.8f);
		notasGeneros.put("drama",8.6f);
		notasGeneros.put("mystery",8.4f);
		notasGeneros.put("war",8.2f);
		notasGeneros.put("history",8.0f);
		notasGeneros.put("music",7.8f);
		notasGeneros.put("western",7.6f);
		notasGeneros.put("documentary",7.4f);
		notasGeneros.put("horror",7.2f);
		notasGeneros.put("foreign",7.0f);
		notasGeneros.put("tv",6.8f);
		notasGeneros.put("movie",6.6f);
		notasGeneros.put("indie",6.4f);
	}
}
