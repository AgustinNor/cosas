package DTO;

import org.openqa.selenium.WebDriver;

public class DtoService {

    WebDriver webdriver; //controlador web
    String screenFolder; //ruta de imagenes
    String step; //paso actual de la tarea
    Integer stepNum; //numero del paso actual

    public DtoService(WebDriver webdriver, String screenFolder, String step, Integer stepNum) {
        this.webdriver = webdriver;
        this.screenFolder = screenFolder;
        this.step = step;
        this.stepNum = stepNum;
    }

    public WebDriver getWebdriver() {
        return webdriver;
    }

    public void setWebdriver(WebDriver webdriver) {
        this.webdriver = webdriver;
    }

    public String getScreenFolder() {
        return screenFolder;
    }

    public void setScreenFolder(String screenFolder) {
        this.screenFolder = screenFolder;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public Integer getStepNum() {
        return stepNum;
    }

    public void setStepNum(Integer stepNum) {
        this.stepNum = stepNum;
    }

}
