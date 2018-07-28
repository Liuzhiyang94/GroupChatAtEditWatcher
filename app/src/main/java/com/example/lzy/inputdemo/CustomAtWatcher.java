package com.example.lzy.inputdemo;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.webkit.WebHistoryItem;
import android.widget.EditText;
import android.widget.MediaController;

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CustomAtWatcher implements TextWatcher {
    private EditText editText;
    private boolean isEdit = false;
    private int location = 0;
    private ArrayList<Integer[]> list = new ArrayList<>();
    private String lastMsg;

    abstract void onNeedToCallMemberPage();

    private boolean hasCallMemberPage = false;
    private String bringBackNickName = "";

    public CustomAtWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isEdit) {
            editText.setSelection(location);
            isEdit = false;
            return;
        }

        if (lastMsg != null) {
            //删除字符模块
            if (lastMsg.length() > s.length()) {
                isEdit = true;
                boolean hasEdit = false;
                int removeIndex = 0;
                for (int i = 0; i < list.size(); i++) {
                    Integer[] integers = list.get(i);
                    if (editText.getSelectionStart() >= integers[0] && editText.getSelectionStart() < integers[1]) {
                        removeIndex = i;
                        hasEdit = true;
                        break;
                    }
                }
                if (hasEdit) {
                    for (int i = 0; i < list.size(); i++) {
                        Integer[] integers1 = list.get(i);
                        Integer[] integers2 = list.get(removeIndex);
                        if (integers1[0] >= integers2[1]) {//在操作角标后面的所有spanstring角标位置都要相应往后推增加字符的长度
                            Integer[] integers3 = list.get(i);
                            integers3[0] = integers3[0] - (lastMsg.length() - s.length());//这边删除了几个字符,就要给几
                            integers3[1] = integers3[1] - (lastMsg.length() - s.length());
                        }
                    }
                    list.remove(removeIndex);
                } else {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            Integer[] integers1 = list.get(i);
                            if (integers1[0] >= (editText.getSelectionStart() - (lastMsg.length() - s.length()))) {
                                integers1[0] = integers1[0] - (lastMsg.length() - s.length());
                                integers1[1] = integers1[1] - (lastMsg.length() - s.length());
                            }
                        }
                    }
                }

                StringBuffer stringBuffer = new StringBuffer(s.toString());
                location = editText.getSelectionStart();
                editText.setText(createNewSpan(stringBuffer));
                return;
            }
        }

        //添加字符模块
        StringBuffer stringBuffer = new StringBuffer(s.toString());
        if (TextUtils.equals(s.toString().charAt(editText.getSelectionStart() - 1) + "", "＠") && judgeChineseOrSpace(stringBuffer)) {
            //触发@列表
            if (!hasCallMemberPage) {
                onNeedToCallMemberPage();
                return;
            }

            isEdit = true;
            StringBuffer replace = stringBuffer.replace(editText.getSelectionStart() - 1, editText.getSelectionStart(), bringBackNickName);
            Integer[] integers = new Integer[2];
            integers[0] = editText.getSelectionStart() - 1;
            integers[1] = editText.getSelectionStart() - 1 + bringBackNickName.length();
            boolean hasEdit = false;
            int removeIndex = 0;
            for (int i = 0; i < list.size(); i++) {
                Integer[] integers1 = list.get(i);
                if (editText.getSelectionStart() - 1 > integers1[0] && editText.getSelectionStart() - 1 < integers1[1]) {
                    removeIndex = i;
                    hasEdit = true;
                    break;
                }
            }
            if (hasEdit) {
                for (int i = 0; i < list.size(); i++) {
                    Integer[] integers1 = list.get(i);
                    Integer[] integers2 = list.get(removeIndex);
                    if (integers1[0] >= integers2[1]) {//在操作角标后面的所有spanstring角标位置都要相应往后推增加字符的长度
                        Integer[] integers3 = list.get(i);
                        integers3[0] = integers3[0] + bringBackNickName.length();
                        integers3[1] = integers3[1] + bringBackNickName.length();
                    }
                }
                list.remove(removeIndex);
            } else {
                //如果说没有修改已高亮部分,但是修改的地点在高亮部分的前面,也要往后推字符长度
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Integer[] integers1 = list.get(i);
                        if (integers1[0] >= editText.getSelectionStart() - 1) {
                            integers1[0] = integers1[0] + bringBackNickName.length();
                            integers1[1] = integers1[1] + bringBackNickName.length();
                        }
                    }
                }
            }
            list.add(integers);//新添加字符的角标
            SpannableString newSpan = createNewSpan(replace);
            location = editText.getSelectionStart() - 1 + bringBackNickName.length();
            editText.setText(newSpan);
        } else {
            //添加别的字符的话,就要判断光标是否在各组span的中间
            boolean hasEdit = false;
            int removeIndex = 0;
            for (int i = 0; i < list.size(); i++) {
                Integer[] integers1 = list.get(i);
                //这边要还原成原始起点,因为用户可能联想输入 如下都同理,输入都有可能联想
                if (editText.getSelectionStart() - (stringBuffer.length() - lastMsg.length()) > integers1[0] && editText.getSelectionStart() - (stringBuffer.length() - lastMsg.length()) < integers1[1]) {
                    removeIndex = i;
                    hasEdit = true;
                    break;
                }
            }
            if (hasEdit) {
                for (int i = 0; i < list.size(); i++) {
                    Integer[] integers1 = list.get(i);
                    Integer[] integers2 = list.get(removeIndex);
                    if (integers1[0] >= integers2[1]) {//在操作角标后面的所有spanstring角标位置都要相应往后推增加字符的长度
                        Integer[] integers3 = list.get(i);
                        integers3[0] = integers3[0] + (stringBuffer.length() - lastMsg.length());//这边增加了几个字  就要给几
                        integers3[1] = integers3[1] + (stringBuffer.length() - lastMsg.length());
                    }
                }
                list.remove(removeIndex);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    Integer[] integers1 = list.get(i);
                    if (integers1[0] >= (editText.getSelectionStart() - (stringBuffer.length() - lastMsg.length()))) {//在操作角标后面的所有spanstring角标位置都要相应往后推增加字符的长度
                        Integer[] integers3 = list.get(i);
                        integers3[0] = integers3[0] + (stringBuffer.length() - lastMsg.length());
                        integers3[1] = integers3[1] + (stringBuffer.length() - lastMsg.length());
                    }
                }
            }
            isEdit = true;
            SpannableString newSpan = createNewSpan(stringBuffer);
            location = editText.getSelectionStart();
            editText.setText(newSpan);
        }
    }

    //创建富文本
    private SpannableString createNewSpan(StringBuffer stringBuffer) {
        lastMsg = stringBuffer.toString();
        SpannableString spannableString = new SpannableString(stringBuffer);
        for (int i = 0; i < list.size(); i++) {
            Integer[] integers = list.get(i);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#7ece22")), integers[0]
                    , integers[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return spannableString;
    }

    //当只有是第二字符的时候,才会去判断前面是中文还是空格
    private boolean judgeChineseOrSpace(StringBuffer stringBuffer) {
        boolean isAtSign = false;
        if (stringBuffer.length() > 1) {
            if (editText.getSelectionStart() == 1) {//当前有字,且光标在第0位置
                return true;
            }
            String s = stringBuffer.toString().charAt(editText.getSelectionStart() - 2) + "";
            String pattern = "[\u4e00-\u9fa5\\s]";
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(s);
            if (matcher.matches()) {
                isAtSign = true;
            }
        } else {
            isAtSign = true;
        }
        return isAtSign;
    }

    public void bringBackNickName(String nickName) {
        hasCallMemberPage = false;
        if (TextUtils.isEmpty(nickName)) {
            isEdit = true;
            //当没有选择任何昵称直接返回的时候,要算上@这符号站的字符,
            for (int i = 0; i < list.size(); i++) {
                Integer[] integers = list.get(i);
                integers[0] = integers[0] + 1;
                integers[1] = integers[1] + 1;
            }
            StringBuffer stringBuffer = new StringBuffer(editText.getText().toString());
            SpannableString newSpan = createNewSpan(stringBuffer);
            location = editText.getSelectionStart();
            editText.setText(newSpan);
        } else {
            bringBackNickName = "＠" + nickName + " ";
            //先去把输入的@字符给删除掉
            isEdit = true;
            StringBuffer stringBuffer = new StringBuffer(editText.getText());
            StringBuffer delete = stringBuffer.delete(editText.getSelectionStart() - 1, editText.getSelectionStart());
            location = editText.getSelectionStart() - 1;
            SpannableString newSpan = createNewSpan(delete);
            editText.setText(newSpan);//还原成没有输入的@的原装,光标停留在编辑前的位置

            hasCallMemberPage = true;//在重置完原数据后,要开始重新触发@逻辑了,这时候如果hasCallMemberPage!=true的话,会重新触发弹窗逻辑
            editText.getText().insert(editText.getSelectionStart(), "＠");
            hasCallMemberPage = false;//等设置完数据后,就可以释放弹窗逻辑了


        }


    }
    
    public void reset() {
        isEdit = false;
        location = 0;
        list.clear();
        lastMsg = "";
        hasCallMemberPage = false;
        bringBackNickName = "";
    }


}
