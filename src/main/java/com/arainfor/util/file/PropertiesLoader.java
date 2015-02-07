package com.arainfor.util.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by arainfor on 12/26/14.
 */
public class PropertiesLoader {

    private final Properties props = new Properties();

    public PropertiesLoader(String pathFileName) throws IOException {
        InputStream inputStream = new FileInputStream(pathFileName);
        props.load(inputStream);
    }

    public Properties getProps() {
        return props;
    }
}
