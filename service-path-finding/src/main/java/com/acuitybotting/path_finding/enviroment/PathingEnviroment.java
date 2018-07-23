package com.acuitybotting.path_finding.enviroment;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zachary Herridge on 7/23/2018.
 */
@Slf4j
public class PathingEnviroment {

    public static final File BASE = new File(System.getProperty("user.home") + File.separator + "Pathing" + File.separator);

    public static final File JSON = new File(BASE, "json");

    public static final File HPA = new File(JSON, "hpa");
    public static final File NODES = new File(HPA, "nodes");
    public static final File PATHS = new File(HPA, "paths");
    public static final File EDGES = new File(HPA, "edges");
    public static final File REGIONS = new File(HPA, "regions");
    public static final File REGION_FLAGS = new File(HPA, "flags");

    public static final File RS = new File(JSON, "rs");
    public static final File REGION_INFO = new File(RS, "info");

    public static final File XTEAS = new File(BASE, "xteas");

    public static final File ACUITY_RENDERINGS = new File(BASE, "\\img\\acuity_regions\\");
    public static final File RL_RENDERINGS = new File(BASE, "\\img\\rl_regions\\");
    public static File[] IMG_DIRECTORIES = new File[]{ACUITY_RENDERINGS, RL_RENDERINGS};

    public static final File[] DIRECTORIES = {NODES, PATHS, EDGES, REGIONS, REGION_FLAGS, REGION_INFO, XTEAS};

    private static Gson gson = new Gson();

    static {
        for (File directory : DIRECTORIES) {
            if (!directory.exists()) directory.mkdirs();
        }
        for (File directory : IMG_DIRECTORIES) {
            if (!directory.exists()) directory.mkdirs();
        }
    }

    public static void save(File directory, String key, Object value) {
        save(directory, key, value, gson);
    }

    public static void save(File directory, String key, Object value, Gson gson) {
        try {
            Files.write(new File(directory, key + ".json").toPath(), gson.toJson(value).getBytes());
        } catch (IOException e) {
            log.error("Error during write.", e);
        }
    }

    public static <T> Optional<T> loadFrom(File directory, String key, Class<T> type) {
        return loadFrom(directory, key, type, gson);
    }

    public static <T> Optional<T> loadFrom(File directory, String key, Class<T> type, Gson gson) {
        try {
            File file = new File(directory, key + ".json");
            if (!file.exists()) return Optional.empty();
            return Optional.ofNullable(gson.fromJson(new BufferedReader(new FileReader(file)), type));
        } catch (IOException e) {
            log.error("Error during read.", e);
        }
        return Optional.empty();
    }

    public static <T> List<T> loadAllFrom(File directory, Class<T> type) {
        return loadAllFrom(directory, type, gson);
    }

    public static <T> List<T> loadAllFrom(File directory, Class<T> type, Gson gson) {
        List<T> result = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null) return result;
        for (File file : files) {
            try {
                result.add(gson.fromJson(new BufferedReader(new FileReader(file)), type));
            } catch (IOException e) {
                log.error("Error during read.", e);
            }
        }
        return result;
    }

    public static void deleteAll(File directory) {
        File[] files = directory.listFiles();
        if (files == null) return;
        ExecutorUtil.run(30, executorService -> {
            for (File file : files) {
                executorService.submit(file::delete);
            }
        });
    }
}
