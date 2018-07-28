package com.example.lzy.inputdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.List;

public class MembmerPage extends Activity {

    private ListView listView;
    private ArrayList<MemberInfo> memberInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_member);
        listView = findViewById(R.id.member_lv);

        listView.setAdapter(new MemberAdapter(createTestCode(), this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemberInfo memberInfo = memberInfos.get(position);
                Intent intent = new Intent();
                intent.putExtra("bringBackNickName",memberInfo.nickName);
                setResult(400,intent);
                finish();
            }
        });
    }

    private List<MemberInfo> createTestCode() {

        memberInfos = new ArrayList<>();
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.nickName = "testName1";
        memberInfo.pin = "testName1";
        MemberInfo memberInfo1 = new MemberInfo();
        memberInfo1.nickName = "testName2";
        memberInfo1.pin = "testName2";
        MemberInfo memberInfo2 = new MemberInfo();
        memberInfo2.nickName = "testName3";
        memberInfo2.pin = "testName3";
        MemberInfo memberInfo3 = new MemberInfo();
        memberInfo3.nickName = "testName4";
        memberInfo3.pin = "testName4";
        memberInfos.add(memberInfo);
        memberInfos.add(memberInfo1);
        memberInfos.add(memberInfo2);
        memberInfos.add(memberInfo3);
        return memberInfos;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("bringBackNickName","");
        setResult(400,intent);
        finish();

    }
}
