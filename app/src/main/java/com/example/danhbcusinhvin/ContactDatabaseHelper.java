package com.example.danhbcusinhvin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Quản lý file SQLite: tạo database, tạo bảng và nâng cấp cấu trúc.
 * Các thao tác thêm, sửa, xóa, tìm kiếm sẽ được gọi ở lớp Repository.
 */
public class ContactDatabaseHelper extends SQLiteOpenHelper {
    // Tên file database trên thiết bị. Giữ tên cũ để nhận diện database đã tạo trước đó.
    private static final String DATABASE_NAME = "ContatDatabaseHelper.db";

    // Tăng số phiên bản khi thay đổi cấu trúc để Android gọi onUpgrade().
    private static final int DATABASE_VERSION = 2;

    // Bảng lớp: mỗi lớp có mã, tên lớp, năm khóa và khoa/ngành.
    public static final String TABLE_CLASSES = "Lop";

    // Bảng cựu sinh viên: liên kết với bảng Lop qua CLASS_ID.
    public static final String TABLE_CONTACTS = "CuuSinhVien";

    // Các cột của bảng Lop.
    public static final String COLUMN_CLASS_ID = "CLASS_ID";
    public static final String COLUMN_CLASS_NAME = "TenLop";
    public static final String COLUMN_COURSE_YEAR = "Course_Year";
    public static final String COLUMN_DEPARTMENT = "Department";

    // Các cột của bảng CuuSinhVien.
    public static final String COLUMN_CONTACT_ID = "MSV";
    public static final String COLUMN_NAME = "HoTen";
    public static final String COLUMN_PHONE = "PHONE";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_WORKPLACE = "WORK_PLACE";
    public static final String COLUMN_NOTE = "NOTE";
    public static final String COLUMN_CREATED_AT = "ADD_TIME";
    public static final String COLUMN_UPDATED_AT = "UPDATE_TIME";

    /** Nhận Context để mở hoặc tạo database thuộc ứng dụng. */
    public ContactDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Bật kiểm tra khóa ngoại CuuSinhVien.CLASS_ID -> Lop.CLASS_ID.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Android tự gọi khi database được tạo lần đầu.
        createTables(db);
        ensureCompatibleColumns(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Bổ sung cột còn thiếu, giữ lại những bản ghi đã có.
        ensureCompatibleColumns(db);
    }

    private void createTables(SQLiteDatabase db) {
        // Mã lớp tự tăng; DEFAULT cung cấp giá trị mặc định cho năm và khoa/ngành.
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CLASSES + " ("
                + COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CLASS_NAME + " TEXT NOT NULL, "
                + COLUMN_COURSE_YEAR + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_DEPARTMENT + " TEXT NOT NULL DEFAULT ''"
                + ")");

        // Mã cựu sinh viên tự tăng. CLASS_ID liên kết người với một lớp.
        // Khi xóa lớp, CLASS_ID được đặt NULL để giữ lại thông tin liên hệ.
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + " ("
                + COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_CLASS_ID + " INTEGER, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_WORKPLACE + " TEXT, "
                + COLUMN_NOTE + " TEXT, "
                + COLUMN_CREATED_AT + " TEXT, "
                + COLUMN_UPDATED_AT + " TEXT, "
                + "FOREIGN KEY (" + COLUMN_CLASS_ID + ") REFERENCES "
                + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + ") ON UPDATE CASCADE ON DELETE SET NULL"
                + ")");
    }

    private void ensureCompatibleColumns(SQLiteDatabase db) {
        // Tạo bảng nếu chưa có, sau đó kiểm tra các cột cần thiết.
        createTables(db);

        ensureColumn(db, TABLE_CLASSES, COLUMN_CLASS_NAME, "TEXT NOT NULL DEFAULT ''");
        ensureColumn(db, TABLE_CLASSES, COLUMN_COURSE_YEAR, "INTEGER NOT NULL DEFAULT 0");
        ensureColumn(db, TABLE_CLASSES, COLUMN_DEPARTMENT, "TEXT NOT NULL DEFAULT ''");

        // Chuyển dữ liệu từ tên cột cũ bị viết sai nếu cột đó tồn tại.
        if (hasColumn(db, TABLE_CLASSES, "Deparment")) {
            db.execSQL("UPDATE " + TABLE_CLASSES + " SET " + COLUMN_DEPARTMENT
                    + " = Deparment WHERE " + COLUMN_DEPARTMENT + " = ''");
        }

        ensureColumn(db, TABLE_CONTACTS, COLUMN_NAME, "TEXT");
        ensureColumn(db, TABLE_CONTACTS, COLUMN_CLASS_ID, "INTEGER REFERENCES "
                + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + ") ON DELETE SET NULL");
        ensureColumn(db, TABLE_CONTACTS, COLUMN_PHONE, "TEXT");
        ensureColumn(db, TABLE_CONTACTS, COLUMN_EMAIL, "TEXT");
        ensureColumn(db, TABLE_CONTACTS, COLUMN_WORKPLACE, "TEXT");
        ensureColumn(db, TABLE_CONTACTS, COLUMN_NOTE, "TEXT");
        ensureColumn(db, TABLE_CONTACTS, COLUMN_CREATED_AT, "TEXT");
        ensureColumn(db, TABLE_CONTACTS, COLUMN_UPDATED_AT, "TEXT");
    }

    private void ensureColumn(SQLiteDatabase db, String table, String column, String definition) {
        // Chỉ thêm cột khi chưa có, tránh ALTER TABLE trùng lặp mỗi lần mở database.
        if (!hasColumn(db, table, column)) {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }

    private boolean hasColumn(SQLiteDatabase db, String table, String column) {
        // PRAGMA table_info trả về tên các cột đang có trong bảng SQLite.
        try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null)) {
            int nameColumn = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                if (column.equalsIgnoreCase(cursor.getString(nameColumn))) {
                    return true;
                }
            }
        }
        return false;
    }
}
