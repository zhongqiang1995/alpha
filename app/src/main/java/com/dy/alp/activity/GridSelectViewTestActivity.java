package com.dy.alp.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.azl.base.StatusActivity;
import com.azl.view.grid.select.GridSelectFileView;
import com.dy.alp.R;

import java.util.ArrayList;
import java.util.List;

public class GridSelectViewTestActivity extends StatusActivity {

    GridSelectFileView selectFileView;
    Button bt1, bt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_select_view_test);


        selectFileView = findViewById(R.id.selectFileView);
        selectFileView.setOpenType(GridSelectFileView.OPEN_TYPE_ALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectFileView.onActivityForResult(requestCode, resultCode, data);

    }


    public void click1(View view) {
        List<String> list = new ArrayList<>();
        list.add(Environment.getExternalStorageDirectory()+"/111.png");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        selectFileView.addData(list);
    }

    public void click2(View view) {
        List<String> list = new ArrayList<>();
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        selectFileView.setData(list);
    }

    public void click3(View view) {
        List<String> list = new ArrayList<>();
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        selectFileView.addData(list);
    }

    public void click4(View view) {
        List<String> list = new ArrayList<>();
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e4b9562.jpg");
        list.add("http://www.win4000.com/mobile_detail_143719_3.html");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e6a999b.jpg");
        list.add("http://pic1.win4000.com/mobile/2018-03-06/5a9e56e7c83fc.jpg");

        selectFileView.setData(list);
    }

    public void click5(View view) {
        selectFileView.remove(0);
    }

    public void click6(View view) {
        selectFileView.remove(0, 2);
    }

    public void click7(View view) {
        EditText ed1 = findViewById(R.id.ed1);
        EditText ed3 = findViewById(R.id.ed2);

        selectFileView.remove(Integer.valueOf(ed1.getText().toString()), Integer.valueOf(ed3.getText().toString()));
    }

    public void click8(View view) {
        EditText ed3 = findViewById(R.id.ed3);
        selectFileView.setMaxCount(Integer.valueOf(ed3.getText().toString()));
    }

    public void click9(View view) {
        EditText ed3 = findViewById(R.id.ed4);
        selectFileView.setColumn(Integer.valueOf(ed3.getText().toString()));
    }
}
