package com.gwx.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @ClassName: PropertiesUtil
 * @Description:
 * @Auther: Thomas
 * @Version: 1.0
 * @create 2021/5/30 14:55
 */
public class PropertiesUtil {
    public static Properties parsePro(String filePath) {
        Properties prop = new Properties();
        InputStream in = PropertiesUtil.class.getResourceAsStream(filePath);
        try {
            prop.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
