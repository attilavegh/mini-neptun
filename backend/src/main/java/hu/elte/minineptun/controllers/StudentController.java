package hu.elte.minineptun.controllers;

import hu.elte.minineptun.entities.Role;
import hu.elte.minineptun.entities.Student;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import hu.elte.minineptun.entities.Subject;
import hu.elte.minineptun.entities.User;
import hu.elte.minineptun.repositories.StudentRepository;
import hu.elte.minineptun.repositories.SubjectRepository;
import hu.elte.minineptun.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/{username}/subjects")
    public ResponseEntity<List<Subject>> getSubjectsByUsername(@PathVariable String username) {
        Optional<Student> oStudent = studentRepository.findByUsername(username);
        if (!oStudent.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(oStudent.get().getSubjects());
    }

    @PostMapping("/register")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        Optional<User> oStudent = userRepository.findByUsername(student.getUsername());

        if (oStudent.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        student.setId(null);
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setRole(Role.ROLE_STUDENT);
        student.setSemester(1);
        student.setYearStarted(Calendar.getInstance().get(Calendar.YEAR));
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @PatchMapping("/subjects")
    public ResponseEntity<Student> manageSubjects(@RequestBody Student student) {
        if (student.getSubjects() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Student> oStudent = studentRepository.findByUsername(student.getUsername());
        if (!oStudent.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        for (Subject subject: student.getSubjects()) {
            Optional<Subject> oSubject = subjectRepository.findById(subject.getId());
            if (!oSubject.isPresent()) {
                return ResponseEntity.notFound().build();
            }
        }

        oStudent.get().setSubjects(student.getSubjects());
        return ResponseEntity.ok(studentRepository.save(oStudent.get()));
    }
}

