package com.mobileplay.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mobileplay.R;
import com.mobileplay.common.CommonUtils;
import com.mobileplay.doamain.User;
import com.mobileplay.gen.App;
import com.mobileplay.gen.DaoSession;
import com.mobileplay.gen.UserDao;
import com.mobileplay.pager.BasePager;
import com.mobileplay.pager.NetAudioPager;
import com.mobileplay.pager.NetVideoPager;
import com.mobileplay.pager.VideoPager;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_tag;
    public ArrayList<BasePager> basePagers = new ArrayList<>();
    public int pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonUtils.isGrantExternalRW(this, 0);
        initView();
        initListener();

//        basePagers.add(new VideoPager(this));
//        basePagers.add(new AudioPager(this));
//        basePagers.add(new NetVideoPager(this));
//        basePagers.add(new NetAudioPager(this));

        Observable.just(new VideoPager(this), new VideoPager(this), new NetVideoPager(this), new NetAudioPager(this))
                .subscribe(new Consumer<BasePager>() {
                               @Override
                               public void accept(BasePager basePager) throws Exception {
                                   basePagers.add(basePager);
                               }
                           }
                );


        rg_tag.check(R.id.rb_video);
//        initDAO();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("TAG", "Activity onConfigurationChanged");
    }

    private void setfragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, basePagers.get(pos));
        fragmentTransaction.commit();
    }

    private void initView() {
        CommonUtils.debugContext = this;
        rg_tag = (RadioGroup) findViewById(R.id.rg_tag);
    }

    private void initListener() {
        rg_tag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_audio:
                        pos = 1;
                        break;
                    case R.id.rb_net_video:
                        pos = 2;
                        break;
                    case R.id.rb_net_audio:
                        pos = 3;
                        break;
                    default:
                        pos = 0;
                        break;
                }
                setfragment();
            }
        });
    }

    private UserDao userDao;

    private void initDAO() {
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        Database db = ((App) getApplication()).getDb();
//        db.execSQL("DROP TABLE IF EXISTS USER");
//        db.execSQL("create table IF NOT EXISTS USER");
        userDao = daoSession.getUserDao();

        insert();
        query();
    }

    private void insert() {

//        userDao.deleteAll();
        User user = new User();
//        user.setId((long)20);
        user.setAge("24");
        user.setName("hello");
        userDao.insert(user);
        user = new User();
//        user.setId((long)3);
        user.setAge("35");
        user.setName("hi");
        userDao.insert(user);
    }

    private void query() {
        QueryBuilder<User> userQueryBuilder = userDao.queryBuilder().orderAsc(UserDao.Properties.Age);

        List<User> list = userQueryBuilder.list();
        Log.i("TAG", "query: " + list);
    }
}
