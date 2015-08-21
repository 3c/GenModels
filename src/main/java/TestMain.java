import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by cx on 8/21/15.
 */
public class TestMain {

    private static final Logger logger = LoggerFactory.getLogger(TestMain.class);


    static TestMain testMain;

    public static void main(String[] args) {

        System.out.println(TestMain.class.getPackage().getName());
        URL location = TestMain.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location.getFile());
    }

}
