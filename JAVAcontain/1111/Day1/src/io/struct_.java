package io;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class struct_ {
    static void main( String[] args) {
        //IO流的框架
        try {
            FileUtils.copyFile(new File("源文件"), new File("目标文件"));
            FileUtils.copyDirectory(new File("源文件夹"), new File("目标文件夹"));
            FileUtils.write(new File("目标文件"), "内容");
            FileUtils.writeLines(new File("目标文件"), Collections.singleton("内容"));
        } catch (IOException e) {
            throw new  RuntimeException(e);
        }


    }
}
