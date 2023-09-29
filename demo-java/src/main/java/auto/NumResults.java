package auto;

import DTO.DtoService;
import SDK.InputData;
import SDK.PathFiles;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import SDK.FilesManagement;

public class NumResults {

    private static Logger logJava = Logger.getLogger(NumResults.class);

    static WebDriver driver = null;
    static Integer stepNumFile = 0;

    public static Boolean openWeb(DtoService dtoService, PathFiles pathTxt, InputData inputData) throws Exception {
        String value;
        WebElement results;
        Boolean generateFile = false;

        stepNumFile = 0;

        String fileName = "";
        try{
            logJava.info("Inicio - Ingresar a pagina web");

            try{
                driver = dtoService.getWebdriver();
            }catch (Exception e){
                throw new Exception("No se pudo establecer driver para encontrar cantidad de resultados de busqueda");
            }

            try{
                results = driver.findElement(By.id("result-stats"));
            }catch (Exception e){
                throw new Exception("No se encontro el numero de la cantidad de resultados de busqueda");
            }

            Thread.sleep(1000);

            try{
                value = results.getText();
            }catch (Exception e){
                throw new Exception("No se pudo obtener la cantidad de resultados de busqueda");
            }

            fileName = "Resultados";

            try{
                dtoService.setStepNum(++stepNumFile);
                FilesManagement.generateTxt(dtoService, value, fileName, pathTxt, inputData);
                generateFile = true;
            }catch (Exception e){
                throw e;
            }

        }catch (Exception e){
            logJava.error(e.getMessage());
            throw e;
        }

        return generateFile;
    }

}
