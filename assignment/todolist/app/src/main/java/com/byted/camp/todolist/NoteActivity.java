package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.TodoContract.NoteEntry;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.ui.NoteListAdapter;

import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.byted.camp.todolist.beans.State.TODO;

public class NoteActivity extends AppCompatActivity  {

    private EditText editText;
    private Button addBtn;
    private int priority_num=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);


        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        Spinner priority = (Spinner) findViewById(R.id.spinner);
        priority.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("TAG", "is selected" );
                       String content = adapterView.getItemAtPosition(i).toString();
                        switch(content){
                            case "马上处理":
                                priority_num=3;
                                Log.i("TAG", "选择马上处理" );
                                break;
                            case "正常处理":
                                priority_num=2;
                                break;
                            case "可延迟处理":
                                priority_num=1;
                                break;
                            default:
                                break;

                        }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("TAG", "not selected" );
                 priority_num=1;
            }
        });

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();

                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }



                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content) {

        // TODO 插入一条新数据，返回是否插入成功

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NAME_Date, date);
        values.put(NoteEntry.COLUMN_NAME_content, content);
        values.put(NoteEntry.COLUMN_NAME_Id,"111");
        values.put(NoteEntry.COLUMN_NAME_State,"TODO");
        values.put(NoteEntry.COLUM_NAME_Priority,priority_num);

        TodoDbHelper dbhelper = new TodoDbHelper(this);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        long newRowId = db.insert(NoteEntry.TABLE_NAME, null, values);

        if(newRowId > 0)
            return true;
        else
            return false;
    }

}
