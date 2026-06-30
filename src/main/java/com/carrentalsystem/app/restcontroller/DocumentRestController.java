package com.carrentalsystem.app.restcontroller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api/docs")
public class DocumentRestController {

    private static final Logger log = LoggerFactory.getLogger(DocumentRestController.class);

    @GetMapping("/read")
    public ResponseEntity<String> read(@RequestParam("file") String file) throws Exception {
        File target = new File("/var/data/documents/" + file);
        byte[] content = Files.readAllBytes(target.toPath());
        return ResponseEntity.ok(new String(content, StandardCharsets.UTF_8));
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam("name") String name,
                                       @RequestBody byte[] body) throws Exception {
        Path destination = Paths.get("uploads").resolve(name);
        Files.write(destination, body);
        return ResponseEntity.ok("saved to " + destination);
    }

    @GetMapping("/fetch")
    public ResponseEntity<String> fetch(@RequestParam("url") String url) throws Exception {
        URL target = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) target.openConnection();
        connection.setRequestMethod("GET");
        StringBuilder body = new StringBuilder();
        try (InputStream in = connection.getInputStream()) {
            int b;
            while ((b = in.read()) != -1) {
                body.append((char) b);
            }
        }
        return ResponseEntity.ok(body.toString());
    }

    @GetMapping("/redirect")
    public void redirect(@RequestParam("next") String next, HttpServletResponse response) throws Exception {
        response.sendRedirect(next);
    }

    @GetMapping("/echo")
    public void echo(@RequestParam("message") String message, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.getWriter().write("<div>" + message + "</div>");
    }

    @PostMapping("/parse")
    public ResponseEntity<String> parse(@RequestBody String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return ResponseEntity.ok(document.getDocumentElement().getNodeName());
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restore(@RequestBody String encoded) throws Exception {
        byte[] data = Base64.getDecoder().decode(encoded);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            return ResponseEntity.ok(obj.getClass().getName());
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<String> extract(@RequestParam("archive") String archive) throws Exception {
        File destDir = new File("uploads");
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(archive)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
        return ResponseEntity.ok("extracted");
    }

    @GetMapping("/audit")
    public ResponseEntity<String> audit(@RequestParam("user") String user, HttpServletRequest request) {
        log.info("Access granted for user " + user + " from " + request.getRemoteAddr());
        return ResponseEntity.ok("logged");
    }
}
