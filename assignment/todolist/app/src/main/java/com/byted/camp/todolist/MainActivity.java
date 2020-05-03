package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.db.TodoContract.NoteEntry;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.byted.camp.todolist.beans.State.DONE;
import static com.byted.camp.todolist.beans.State.TODO;
import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {

        // TODO 从数据库中查询数据，并转换成 JavaBeans

        List<Note> noteList = new ArrayList<Note>();
        //从数据库中取数据

        TodoDbHelper helper = new TodoDbHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sortOrder =
                NoteEntry.COLUM_NAME_Priority + " DESC";

        Cursor cursor = db.query(
                NoteEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder              // The sort order排列方式
        );

        while(cursor.moveToNext()){

            String id = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NAME_Id));
            String content = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NAME_content));
            String state = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NAME_State));
            String date = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NAME_Date));
            String priority = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUM_NAME_Priority));

            long idLong = Long.valueOf(id);
            Note note = new Note(idLong);
            note.setContent(content);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date dateTime=null;
            try{
                dateTime = simpleDateFormat.parse(date);
                note.setDate(dateTime);
            }catch (ParseException e){
                e.printStackTrace();
            }

            if(state.equals("TODO")){
                note.setState(TODO);
            }else if(state.equals("DONE")){
                note.setState(DONE);
            }else{
                out.println("wrong state");
            }

            note.setPriority(Integer.parseInt(priority));

            noteList.add(note);
        }

        cursor.close();

        return noteList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        TodoDbHelper dbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String getdate = simpleDateFormat.format(note.getDate());

        String selection = NoteEntry.COLUMN_NAME_Date + " LIKE ?";
        String[] selectionArgs = {getdate};
        int deletedRows = db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);


    }

    private void updateNode(Note note) {
        // TODO:更新数据
        TodoDbHelper dbHelper1 = new TodoDbHelper(this);
        SQLiteDatabase db = dbHelper1.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NAME_State,"DONE");
        values.put(NoteEntry.COLUM_NAME_Priority,0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String getdate = simpleDateFormat.format(note.getDate());

        String selection = NoteEntry.COLUMN_NAME_Date + " LIKE ?";
        String[] selectionArgs = {getdate};

        int count = db.update(
                NoteEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);


    }

}
