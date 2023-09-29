package auto;

import DTO.DtoService;
import SDK.InputData;
import SDK.PathFiles;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import SDK.FilesManagement;

public class SearhPage {

    private static Logger logJava = Logger.getLogger(SearhPage.class);

    static WebDriver driver = null;
    static Integer stepNum = 0;

    public static String searhPage(DtoService dtoService, PathFiles pathImages, InputData inputData) throws Exception {
        String value;
        WebElement results;

        stepNum = 0;
        try{
            logJava.info("Inicio - buscar pagina web");

            JSONObject inputs = inputData.getInputs();

            try{
                driver = dtoService.getWebdriver();
            }catch (Exception e){
                throw new Exception("No se pudo establecer driver para buscar pagina web");
            }

            try {
                Thread.sleep(2000);
                dtoService.setStep("Pagina de inicio");
                dtoService.setStepNum(++stepNum);
                FilesManagement.takeScreeshot(dtoService, pathImages, inputData);

            } catch (Exception e) {
                throw e;
            }

            putText(inputs, dtoService, pathImages, inputData);

            Thread.sleep(3000);

            try{
                results = driver.findElement(By.id("result-stats"));
            }catch (Exception e){
                throw new Exception("No se encontro el numero de la cantidad de busquedas");
            }

            Thread.sleep(1000);

            try{
                value = results.getText();
            }catch (Exception e){
                throw new Exception("No se pudo obtener nombre de la primera pagina encontrada");
            }




        }catch (Exception e){
            logJava.error(e.getMessage());
            dtoService.setStep(e.getMessage());
            dtoService.setStepNum(++stepNum);
            FilesManagement.takeScreeshot(dtoService, pathImages, inputData);
            throw e;
        }

        return value;
    }

    private static void putText(JSONObject inputs, DtoService dtoService, PathFiles pathImages, InputData inputData) throws Exception {
        WebElement searh = null;
        String putSearch;

        try{
            try{
                putSearch = inputs.getString("BUSCAR");
                searh = driver.findElement(By.id("APjFqb"));
                searh.sendKeys(putSearch);
            }catch (Exception e){
                throw new Exception("No se pudo extraer datos de inputs para buscar pagina web");
            }

            try {
                Thread.sleep(2000);
                dtoService.setStep("Ingresar texto de busqueda");
                dtoService.setStepNum(++stepNum);
                FilesManagement.takeScreeshot(dtoService, pathImages, inputData);

            } catch (Exception e) {
                throw e;
            }

            try{
                searh.sendKeys(Keys.ENTER);
            }catch (Exception e){
                throw new Exception("No se pudo presionar 'Enter' para buscar pagina web");
            }

            try {
                Thread.sleep(2000);
                dtoService.setStep("Buscar pagina web");
                dtoService.setStepNum(++stepNum);
                FilesManagement.takeScreeshot(dtoService, pathImages, inputData);

            } catch (Exception e) {
                throw e;
            }


        }catch (Exception e){

            throw e;
        }
    }
}
