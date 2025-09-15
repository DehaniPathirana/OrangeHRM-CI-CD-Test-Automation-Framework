// src/test/java/com/orangehrm/dataproviders/LoginDataProvider.java
package com.orangehrm.dataproviders;

import org.testng.annotations.DataProvider;
import java.util.Map;

public class LoginDataProvider {

    private static final String TEST_DATA_PATH = "src/test/resources/testdata/login_data.xlsx";

    @DataProvider(name = "validLoginData")
    public Object[][] getValidLoginData() {
        return ExcelDataProvider.getTestData(TEST_DATA_PATH, "ValidLogin");
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] getInvalidLoginData() {
        return ExcelDataProvider.getTestData(TEST_DATA_PATH, "InvalidLogin");
    }

    @DataProvider(name = "loginTestData", parallel = true)
    public Object[][] getAllLoginData() {
        return new Object[][] {
                {"Admin", "admin123", true},
                {"InvalidUser", "admin123", false},
                {"Admin", "InvalidPassword", false},
                {"", "", false},
                {"Admin", "", false},
                {"", "admin123", false}
        };
    }
}