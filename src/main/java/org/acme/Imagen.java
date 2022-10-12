package org.acme;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.faulttolerance.*;

import org.json.JSONException;
import org.json.JSONObject;

@Path ("/imagen")
@Produces(MediaType.APPLICATION_JSON)
public class Imagen {

    @GET
    @Timeout(value=5000L)
    @Retry(maxRetries = 10)
    @Bulkhead(value = 1)
    @Fallback(fallbackMethod = "getFallback")
    @Produces(MediaType.TEXT_PLAIN)
    public String imagen() throws IOException, JSONException{
        String url = "https://api.unsplash.com/photos/random?query=Art&client_id=6SUtk4ov8sdxqeT6tfo9KPXxlcz41sAGf7eCpBlywyw";
        JSONObject json;
        String resultado;

        URL request = new URL (url);

        HttpURLConnection connection = (HttpURLConnection) request.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        
        resultado = convertirImagen(connection);

        json = new JSONObject(resultado);

        return json.toString();
    }

    public String convertirImagen (HttpURLConnection connection) throws IOException, JSONException{
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder resultado = new StringBuilder();
        String linea;
        String res;
        
        while ((linea = rd.readLine()) != null) {   // Mientras el BufferedReader se pueda leer, agregar contenido a resultado
            resultado.append(linea);
        }
        rd.close();   // Cerrar el BufferedReader
        
        res = resultado.toString();   // Regresar resultado, pero como cadena, no como StringBuilder

        return res;
    }

    public String getFallback() throws JSONException {
        String mensaje = "Ocurrio un error al lanzar la peticion a la API.";
        return mensaje;
    }  
}
