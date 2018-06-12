package com.acuitybotting.script.repository;

import com.acuitybotting.script.repository.obfuscator.ObfuscatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */
@Component
public class ScriptRepositoryRunner implements CommandLineRunner{

    private final ObfuscatorService obfuscatorService;

    @Autowired
    public ScriptRepositoryRunner(ObfuscatorService obfuscatorService) {
        this.obfuscatorService = obfuscatorService;
    }

    @Override
    public void run(String... strings) throws Exception {
        obfuscatorService.obfuscate(
                new File("C:\\Users\\S3108772\\IdeaProjects\\Acuity Project\\service-script-repository\\src\\main\\resources\\allatori.jar"),
                new File("C:\\Users\\S3108772\\IdeaProjects\\Acuity Project\\service-script-repository\\src\\main\\resources\\config-placeholder.xml"),
                new File("C:\\Users\\S3108772\\IdeaProjects\\Acuity Project\\service-script-repository\\src\\main\\resources\\config.xml"),
                new File("test.jar"),
                new File("test-obbed.jar")
        );
    }
}
