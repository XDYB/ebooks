package me.chunsheng.ebooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;


import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnPageChangeListener,
        View.OnClickListener {

    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    public static final String SAMPLE_FILE = "Effective_Java.pdf";

    private int[] chapterIndexs;
    private int[] itemIndexs;

    /**
     * {@link android.widget.Button} to move to the previous page.
     */
    private Button mButtonPrevious;

    /**
     * {@link android.widget.Button} to move to the next page.
     */
    private Button mButtonNext;

    private ProgressBar progressBar;

    /**
     * to show pdf page.
     */
    private PDFView pdfView;

    private String pdfName = SAMPLE_FILE;

    private Integer pageNumber = 1;
    private Integer pageCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScrollingActivity.class).putExtra("ITEM_INDEX", getPageItemIndex(pageNumber)));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mButtonPrevious = (Button) findViewById(R.id.previous);
        mButtonPrevious.setOnClickListener(this);
        mButtonNext = (Button) findViewById(R.id.next);
        mButtonNext.setOnClickListener(this);
        pdfView = (com.joanzapata.pdfview.PDFView) findViewById(R.id.pdfView);
        Resources res = getResources();
        chapterIndexs = res.getIntArray(R.array.chapter_index);
        itemIndexs = res.getIntArray(R.array.item_index);

        if (null != savedInstanceState) {
            pageNumber = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }

        progressBar = (ProgressBar) findViewById(R.id.pdf_progress);
        progressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(this)
                .build());
        new Thread(new Runnable() {
            @Override
            public void run() {
                display(SAMPLE_FILE, pageNumber);
            }
        }).start();
        pdfView.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1300);
        checkVersion();
    }

    /**
     * 检查版本更新
     */
    public void checkVersion() {
        new UpdateTask(this, true).update();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != pageNumber) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, pageNumber);
        }
    }

    private void display(String assetFileName, int index) {
        setTitle(pdfName = assetFileName);
        pdfView.fromAsset(assetFileName)
                .defaultPage(index)
                .onPageChange(this)
                .load();
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    private void showPage(int index) {
        if (pageCount <= index) {
            return;
        }
        pdfView.jumpTo(index);
        mButtonPrevious.setEnabled(0 != pageNumber);
        mButtonNext.setEnabled(index + 1 < pageCount);
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        this.pageCount = pageCount;
        pageNumber = page;
        setTitle(String.format("%s(%s/%s)", "Effective Java", page, pageCount));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (SAMPLE_FILE.equals(pdfName)) {
            display(SAMPLE_FILE, 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//            case R.id.action_info:
//                new AlertDialog.Builder(this)
//                        .setMessage(R.string.intro_message)
//                        .setPositiveButton(android.R.string.ok, null)
//                        .show();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                // Move to the previous page
                showPage(pageNumber - 1);
                break;
            }
            case R.id.next: {
                // Move to the next page
                showPage(pageNumber + 1);
                break;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.chapter_1:
                showPage(chapterIndexs[1]);
                break;
            case R.id.chapter_2:
                showPage(chapterIndexs[2]);
                break;
            case R.id.chapter_3:
                showPage(chapterIndexs[3]);
                break;
            case R.id.chapter_4:
                showPage(chapterIndexs[4]);
                break;
            case R.id.chapter_5:
                showPage(chapterIndexs[5]);
                break;
            case R.id.chapter_6:
                showPage(chapterIndexs[6]);
                break;
            case R.id.chapter_7:
                showPage(chapterIndexs[7]);
                break;
            case R.id.chapter_8:
                showPage(chapterIndexs[8]);
                break;
            case R.id.chapter_9:
                showPage(chapterIndexs[9]);
                break;
            case R.id.chapter_10:
                showPage(chapterIndexs[10]);
                break;
            case R.id.chapter_11:
                showPage(chapterIndexs[11]);
                break;
            case R.id.nav_share:
                share("天哪，原来程序员读书可以和运行代码并发进行，太棒了，" +
                        "快来下载体验不一样的读书体验吧.");
                break;
            case R.id.nav_send:
                showSendDialog();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 获取当前页面所在条目
     *
     * @param pageNumber
     * @return
     */
    public int getPageItemIndex(int pageNumber) {
        int index = 75;
        while (pageNumber < itemIndexs[index]) {
            index--;
        }
        return index;
    }

    /**
     * 分享应用到社交媒体
     *
     * @param content
     */
    private void share(String content) {
        Bitmap bm = takeScreenShot(this);
        savePic(bm, "shareBookScreen");
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + "shareBookScreen.png";
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(imagePath));
        shareTo(content, imageUri);
    }

    private static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    // 保存到sdcard
    private void savePic(Bitmap b, String strFileName) {
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + strFileName + ".png";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shareTo(String content, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) { //uri 是图片的地址
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //自定义选择框的标题
        startActivity(Intent.createChooser(shareIntent, "邀请好友读书啦"));
    }


    /**
     * 显示联系对话框，显示联系方式
     */
    public void showSendDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setView(R.layout.dialog_send_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
