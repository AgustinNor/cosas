package SDK;

import DTO.DtoService;
import org.apache.log4j.Logger;

public class JSONManagement {

    private static Logger logJava = Logger.getLogger(JSONManagement.class);

    static String screenshotsPhase = "";
    static String txtFile = "";

    //JSON final
    static String outputJson = "";
    static String screenShotsJson = "";
    static String fileJson = "";
    static String successJson = "";
    static String invalidTasks = "";

    //Resultados automatizacion

    //El resultado es un String
    public static void msjResult(String result, String output, Integer step) throws Exception{
        try{
            System.out.println("output = " + output);

            String msj;
            msj = "\"" + output + "\": \"" + result + "\"";

            addResult(msj, step);
        }catch (Exception e){
            throw new Exception("No se pudo generar resultado con un dato de tipo String");
        }
    }

    //El resultado es un Integer
    public static void msjResult(Integer result, String output, Integer step) throws Exception{
        try{
            String msj;
            msj = "\"" + output + "\": " + result;

            addResult(msj, step);
        }catch (Exception e){
            throw new Exception("No se pudo generar resultado con un dato de tipo String");
        }

    }

    //El resultado es un Boolean
    public static void msjResult(Boolean result, String output, Integer step) throws Exception{
        try{
            String msj;
            msj = "\"" + output + "\": " + result;

            addResult(msj, step);
        }catch (Exception e){
            throw new Exception("No se pudo generar resultado con un dato de tipo String");
        }
    }

    //Agrupar resultados
    public static void addResult(String msj, Integer step) throws Exception{
        try{
            if(outputJson.equals("")){
                outputJson = "{\n\"step\": " + step + ", \n" + msj + "\n}";
            } else {
                outputJson = outputJson + ",\n{\n\"step\": " + step + ", \n" + msj + "\n}";
            }

        }catch (Exception e){
            throw new Exception("No se pudo agrupar resultado de automatizacion");
        }

    }

    //Capturas de pantalla

    //Adjuntar captura de pantalla
    public static void screenshotsPhase(DtoService dtoService, String newFile) throws Exception {
        try {
            //Formar Json con los path de las capturas de pantalla
            Integer stepNum = dtoService.getStepNum();

            if (screenshotsPhase.equals("")){
                screenshotsPhase = "{\n"
                        + "                        \"identifier\": " + stepNum + ",\n"
                        + "                        \"path\": \"" + newFile + "\" \n"
                        + "                    }";
            }else {
                screenshotsPhase = screenshotsPhase + ",\n{\n"
                        + "                        \"identifier\": " + stepNum + ",\n"
                        + "                        \"path\": \"" + newFile + "\" \n"
                        + "                    }";
            }


        } catch (Exception e) {
            logJava.error("No se pudo configurar screenshotsPhase");
            throw new Exception("No se pudo configurar screenshotsPhase");
        }

    }

    //Agrupar imagenes
    public static void addScreenshots(Integer step) throws Exception{
        try{
            if(screenShotsJson.equals("")){
                screenShotsJson = "{\n\"step\": " + step + ", \n\"screenshots\": [" + screenshotsPhase + "]}";
            } else {
                screenShotsJson = screenShotsJson + ",\n{\n\"step\": " + step + ", \n\"screenshots\": [" + screenshotsPhase + "]}";
            }

            screenshotsPhase = "";

        }catch (Exception e){
            throw new Exception("No se pudo agrupar capturas de pantalla de automatizaciones");
        }

    }

    //Archivos

    //Adjuntar archivos

    public static void txtFileJSON(DtoService dtoService, String newFile) throws Exception {
        try {
            Integer stepNum = dtoService.getStepNum();

            if(txtFile.equals("")){
                txtFile = "{\n"
                        + "                    \"identifier\": " + stepNum + ",\n"
                        + "                    \"path\": \"" + newFile + "\" \n"
                        + "                 }";
            }else {
                txtFile = txtFile + "{\n"
                        + "                        \"identifier\": " + stepNum + ",\n"
                        + "                        \"path\": \"" + newFile + "\" \n"
                        + "                    }";
            }



        } catch (Exception e) {
            logJava.error("No se pudo configurar txtFileJSON");
            throw new Exception("No se pudo configurar txtFileJSON");
        }
    }

    //Agrupar archivos
    public static void addFile(Integer step) throws Exception{
        try{
            if(fileJson.equals("")){
                fileJson = "{\n\"step\": " + step + ", \n\"files\": [" + txtFile + "]}";
            } else {
                fileJson = fileJson + ",\n{\n\"step\": " + step + ", \n\"files\": [" + txtFile + "]}";
            }
            txtFile = "";
        }catch (Exception e){
            logJava.error("No se pudo configurar txtFileJSON");
            throw new Exception("No se pudo agrupar capturas de pantalla de automatizaciones");
        }

    }

    //Adjuntar success
    public static void addSuccess(Boolean success, Integer step) throws Exception{
        try{
            if(successJson. equals("")){
                successJson = "{\n\"step\": " + step + ", \n\"success\": " + success + "\n}";
            }else {
                successJson = successJson + ",\n{\n\"step\": " + step + ", \n\"success\": " + success + "\n}";
            }

        }catch (Exception e){
            throw new Exception("No se pudo clasificar automatizaciones exitosas o fallidas");
        }

    }

    //Tareas indefinidas
    public static void addInvalidTasks(String invalidTask[])throws Exception{
        try{
            for (String invalid : invalidTask) {
                if (invalid == null) {
                    break;
                }

                if (invalidTasks.equals("")) {
                    invalidTasks = "\"" + invalid + "\"";
                } else {
                    invalidTasks = invalidTasks + ",\"" + invalid + "\"";
                }
            }

        }catch (Exception e){
            throw new Exception("No se pudo separar tareas invalidas");
        }

    }



    //Agrupar todos los JSON para su envio
    public static String finalJSON(String inboxId, String token) throws Exception {
        String finalJSON;
        try {
            logJava.info("Construccion de json con todos los resultados");

            finalJSON = "{\"inboxId\": \"" + inboxId + "\",\n"
                    + "\"token\": \"" + token + "\",\n"
                    + "\"data\": {\n"
                    + "        \"outputs\": [\n"
                    + "            \n"
                    + "                " + outputJson
                    + "            \n"
                    + "        ],\n"
                    + "        \"screenshot_list\": [\n"
                    + "            \n"
                    + "                " + screenShotsJson
                    + "            \n"
                    + "        ],\n"
                    + "        \"file_list\": [\n"
                    + "            \n"
                    + "                " + fileJson
                    + "            \n"
                    + "        ],\n"
                    + "        \"executed_steps\": [\n"
                    + "            \n"
                    + "                " + successJson
                    + "            \n"
                    + "        ]\n"
                    + "    },\n"
                    + "\"undefinedTasks\": [\n"
                    + "     \n"
                    +           invalidTasks
                    + "     \n"
                    + "\n]"
                    + "}";

        } catch (Exception e) {
            logJava.error("No se pudo establecer json de salida");
            throw new Exception("No se pudo establecer json de salida");
        }

        return finalJSON;
    }

}
