package com.xykj.persenter;

import com.xykj.bean.GroupInfo;
import com.xykj.model.RegistModel;
import com.xykj.utils.Common;
import com.xykj.view.BooleanView;
import com.xyy.net.FileRequestItem;

import java.io.File;
import java.util.List;

public class RegistPersenter extends IPersenter<BooleanView> {

    public void regist(String nick, String psw, String sex, String sign, List<GroupInfo> groups, String photoPath){
        //拼接所加入的群组参数
       StringBuilder sb = new StringBuilder();
        if(groups!=null && groups.size()>0){
            sb.append("[");
            int size = groups.size();
            for(int i= 0 ; i < size;i++){
                sb.append(groups.get(i).getId()).append(","); //[1,2,3,
            }
            int len = sb.length();
            sb.replace(len-1,len,"]");
        }
        FileRequestItem.Builder builder = new FileRequestItem.Builder()
                .url(Common.URL_REGIST)
                .addStringParam("nick",nick)
                .addStringParam("psw",psw)
                .addStringParam("sex",sex)
                .addStringParam("sign",sign)
                .addStringParam("tags",sb.toString());
        //头像
        if(null != photoPath){
            File photo = new File(photoPath);
            builder.addFileParam("photo",photo);
        }
        RegistModel model = new RegistModel(builder.build());
        model.execute(new BaseCallback<Boolean>(this,view) {
            @Override
            public void onSuccess(Boolean data) {
                view.showResult(data);
            }
        });

    }
}
