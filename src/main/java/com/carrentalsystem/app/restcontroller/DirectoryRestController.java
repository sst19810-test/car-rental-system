package com.carrentalsystem.app.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import java.io.File;

@RestController
@RequestMapping("/api/directory")
public class DirectoryRestController {

    @GetMapping("/find")
    public ResponseEntity<Integer> find(@RequestParam("username") String username,
                                        @RequestParam("password") String password) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory.newDocumentBuilder().parse(new File("users.xml"));
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "/users/user[name/text()='" + username
                + "' and password/text()='" + password + "']";
        NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        return ResponseEntity.ok(nodes.getLength());
    }
}
