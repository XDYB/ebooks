package me.chunsheng.ebooks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.joanzapata.pdfview.util.FileUtils;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import me.chunsheng.ebooks.floder.IconTreeItemHolder;


public class ScrollingActivity extends AppCompatActivity {

    private android.support.design.widget.AppBarLayout appBarLayout;
    private android.support.design.widget.CollapsingToolbarLayout toolbarLayout;
    private HighlightJsView codeShow;
    private LinearLayout llTreeContent;
    private AndroidTreeView tView;
    private ProgressBar progressBar;
    private TreeNode root;

    private int itemIndex = 1;
    private Map itemDirMap;
    private String codeCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        itemDirMap = getHashMapResource(this, R.xml.item_code_arrays);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        codeShow = ((HighlightJsView) findViewById(R.id.codeShow));
        //change theme and set language to auto detect
        codeShow.setTheme(Theme.ATELIER_ESTUARY_DARK);
        codeShow.setHighlightLanguage(Language.AUTO_DETECT);

        progressBar = (ProgressBar) findViewById(R.id.code_progress);
        toolbarLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 1500);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveFile();
                    String imagePath = Environment.getExternalStorageDirectory() + File.separator + "Test.java";
                    System.out.println("**********");
                    runProcess("javac " + imagePath);
                    System.out.println("**********");
                    runProcess("java " + imagePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (codeCache.contains("main")) {
                    Snackbar.make(view, "代码运行出错啦", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "代码没有main()方法，不能运行哈", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        llTreeContent = (LinearLayout) findViewById(R.id.llTreeContent);

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

        itemIndex = getIntent().getIntExtra("ITEM_INDEX", 1);
        setTitle(String.format("第%s条", itemIndex));
        Log.e("Tag:", "itemIndex:" + itemIndex);
        showCode(itemIndex);
    }

    public void saveFile() {
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + "Test.java";
        try (PrintWriter out = new PrintWriter(imagePath)) {
            out.println("public class Tree<T> {\n" +
                    "    private Node<T> root;\n" +
                    "\n" +
                    "    public Tree(T rootData) {\n" +
                    "        root = new Node<T>();\n" +
                    "        root.data = rootData;\n" +
                    "        root.children = new ArrayList<Node<T>>();\n" +
                    "    }\n" +
                    "\n" +
                    "    public static class Node<T> {\n" +
                    "        private T data;\n" +
                    "        private Node<T> parent;\n" +
                    "        private List<Node<T>> children;\n" +
                    "    }\n" +
                    "\n" +
                    "    public static void main(String[] args) {\n" +
                    "        Log.e(\"Tag:\",\"coem in java...\");\n" +
                    "    }\n" +
                    "}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printLines(String cmd, InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(cmd + " " + line);
        }
    }

    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
    }


    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            showCodeWithPath(item.path);
            progressBar.setVisibility(View.VISIBLE);
            toolbarLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 1000);
            Log.e("Click:", "item:" + item.text);
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Toast.makeText(ScrollingActivity.this, "Long click: " + item.text, Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    /**
     * 展示对应页面的代码，以及运行结果
     *
     * @param pageIndex
     */
    public void showCode(int pageIndex) {
        String path = getCodePath(pageIndex);
        showCodeWithPath(path);
    }

    public void showCodeWithPath(String pathAsset) {
        String showStr;
        try {
            AssetManager am = getAssets();
            InputStream inputStream = am.open(pathAsset);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            showStr = new String(buffer, "UTF-8");
            codeCache = showStr;
            codeShow.setSource(showStr);
            codeShow.refresh();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 根据页，判断需要显示的代码路径
     *
     * @param pageStr
     */
    public String getCodePath(int pageStr) {

        String codePath;
        String showPath;

        while (!itemDirMap.containsKey(String.valueOf(pageStr))) {
            pageStr++;
        }
        codePath = (String) itemDirMap.get(String.valueOf(pageStr));
        try {
            JSONObject jsonObject = new JSONObject(codePath);
            String chapterStr = jsonObject.getString("chapter");
            String nameStr = jsonObject.getString("name");
            String contentStr = jsonObject.getString("content");
            showPath = "Code/" + chapterStr + "/" + nameStr + "/" + new JSONArray(contentStr).get(0);
            showTreeFile(chapterStr, nameStr, new JSONArray(contentStr));
        } catch (Exception e) {
            e.printStackTrace();
            showPath = "";
        }

        return showPath;
    }


    /**
     * 树形展示文件结构
     */
    public void showTreeFile(String chapterStr, String name, JSONArray jsonArray) {

        root = TreeNode.root();
        TreeNode computerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, chapterStr, ""));

        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, name, ""));

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String endName = (String) jsonArray.get(i);
                String showPath = "Code/" + chapterStr + "/" + name + "/" + jsonArray.get(i);
                TreeNode file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, endName, showPath));
                downloads.addChildren(file1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        computerRoot.addChildren(downloads);
        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        tView.setUse2dScroll(true);

        root.addChildren(computerRoot);
        llTreeContent.addView(tView.getView());
        tView.expandAll();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_scrolling, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId()) {
            case R.id.action_theme:
                final Theme[] themes = Theme.values();
                String[] items = new String[themes.length];
                for (int i = 0; i < themes.length; i++) {
                    items[i] = themes[i].getName();
                }
                new AlertDialog.Builder(this)
                        .setTitle(Html.fromHtml("选择代码主题<a href=\"https://highlightjs.org/static/demo/\"></a>"))
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                codeShow.setTheme(themes[which]);
                                codeShow.refresh();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 解析XML文件为Map
     *
     * @param c
     * @param hashMapResId
     * @return
     */
    public static Map<String, String> getHashMapResource(Context c, int hashMapResId) {
        Map<String, String> map = null;
        XmlResourceParser parser = c.getResources().getXml(hashMapResId);

        String key = null, value = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("utils", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("map")) {
                        boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

                        map = isLinked
                                ? new LinkedHashMap<String, String>()
                                : new HashMap<String, String>();
                    } else if (parser.getName().equals("entry")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("entry")) {
                        map.put(key, value);
                        key = null;
                        value = null;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (null != key) {
                        value = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return map;
    }


}
