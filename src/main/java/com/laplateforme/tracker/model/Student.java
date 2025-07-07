package com.laplateforme.tracker.model;

public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private double grade;

    // Constructeurs
    public Student() {}

    public Student(String firstName, String lastName, int age, double grade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.grade = grade;
    }

    public Student(int id, String firstName, String lastName, int age, double grade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.grade = grade;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }

    @Override
    public String toString() {
        return String.format("Student{id=%d, firstName='%s', lastName='%s', age=%d, grade=%.2f}",
                id, firstName, lastName, age, grade);
    }
}

