package com.vs.hotrepairgogo;

import com.tencent.tinker.lib.service.PatchResult;

import java.util.HashMap;

/**
 * @author: S
 * @date: 2019/2/1 11:52
 * @description:
 */
public  interface RepairCallBack {
    void repairResult(PatchResult patchResult);
    void onSuccessConfig(HashMap<String, String> hashMap);
    void onFailConfig(Exception e);
}
