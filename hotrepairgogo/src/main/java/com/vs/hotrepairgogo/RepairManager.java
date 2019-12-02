package com.vs.hotrepairgogo;

import android.util.Log;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.service.PatchResult;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;
import com.tinkerpatch.sdk.tinker.callback.ResultCallBack;

import java.util.HashMap;

/**
 * @author: S
 * @date: 2019/2/1 11:30
 * @description:
 */
public class RepairManager {

    private static final String TAG =  "RepairManager";
    static ApplicationLike tinkerAppLike;
    static RepairCallBack callBack;

    //仅仅执行一次查询补丁包的方法
    public static void With(RepairCallBack cb){
        callBack = cb;
        repair().fetchPatchUpdate(true);//仅仅且立即查询一次
    }

    //传入小时数 将会在hour小时内 每10min轮询一次查询
    public static void With(int hour,RepairCallBack cb){
        callBack = cb;
        repair();
        new FetchPatchHandler().fetchPatchWithInterval(hour);//按hour内 轮询10min查询一次
    }

    private static TinkerPatch repair(){
        tinkerAppLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
        return TinkerPatch.init(tinkerAppLike)
                //是否自动反射Library路径,无须手动加载补丁中的So文件
                //注意,调用在反射接口之后才能生效,你也可以使用Tinker的方式加载Library
                .reflectPatchLibrary()
                //设置收到后台回退要求时,锁屏清除补丁
                //默认是等主进程重启时自动清除
                .setPatchRollbackOnScreenOff(true)
                //设置补丁合成成功后,锁屏重启程序
                //默认是等应用自然重启
                .setPatchRestartOnSrceenOff(true)
                //可以通过ResultCallBack设置对合成后的回调,例如弹框什么
                .setPatchResultCallback(new ResultCallBack() {
                    @Override
                    public void onPatchResult(PatchResult patchResult) {
//                            new AlertDialog.Builder(getApplicationContext()).create()
//                                    .setTitle("升级成功");
//                        Log.e(TAG,"修复 成功~~~~~~~");
                        if (callBack != null){
                            Log.d(TAG,"repairResult");
                            callBack.repairResult(patchResult);
                        }else{
                            Log.e(TAG,"RepairCallBack is null");
                        }
                    }
                })
                //自定义参数
                .fetchDynamicConfig(new ConfigRequestCallback() {
                    @Override
                    public void onSuccess(HashMap<String, String> hashMap) {
//                        Log.e(TAG,"成功了 HashMap:" + hashMap.toString());
                        if (callBack != null){
                            Log.d(TAG,"onSuccessConfig");
                            callBack.onSuccessConfig(hashMap);
                        }else{
                            Log.e(TAG,"RepairCallBack is null");
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
//                        Log.e(TAG,"失败了啊:"  + e.toString());
                        if (callBack != null){
                            Log.d(TAG,"onFailConfig");
                            callBack.onFailConfig(e);
                        }else{
                            Log.e(TAG,"RepairCallBack is null");
                        }
                    }
                }, true);
                //立即查询一次更新
//                .fetchPatchUpdate(true);

    }
}
