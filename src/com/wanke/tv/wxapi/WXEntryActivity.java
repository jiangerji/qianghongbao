package com.wanke.tv.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.wanke.tv.R;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        //        api.registerApp(APP_ID);
        //        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    private void setShared() {
        SharedPreferences settings = getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putBoolean("isShared", true);
        editor.commit();
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        switch (resp.errCode)
        {
        case BaseResp.ErrCode.ERR_OK:
            result = R.string.errcode_success;
            setShared();
            break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
            result = R.string.errcode_cancel;
            break;
        case BaseResp.ErrCode.ERR_AUTH_DENIED:
            result = R.string.errcode_deny;
            break;
        default:
            result = R.string.errcode_unknown;
            break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }

}
