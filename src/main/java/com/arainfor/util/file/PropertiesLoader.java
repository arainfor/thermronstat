package com.arainfor.util.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by arainfor on 12/26/14.
 */
public class PropertiesLoader {

    Properties props = new Properties();

    public PropertiesLoader(String pathFileName) throws IOException {
        InputStream inputStream = new FileInputStream(pathFileName);
        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + pathFileName + "' not found in the classpath");
        }
    }

    public Properties getProps() {
        return props;
    }
}
