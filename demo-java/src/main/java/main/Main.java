package main;

import DTO.DtoService;
import SDK.InputData;
import SDK.PathFiles;
import SDK.API;
import SDK.JSONManagement;
import service.Driver;
import auto.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class    Main {

    private static Logger logJava = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception{

        //Carpeta en la que se esta ejecutando el JAR
        Path jarFolder = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

        //Almacenar argumento de entrada
        String hash;

        //Ruta de la carpeta donde esta el archivo log4j.properties
        String log4jConfigFile = jarFolder + "/log4j.properties";

        //Configuracion properties
        File propertiesFile; //Verificador para encontrar archivo "properties"
        String properties = ""; //Ubicacion archivo "properties.txt"
        InputStream inputStream = null; //Referencia al archivo "properties.txt" cuando se ejecuta el JAR
        BufferedReader reader; //Contenido archivo "properties.txt"

        //Contenido archivo "properties.txt"
        String[] validTasks = null; //Lista de tareas validas
        String uri_get = ""; //URI para realizar peticion GET
        String uri_post = ""; //URI para realizar peticion POST

        //Segmentacion de datos obtenidos en cliente
        InputData inputData;
        JSONObject data; //Objeto JSON que contiene los datos de la variable cliente
        String token = ""; //Variable que contendra el "token" para enviarlo como respuesta
        String projectId; //Variable que contendra el "projectId" para enviarlo como respuesta
        String inboxId = ""; //Variable que contendra el "inboxId" para enviarlo como respuesta
        JSONArray tasks; //Id de tareas a ejecutar
        JSONArray outputs; //Nombres de las claves de los resultados de las tareas
        String url; //URL de la pagina web a automatizar

        //Carpeta que contendra los archivos generados
        String folderPath; //Ruta de almacenamiento de archivos
        File folder; //Ruta para generar la carpeta de almacenamiento

        //Controlador del navegador
        WebDriver driver = null;

        //Variables vacias para generar dtoService
        DtoService dtoService; //Declarar DtoService para almacenar datos generales que se usan en todas las automatizaciones
        String step = ""; //Nombre del paso actual dentro de una tarea

        //Navegador en el que se ejecutara la automatizacion
        String browser;

        //Validar tareas
        String[] tasksList; //Lista de tareas
        String[] validatedTask; //Lista Tareas validas
        String[] invalidTask; //Lista Tareas invalidas
        Integer validatedTaskCount = 0; //Cantidad tareas validas
        Integer invalidTaskCount = 0; //Contador tareas invalidas

        //Ejecutar tareas
        Boolean success; //Indicar si la tarea si finalizo correctamente
        String result; //Resultado de la tarea
        Boolean resultBoolean;

        //Verificar si se enviaron los datos
        Boolean confirmSendData = false;

        //Carpetas temporales
        String temporalFolder; //Carpeta raiz de evidencias
        File temporalFile; //Comprobar si existe la carpeta raiz de evidencias

        //Construir JSON de salida
        PathFiles pathImage = new PathFiles(new String[0], "jpg");
        PathFiles pathTxt = new PathFiles(new String[0], "txt");

        try {
            try {
                // Verificar si el archivo "log4j.properties" existe
                File log4j = new File(log4jConfigFile);

                if (log4j.exists()) {
                    // Configurar Log4j con el archivo de configuración
                    PropertyConfigurator.configure(log4j.toString());
                }

            } catch (Exception e) {
                throw new Exception("No se pudo establecer configuracion de log4j");
            }

            logJava.info("---Inicio de automatizacion---");

            //Verificar si el JAR recibe un solo argumento
            if (args.length != 1) {

                logJava.error("Falta argumento");
                throw new Exception("Falta argumento");

            }

            //Asignar valor del argumento a la variable HASH
            hash = args[0];
            if (hash == null || hash.equals("")) {
                throw new Exception("Argumento invalido");
            }

            // Obtener la referencia al archivo "properties.txt" dentro del JAR
            logJava.info("Inicializar properties");
            try {
                propertiesFile = new File(jarFolder + "/properties.txt");

                //Verificar si el archivo "properties.txt" esta en la carpeta donde se ejecuta el JAR
                if (propertiesFile.exists() && propertiesFile != null) {
                    properties = jarFolder + "/properties.txt";
                } else {
                    //Se utilizara el archivo "properties.txt" que esta en la carpeta "resources/properties/properties.txt" como clase
                    inputStream = Main.class.getResourceAsStream("/properties/properties.txt");
                }

            } catch (Exception exception) {
                logJava.fatal("No se encontro properties");
                throw new Exception("No se encontro properties");
            }

            // Leer el contenido del archivo y asignarlo a una variable
            try {
                if (propertiesFile.exists() && propertiesFile != null) {
                    //Obtener contenido del archivo "properties.txt" que esta en la carpeta del JAR
                    reader = new BufferedReader(new FileReader(properties));
                } else {
                    //Obtener contenido del archivo "properties.txt" que esta como clase al interior del JAR
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                }

                String line;
                while ((line = reader.readLine()) != null) {

                    //Obtener lista de tareas validas
                    if (line.contains("VALID_TASK")) {
                        String[] lineSplit = line.split(":");

                        String search = ";";
                        String[] taskLine = lineSplit[1].split("");
                        Integer cont = 0;
                        for (String taskSeparate : taskLine) {
                            if (taskSeparate.equals(search)) {
                                cont++;
                            }
                        }

                        if(cont > 1) {
                            validTasks = lineSplit[1].split(";");
                        } else if (cont == 1){
                            validTasks = new String[1];
                            validTasks[0] =lineSplit[1].replace(String.valueOf(search),"");

                        } else {
                            validTasks = new String[1];
                            validTasks[0] = lineSplit[1];

                        }
                    }

                    //Obtener URI para la peticion GET
                    if (line.contains("URI_GET")) {
                        String[] lineSplit = line.split("&&");
                        uri_get = lineSplit[1];
                        uri_get = uri_get + hash + "/data";

                    }
                    //Obtener URI para la peticion POST
                    if (line.contains("URI_POST")) {
                        String[] lineSplit = line.split("&&");
                        uri_post = lineSplit[1];
                        uri_post = uri_post + hash + "/report";

                    }
                }
            } catch (Exception e) {
                logJava.error("error al leer el archivo properties");
                throw new Exception("No se pudo leer el archivo properties");
            }

            logJava.info("Establecer conexion con el servidor");
            //Realizar peticion GET para obtener datos de entrada
            try{
                data = API.getMethod(uri_get);
            } catch (Exception e){
                throw e;
            }

            inputData = new InputData("", "", "", null, null, null);

            InputData.setDataInputs(data, inputData);

            token = inputData.getToken();
            projectId = inputData.getProjectId();
            inboxId = inputData.getInboxId();
            tasks = inputData.getTasks();
            outputs = inputData.getOutputs();

            //Generar carpeta de evidencias
            try {
                folderPath = Paths.get(jarFolder + "/evidences/" + projectId + "/" + inboxId + "/files").toString();
                folder = new File(folderPath);
                if (!folder.exists()) {
                    folder.mkdir();
                }
            } catch (Exception exception) {
                logJava.error("No se pudo generar carpeta de evidencias");
                throw new Exception("No se pudo generar carpeta de evidencias");
            }

            logJava.info("Iniciar driver");
            //Configurar controlador del navegador web
            try {
                try {
                    Thread.sleep(2000);

                } catch (Exception e) {
                    throw e;
                }

                //Para utilizar otro navegador, se debe cambiar el valor de la variable browser
                //Realizar las configuraciones correspondientes en la clase Driver segun el navegador a utilizar

                //Para obtener el navegador como parametro utilice la siguiente linea:
                //browser = inputData.getInputs().getString("NAVEGADOR");
                //Debe reemplazar "NAVEGADOR" por la clave ingresada en XRUNNER

                browser = "chrome";

                //Iniciar controlador web
                driver = Driver.generateDriver(browser);

                //Maximizar ventana del navedor
                driver.manage().window().maximize();

                url = inputData.getInputs().getString("URL_AMBIENTE");
                if ("".equals(url)) {
                    logJava.error("El contenido de URL_AMBIENTE esta vacio");
                    throw new Exception("El contenido de URL_AMBIENTE esta vacio");
                }

                //Redirigir navegador a la URL
                driver.get(url);

            } catch (Exception e) {
                logJava.error("Driver no establecido");
                throw e;
            }

            Thread.sleep(2000);

            //Iniciar DtoService con parametros basicos
            try {
                dtoService = new DtoService(driver, jarFolder.toString(), step, 0);
            } catch (Exception e) {
                logJava.error("No se pudo establecer dtoService");
                throw new Exception("No se pudo establecer dtoService");
            }

            logJava.info("Validacion de tareas");

            //Validar tareas
            JSONObject jsonObject;
            String keySearch = "_id";
            try {
                tasksList = new String[tasks.length()];
                for (int i = 0; i < tasks.length(); i++) {
                    jsonObject = tasks.getJSONObject(i);
                    for (String key : jsonObject.keySet()) {
                        // Obtener el valor asociado a la clave
                        Object value = jsonObject.get(key);
                        if (key.equals(keySearch)) {
                            if (value instanceof String) {
                                tasksList[i] = tasks.getJSONObject(i).getString(keySearch);
                            } else {
                                tasksList[i] = value.toString();
                            }
                        } else {
                            tasksList[i] = "undefined";
                        }
                    }
                }

            } catch (Exception e) {
                logJava.error("No se pudo listar las tareas");
                throw new Exception("No se pudo listar las tareas");
            }

            //Separar tareas en validas e invalidas
            try {
                try{
                    validatedTask = new String[tasksList.length];
                    invalidTask = new String[tasksList.length];

                    for (String task : tasksList) {
                        boolean encontrado = false;
                        //Comparar cada tarea con las tareas validas del archivo "properties.txt"
                        for (String elemento : validTasks) {
                            if (elemento.equals(task)) {
                                validatedTask[validatedTaskCount++] = task;
                                encontrado = true;
                            }
                        }
                        //Si la tarea no se igual a ninguna del archivo properties, se considera invalida
                        if (!encontrado) {
                            invalidTask[invalidTaskCount] = task;
                            invalidTaskCount++;
                        }
                    }
                }catch (Exception e){
                    throw new Exception("No se pudo separar tareas validas e invalidas");
                }
                try{
                    JSONManagement.addInvalidTasks(invalidTask);
                }catch (Exception e){
                    throw e;
                }

            } catch (Exception e) {
                logJava.error(e.getMessage());
                throw e;
            }

            //Ejecutar tareas
            for (int i = 0; i < validatedTask.length; i++) {

                success = true;

                if(outputs.length() > i){

                    switch (validatedTask[i]) {

                        case "647fe4625c416371c348a528":
                            //Realizar busqueda en Google
                            result = "";
                            try {

                                try {
                                    logJava.info("Buscar pagina web");

                                    //Realizar proceso automatizado
                                    result = SearhPage.searhPage(dtoService, pathImage, inputData);

                                    //Verificar resultado
                                    if (result.equals("")) {
                                        throw new Exception("No se obtuvo resultado");
                                    }

                                } catch (Exception e) {
                                    logJava.error(e.getMessage());
                                    success = false;
                                }

                                try{
                                    //Almacenar resultado
                                    JSONManagement.msjResult(result, outputs.getString(i), i);
                                } catch (Exception e){
                                    throw e;
                                }

                                try{
                                    //Agrupar capturas de pantalla
                                    JSONManagement.addScreenshots(i);
                                }catch (Exception e){
                                    throw e;
                                }

                            } catch (Exception e) {
                                logJava.error(e.getMessage());
                                success = false;
                            }


                            break;

                        case "6465603178df3b23ec3b3ede":
                            //Registrar resultados de busqueda en un txt
                            resultBoolean = false;
                            try {
                                try {
                                    logJava.info("Registrar cantidad de resultados en un txt");
                                    resultBoolean = NumResults.openWeb(dtoService, pathTxt, inputData);
                                    if (resultBoolean.equals(false)) {
                                        throw new Exception("No se obtuvo resultado");
                                    }

                                    //Adjuntar archivos al JSON que contiene el conjunto de archivos de todas las automatizaciones
                                    JSONManagement.addFile(i);

                                } catch (Exception e) {
                                    logJava.error(e.getMessage());
                                    success = false;
                                }

                                JSONManagement.msjResult(resultBoolean, outputs.getString(i), i);

                            } catch (Exception e) {
                                logJava.error(e.getMessage());
                                success = false;
                            }

                            break;

                        case "64347f1b3d38406ce5a8d843":
                            //Tarea que produce fallo
                            result = "";
                            try {
                                try {
                                    logJava.info("Tarea Fallida");
                                    result = "";
                                    if (result.equals("")) {throw new Exception();}

                                } catch (Exception e) {
                                    logJava.error("Resultado esta vacio");
                                    success = false;
                                }

                                JSONManagement.msjResult(result, outputs.getString(i), i);

                            } catch (Exception e) {
                                success = false;
                            }

                            break;

                        default:
                            logJava.error("Fallo switch");
                    }

                    //Adjuntar resultado al JSON que contiene la verificacion de todas las automatizacion que lograron o no completar su ejecucion
                    JSONManagement.addSuccess(success, i);
                }


            }

            Thread.sleep(2000);

            //Finalizar controlador web
            if (driver != null) {
                driver.quit();
            }

            //Enviar datos a API por peticion POST
            try{
                sendData(uri_post, inboxId, token);
                confirmSendData = true;
            }catch (Exception e){
                throw e;
            }

            Thread.sleep(3000);

            try {
                temporalFolder = jarFolder + "/evidences";
                temporalFile = new File(temporalFolder);

                if (temporalFile.exists() && temporalFile.isDirectory()) {
                    File[] archivos = temporalFile.listFiles();

                    if (archivos != null) {
                        for (File archivo : archivos) {
                            if (archivo.isDirectory()) {
                                borrarDirectorio(archivo); // Si es una subcarpeta, borrar su contenido recursivamente
                            } else {
                                archivo.delete(); // Si es un archivo, borrarlo
                            }
                        }
                    }
                }

            } catch (Exception e) {
                throw new Exception("No se pudo eliminar carpeta temporal");
            }



        } catch (Exception e) {

            if (driver != null) {
                driver.quit();
            }

            if (e == null) {
                logJava.error("Fallo la automatizacion");
                throw new Exception("Fallo la automatizacion");
            }
            try{
                if (confirmSendData.equals(false)){
                    sendData(uri_post, inboxId, token);
                }
            }catch (Exception ex){
                throw ex;
            }


            logJava.error(e.getMessage());
            throw e;
        } finally {
            logJava.info("---Finalizo la ejecucion---");
        }
    }

    private static void borrarDirectorio(File directorio) throws Exception {
        //Eliminar carpeta
        try {
            try {
                File[] archivos = directorio.listFiles();

                if (archivos != null) {
                    for (File archivo : archivos) {
                        if (archivo.isDirectory()) {
                            borrarDirectorio(archivo); // Si es una subcarpeta, borrar su contenido recursivamente
                        } else {
                            archivo.delete(); // Si es un archivo, borrarlo
                        }
                    }
                }

                directorio.delete(); // Borrar la carpeta vacía
            } catch (Exception e) {
                throw new Exception("No se pudo eliminar el directorio");
            }
        } catch (Exception e) {
            throw e;
        }

    }

    //Enviar datos a API
    private static void sendData(String URI_POST, String inboxId, String token)throws Exception{
        try{
            logJava.info("Envio de resultados a servidor");

            try{
                API.postMethod(URI_POST, inboxId, token);
            } catch (Exception e){
                throw e;
            }

        }catch (Exception e){
            throw e;
        }

    }

}