package com.example.student;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@RestController
@RequestMapping("/students")
public class StudentController {
    private List<Student> studentData;

    public StudentController() {
        loadStudentData();
    }

    private void loadStudentData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            ClassPathResource resource = new ClassPathResource("students.json");
            studentData = objectMapper.readValue(resource.getInputStream(), typeFactory.constructCollectionType(List.class, Student.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public ResponseEntity<List<Student>> getStudents(@RequestParam("page") int page,
                                                     @RequestParam("size") int size) {
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, studentData.size());
        List<Student> paginatedStudents = studentData.subList(startIndex, endIndex);

        return ResponseEntity.ok(paginatedStudents);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public ResponseEntity<List<Student>> filterStudents(@RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "marks", required = false) Integer marks) {
        List<Student> filteredStudents = studentData.stream()
                .filter(student -> name == null || student.getName().contains(name))
                .filter(student -> marks == null || student.getTotalMarks() >= marks)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredStudents);
    }
}

