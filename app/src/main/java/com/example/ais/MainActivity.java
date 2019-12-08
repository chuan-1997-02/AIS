package com.example.ais;



import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ais.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.ais.BaiduiOCR;
import com.example.ais.Base64Util;

import org.json.JSONObject;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ais.SaveToExcel;

//import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.example.ais.GetDir.getExcelDir;


public class MainActivity extends AppCompatActivity implements MainContract.View{


    private Context mContext;
    private TextView textView;
    private ImageView imageView;
    private Button button;
    private Button button1;
    @InjectView(R.id.基站名称)
    EditText 基站名称;
    @InjectView(R.id.资产名称)
    EditText 资产名称;
    @InjectView(R.id.资产标签号)
    EditText 资产标签号;
    @InjectView(R.id.规格型号)
    EditText 规格型号;
    @InjectView(R.id.生产厂商)
    EditText 生产厂商;
    @InjectView(R.id.数量)
    EditText 数量;

    File mTmpFile;
    Uri imageUri;
    private LinearLayout save;
    private LinearLayout takepic;
    private LinearLayout commit;
    private String excelPath;
    private SaveToExcel saveToExcel;
    private String Assets_name;
    private String Assets_numb;
    private String base_station_name;
    private String Assets_type;
    private String Manufacturer;
    private String numbs;






    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);
        excelPath = getExcelDir()+ File.separator+"demo.xls";
        saveToExcel = new SaveToExcel(this,excelPath);
        mContext = this;
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.TakePhoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePhoto();
                Resources r = mContext.getResources();
                Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.test);
                Log.d("bmp1", bmp.toString());
                Log.d("bmp2", getBitmapByte(bmp).toString());
                textView.setText(getResult(getBitmapByte(bmp)));
            }
        });

        save = (LinearLayout)findViewById(R.id.save);
        save.setOnClickListener(onClickListener);
        takepic = (LinearLayout)findViewById(R.id.takepic);
        takepic.setOnClickListener(onClickListener);
        commit = (LinearLayout)findViewById(R.id.commit);
        commit.setOnClickListener(onClickListener);
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @OnClick
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.save:
                    Assets_name =资产名称.getText().toString().trim();
                    Assets_numb =资产标签号.getText().toString().trim();
                    Assets_type =规格型号.getText().toString().trim();
                    Manufacturer =生产厂商.getText().toString().trim();
                    base_station_name =基站名称.getText().toString().trim();
                    numbs =数量.getText().toString().trim();
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST);
                    } else {

                        saveToExcel.writeToExcel(base_station_name,Assets_name,Assets_numb,Assets_type,Manufacturer,numbs);
                        //Assets资产 base_station基站
                    }
                    break;
                case R.id.takepic:
                    takePhoto();
                    Toast.makeText(MainActivity.this, "takepic", Toast.LENGTH_LONG);
                    break;
                case R.id.commit:
                    Toast.makeText(MainActivity.this, "commit", Toast.LENGTH_LONG);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void updateUI(String s) {
        textView.setText(s);
    }

    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
            return false;
        }else {
            return true;
        }
    }

    private void takePhoto(){

        if (!hasPermission()) {
            return;
        }

        Intent intent = new Intent();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/img/";
        File file = new File(path);
        if (!file.exists()) {
            new File(path).mkdirs();
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mTmpFile = new File(path, filename + ".jpg");
        mTmpFile.getParentFile().mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String authority = getPackageName() + ".provider";
            imageUri = FileProvider.getUriForFile(this, authority, mTmpFile);
        } else {
            imageUri = Uri.fromFile(mTmpFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }
                takePhoto();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToExcel.writeToExcel(base_station_name,Assets_name,Assets_numb,Assets_type,Manufacturer,numbs);
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap photo = BitmapFactory.decodeFile(mTmpFile.getAbsolutePath());
            //mPresenter.getRecognitionResultByImage(photo);
            imageView.setImageBitmap(photo);
        }
    }


    public byte[] getBitmapByte(Bitmap bitmap){   //将bitmap转化为byte[]类型也就是转化为二进制
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }

    public String getResult(byte[] imgData) {


        String recogniseUrl = "https://aip.baidubce.com/rest/2.0/solution/v1/iocr/recognise";
        String result1 = null;

        //String filePath = "D:\\Workspace\\Javawork\\baiduocr\\res\\test.jpg";
        try {
            //byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String recogniseParams = "templateSign=7d59619c3d1e98c44f61a98bd81994a7&image=" + URLEncoder.encode(imgStr, "UTF-8");
            //String classifierParams = "classifierId=your_classfier_id&image=" + URLEncoder.encode(imgStr, "UTF-8");
            Log.d("bmp3",imgData.toString());

            Log.d("bmp4",imgStr);

            String accessToken = getAuth();
            Log.d("bmp5", accessToken);



            String result = HttpUtil.post(recogniseUrl, accessToken, recogniseParams);
            result1 = result;
            Log.d("bmp6", imgStr);

            // String result = HttpUtil.post(recogniseUrl, accessToken, classifierParams);
            //System.out.println(result);
            //getJson.Json(result);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result1;
    }

    public static String getAuth() {

        String clientId = "G7AqxHlhGNrb3t8tZwzU1MVf";

        String clientSecret = "pFuCgB4HscigzVBs7lFOGqhZFBTGB5xa";

        return getAuth(clientId, clientSecret);
    }


    public static String getAuth(String ak, String sk) {

        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost

                + "grant_type=client_credentials"

                + "&client_id=" + ak

                + "&client_secret=" + sk;
        Log.d("bmp7",getAccessTokenUrl);
        try {
            URL realUrl = new URL(getAccessTokenUrl);

            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            Map<String, List<String>> map = connection.getHeaderFields();

            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");

            return access_token;
        } catch (Exception e) {
            //System.err.printf("get token fail!");
            Log.d("bmp9","get token fail!");
            e.printStackTrace(System.err);
        }
        return null;
    }



}
