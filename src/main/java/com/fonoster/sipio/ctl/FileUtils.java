package com.fonoster.sipio.ctl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

class FileUtils {
    private ObjectMapper yamlReader;
    private ObjectMapper mapper;

    FileUtils() {
        this.yamlReader = new ObjectMapper(new YAMLFactory());
        this.mapper = new ObjectMapper();
    }

    void writeFile(String path, String text) throws IOException {
        File file = new File(path);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(text);
        out.close();
    }

    String readFile (String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path), java.nio.charset.StandardCharsets.UTF_8);
        String result = "";

        Iterator i = lines.iterator();
        while(i.hasNext()) {
            result += i.next() + "\n";
        }

        return result;
    }

    String getJsonString(String yamlFile) throws IOException {
        String yaml = this.readFile(yamlFile);
        Object obj = this.yamlReader.readValue(yaml, java.lang.Object.class);
        return this.mapper.writeValueAsString(obj);
    }

}
