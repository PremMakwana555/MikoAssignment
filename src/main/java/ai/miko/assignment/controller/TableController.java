package ai.miko.assignment.controller;

import ai.miko.assignment.enums.Status;
import ai.miko.assignment.model.QueryStatus;
import ai.miko.assignment.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
public class TableController {

    @Autowired
    private TableService tableService;

    @PostMapping("/run")
    public ResponseEntity<Object> create(@RequestBody String query) {

        var result = tableService.executeQuery(query);
        if (result == null)
            return new ResponseEntity<>(QueryStatus.builder().status(Status.SUCCESS).query(query).build(), HttpStatus.CREATED);
        else
            return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<QueryStatus>> history() {
        return ResponseEntity.ok(tableService.getHistory());
    }
}
