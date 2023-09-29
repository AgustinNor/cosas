package service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Driver {

    private static Logger logJava = Logger.getLogger(Driver.class);

    public static WebDriver generateDriver(String browser) throws Exception {

        logJava.info("Inicio configuracion controlador web");

        WebDriver driver;

        browser = browser.toUpperCase();
        browser = browser.replaceAll(" ", "");

        try {

            if (browser.equals("CHROME") || browser.equals("GOOGLECHROME") || browser.equals("GOOGLE CHROME")) {

                try {
                    //Iniciar controlador de Chrome
                    WebDriverManager.chromedriver().setup();

                    //Declarar opciones para iniciar Chrome
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    options.addArguments("--incognito");
                    options.addArguments("--remote-allow-origins=*");
                    options.addArguments("--disable-notifications"); // Opcional: desactiva las notificaciones del navegador
                    options.addArguments("--disable-popup-blocking"); // Opcional: desactiva el bloqueo de ventanas emergentes
                    options.addArguments("--disable-infobars"); // Opcional: desactiva las barras de información

                    //Driver iniciado con las opciones declaradas
                    driver = new ChromeDriver(options);

                } catch (Exception e) {
                    throw new Exception("No se pudo iniciar chrome driver");
                }

            } else {
                throw new Exception("El navegador "+ browser + "no es compatible o es indefinido, la automatizacion es compatible con Chrome");
            }

            logJava.info("Termino configuracion controlador web");

        } catch (Exception e) {
            if (e == null) {
                throw new Exception("Fallo generador driver");
            }
            logJava.error(e.getMessage());
            throw e;
        }

        return driver;

    }
}
