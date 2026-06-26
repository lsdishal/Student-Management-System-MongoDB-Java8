package com.student.model;

public class Student extends Audit {

    private int studentId;
    private String studentName;
    private String department;
    private int year;
    private String course;
    private double marks;
    private double cgpa;
    private double attendance;
    private String city;
    private String gender;
    private int semester;
    private String admissionDate;
    private Audit audit;
    
    public Student() {

    }

    public Student(int studentId, String studentName,
            String department, int year,
            String course, double marks,
            double cgpa, double attendance,
            String city, String gender,
            int semester, String admissionDate) {

        this.studentId = studentId;
        this.studentName = studentName;
        this.department = department;
        this.year = year;
        this.course = course;
        this.marks = marks;
        this.cgpa = cgpa;
        this.attendance = attendance;
        this.city = city;
        this.gender = gender;
        this.semester = semester;
        this.admissionDate = admissionDate;
        this.audit = new Audit();
    }

    // Generate Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }
}
