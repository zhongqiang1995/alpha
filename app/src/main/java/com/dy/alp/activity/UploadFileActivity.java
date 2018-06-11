package com.dy.alp.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.azl.api.AlphaApiService;
import com.azl.file.bean.Info;
import com.azl.file.helper.D;
import com.azl.handle.action.HandleMsg;
import com.azl.handle.anno.Mark;
import com.azl.obs.data.DataGet;
import com.azl.obs.util.CompressionImageUtil;
import com.dy.alp.R;

import java.io.File;

public class UploadFileActivity extends AppCompatActivity {

    private EditText mEdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);
        HandleMsg.bind(this);
        mEdText = findViewById(R.id.edText);
        mEdText.setText(Environment.getExternalStorageDirectory().getAbsolutePath());


    }

    public void doClick1(View view) {
//        File newFile = CompressionImageUtil.getCompressionImgFile(new File(Environment.getExternalStorageDirectory(), "1.jpg"), this, 100);
        D.upload("https://fs.dev.gdy.io/usr/api/uload?pub=1&token=EF138A4544815DE5EA4097AFD733E6A3", mEdText.getText().toString(), "upload", null);
    }

    public void doClick2(View view) {
        D.stop(mEdText.getText().toString());
    }


    @Mark("upload")
    public void $upload$(Info info) {

        Log.e("UploadFileActivity", "status:" + info.getStatus() + " info:" + info.getInfo() + " total:" + info.getLength() + " progress:" + info.getProgress());

    }
}
