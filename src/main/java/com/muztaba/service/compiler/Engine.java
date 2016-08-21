package com.muztaba.service.compiler;

import com.muztaba.service.compiler.util.CompileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by seal on 8/9/16.
 */
public class Engine {
    private static final Logger logger = LoggerFactory.getLogger(Compiler.class);

    private static final String outputFileName = "/output.txt";

    public CompileStatus compile(ProcessBuilder processBuilder) {
        logger.info("Code compilation started...");
//        logger.debug("fileLocation, {}", fileLocation);
//        logger.info("file location, {}", fileLocation);
        boolean compiled = true;
//        processBuilder.directory(new File(fileLocation));
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            String temp;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                process.waitFor();
                while ((temp = reader.readLine()) != null) {
                    compiled = false;
                    logger.error("{}", temp);
                }
                if (!compiled) {
                    inputStream.close();
                    return CompileStatus.COMPILE_ERROR;
                }
                inputStream.close();
                return CompileStatus.COMPILE_SUCCESS;
            }
        } catch (IOException | InterruptedException e) {
            logger.info("error at compilation, {}", e);
        }

        return CompileStatus.COMPILE_ERROR;
    }

    public CompileStatus execute(ProcessBuilder processBuilder) {
        logger.info("Execution started");

        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            if (!process.waitFor(2000, TimeUnit.MILLISECONDS))
                return CompileStatus.TIME_LIMIT_EXIT;
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return CompileStatus.RUN_TIME_ERROR;
            }
        } catch (Exception e) {
            return CompileStatus.EXECUTION_ERROR;
        }

        return CompileStatus.EXECUTION_SUCCESS;
    }
}