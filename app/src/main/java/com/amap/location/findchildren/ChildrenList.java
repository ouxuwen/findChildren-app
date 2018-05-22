package com.amap.location.findchildren;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.amap.location.findchildren.RequestConnect.SERVER_URL;
import static com.amap.location.findchildren.RequestConnect.UNBIND_CHILDREN;
import static com.amap.location.findchildren.RequestConnect.bindChildren;

/**
 * Created by kevin.zhang on 2018/4/17.
 */

public class ChildrenList extends Activity implements AdapterView.OnItemClickListener{
    Context mContext;
    List<Children> childrenInfo;
    ChildrenAdapter spcAdapter;
    ListView list_test;
    private Handler handler=null;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = ChildrenList.this;
        super.onCreate(savedInstanceState);
        handler=new Handler();
        setContentView(R.layout.children_list);

        list_test =  (ListView)findViewById(R.id.child_list);
        Intent intent = getIntent();
        childrenInfo = (ArrayList<Children>) intent.getSerializableExtra("child");

        spcAdapter	= new ChildrenAdapter(childrenInfo,this);

        list_test.setAdapter(spcAdapter);
        list_test.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final int index = position;
        final Runnable   runnableUi=new  Runnable(){
            @Override
            public void run() {
                //更新界面

                childrenInfo.remove(index);
                list_test.setAdapter(spcAdapter);
                Toast.makeText(getApplicationContext(),"删除成功 !",Toast.LENGTH_LONG).show();
            }

        };
        builder = new AlertDialog.Builder(mContext);
        alert = builder
                .setTitle("系统提示：")
                .setMessage("确定删除吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread(){
                            @Override
                            public void run() {
                                Looper.prepare();

                                String parent_email = (String)SPUtils.get(mContext,"email","");
                                String result = bindChildren(parent_email,childrenInfo.get(index).getEmail(),SERVER_URL+UNBIND_CHILDREN);
                                if(result.indexOf("success")>-1){
                                    try {
                                        SPUtils.put(mContext,"son",new JSONObject(result).getString("data"));
                                        handler.post(runnableUi);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                Toast.makeText(mContext,result,Toast.LENGTH_SHORT).show();

                                Looper.loop();
                            }
                        }.start();
                    }
                })
                .create();             //创建AlertDialog对象
        alert.show();                    //显示对话框



    }

}
