package com.xykj.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xykj.vwill.R;
import com.xyy.utils.TipsUtil;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetPictureFragment extends DialogFragment {
    private static final int REQUEST_CONTENT = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_CROP = 3;
    //裁剪结果的Uri
    private Uri imageUri;
    //拍照保存地址
    private Uri captureUri;
    private String tempPath;
    //是否是1:1的裁剪尺寸
    private boolean is1v1Size = true;


    public GetPictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        // /mnt/sdrac/Android/data/com.xykj.vwill/cache/temp
        tempPath = getActivity().getExternalCacheDir() + "/temp";
        File f = new File(tempPath);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                throw new IllegalArgumentException("临时文件夹创建失败");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_picture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_content).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_camera).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_content:
                    Intent it = new Intent(Intent.ACTION_GET_CONTENT);
                    it.setType("image/*");
                    it.addCategory(Intent.CATEGORY_OPENABLE);
                    it.putExtra("return-data", false);
                    startActivityForResult(it, REQUEST_CONTENT);
                    break;
                case R.id.btn_camera:
                    Intent it1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureUri = Uri.parse("file://" + tempPath + "/" + System.currentTimeMillis() + ".jpg");
                    it1.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
                    it1.putExtra("noFaceDetection", false);
                    startActivityForResult(it1, REQUEST_CAMERA);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CONTENT:
                    Uri uri = data.getData();
                    if (Build.VERSION.SDK_INT >= 19) {
                        String path = getPath(getActivity(), uri);
                        Uri cropUri = Uri.fromFile(new File(path));
                        startCrop(cropUri);
                    } else {
                        startCrop(uri);
                    }
                    break;
                case REQUEST_CROP:
                    if (null != onGetPictureListener) {
                        onGetPictureListener.onResult(imageUri);
                    }
                    dismiss();
                    break;
                case REQUEST_CAMERA:
                    //启动裁剪
                    startCrop(captureUri);
                    break;
            }
        } else {
            dismiss();
        }
    }

    private void startCrop(Uri uri) {
        Intent it = new Intent("com.android.camera.action.CROP");
        it.setDataAndType(uri, "image/*");
        it.putExtra("crop", true);
        if(is1v1Size) {
            //1:1的形式裁剪
            it.putExtra("aspectX", 1);
            it.putExtra("aspectY", 1);
        }
//        //固定大小
//        it.putExtra("outputX",200);
//        it.putExtra("outputY",200);
        it.putExtra("outputFormat", "JPEG");
        it.putExtra("return-data", false);
        //保存的位置
        imageUri = Uri.parse("file://" + tempPath + "/" + System.currentTimeMillis());
        it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(it, REQUEST_CROP);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getPath(Context context, Uri uri) {
        String path = "";
        //  content://com.android.providers.media.documents/document/image:840
        String[] columns = {MediaStore.Images.Media.DATA};
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String idStr = DocumentsContract.getDocumentId(uri);  // image:840
            String id = idStr.split(":")[1]; // 840
            //查询绝对路径
            Cursor c = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    MediaStore.Images.Media._ID + "=?", new String[]{id}, null);
            if (c.moveToFirst()) {
                path = c.getString(0);
            }
            c.close();
        } else {
            String s = uri.toString();
            if (s.startsWith("file://")) {
                path = s;
            } else {
                // content://com.android.providers.media/image/840
                Cursor c = context.getContentResolver().query(uri, columns, null, null, null);
                if (c.moveToFirst()) {
                    path = c.getString(0);
                }
                c.close();
            }
        }
        return path;
    }

    public interface OnGetPictureListener {
        void onResult(Uri uri);
    }

    private OnGetPictureListener onGetPictureListener;

    public void setOnGetPictureListener(OnGetPictureListener onGetPictureListener) {
        this.onGetPictureListener = onGetPictureListener;
    }

    public void clearTempFiles() {
        new Thread() {
            @Override
            public void run() {
                File f = new File(tempPath);
                File[] fs = f.listFiles();
                if (fs != null && fs.length > 0) {
                    for (int i = 0; i < fs.length; i++) {
                        fs[i].delete();
                    }
                }
            }
        }.start();
    }

    public boolean isIs1v1Size() {
        return is1v1Size;
    }

    public void setIs1v1Size(boolean is1v1Size) {
        this.is1v1Size = is1v1Size;
    }
}
