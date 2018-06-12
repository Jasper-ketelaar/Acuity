package com.acuitybotting.script.repository.compile;

import com.acuitybotting.script.repository.compile.util.UnzipUtility;
import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */

@Service
public class CompileService {

    public void compile(File botLib, File input, File output){
        if (!output.exists()) output.mkdir();
        Set<String> javaFiles = new HashSet<>();
        findFilesToCompile(input, javaFiles);
        for (String javaFile : javaFiles) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, "-d", output.getAbsolutePath(), "-cp", botLib.getAbsolutePath(), javaFile);
        }
    }

    private void findFilesToCompile(File file,  Set<String> javaFiles ){
        File[] files = file.listFiles();
        if (files == null) return;
        for (File child : files) {
            if (child.getName().endsWith(".java")) javaFiles.add(child.getAbsolutePath());
            if (child.isDirectory()) findFilesToCompile(child, javaFiles);
        }
    }

    public void unzip(File input, File output) throws IOException {
        new UnzipUtility().unzip(input.getPath(), output.getPath());
    }
}
