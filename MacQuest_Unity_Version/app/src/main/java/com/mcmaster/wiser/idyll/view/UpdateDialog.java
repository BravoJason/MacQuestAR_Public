package com.mcmaster.wiser.idyll.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mcmaster.wiser.idyll.R;


/**
 * @author stevegong
 * @describe 自定义居中弹出dialog
 */
public class UpdateDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private int layoutResID;

    /**
     * 要监听的控件id
     */
    private int[] listenedItems;

    private OnCenterItemClickListener listener;

    private String newVersionCode;
    private String updateMessage;

    private TextView versionCodeTV;
    private TextView updateMessageTV;

    public UpdateDialog(Context context, int layoutResID, int[] listenedItems) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.layoutResID = layoutResID;
        this.listenedItems = listenedItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
//        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画效果
        setContentView(layoutResID);
        // 宽度全屏
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 4 / 5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        // 点击Dialog外部消失
        setCanceledOnTouchOutside(true);

        for (int id : listenedItems) {
            findViewById(id).setOnClickListener(this);
        }

        versionCodeTV = (TextView) findViewById(R.id.updataversioncode);
        versionCodeTV.setText(newVersionCode);
        updateMessageTV = (TextView) findViewById(R.id.updataversion_msg);
        updateMessageTV.setText(updateMessage);
    }



    public void setNewVersionCode(String newVersionCode) {
        this.newVersionCode = newVersionCode;
        if (versionCodeTV != null) {
            versionCodeTV.setText(newVersionCode);
        }
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
        if (updateMessageTV != null) {
            updateMessageTV.setText(updateMessage);
        }
    }

    public interface OnCenterItemClickListener {

        void OnCenterItemClick(UpdateDialog dialog, View view);

    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        listener.OnCenterItemClick(this, view);
    }
}

