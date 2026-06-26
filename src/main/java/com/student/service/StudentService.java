package com.student.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.student.db.MongoUtil;
import com.student.exception.DuplicateStudentException;
import com.student.exception.InvalidCGPAException;
import com.student.exception.InvalidFieldException;
import com.student.exception.StudentNotFoundException;
import com.student.model.Student;

public class StudentService {

    private static final Set<String> validFields
            = new HashSet<>(Arrays.asList(
                    "studentId",
                    "studentName",
                    "department",
                    "year",
                    "course",
                    "marks",
                    "cgpa",
                    "attendance",
                    "city",
                    "gender",
                    "semester",
                    "admissionDate"
            ));
    MongoDatabase db = MongoUtil.getDatabase();
    MongoCollection<Document> collection = db.getCollection("students");

    public StudentService() {
        collection.createIndex(
                Indexes.ascending("studentId"),
                new IndexOptions().unique(true));

        collection.createIndex(
                Indexes.ascending("department"));

        collection.createIndex(
                Indexes.ascending("city"));

        collection.createIndex(
                Indexes.ascending("semester"));

        collection.createIndex(
                Indexes.descending("cgpa"));

        collection.createIndex(Indexes.compoundIndex(
                Indexes.ascending("department"),
                Indexes.ascending("semester")
        ));
    }

    public void isPresent(String field) throws InvalidFieldException {
        if (!validFields.contains(field)) {
            throw new InvalidFieldException("Invalid Field : " + field);
        }
    }

    public void validateCgpa(double cgpa) throws InvalidCGPAException {
        if (cgpa < 0 || cgpa > 10) {
            throw new InvalidCGPAException("CGPA must be between 0 and 10");
        }
    }

    public void insertStudent(Student s) throws DuplicateStudentException, InvalidCGPAException {
        Document existing = collection.find(Filters.eq("studentId", s.getStudentId())).first();

        if (existing != null) {
            throw new DuplicateStudentException("Student with ID " + s.getStudentId() + " already exists.");
        }
        validateCgpa(s.getCgpa());
        Document doc = new Document();

        doc.append("studentId", s.getStudentId());
        doc.append("studentName", s.getStudentName());
        doc.append("department", s.getDepartment());
        doc.append("year", s.getYear());
        doc.append("course", s.getCourse());
        doc.append("marks", s.getMarks());
        doc.append("cgpa", s.getCgpa());
        doc.append("attendance", s.getAttendance());
        doc.append("city", s.getCity());
        doc.append("gender", s.getGender());
        doc.append("semester", s.getSemester());
        doc.append("admissionDate", s.getAdmissionDate());
        doc.append("createdDate", s.getCreatedDate().toString());
        doc.append("modifiedDate", s.getModifiedDate().toString());

        collection.insertOne(doc);
        System.out.println("Student Inserted");
    }

    public void findStudentById(int id) throws StudentNotFoundException {
        Document doc = collection.find(Filters.eq("studentId", id)).first();

        if (doc == null) {
            throw new StudentNotFoundException("No student found with ID: " + id);
        }
        printTable(doc);
    }

    public void UpdateStudent(int id, String field, Object value) throws InvalidCGPAException, StudentNotFoundException, InvalidFieldException {
        isPresent(field);
        if (field.equals("cgpa")) {
            validateCgpa((double) value);
        }
        Document doc = collection.find(Filters.eq("studentId", id)).first();

        if (doc == null) {
            throw new StudentNotFoundException("No student found with ID: " + id);
        }
        collection.updateOne(Filters.eq("studentId", id), Updates.combine(
                Updates.set(field, value),
                Updates.set("modifiedDate", java.time.LocalDateTime.now().toString())));
        System.out.println("Updated Successfully");

    }

    public void deleteStudent(String field, Object value) throws InvalidFieldException, StudentNotFoundException {
        isPresent(field);
        Document doc = collection.find(Filters.eq(field, value)).first();

        if (doc == null) {
            throw new StudentNotFoundException("No student found");
        }
        collection.deleteOne(Filters.eq(field, value));
        System.out.println("deleted Successfully");
    }

    public void searchStudent(String field, Object value) throws InvalidFieldException, StudentNotFoundException {
        isPresent(field);
        FindIterable<Document> docs = collection.find(Filters.eq(field, value));

        boolean found = false;

        for (Document doc : docs) {
            found = true;
            printTable(doc);
        }

        if (!found) {
            throw new StudentNotFoundException("No student found");
        }
    }

    public void searchStudents(String field, Object value, String field2, Object value2) throws InvalidFieldException, StudentNotFoundException {
        isPresent(field);
        isPresent(field2);
        FindIterable<Document> docs = collection.find(Filters.and(
                Filters.eq(field, value),
                Filters.eq(field2, value2)
        ));

        boolean found = false;

        for (Document doc : docs) {
            found = true;
            printTable(doc);
        }

        if (!found) {
            throw new StudentNotFoundException("No Student Found");
        }

    }

    public void pagination(int page, int size) {
        if (page <= 0 || size <= 0) {
            System.out.println("Invalid Page or Size");
            return;
        }

        System.out.println("Showing Page " + page);
        FindIterable<Document> docs = collection.find().skip((page - 1) * size).limit(size);
        boolean found = false;
        for (Document doc : docs) {
            found = true;
            printTable(doc);
        }
        if (!found) {
            System.out.println("No Student Found");
        }
    }

    public void sorting(String field, boolean asc, Scanner in) throws InvalidFieldException {
        isPresent(field);
        FindIterable<Document> docs;
        System.out.println("Since there are lots of records, provide page size and number to print limited recrods ");
        System.out.println("Enter page size : ");
        int pageSize = in.nextInt();
        System.out.println("Enter page number : ");
        int pageNumber = in.nextInt();
        if (asc) {
            docs = collection.find().sort(Sorts.ascending(field));
        } else {
            docs = collection.find().sort(Sorts.descending(field));
        }
        boolean found = false;

        docs = docs.skip((pageNumber - 1) * pageSize).limit(pageSize);
        System.out.printf("%-12s %-15s%n", "Student ID", field);
        for (Document doc : docs) {
            found = true;
            System.out.printf("%-12s %-15s%n", doc.get("studentId"), doc.get(field));
        }
        if (!found) {
            System.out.println("No Student Found");
        }
    }

    public void countStudentsByDepartment() {
        AggregateIterable<Document> docs = collection.aggregate(Arrays.asList(
                group("$department", sum("count", 1))
        ));

        docs.forEach((doc) -> System.out.println(doc.getString("_id") + " : " + doc.getInteger("count")));
    }

    public void averageCgpaByDepartment() {
        AggregateIterable<Document> docs = collection.aggregate(Arrays.asList(group("$department", avg("averageCgpa", "$cgpa"))));
        docs.forEach((doc) -> System.out.println(doc.getString("_id") + " : " + doc.getDouble("averageCgpa")));
    }

    public void top10Students() {
        FindIterable<Document> docs = collection.find().sort(Sorts.descending("cgpa")).limit(10);
        docs.forEach((doc) -> System.out.println("Student ID : " + doc.get("studentId") + "  CGPA : " + doc.get("cgpa")));
    }

    public void countStudentsByCity() {
        AggregateIterable<Document> docs = collection.aggregate(Arrays.asList(
                group("$city", sum("count", 1))
        ));
        boolean found = false;
        for (Document doc : docs) {
            found = true;
            System.out.println(doc.getString("_id") + " : " + doc.getInteger("count"));
        }
        if (!found) {
            System.out.println("No Documents Found");
        }
        //docs.forEach((doc)->System.out.println(doc.getString("_id")+" : "+doc.getInteger("count")));

    }

    public void attendanceAnalysis() {
        List<Document> docs = collection.find()
                .into(new ArrayList<>());
        docs.stream()
                .collect(Collectors.groupingBy((doc)
                        -> {
                    double atte = doc.getDouble("attendance");
                    if (atte < 75) {
                        return "less than 75";
                    } else if (atte >= 75 && atte < 85) {
                        return "between 75 to 85";
                    } else {
                        return "greater than 85";
                    }
                }, Collectors.counting()))
                .forEach((cat, counting) -> System.out.println(cat + " : " + counting));
    }

    public void findAllStudents() {
        List<Document> docs = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                docs.add(cursor.next());
            }
        }

        if (docs.isEmpty()) {
            System.out.println("No students found in the collection.");
            return;
        }

        // Column headers and widths
        String[] headers = {"ID", "Name", "Dept", "Yr", "Course", "Marks", "CGPA", "Attend", "City", "Gender", "Sem", "Adm.Date"};
        String[] keys = {"studentId", "studentName", "department", "year", "course", "marks", "cgpa", "attendance", "city", "gender", "semester", "admissionDate"};
        int[] widths = {4, 10, 5, 3, 6, 6, 5, 7, 10, 7, 4, 12};

        // Build border line
        StringBuilder border = new StringBuilder("+");
        for (int w : widths) {
            border.append("-".repeat(w + 2)).append("+");
        }

        // Print header
        System.out.println(border);
        StringBuilder header = new StringBuilder("|");
        for (int i = 0; i < headers.length; i++) {
            header.append(String.format(" %-" + widths[i] + "s |", headers[i]));
        }
        System.out.println(header);
        System.out.println(border);

        // Print each row
        for (Document doc : docs) {
            StringBuilder row = new StringBuilder("|");
            for (int i = 0; i < keys.length; i++) {
                row.append(String.format(" %-" + widths[i] + "s |", String.valueOf(doc.get(keys[i]))));
            }
            System.out.println(row);
        }
        System.out.println(border);
    }

    private void printTable(Document doc) {
        String line = "+----------------+-------------------------+";
        System.out.println(line);
        System.out.printf("| %-14s | %-23s |%n", "Field", "Value");
        System.out.println(line);

        String[][] fields = {
            {"Student ID", String.valueOf(doc.get("studentId"))},
            {"Name", String.valueOf(doc.get("studentName"))},
            {"Department", String.valueOf(doc.get("department"))},
            {"Year", String.valueOf(doc.get("year"))},
            {"Course", String.valueOf(doc.get("course"))},
            {"Marks", String.valueOf(doc.get("marks"))},
            {"CGPA", String.valueOf(doc.get("cgpa"))},
            {"Attendance", String.valueOf(doc.get("attendance"))},
            {"City", String.valueOf(doc.get("city"))},
            {"Gender", String.valueOf(doc.get("gender"))},
            {"Semester", String.valueOf(doc.get("semester"))},
            {"Admission Date", String.valueOf(doc.get("admissionDate"))}
        };

        for (String[] row : fields) {
            System.out.printf("| %-14s | %-23s |%n", row[0], row[1]);
        }
        System.out.println(line);
        System.out.printf("| %-14s | %-23s |%n","Created Date", doc.getString("createdDate"));
        System.out.printf("| %-14s | %-23s |%n","Modified Date", doc.getString("modifiedDate"));
    }

    public void exportToCSV() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("students.csv"));
        List<Document> docs = collection.find().into(new ArrayList<>());
        bw.write("studentId,studentName,department,year,course,marks,cgpa,attendance,city,gender,semester,admissionDate,createdDate,modifiedDate");
        bw.newLine();
        for (Document doc : docs) {
            bw.write(String.join(",",
                    doc.get("studentId").toString(),
                    doc.getString("studentName"),
                    doc.getString("department"),
                    doc.get("year").toString(),
                    doc.getString("course"),
                    doc.get("marks").toString(),
                    doc.get("cgpa").toString(),
                    doc.get("attendance").toString(),
                    doc.getString("city"),
                    doc.getString("gender"),
                    doc.get("semester").toString(),
                    doc.getString("admissionDate"),
                    doc.getString("createdDate"),
                    doc.getString("modifiedDate")
            ));
            bw.newLine();
        }
        System.out.println("Students exported successfully to students.csv");
        bw.close();
    }

}
