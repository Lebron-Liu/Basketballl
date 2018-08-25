package com.xykj.vwill;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xykj.adapter.MediaAdapter;
import com.xykj.bean.MediaItem;
import com.xykj.utils.Common;
import com.xyy.utils.TipsUtil;
import com.xyy.utils.XImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取媒体的Activity
 */
public class GetMediaActivity extends VWillBaseActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_CROP = 2;
    //媒体类型
    private int type;
    private MediaAdapter adapter;
    //功能键图标
    private int functionIcon;
    //默认的缩略图图标
    private int defThumbIcon;
    private ArrayList<MediaItem> userSelectMedia;
    //可选择的最大数量
    private int maxSize;
    private String tempPath;
    //裁剪结果的Uri
    private Uri imageUri;
    //拍照保存地址
    private Uri captureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //要获取的媒体类型
        Intent it = getIntent();
        type = it.getIntExtra("type", 0);
        maxSize = it.getIntExtra("max",0);
        if(it.hasExtra("data")){
            userSelectMedia = it.getParcelableArrayListExtra("data");
        }else{
            userSelectMedia = new ArrayList<>(maxSize);
        }
        super.onCreate(savedInstanceState);
        tempPath = getExternalCacheDir() + "/temp";
        File f = new File(tempPath);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                throw new IllegalArgumentException("临时文件夹创建失败");
            }
        }
    }

    @Override
    protected void initLayout() {
        GridView gridView = findViewById(R.id.grid_media);
        //设置适配器
        adapter = new MediaAdapter(this, functionIcon);
        adapter.setType(type);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    // 进入相应的功能页面
                    switch (type){
                        case Common.TYPE_IMAGE:
                            //拍照
                            Intent it1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            captureUri = Uri.parse("file://" + tempPath + "/" + System.currentTimeMillis() + ".jpg");
                            it1.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
                            it1.putExtra("noFaceDetection", false);
                            startActivityForResult(it1, REQUEST_CAMERA);
                            break;
                        case Common.TYPE_VIDEO:
                            //录像
                            break;
                        case Common.TYPE_AUDIO:
                            //录音
                            break;
                    }
                }else{
                    MediaItem item = adapter.getItem(i);
                    if(item.isChecked()){
                        item.setChecked(false);
                        userSelectMedia.remove(item);
                        adapter.notifyDataSetChanged();
                    }else{
                        if(userSelectMedia.size() == maxSize){
                            TipsUtil.toast(GetMediaActivity.this,"已经达到选择的上限");
                        }else {
                            item.setChecked(true);
                            userSelectMedia.add(item);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        new LoadTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    //拍照完毕，裁剪
                    startCrop(captureUri);
                }
                break;
            case REQUEST_CROP:
                // 获取裁剪的图像，添加到用户选择中，回到文章发布界面
                String path = imageUri.getPath();
                userSelectMedia.add(new MediaItem(path,"",R.drawable.filesystem_icon_photo));
                backResult();
                break;
        }
    }
    private void startCrop(Uri uri) {
        Intent it = new Intent("com.android.camera.action.CROP");
        it.setDataAndType(uri, "image/*");
        it.putExtra("crop", true);
        it.putExtra("outputFormat", "JPEG");
        it.putExtra("return-data", false);
        //保存的位置
        imageUri = Uri.parse("file://" + tempPath + "/" + System.currentTimeMillis());
        it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(it, REQUEST_CROP);
    }


    class LoadTask extends AsyncTask<Object, Void, List<MediaItem>> {

        ProgressDialog dialog;
        Uri uri = null;
        String[] columns = null;
        //主线程中，在doInbackground执行之前
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(GetMediaActivity.this,"","正在加载中...");
        }

        @Override
        protected List<MediaItem> doInBackground(Object... objects) {
            initLoad();
            Cursor c = getContentResolver().query(uri, columns, null, null, null);
            List<MediaItem> list = new LinkedList<>();
            while (c.moveToNext()) {
                String title = c.getString(0);
                String path = c.getString(1);
                list.add(new MediaItem(path, title, defThumbIcon));
                //如果是视频，并且没有缩略图，生成缩略图保存起来
                if (type == Common.TYPE_VIDEO && !XImageLoader.getInstance(GetMediaActivity.this).isHasLocalCache(path)) {
                    //没有缩略图则生成缩略图
                    Bitmap thumb = null;
                    //在api10以上
                    if (Build.VERSION.SDK_INT >= 10) {
                        MediaMetadataRetriever mr = new MediaMetadataRetriever();
                        try {
                            //设置视频源
                            mr.setDataSource(path);
                            //获取视频上的某一帧图像
                            thumb = mr.getFrameAtTime();
                        } catch (Exception e) {
                            thumb = ThumbnailUtils.createVideoThumbnail(path,
                                    MediaStore.Images.Thumbnails.MICRO_KIND);
                        } finally {
                            //释放资源
                            mr.release();
                        }
                    } else {
                        thumb = ThumbnailUtils.createVideoThumbnail(path,
                                MediaStore.Images.Thumbnails.MICRO_KIND);
                    }
                    if (thumb != null) {
                        XImageLoader.getInstance(GetMediaActivity.this).saveThumbInLocal(path, thumb);
                    }
                }
            }
            c.close();
            //初始化用户已经选择的内容的状态(将已选择的勾上)
            if(!userSelectMedia.isEmpty()){
                int userSize = userSelectMedia.size();
                int loadSize = list.size();
                for(int i = 0 ; i < userSize;i++){
                    for(int j = 0 ; j < loadSize;j++){
                        if(userSelectMedia.get(i).equals(list.get(j))){
                            list.get(j).setChecked(true);
                            break;
                        }
                    }
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<MediaItem> mediaItems) {
            if (!mediaItems.isEmpty()) {
                adapter.setList(mediaItems);
            }
            dialog.dismiss();
        }

        private void initLoad() {
            switch (type) {
                case Common.TYPE_IMAGE:
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    columns = new String[]{MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA};
                    break;
                case Common.TYPE_VIDEO:
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    columns = new String[]{MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA};
                    break;
                case Common.TYPE_AUDIO:
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    columns = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA};
                    break;
            }
        }
    }

    @Override
    protected String getActivityTitle() {
        switch (type) {
            case Common.TYPE_IMAGE:
                functionIcon = R.drawable.ic_camera;
                defThumbIcon = R.drawable.filesystem_icon_photo;
                return "图片";
            case Common.TYPE_VIDEO:
                functionIcon = R.drawable.ic_video;
                defThumbIcon = R.drawable.filesystem_icon_movie;
                return "视频";
            case Common.TYPE_AUDIO:
                functionIcon = R.drawable.ic_translation;
                defThumbIcon = R.drawable.filesystem_icon_music;
                return "音频";
        }
        return super.getActivityTitle();
    }

    @Override
    protected void onTitleRightViewClick(View v) {
        backResult();
    }

    private void backResult(){
        if(!userSelectMedia.isEmpty()){
            Intent it = new Intent();
            it.putExtra("result",userSelectMedia);
            it.putExtra("type",type);
            setResult(RESULT_OK,it);
        }
        finish();
    }

    @Override
    protected int getType() {
        return TYPE_CANCEL_SURE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_get_media;
    }
}
