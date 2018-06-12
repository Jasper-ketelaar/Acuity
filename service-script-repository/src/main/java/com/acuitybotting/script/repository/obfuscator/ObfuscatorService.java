package com.acuitybotting.script.repository.obfuscator;

import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */
@Service
public class ObfuscatorService {

    public void obfuscate(File allatoriJar, File baseConfig, File activeConfig, File inputJar, File outputJar) throws Exception {
        String baseConfigString = Files.readAllLines(baseConfig.toPath()).stream().collect(Collectors.joining("\n"));
        baseConfigString = baseConfigString.replaceFirst("<jar placeholder=\"placeholder\"/>", "<jar out=\"" + outputJar.getPath() + "\" in=\"" + inputJar.getPath() + "\"/>");
        Files.write(activeConfig.toPath(), baseConfigString.getBytes());
        obfuscate(allatoriJar, activeConfig);
    }

    public void obfuscate(File allatoriJar, File activeConfig) throws Exception {
        final ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{allatoriJar.toPath().toUri().toURL()}, ObfuscatorService.class.getClassLoader());
        Class<?> obfuscateClass = Class.forName("com.allatori.Obfuscate", true, classLoader);
        Method mainMethod = obfuscateClass.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) new String[]{activeConfig.getPath()});
    }
}
