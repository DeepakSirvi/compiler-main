package com.compiler.app.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compiler.app.payload.CodeRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CompileController {

    @PostMapping("/compile")
    public String compileCode(@RequestBody CodeRequest codeRequest) {
        System.err.println("compiler code");
        String language = codeRequest.getLanguage();
        String code = codeRequest.getCode();
        String result;

        try {
            // Save code to a file
            String fileName = saveCodeToFile(language, code);

            // Compile and run the code
            result = executeCode(language, fileName);

        } catch (IOException | InterruptedException e) {
            result = "Error: " + e.getMessage();
        }

        return result;
    }

    private String saveCodeToFile(String language, String code) throws IOException {
        System.err.println("save code to file");
        String extension;
        switch (language) {
            case "java":
                extension = ".java";
                break;
            case "c":
                extension = ".c";
                break;
            case "cpp":
                extension = ".cpp";
                break;
            case "python":
                extension = ".py";
                break;
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }
        String fileName = "temp" + extension;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(code);
        }
        return fileName;
    }

    private String executeCode(String language, String fileName) throws IOException, InterruptedException {
        String command;
        System.err.println("execute code");
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        switch (language) {
            case "java":
                command = isWindows ?
                    String.format("cmd.exe /c javac %s && java %s", fileName, fileName.replace(".java", "")) :
                    String.format("/bin/sh -c javac %s && java %s", fileName, fileName.replace(".java", ""));
                break;
            case "c":
                command = isWindows ?
                    String.format("cmd.exe /c gcc %s -o temp && temp", fileName) :
                    String.format("/bin/sh -c gcc %s -o temp && ./temp", fileName);
                break;
            case "cpp":
                command = isWindows ?
                    String.format("cmd.exe /c g++ %s -o temp && temp", fileName) :
                    String.format("/bin/sh -c g++ %s -o temp && ./temp", fileName);
                break;
            case "python":
                command = isWindows ?
                    String.format("cmd.exe /c python %s", fileName) :
                    String.format("/bin/sh -c python %s", fileName);
                break;
            default:
                throw new IllegalArgumentException("Unsupported language: " + language);
        }

        System.out.println(command);
        Process process = isWindows ?
            Runtime.getRuntime().exec(command) :
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = outputReader.readLine()) != null) {
            output.append(line).append("\n");
        }
        while ((line = errorReader.readLine()) != null) {
            output.append(line).append("\n");
        }
        process.waitFor();
        System.err.println(output.toString());
        return output.toString();
    }
}
