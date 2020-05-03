package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                    NoteEntry._ID + " INTEGER PRIMARY KEY," +
                    NoteEntry.COLUMN_NAME_Date + " TEXT," +
                    NoteEntry.COLUMN_NAME_State + " TEXT,"+
                    NoteEntry.COLUMN_NAME_Id + " TEXT,"+
                    NoteEntry.COLUM_NAME_Priority+" TEXT,"+
                    NoteEntry.COLUMN_NAME_content + " TEXT)";




    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TodoContract() {
    }

    public static class NoteEntry implements BaseColumns {


        public static final String TABLE_NAME = "todoEntry";

        public static final String COLUMN_NAME_Id = "id";

        public static final String COLUMN_NAME_Date = "date";

        public static final String COLUMN_NAME_State = "state";

        public static final String COLUMN_NAME_content = "content";

        public static  final String COLUM_NAME_Priority = "priority";
    }


}
