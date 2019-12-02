package com.test.sdk.hotrepairgogo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.service.PatchResult;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.vs.hotrepairgogo.RepairCallBack;
import com.vs.hotrepairgogo.RepairManager;

import java.util.HashMap;


public class MyApp extends Application {

    ApplicationLike tinkerApplicationLike;
    @Override
    public void onCreate() {
        super.onCreate();

        InitHotRepair();
//        InitHotRepair2();
    }

    private void InitHotRepair2() {
        // 我们可以从这里获得Tinker加载过程的信息
        tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();

        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
        TinkerPatch.init(tinkerApplicationLike)
                .reflectPatchLibrary()
                .setPatchRollbackOnScreenOff(true)
                .setPatchRestartOnSrceenOff(true)
                .setFetchPatchIntervalByHours(3);

        // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    /**
     * 热修复
     */
    private void InitHotRepair() {
        if (BuildConfig.TINKER_ENABLE) { //这个值判断是否开启补丁热修复功能 在tinkerpatch.gradle文件修改
            RepairManager.With(new RepairCallBack() {
                @Override
                public void repairResult(PatchResult patchResult) {
                    /**
                     * 补丁安装成功的话会返回
                     */
                    Log.e("HOT","修复 成功~~~~~~~");
                }

                @Override
                public void onSuccessConfig(HashMap<String, String> hashMap) {
                    /**
                     * Tinker 平台上有新建 在线参数 就会有返回，如果没有新建 会在onFailConfig返回异常
                     * 是否有新建不影响 补丁的更新 可以不管
                     */
                    Log.e("HOT","成功了 HashMap:" + hashMap.toString());
                }

                @Override
                public void onFailConfig(Exception e) {
                    Log.e("HOT","失败了啊:"  + e.toString());
                }
            });
        }

    }

}
