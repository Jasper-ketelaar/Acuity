package com.acuitybotting.script.repository.compile;

import com.acuitybotting.script.repository.compile.util.UnzipUtility;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by Zachary Herridge on 6/12/2018.
 */

@Service
public class CompileService {

    public void unzip(File input, File output) throws IOException {
        new UnzipUtility().unzip(input.getPath(), output.getPath());
    }
}
