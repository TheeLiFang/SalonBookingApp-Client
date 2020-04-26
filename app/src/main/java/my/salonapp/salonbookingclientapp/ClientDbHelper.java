package my.salonapp.salonbookingclientapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ClientDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    public static final String DATABASE_NAME = "client.db";

    // Table name
    private static final String TABLE_NAME = "client";

    // Column names
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_CLIENT_NAME = "client_name";
    public static final String COLUMN_CLIENT_PASSWORD = "client_password";
    public static final String COLUMN_CLIENT_EMAIL = "client_email";
    public static final String COLUMN_CLIENT_PHONE = "client_phone";
    public static final String COLUMN_CLIENT_ALLERGIC_REMARK = "client_allergic_remark";
    public static final String COLUMN_CLIENT_REMARK = "client_remark";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_CLIENT_ID + " INTEGER,"
                    + COLUMN_CLIENT_NAME + " TEXT,"
                    + COLUMN_CLIENT_PASSWORD + " TEXT,"
                    + COLUMN_CLIENT_EMAIL + " TEXT,"
                    + COLUMN_CLIENT_PHONE + " TEXT,"
                    + COLUMN_CLIENT_ALLERGIC_REMARK + " TEXT,"
                    + COLUMN_CLIENT_REMARK + " TEXT"
                    + ")";

    private Context context;

    public ClientDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create client table
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Insert a new client
    public long addClient(Client client) {
        // Get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CLIENT_ID, client.getClientId());
        values.put(COLUMN_CLIENT_NAME, client.getClientName());
        values.put(COLUMN_CLIENT_PASSWORD, client.getClientPassword());
        values.put(COLUMN_CLIENT_EMAIL, client.getClientEmail());
        values.put(COLUMN_CLIENT_PHONE, client.getClientPhone());
        values.put(COLUMN_CLIENT_ALLERGIC_REMARK, client.getClientAllergicRemark());
        values.put(COLUMN_CLIENT_REMARK, client.getClientRemark());

        // Insert row
        long id = db.insert(TABLE_NAME, null, values);

        // Close db connection
        db.close();

        // Return newly inserted row id
        return id;
    }

    // Insert a new list of client
    public void addAllClients(List<Client> clients) {
        SQLiteDatabase db = getWritableDatabase();

        for (Client client : clients) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_CLIENT_ID, client.getClientId());
            values.put(COLUMN_CLIENT_NAME, client.getClientName());
            values.put(COLUMN_CLIENT_EMAIL, client.getClientEmail());
            values.put(COLUMN_CLIENT_PHONE, client.getClientPhone());
            values.put(COLUMN_CLIENT_ALLERGIC_REMARK, client.getClientAllergicRemark());
            values.put(COLUMN_CLIENT_REMARK, client.getClientRemark());

            db.insert(TABLE_NAME, null, values);
        }

        // Close db connection
        db.close();
    }

    // Get client
    public Client getClient() {
        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s LIMIT 1",
                TABLE_NAME);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Client client = null;

        if (cursor.moveToFirst()) {
            // Prepare client object
            client = new Client(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_ALLERGIC_REMARK)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_REMARK)));
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return client
        return client;
    }

    // Get client ID
    public int getClientId() {
        int id = 0;

        // Select all query
        String selectQuery = String.format("SELECT %1$s FROM %2$s LIMIT 1",
                COLUMN_CLIENT_ID, TABLE_NAME);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID));
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return client ID
        return id;
    }

    // Get client name
    public String getClientName() {
        String name = "";

        // Select all query
        String selectQuery = String.format("SELECT %1$s FROM %2$s LIMIT 1",
                COLUMN_CLIENT_NAME, TABLE_NAME);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME));
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return client name
        return name;
    }

    // Get a client by client ID
    public Client getClientById(int id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        COLUMN_CLIENT_ID, COLUMN_CLIENT_NAME, COLUMN_CLIENT_PASSWORD,
                        COLUMN_CLIENT_EMAIL, COLUMN_CLIENT_PHONE, COLUMN_CLIENT_ALLERGIC_REMARK,
                        COLUMN_CLIENT_REMARK
                },
                COLUMN_CLIENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Prepare client object
        Client client = new Client(
                cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_ALLERGIC_REMARK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_REMARK)));

        // Close db connection
        cursor.close();

        // Return client
        return client;
    }

    // Get all list of clients
    public ArrayList<Client> getAllClients() {
        ArrayList<Client> clients = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s ASC",
                TABLE_NAME, COLUMN_CLIENT_ID);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                clients.add(new Client(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_ALLERGIC_REMARK)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_REMARK))));
            } while (cursor.moveToNext());
        }

        // Close connection
        cursor.close();
        db.close();

        // Return clients list
        return clients;
    }

    // Get all list of clients
    public ArrayList<Client> getAllSpinnerClients() {
        ArrayList<Client> clients = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT DISTINCT %1$s, %2$s FROM %3$s ORDER BY %4$s ASC",
                COLUMN_CLIENT_ID, COLUMN_CLIENT_NAME, TABLE_NAME, COLUMN_CLIENT_NAME);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                clients.add(new Client(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME))));
            } while (cursor.moveToNext());
        }

        // Close connection
        cursor.close();
        db.close();

        // Return clients list
        return clients;
    }

    // Update client by ID
    public int updateClientById(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CLIENT_NAME, client.getClientName());
        values.put(COLUMN_CLIENT_EMAIL, client.getClientEmail());
        values.put(COLUMN_CLIENT_PHONE, client.getClientPhone());
        values.put(COLUMN_CLIENT_ALLERGIC_REMARK, client.getClientAllergicRemark());
        values.put(COLUMN_CLIENT_REMARK, client.getClientRemark());

        // Updating row
        return db.update(TABLE_NAME, values, COLUMN_CLIENT_ID + "=?",
                new String[]{String.valueOf(client.getClientId())});
    }

    // Update client password by ID
    public int updateClientPasswordById(int id, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CLIENT_PASSWORD, password);

        // Updating row
        return db.update(TABLE_NAME, values, COLUMN_CLIENT_ID + "=?",
                new String[]{String.valueOf(id)});
    }


    // Delete all clients
    public void deleteAllClients() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
