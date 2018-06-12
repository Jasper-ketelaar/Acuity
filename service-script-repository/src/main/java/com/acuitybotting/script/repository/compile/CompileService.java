package com.acuitybotting.script.repository.compile;

import com.acuitybotting.script.repository.compile.util.UnzipUtility;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
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

    public void jar(File classDirectory, File outputJar) throws IOException, ManifestException {
        Manifest manifest1 = new Manifest();
        manifest1.addConfiguredAttribute(new Manifest.Attribute("Manifest-Version", "1.0"));

        Jar jar = new Jar();
        jar.setProject(new Project());
        jar.addConfiguredManifest(manifest1);
        jar.setBasedir(classDirectory);
        jar.setDestFile(outputJar);
        jar.setIncludes("com/**");
        jar.execute();
    }

    public void compile(File botLib, File input, File output){
        Set<String> javaFiles = new HashSet<>();
        findFilesToCompile(input, javaFiles);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String classesString = javaFiles.stream().collect(Collectors.joining(" "));
        if (!output.exists()) output.mkdir();
        compiler.run(null, null, null, "-d", output.getAbsolutePath(), "-cp", botLib.getAbsolutePath(), classesString);
    }

    private void findFilesToCompile(File parent, Set<String> result ){
        File[] children = parent.listFiles();
        if (children == null) return;
        for (File child : children) {
            if (child.getName().endsWith(".java")) result.add(child.getAbsolutePath());
            if (child.isDirectory()) findFilesToCompile(child, result);
        }
    }

    public void unzip(File inputZip, File outputDir) throws IOException {
        new UnzipUtility().unzip(inputZip.getPath(), outputDir.getPath());
    }
}
