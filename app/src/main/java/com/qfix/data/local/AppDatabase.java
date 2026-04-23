package com.qfix.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.qfix.data.local.dao.CategoryDao;
import com.qfix.data.local.dao.ComplaintDao;
import com.qfix.data.local.dao.FeedbackDao;
import com.qfix.data.local.dao.UpdateDao;
import com.qfix.data.local.dao.UserDao;
import com.qfix.data.model.Category;
import com.qfix.data.model.Complaint;
import com.qfix.data.model.Feedback;
import com.qfix.data.model.Update;
import com.qfix.data.model.User;
import com.qfix.utils.Converters;

@Database(entities = {Complaint.class, User.class, Feedback.class, Update.class, Category.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ComplaintDao complaintDao();
    public abstract UserDao userDao();
    public abstract FeedbackDao feedbackDao();
    public abstract UpdateDao updateDao();
    public abstract CategoryDao categoryDao();
}
