package SDK;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class API{

    private static Logger logJava = Logger.getLogger(API.class);

    public static JSONObject getMethod(String URI_GET)  throws Exception{
        String cliente;
        JSONObject data;

        try{
            logJava.info("Establecer conexion con el servidor");
            try {
                //Iniciar cliente
                HttpClient client = HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();

                //Realizar peticion GET
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(URI_GET))
                        .GET().build();

                //Almacenar datos recibidos
                cliente = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .join();

                if(cliente.equals("")){
                    throw new Exception("Se realizo peticion GET, pero los datos de entrada estan vacios");
                }

            } catch (Exception e) {
                throw new Exception("No se pudo establecer conexión con el servidor");
            }

            //Almacenar datos recibidos en objeto JSON "data"
            logJava.info("Almacenar datos de entrada en un objeto JSON");
            try {
                data = new JSONObject(cliente);
                JSONObject dataContent;
                dataContent = data.getJSONObject("data");

                //Verificar que data no este vacio
                if (dataContent.toString().equals("{}")) {
                    throw new Exception("El contenido de data esta vacio");
                }
            } catch (Exception e) {

                if (e.getMessage() == null) {
                    throw new Exception("No se encontro data o es invalido");
                }else{
                    throw e;
                }

            }
        }catch (Exception e){
            logJava.error(e.getMessage());
            throw e;
        }

        return data;
    }

    public static void postMethod(String URI_Post, String inboxId, String token ) throws Exception {
        logJava.info("Inicio de envio de datos");

        //datos de entrada
        String json;

        //generar post
        HttpPost request;
        MultipartEntityBuilder builder;

        try {
            // URL del servidor en Node.js
            try {
                json = JSONManagement.finalJSON(inboxId, token);
            } catch (Exception e) {
                throw e;
            }

                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                logJava.info("Ejecucion de post");

                try {
                    request = new HttpPost(URI_Post);
                } catch (Exception e) {
                    logJava.error("No se pudo iniciar HttpPost");
                    throw new Exception("No se pudo iniciar HttpPost");
                }

                try {
                    //Crear multipart entity para el envio de datos
                    logJava.info("Creacion de multipart");
                    builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.STRICT);

                    //Anexar JSON final al envio
                    builder.addTextBody("payload", json, ContentType.APPLICATION_JSON);
                } catch (Exception e) {
                    logJava.error("No se pudo generar multipart entity");
                    throw new Exception("No se pudo generar multipart entity");
                }

                //Anexar imagenes al envio de datos

                PathFiles[] pathFiles = PathFiles.obtenerInstancias();

                for (PathFiles path : pathFiles) {

                    switch (path.getDataType()){
                        case "jpg":
                            try {
                                logJava.info("Adicion de imagenes a multipart");
                                if (path.getPathArray() != null) {
                                    for (String imagePath : path.getPathArray()) {
                                        if(imagePath != "" && imagePath != null){
                                            File imageFile = new File(imagePath);
                                            builder.addBinaryBody("image", imageFile, ContentType.IMAGE_JPEG, imageFile.getName());
                                        }
                                    }
                                } else {
                                    logJava.error("Se inicia la clase PathFiles con extension 'jpg', pero no contiene datos");
                                }
                            } catch (Exception e) {
                                logJava.error("No se pudo añadir imagenes a multipart");
                                throw new Exception("No se pudo añadir imagenes a multipart entity");
                            }

                            break;

                        case "txt":
                            //Anexar archivos txt al envio de datos
                            try {
                                logJava.info("Adicion de archivos txt a multipart");
                                if (path.getPathArray() != null /* || !path.getPathArray()[0].equals("") */) {
                                    for (String filePath : path.getPathArray()) {
                                        if(filePath != "" && filePath != null){
                                            File file = new File(filePath);
                                            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
                                        }

                                    }
                                } else {
                                    logJava.error("Se inicia la clase PathFiles con extension 'txt', pero no contiene datos");
                                }
                            } catch (Exception e) {
                                logJava.error("No se pudo añadir archivos a multipart");
                                throw new Exception("No se pudo añadir archivos a multipart entity");
                            }


                            break;

                        default:
                            logJava.error("La extension" + path.getDataType() + " no ha sido configurada para su envio");
                    }

                }

                //Construccion de HTTP entity
                try {
                    logJava.info("Construccion final de HTTPEntity");
                    HttpEntity multipartEntity = builder.build();

                    //Preparan envio de datos
                    request.setEntity(multipartEntity);
                } catch (Exception e) {
                    logJava.error("No se pudo establecer HttpEntity");
                    throw new Exception("No se pudo establecer HttpEntity");
                }

                //Ejecutar request y procesar respuesta
                try {
                    logJava.info("Ejecucion de request");
                    CloseableHttpResponse response = httpClient.execute(request);
                    int statusCode = response.getCode();
                    String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                    System.out.println("Codigo de estatus = "+statusCode);

                } catch (Exception e) {
                    logJava.error("No se pudo ejecutar request");
                    throw new Exception("No se pudo ejecutar request");
                }
            } catch (Exception e) {
                logJava.error("No se pudo generar request en API");
                throw e;
            }

        } catch (Exception e) {
            logJava.error("Fallo envio de datos");
            throw e;
        }

    }
}
