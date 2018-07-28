package com.example.lzy.inputdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private CustomAtWatcher customAtWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et_main);

        customAtWatcher = new CustomAtWatcher(editText) {
            @Override
            void onNeedToCallMemberPage() {
                startActivityForResult(new Intent(MainActivity.this, MembmerPage.class), 200);
            }
        };
        editText.addTextChangedListener(customAtWatcher);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            String bringBackNickName = data.getStringExtra("bringBackNickName");
            Log.d("lzy", "onActivityResult: " + bringBackNickName);
            customAtWatcher.bringBackNickName(bringBackNickName);
        }

    }
}
