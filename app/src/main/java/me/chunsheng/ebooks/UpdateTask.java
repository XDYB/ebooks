package me.chunsheng.ebooks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Util for app update task.
 */
public class UpdateTask extends AsyncTask<String, String, String> {
    private Context context;
    private boolean isUpdateOnRelease;
    //public static final String updateUrl = "https://api.github.com/repos/geeeeeeeeek/WeChatLuckyMoney/releases/latest";
    public static final String updateUrl = "https://raw.githubusercontent.com/hpu-spring87/ebooks/master/update.json";

    public UpdateTask(Context context, boolean needUpdate) {
        this.context = context;
        this.isUpdateOnRelease = needUpdate;
        if (this.isUpdateOnRelease) Toast.makeText(context, "正在检查新版本……", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else {
                // Close the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            return null;
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {

            Log.e("更新信息：", result);

            JSONObject release = new JSONObject(result);

            // Get current version
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int versionCode = pInfo.versionCode;
            int latestVersion = release.getInt("versionCode");
            final String updateTitle = release.getString("updateTitle");
            final String updateMsg = release.getString("updateMsg");
            final String downLoadUrl = release.getString("downloadUrl");

            if (versionCode < latestVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(updateMsg);
                builder.setTitle(updateTitle);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Need update.
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downLoadUrl));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(browserIntent);
                        Toast.makeText(context, "正在为您准备下载", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (this.isUpdateOnRelease)
                Toast.makeText(context, "更新出错啦", Toast.LENGTH_LONG).show();
        }
    }

    public void update() {
        super.execute(updateUrl);
    }
}