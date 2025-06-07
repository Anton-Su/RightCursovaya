package com.example.rightcursovaya;

public class Timetable {

    public Timetable(Long id, String name, String surname, String patronymic, String birthday_date, String specialization, String start_vacation_date, String end_vacation_date) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.birthday_date = birthday_date;
        this.specialization = specialization;
        this.start_vacation_date = start_vacation_date;
        this.end_vacation_date = end_vacation_date;
    }

    public Timetable() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setBirthday_date(String birthday_date) {
        this.birthday_date = birthday_date;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setStart_vacation_date(String start_vacation_date) {
        this.start_vacation_date = start_vacation_date;
    }

    public void setEnd_vacation_date(String end_vacation_date) {
        this.end_vacation_date = end_vacation_date;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getBirthday_date() {
        return birthday_date;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getStart_vacation_date() {
        return start_vacation_date;
    }

    public String getEnd_vacation_date() {
        return end_vacation_date;
    }

    private Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String birthday_date;

    private String specialization;

    private String start_vacation_date;

    private String end_vacation_date;

}
