package SDK;

import DTO.DtoService;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilesManagement {

    private static Logger logJava = Logger.getLogger(FilesManagement.class);

    public static void takeScreeshot(DtoService dtoService, PathFiles pathImage, InputData inputData) throws Exception {

        logJava.info("Realizar captura de pantalla");

        String newFile;
        WebDriver driver;
        String paso;
        String projectId;
        String inboxId;
        String folderLocation;
        File capturaPantalla;
        String file;
        String folderPath = dtoService.getScreenFolder();
        String newFolderLocation;


        try {

            try {
                paso = dtoService.getStep();
                logJava.info("Paso: " + paso);
                if (paso == null) {
                    logJava.error("Al Capturar pantalla el numero del paso esta vacio");
                    throw new Exception("Al Capturar pantalla el numero del paso esta vacio");
                }
            } catch (Exception e) {
                logJava.error("En Capturar pantalla no se pudo establecer numero de paso o es invalido");
                throw new Exception("En Capturar pantalla no se pudo establecer numero de paso o es invalido");
            }

            try {
                projectId = inputData.getProjectId();
                if (projectId == null) {
                    logJava.error("En Capturar pantalla projectId esta vacio");
                    throw new Exception("En Capturar pantalla projectId esta vacio");
                }
            } catch (Exception e) {
                logJava.error("En Capturar pantalla no se pudo establecer projectId o es invalido");
                throw new Exception("En Capturar pantalla no se pudo establecer projectId o es invalido");
            }

            try {
                inboxId= inputData.getInboxId();
                if (inboxId == null) {
                    logJava.error("En Capturar pantalla inboxId esta vacio");
                    throw new Exception("En Capturar pantalla inboxId esta vacio");
                }
            } catch (Exception e) {
                logJava.error("En Capturar pantalla no se pudo establecer inboxId o es invalido");
                throw new Exception("En Capturar pantalla no se pudo establecer inboxId o es invalido");
            }

            try {
                driver = dtoService.getWebdriver();
                if (driver == null) {
                    logJava.error("En Capturar pantalla, driver esta vacio");
                    throw new Exception("En Capturar pantalla, driver esta vacio");
                }
            } catch (Exception e) {
                logJava.error("En Capturar pantalla, no se pudo establecer driver o es invalido");
                throw new Exception("En Capturar pantalla, no se pudo establecer driver o es invalido");
            }

            try {
                folderLocation = "evidences/" + projectId + "/" + inboxId + "/";
                newFolderLocation = folderPath + "/evidences/" + projectId + "/" + inboxId + "/";

            } catch (Exception e) {
                logJava.error("No se pudo establecer ubicacion para capturas de pantalla");
                throw new Exception("No se pudo establecer ubicacion para capturas de pantalla");
            }

            try {
                new File(newFolderLocation).mkdirs();
            } catch (Exception e) {
                logJava.error("No se pudo generar carpeta para almacenar capturas de pantalla");
                throw new Exception("No se pudo generar carpeta para almacenar capturas de pantalla");
            }

            try {
                newFile = newFolderLocation + actualTime() + " " + paso + ".jpg";
                file = folderLocation + actualTime() + " " + paso + ".jpg";
            } catch (Exception e) {
                logJava.error("No se pudo establecer nombre de captura de pantalla");
                throw new Exception("No se pudo establecer nombre de captura de pantalla");
            }

            // Capturar la pantalla del navegador como un objeto de tipo File
            try {
                capturaPantalla = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            } catch (Exception e) {
                logJava.error("No se pudo realizar la captura de pantalla");
                throw new Exception("No se pudo realizar la captura de pantalla");
            }

            //Adjuntar ruta en datos de salida
            try{
                JSONManagement.screenshotsPhase(dtoService, file);
            }catch (Exception e){
                throw e;
            }

            try {
                formatPath(pathImage, newFile);

            } catch (Exception e) {
                logJava.error(e.getMessage());
                throw e;

            }

            // Guardar la captura de pantalla en un archivo
            try {
                capturaPantalla.renameTo(new File(newFile));
            } catch (Exception e) {
                logJava.error("No se pudo almacenar la captura de pantalla");
                new Exception("No se pudo almacenar la captura de pantalla");
            }

        } catch (Exception e) {
            throw e;
        }

    }

    public static void generateTxt(DtoService dtoService, String body, String nameFile, PathFiles pathImage, InputData inputData) throws Exception{
        String projectId;
        String inboxId;
        String actualTime;

        String folderLocation;
        String newFile;
        BufferedWriter buffer;
        String file;
        String folderPath = dtoService.getScreenFolder();
        String newFolderLocation;


        try{
            logJava.info("Gen   erar txt");
            try {
                projectId = inputData.getProjectId();
                inboxId = inputData.getInboxId();
            } catch (Exception e) {
                logJava.error("No se pudo obtener datos de dtoService para generar txt");
                throw new Exception("No se pudo obtener datos de dtoService para generar txt");
            }

            try {
                actualTime = actualTime();
            } catch (Exception e) {
                logJava.error("No se pudo obtener hora actual para generar txt");
                throw new Exception("No se pudo obtener hora actual para generar txt");
            }

            try {
                folderLocation = "evidences/" + projectId + "/" + inboxId + "/";
                newFolderLocation = folderPath + "/evidences/" + projectId + "/" + inboxId + "/";

                newFile = newFolderLocation + actualTime + " " + nameFile + ".txt";
                file = folderLocation + actualTime + " " + nameFile  + ".txt";
            } catch (Exception e) {
                logJava.error("No se pudo establecer ubicacion para almacenar txt");
                throw new Exception("No se pudo establecer ubicacion para almacenar txt");
            }

            try {
                new File(newFolderLocation).mkdirs();
            } catch (Exception e) {
                logJava.error("No se pudo generar txt");
                throw new Exception("No se pudo generar txt");
            }

            try {
                FileWriter writer = new FileWriter(newFile);
                buffer = new BufferedWriter(writer);
            } catch (Exception e) {
                logJava.error("No se pudo establecer configuracion para editar el archivo");
                throw new Exception("No se pudo establecer configuracion para editar el archivo");
            }

            try {
                buffer.write(body);
                buffer.newLine();
            } catch (Exception e) {
                logJava.error("No se pudo añadir texto a archivo txt");
                throw new Exception("No se pudo añadir texto a archivo txt");
            }

            // Cerrar el BufferedWriter
            try {
                buffer.close();
            } catch (Exception e) {
                logJava.error("No se pudo cerrar editor de txt");
                throw new Exception("No se pudo cerrar editor de txt");
            }

            try {
                formatPath(pathImage, newFile);
            } catch (Exception e) {
                logJava.error(e.getMessage());
                throw e;

            }

            JSONManagement.txtFileJSON(dtoService, file);

        }catch (Exception e){
            throw e;
        }
    }

    public static void formatPath(PathFiles pathFile, String newFile) throws Exception {
        try {
            String[] newArray = new String[pathFile.getPathArray().length + 1];

            System.arraycopy(pathFile.getPathArray(), 0, newArray, 0, pathFile.getPathArray().length);

            pathFile.setPathArray(newArray);

            pathFile.getPathArray()[newArray.length - 1] = newFile;

        } catch (Exception e) {
            logJava.error("No se pudo establecer path de archivos");
            throw new Exception("No se pudo establecer path de archivos");
        }

    }

    public static String actualTime() throws Exception {
        try {
            //Obtener hora actual
            DateFormat dateFormat = new SimpleDateFormat("HHmmss");
            Date date = new Date();
            return dateFormat.format(date);
        } catch (Exception e) {
            logJava.error("No se pudo establecer hora actual en HHmmss");
            throw new Exception("No se pudo establecer hora actual en HHmmss");
        }

    }

}
