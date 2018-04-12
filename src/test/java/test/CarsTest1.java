package test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.CoreMatchers.is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CarsTest1 {

    private static final int WAIT_MAX = 4;
    static WebDriver driver;

    @BeforeClass
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chromedriver.exe");

        //Reset Database
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        //Reset Database 
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
    }

    @Test
    //Verify that page is loaded and all expected data are visible
    public void test1() throws Exception {
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            Assert.assertThat(rows.size(), is(5));
            return true;
        });
    }

    @Test
    //Verify the filter functionality 
    public void test2() throws Exception {
        WebElement filter = driver.findElement(By.id("filter"));
        filter.sendKeys("2002");

        WebElement tBodyElement = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = tBodyElement.findElements(By.tagName("tr"));
        Assert.assertThat(rows.size(), is(2));
    }

    @Test
    //Verify the clear filter  functionality
    public void test3() throws Exception {
        WebElement filter = driver.findElement(By.id("filter"));
        filter.sendKeys(Keys.CONTROL + "a");
        filter.sendKeys(Keys.BACK_SPACE);

        // filter.clear(); virker ikke
        WebElement tBodyElement = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = tBodyElement.findElements(By.tagName("tr"));
        Assert.assertThat(rows.size(), is(5));
    }

    @Test
    //Verify the sort by year functionality 
    public void test4() throws Exception {
        WebElement year = driver.findElement(By.id("h_year"));
        year.click();

        WebElement tBodyElement = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = tBodyElement.findElements(By.tagName("tr"));
        Assert.assertThat(rows.get(0).getText(), is("938 1996 11/4/1999 Jeep Grand Cherokee Air, moon roof, loaded 4.799,00 kr. Edit | Delete"));
        Assert.assertThat(rows.get(4).getText(), is("940 2005 1/6/2005 Volvo V70 Super cool car 34.000,00 kr. Edit | Delete"));
    }

    @Test
    //Verify the edit by year functionality 
    public void test5() throws Exception {
        WebElement tBodyElement = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = tBodyElement.findElements(By.tagName("tr"));

        WebElement topRow = driver.findElements(By.xpath("//tbody/tr")).get(0);
        WebElement edit = topRow.findElements(By.tagName("a")).get(0);
        edit.click();

        WebElement description = driver.findElement(By.id("description"));
        description.sendKeys(Keys.CONTROL + "a");
        description.sendKeys(Keys.BACK_SPACE);
        description.sendKeys("Cool car");

        WebElement saveButton = driver.findElement(By.id("save"));
        saveButton.click();

        Assert.assertThat(rows.get(0).getText(), is("938 1996 11/4/1999 Jeep Grand Cherokee Cool car 4.799,00 kr. Edit | Delete"));
    }

    @Test
    //Verify the sumbit error functionality 
    public void test6() throws Exception {
        WebElement saveButton = driver.findElement(By.id("save"));
        saveButton.click();

        WebElement sumbitErrElement = driver.findElement(By.id("submiterr"));
        String actualString = sumbitErrElement.getText();
        Assert.assertEquals("All fields are required", actualString);

        WebElement tBodyElement = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = tBodyElement.findElements(By.tagName("tr"));
        Assert.assertThat(rows.size(), is(5));
    }

    @Test
    //Verify the save car functionality 
    public void test7() throws Exception {
        WebElement carsInFactory1 = driver.findElement(By.tagName("h4"));
        String actualString1 = carsInFactory1.getText();

        Assert.assertEquals("Number of cars in the factory right now: 5", actualString1);

        WebElement year = driver.findElement(By.id("year"));
        WebElement registered = driver.findElement(By.id("registered"));
        WebElement make = driver.findElement(By.id("make"));
        WebElement model = driver.findElement(By.id("model"));
        WebElement description = driver.findElement(By.id("description"));
        WebElement price = driver.findElement(By.id("price"));

        year.sendKeys("2008");
        registered.sendKeys("2002-5-5");
        make.sendKeys("Kia");
        model.sendKeys("Rio");
        description.sendKeys("New car");
        price.sendKeys("3100");

        WebElement saveButton = driver.findElement(By.id("save"));
        saveButton.click();

        WebElement carsInFactory2 = driver.findElement(By.tagName("h4"));
        String actualString2 = carsInFactory2.getText();

        Assert.assertEquals("Number of cars in the factory right now: 6", actualString2);
    }

}
