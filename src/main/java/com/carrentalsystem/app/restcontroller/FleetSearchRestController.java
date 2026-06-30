package com.carrentalsystem.app.restcontroller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/fleet")
public class FleetSearchRestController {

    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    public FleetSearchRestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchByName(@RequestParam("name") String name) throws Exception {
        List<String> results = new ArrayList<>();
        String query = "SELECT id, name FROM car WHERE name LIKE '%" + name + "%'";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                results.add(rs.getInt("id") + ":" + rs.getString("name"));
            }
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/lookup")
    public ResponseEntity<List<Object>> lookupByType(@RequestParam("type") String type) {
        String sql = "SELECT * FROM car WHERE car_type = '" + type + "'";
        Query nativeQuery = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object> rows = nativeQuery.getResultList();
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Object>> sorted(@RequestParam("column") String column,
                                               @RequestParam("dir") String dir) {
        String sql = "SELECT id, name, price FROM car ORDER BY " + column + " " + dir;
        Query nativeQuery = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object> rows = nativeQuery.getResultList();
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/filter")
    public ResponseEntity<Integer> countByOwner(@RequestBody String owner) throws Exception {
        String query = "SELECT COUNT(*) FROM car WHERE owner_name = '" + owner + "'";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            int count = rs.next() ? rs.getInt(1) : 0;
            return ResponseEntity.ok(count);
        }
    }
}
