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

    public void obfuscate(File allatori, File configPlaceHolder, File config, File input, File output) throws Exception {
        String allatoriConfig = Files.readAllLines(configPlaceHolder.toPath()).stream().collect(Collectors.joining("\n"));
        allatoriConfig = allatoriConfig.replaceFirst("<jar placeholder=\"placeholder\"/>", "<jar out=\"" + output.getPath() + "\" in=\"" + input.getPath() + "\"/>");
        Files.write(config.toPath(), allatoriConfig.getBytes());
        obfuscate(allatori, config);
    }

    public void obfuscate(File allatori, File config) throws Exception {
        final ClassLoader loader = URLClassLoader.newInstance(new URL[]{allatori.toPath().toUri().toURL()}, ObfuscatorService.class.getClassLoader());
        Class<?> clazz = Class.forName("com.allatori.Obfuscate", true, loader);
        Method method = clazz.getMethod("main", String[].class);
        method.invoke(null, (Object) new String[]{config.getPath()});
    }
}
