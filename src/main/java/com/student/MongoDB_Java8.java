package com.student;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import com.mongodb.MongoException;
import com.student.db.MongoUtil;
import com.student.exception.DuplicateStudentException;
import com.student.exception.InvalidCGPAException;
import com.student.exception.InvalidFieldException;
import com.student.exception.StudentNotFoundException;
import com.student.model.Student;
import com.student.service.StudentService;

public class MongoDB_Java8 {

    public static void loadRecords(StudentService service,int startId) {
        Random random = new Random();
        String[] names = {"Dishal", "Rahul", "Priya", "Ananya", "Kiran", "Arjun", "Sneha"};
        String[] department = {"CSE", "ECE", "IT", "MECH", "CHEMICAL", "CIVIL", "EEE", "FASHION", "BIOTECH"};
        int[] years = {1, 2, 3, 4};
        String[] courses = {"BTech", "MTech", "BBA", "MBA", "BCA", "MCA", "BCom", "MCom", "BSW", "MSW", "BS", "MS"};
        String[] cities = {"Chennai", "Bangalore", "Hyderabad", "Mumbai", "Pune", "Delhi", "Munaar", "Coimbatore"};
        String[] gender = {"Male", "Female"};

        for (int i = startId; i < startId+1000; i++) {
            String name = names[random.nextInt(names.length)];
            String dept = department[random.nextInt(department.length)];
            int year = years[random.nextInt(years.length)];
            String course = courses[random.nextInt(courses.length)];
            double mark = 50 + random.nextDouble() * 50;
            double cgpa = 6 + random.nextDouble() * 4;
            mark = Math.round(mark * 100) / 100.0;
            cgpa = Math.round(cgpa * 100) / 100.0;
            double attendance = 60 + random.nextDouble() * 40;
            attendance = Math.round(attendance * 100) / 100.0;
            String city = cities[random.nextInt(cities.length)];
            String gen = gender[random.nextInt(gender.length)];
            int sem = random.nextInt(8) + 1;

            LocalDate start = LocalDate.of(2022, 1, 1);
            LocalDate end = LocalDate.of(2025, 12, 31);

            long days = ChronoUnit.DAYS.between(start, end);

            LocalDate randomDate = start.plusDays(random.nextInt((int) days));
            String admissionDate = randomDate.toString();
            try {
                service.insertStudent(new Student(100 + i, name, dept, year, course, mark, cgpa, attendance, city, gen, sem, admissionDate));
            } catch (DuplicateStudentException | InvalidCGPAException ex) {

            }
        }
    }

    public static void addStudents(StudentService service) {
        try {
            service.insertStudent(
                    new Student(101, "Dishal", "CSE", 2,
                            "BTech", 91, 8.9, 95,
                            "Chennai", "Male", 4,
                            "2024-08-01"));

            service.insertStudent(
                    new Student(102, "Rahul", "ECE", 2,
                            "BTech", 85, 8.2, 90,
                            "Bangalore", "Male", 4,
                            "2024-08-01"));

            service.insertStudent(
                    new Student(103, "Priya", "IT", 2,
                            "BTech", 88, 8.5, 92,
                            "Hyderabad", "Female", 4,
                            "2024-08-01"));

            service.insertStudent(
                    new Student(104, "Ananya", "CSE", 3,
                            "BTech", 93, 9.1, 96,
                            "Chennai", "Female", 5,
                            "2023-08-01"));

            service.insertStudent(
                    new Student(105, "Kiran", "ECE", 1,
                            "BTech", 78, 7.8, 85,
                            "Mumbai", "Male", 2,
                            "2025-08-01"));

            service.insertStudent(
                    new Student(106, "Arjun", "IT", 3,
                            "BTech", 90, 8.8, 94,
                            "Pune", "Male", 6,
                            "2023-08-01"));

            service.insertStudent(
                    new Student(107, "Sneha", "CSE", 4,
                            "BTech", 95, 9.4, 98,
                            "Delhi", "Female", 8,
                            "2022-08-01"));
        } catch (DuplicateStudentException | InvalidCGPAException e) {
            System.out.println(e.getMessage());
        }

    }

    private static Object readValue(Scanner in, String field) {
        Object value;
        switch (field) {
            case "cgpa":
            case "attendance":
            case "marks":
                value = Double.valueOf(in.nextLine());
                break;

            case "year":
            case "semester":
            case "studentId":
                value = Integer.valueOf(in.nextLine());
                break;

            default:
                value = in.nextLine();
                break;
        }
        return value;
    }

    public static void main(String[] args) {
        StudentService service = new StudentService();

        Scanner in = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("1: Insert Student");
                System.out.println("2: Find Student By Id");
                System.out.println("3: Update Student");
                System.out.println("4: Delete Student");
                System.out.println("5: Search Student");
                System.out.println("6: Sort Student");
                System.out.println("7: Department-wise Student Count");
                System.out.println("8: Department-wise Average CGPA");
                System.out.println("9: Top 10 Students");
                System.out.println("10: City-wise Student Count");
                System.out.println("11: Attendance Analysis");
                System.out.println("12: Pagination");
                System.out.println("13: Export To CSV");
                System.out.println("14: Exit");
                System.out.println("15. Load Records - 1000 records");
                System.out.println("Enter your choice : ");
                int choice = in.nextInt();

                switch (choice) {
                    case 1:
                        in.nextLine();
                        System.out.println("Enter the data as listed in order : \nstudentId name department year course marks cgpa attendance city gender semester admissionDate :\n");
                        String[] s = in.nextLine().split(" ");
                        if (s.length != 12) {
                            System.out.println("No of inputs you have provided is wrong");
                            break;
                        }
                        service.insertStudent(new Student(Integer.parseInt(s[0]), s[1], s[2], Integer.parseInt(s[3]),
                                s[4], Double.parseDouble(s[5]), Double.parseDouble(s[6]), Double.parseDouble(s[7]),
                                s[8], s[9], Integer.parseInt(s[10]), s[11]));
                        System.out.println("Student Inserted Successfully");
                        break;
                    case 2:
                        System.out.println("Enter the id to find student");
                        int id = in.nextInt();
                        service.findStudentById(id);
                        break;
                    case 3:
                        System.out.println("Enter the id : ");
                        int id_update = in.nextInt();
                        in.nextLine();
                        System.out.println("Enter the field to update : ");
                        String field = in.nextLine();
                        System.out.println("Enter the value to update : ");
                        Object value = readValue(in, field);
                        service.UpdateStudent(id_update, field, value);
                        break;

                    case 4:
                        in.nextLine();
                        System.out.println("Enter the field to delete the respective record by its field : ");
                        String field_del = in.nextLine();
                        System.out.println("Enter the value to delete the record based on the above field : ");
                        Object value_del = readValue(in, field_del);
                        service.deleteStudent(field_del, value_del);
                        break;
                    case 5:
                        in.nextLine();
                        System.out.println("Enter the field to search : ");
                        String field_search = in.nextLine();
                        System.out.println("Enter the value to search : ");
                        Object value_search = readValue(in, field_search);
                        service.searchStudent(field_search, value_search);
                        break;
                    case 6:
                        in.nextLine();
                        System.out.println("Enter the field to sort : ");
                        String field_sort = in.nextLine();
                        System.out.println("Enter the order (A/D) : ");
                        String order = in.next();
                        service.sorting(field_sort, order.toLowerCase().equals("a"), in);
                        break;
                    case 7:
                        System.out.println("Department-wise Student Count: ");
                        service.countStudentsByDepartment();
                        break;
                    case 8:
                        System.out.println("Department-wise Average CGPA: ");
                        service.averageCgpaByDepartment();
                        break;
                    case 9:
                        System.out.println("Top 10 Students : ");
                        service.top10Students();
                        break;
                    case 10:
                        System.out.println("City-wise Student Count : ");
                        service.countStudentsByCity();
                        break;
                    case 11:
                        System.out.println("Attendance analysis: ");
                        service.attendanceAnalysis();
                        break;
                    case 12:
                        System.out.print("Page: ");
                        int page = in.nextInt();
                        System.out.print("Page Size: ");
                        int size = in.nextInt();

                        service.pagination(page, size);
                        break;
                    case 13:
                        System.out.println("CSV report: ");
                        service.exportToCSV();
                        break;
                    case 14:
                        MongoUtil.close();
                        in.close();
                        return;
                    case 15:
                        System.out.println("Records starting from which number: ");
                        int startId=in.nextInt();
                        loadRecords(service,startId);
                        System.out.println("1000 records loaded successfully");
                        break;
                    default:
                        System.out.println("Invlid choice. provide a valid choice");
                }
            } catch (InvalidCGPAException | StudentNotFoundException | InvalidFieldException | DuplicateStudentException e) {
                System.out.println("Execption raised : " + e.getMessage());
            } catch (MongoException e) {

                System.out.println("Database Error");
            } catch (IOException e) {
                System.out.println("IOError");
            } catch (InputMismatchException e) {

                System.out.println("Invalid Input Type");
                in.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("Invalid Number Format");
            }
        }
    }
}
