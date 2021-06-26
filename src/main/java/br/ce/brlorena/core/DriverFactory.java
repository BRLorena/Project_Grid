package br.ce.brlorena.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import br.ce.brlorena.core.Propriedades.TipoExec;

public class DriverFactory {

//	private static WebDriver driver;
	private static ThreadLocal<WebDriver> threadDriver = new ThreadLocal<WebDriver>() {
		@Override
		protected synchronized WebDriver initialValue() {
			return initDriver();
		}
	};

	private DriverFactory() {
	}

	public static WebDriver getDriver() {
		return threadDriver.get();
	}

	public static WebDriver initDriver() {
		WebDriver driver = null;
		if (Propriedades.TIPOEXEC == TipoExec.LOCAL) {
			switch (Propriedades.BROWSER) {
			case FIREFOX:
				System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");
				driver = new FirefoxDriver();
				break;

			case CHROME:
				System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
				driver = new ChromeDriver();
				break;
			}
		}
		if (Propriedades.TIPOEXEC == TipoExec.GRID) { // inform url and Capabilities = browsers
			DesiredCapabilities cap = null;
			switch (Propriedades.BROWSER) {
			case FIREFOX:
				cap = DesiredCapabilities.firefox();
				break;
			case CHROME:
				cap = DesiredCapabilities.chrome();
				break;
			}
			try {
				driver = new RemoteWebDriver(new URL(" http://192.168.1.6:4444/wd/hub"), cap);
			} catch (MalformedURLException e) {
				System.err.println("Falha na conexão com o GRID");
				e.printStackTrace();
			}
		}

		driver.manage().window().setSize(new Dimension(1200, 765));
		return driver;
	}

	public static void killDriver() {
		WebDriver driver = getDriver();
		if (driver != null) { // Apenas executa o quit se o driver não for null
			driver.quit();
			driver = null; // volta a ser null
		}
		if (threadDriver != null) {
			threadDriver.remove(); // limpar o drive que a thread esta gerenciando
		}
	}
}
