package com.example.danhbcusinhvin;

final class Contact {
    long id;
    long classID;
    String name;
    String className;
    String phone;
    String email;
    String workplace;
    String note;
    String addedTime;
    String updatedTime;

    // Dùng cho luồng giao diện tạm thời hiện có.
    Contact(String name, String phone, String email, String note,
            String addedTime, String updatedTime) {
        this(0, name, -1, "", phone, email, "", note, addedTime, updatedTime);
    }

    // Dùng khi ánh xạ một dòng liên hệ từ SQLite.
    Contact(long id, String name, long classID, String className, String phone,
            String email, String workplace, String note, String addedTime, String updatedTime) {
        this.id = id;
        this.name = name;
        this.classID = classID;
        this.className = className;
        this.phone = phone;
        this.email = email;
        this.workplace = workplace;
        this.note = note;
        this.addedTime = addedTime;
        this.updatedTime = updatedTime;
    }

    long getID() { return id; }
    String getName() { return name; }
    long getClassID() { return classID; }
    String getClassName() { return className; }
    String getPhone() { return phone; }
    String getEmail() { return email; }
    String getWorkplace() { return workplace; }
    String getNote() { return note; }
    String getAddedTime() { return addedTime; }
    String getUpdatedTime() { return updatedTime; }

    void setId(long id) { this.id = id; }
    void setClassID(long classID) { this.classID = classID; }
}
