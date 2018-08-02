package com.classroomsdk;

import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/5/19.
 */

public class WhiteBoradManager {

    private static String sync = "";
    private WBStateCallBack callBack;
    private LocalControl control;
    static private WhiteBoradManager mInstance = null;

    private static AsyncHttpClient client = new AsyncHttpClient();
    private ShareDoc currentMediaDoc;
    private ShareDoc currentFileDoc;
    private ShareDoc defaultFileDoc;
    ShareDoc mBlankShareDoc;
    private boolean isPhotoClassBegin = false;

    public ShareDoc getmBlankShareDoc() {
        return mBlankShareDoc;
    }

    public void setmBlankShareDoc(ShareDoc mBlankShareDoc) {
        this.mBlankShareDoc = mBlankShareDoc;
    }

    private String fileServierUrl = "";
    private int fileServierPort = 80;
    private ConcurrentHashMap<Long, ShareDoc> docMap = new ConcurrentHashMap<Long, ShareDoc>();
    private ConcurrentHashMap<Long, ShareDoc> mediaMap = new ConcurrentHashMap<Long, ShareDoc>();
    private ArrayList<ShareDoc> docList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> classDocList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> adminDocList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> mediaList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> classMediaList = new ArrayList<ShareDoc>();
    private ArrayList<ShareDoc> adminMediaList = new ArrayList<ShareDoc>();
    private int userrole = -1;

    public ShareDoc getDefaultFileDoc() {
        return defaultFileDoc;
    }

    public void setUserrole(int userrole) {
        this.userrole = userrole;
    }

    public void setLocalControl(LocalControl control) {
        this.control = control;
    }

    private boolean isdeling = false;

    public ArrayList<ShareDoc> getDocList() {
        docList.clear();
//        docList.add(mBlankShareDoc);
        for (ShareDoc d : docMap.values()) {
            if (d.getFileid() == 0) {
                docList.add(0, d);
            } else {
                docList.add(d);
            }
        }
        Collections.sort(docList);
        return docList;
    }

    public ArrayList<ShareDoc> getMediaList() {
        mediaList.clear();
        for (ShareDoc d : mediaMap.values()) {
            mediaList.add(d);
        }
        Collections.sort(mediaList);
        return mediaList;
    }

    public ArrayList<ShareDoc> getClassDocList() {
        classDocList.clear();
        classDocList.add(mBlankShareDoc);
        for (ShareDoc d : docMap.values()) {
            if (d.getFilecategory() == 0 && d.getFileid() != 0) {
                classDocList.add(d);
            }
        }
        Collections.sort(classDocList);
        return classDocList;
    }

    public ArrayList<ShareDoc> getAdminDocList() {
        adminDocList.clear();
        adminDocList.add(mBlankShareDoc);
        for (ShareDoc d : docMap.values()) {
            if (d.getFilecategory() == 1 && d.getFileid() != 0) {
                adminDocList.add(d);
            }
        }
        Collections.sort(adminDocList);
        return adminDocList;
    }

    public ArrayList<ShareDoc> getClassMediaList() {
        classMediaList.clear();
        for (ShareDoc d : mediaMap.values()) {
            if (d.getFilecategory() == 0) {
                classMediaList.add(d);
            }
        }
        Collections.sort(classMediaList);
        return classMediaList;
    }

    public ArrayList<ShareDoc> getAdminmMediaList() {
        adminMediaList.clear();
        for (ShareDoc d : mediaMap.values()) {
            if (d.getFilecategory() == 1) {
                adminMediaList.add(d);
            }
        }
        Collections.sort(adminMediaList);
        return adminMediaList;
    }

    public void addDocList(ShareDoc doc) {
        if (doc.isMedia()) {
            mediaMap.put(doc.getFileid(), doc);
        } else {
            if (doc.getType() == 1) {
                defaultFileDoc = doc.clone();
            }
            if (doc.getFileid() == 0) {
                setmBlankShareDoc(doc);
            }
            docMap.put(doc.getFileid(), doc);
        }
    }

    public ShareDoc getCurrentMediaDoc() {
        if (currentMediaDoc == null) {
            return currentMediaDoc = new ShareDoc();
        }
        return currentMediaDoc;
    }

    public String getFileServierUrl() {
        return fileServierUrl;
    }

    public void setFileServierUrl(String fileServierUrl) {
        this.fileServierUrl = fileServierUrl;
    }

    public int getFileServierPort() {
        return fileServierPort;
    }

    public void setFileServierPort(int fileServierPort) {
        this.fileServierPort = fileServierPort;
    }

    public ShareDoc getCurrentFileDoc() {
        if (currentFileDoc == null) {
            return currentFileDoc = new ShareDoc();
        }
        return currentFileDoc;
    }

    public void setCurrentFileDoc(ShareDoc doc) {
        if (doc != null) {
            this.currentFileDoc = doc.clone();
        }
    }

    public void setCurrentMediaDoc(ShareDoc doc) {
        this.currentMediaDoc = doc;
    }

    private WhiteBoradManager() {
    }

    static public WhiteBoradManager getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new WhiteBoradManager();
            }
            return mInstance;
        }
    }

    public void clear() {
        currentMediaDoc = null;
        currentFileDoc = null;

        classDocList.clear();
        classMediaList.clear();
        adminDocList.clear();
        adminMediaList.clear();

        docList.clear();
        mediaList.clear();
        docMap.clear();
        mediaMap.clear();
        defaultFileDoc = null;
//        mInstance = null;
    }

    public void setWBCallBack(WBStateCallBack wbCallBack) {
        this.callBack = wbCallBack;
    }

    public void onPageFinished() {
        if (callBack != null) {
            callBack.onPageFinished();
        }
    }

    public void fullScreenToLc(boolean isFull) {
        if (callBack != null) {
            callBack.onWhiteBoradZoom(isFull);
        }
    }

    public void onWBMediaPublish(String url, boolean isvideo, long fileid) {
        if (callBack != null) {
            callBack.onWhiteBoradMediaPublish(url, isvideo, fileid);
        }
    }

    public void onRoomFileChange(ShareDoc sdoc, boolean isdel, boolean islocal, boolean isClassBegin) {
        ShareDoc doc = null;
        if (sdoc.isMedia()) {
            doc = mediaMap.get(sdoc.getFileid());
        } else {
            doc = docMap.get(sdoc.getFileid());
        }
        if (doc == null) {
            doc = sdoc;
            if (doc.isMedia()) {
                mediaMap.put(doc.getFileid(), doc);
            } else {
                docMap.put(doc.getFileid(), doc);
            }
        } else if (islocal) {
            JSONObject data = Packager.pageSendData(doc);
            try {
                data.put("isDel", isdel);
                TKRoomManager.getInstance().pubMsg("DocumentChange", "DocumentChange", "__allExceptSender", data.toString(), false, null, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (doc != null) {
            if (isdel) {
                if (!doc.isMedia()) {
                    if (docMap.containsKey(doc.getFileid()) && currentFileDoc != null) {
                        if (doc.getFileid() == currentFileDoc.getFileid()) {
                            callGetNextDoc(doc);
                            if (!doc.isMedia()) {
                                localChangeDoc(currentFileDoc);
                            }
                            if (islocal && !doc.isMedia()) {
                                if (isClassBegin) {
                                    JSONObject data = Packager.pageSendData(currentFileDoc);
                                    TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", data.toString(), true, null, null);
                                }
                            }
                        } else {
                            callGetNextDoc(doc);
                            if (islocal && !doc.isMedia()) {
                                if (isClassBegin) {
                                    JSONObject data = Packager.pageSendData(currentFileDoc);
                                    TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", data.toString(), true, null, null);
                                }
                            }
                        }
                    }
                } else {
                    if (mediaMap.containsKey(doc.getFileid())) {
                        callGetNextDoc(doc);
                    }
                }
            } else {
                if (isClassBegin) {
                    currentFileDoc = sdoc;
                    if (!doc.isMedia()) {
                        localChangeDoc(currentFileDoc);
                    }
                }
                if (RoomControler.isDocumentClassification()) {
                    classDocList.add(sdoc);
                } else {
                    docList.add(sdoc);
                }

                if (doc.getType() == 1) {
                    defaultFileDoc = doc.clone();
                    currentFileDoc = defaultFileDoc;
                    localChangeDoc(currentFileDoc);
                }
            }
        }
        if (callBack != null) {
            callBack.onRoomDocChange(isdel, doc.isMedia());
            isdeling = false;
        }
    }

    private void callGetNextDoc(ShareDoc doc) {
        if (RoomControler.isDocumentClassification()) {
            getNextDoc(doc.getFileid(), doc.isMedia(), classDocList);
        } else {
            getNextDoc(doc.getFileid(), doc.isMedia(), docList);
        }
    }

    private int getIndexByDocid(long docid, boolean ismedia, ArrayList<ShareDoc> list) {
        if (!ismedia) {
            for (int i = 0; i < list.size(); i++) {
                ShareDoc dc = list.get(i);
                if (dc.getFileid() == docid) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                ShareDoc dc = list.get(i);
                if (dc.getFileid() == docid) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void getNextDoc(long docid, boolean ismedia, ArrayList<ShareDoc> list) {
        int removeIndex = getIndexByDocid(docid, ismedia, list);
        if (!ismedia) {
            ShareDoc sc = docMap.get(docid);
            list.remove(sc);
            docMap.remove(docid);
            int size = list.size();
            if (removeIndex != -1 && removeIndex <= size && currentFileDoc.getFileid() == docid) {
                if (size == removeIndex) {
                    if (list.size() > 0) {
                        currentFileDoc = list.get(list.size() - 1);
                    } else {
                        currentFileDoc = WhiteBoradManager.getInstance().getmBlankShareDoc();
                    }
                } else {
                    if (list.size() > 0 && removeIndex <= list.size()) {
                        currentFileDoc = list.get(removeIndex);
                    }
                }
                if (userrole == 2) {
                    currentFileDoc = WhiteBoradManager.getInstance().getmBlankShareDoc();
                }
            } else {
                if (removeIndex == -1) {
                    currentFileDoc = WhiteBoradManager.getInstance().getmBlankShareDoc();
                }
            }
        } else {
            ShareDoc sc = mediaMap.get(docid);
            if (RoomControler.isDocumentClassification()) {
                classMediaList.remove(sc);
            } else {
                mediaList.remove(sc);
            }
            mediaMap.remove(docid);
        }
    }

    public void delRoomFile(final String roomID, final long docid, final boolean isMedia, final boolean isClassBegin) {
        isdeling = true;
        String url = "http://" + fileServierUrl + ":" + fileServierPort + "/ClientAPI/" + "delroomfile";
        RequestParams params = new RequestParams();
        params.put("serial", roomID + "");
        params.put("fileid", docid + "");

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    ShareDoc doc = new ShareDoc();
                    doc.setFileid(docid);
                    doc.setMedia(isMedia);
                    onRoomFileChange(doc, true, true, isClassBegin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error");
            }
        });
    }

    public void uploadRoomFile(final String roomID, final String path, boolean isClassBegin) {
        isPhotoClassBegin = isClassBegin;
        isdeling = true;
        String url = "http://" + fileServierUrl + ":" + fileServierPort + "/ClientAPI/" + "uploaddocument";
        UploadFile uf = new UploadFile();
        uf.UploadOperation(url);
        uf.packageFile(path, roomID + "", TKRoomManager.getInstance().getMySelf().peerId, TKRoomManager.getInstance().getMySelf().nickName);
        uf.start();
    }

    public void localChangeDoc(ShareDoc doc) {
        setCurrentFileDoc(doc);
        if (control != null) {
            control.localChangeDoc();
        }
    }

    public void playbackPlayAndPauseController(boolean isplay) {
        if (control != null) {
            control.playbackPlayAndPauseController(isplay);
        }
    }

    public void resumeFileList() {
        if (RoomControler.isDocumentClassification()) {
            for (int i = 0; i < classDocList.size(); i++) {
                ShareDoc dc = classDocList.get(i);
                dc.setCurrentPage(1);
                dc.setPptstep(0);
                dc.setSteptotal(0);
                dc.setPptslide(1);
                if (dc.getFileid() == 0) {
                    dc.setPagenum(1);
                }
                if (currentFileDoc != null && currentFileDoc.getFileid() == dc.getFileid() && dc.getFileid() == 0) {
                    currentFileDoc = dc;
                }
            }
            for (int i = 0; i < adminDocList.size(); i++) {
                ShareDoc dc = adminDocList.get(i);
                dc.setCurrentPage(1);
                dc.setPptstep(0);
                dc.setSteptotal(0);
                dc.setPptslide(1);
                if (currentFileDoc != null && currentFileDoc.getFileid() == dc.getFileid() && dc.getFileid() == 0) {
                    currentFileDoc = dc;
                }
            }
        } else {
            for (int i = 0; i < docList.size(); i++) {
                ShareDoc dc = docList.get(i);
                dc.setCurrentPage(1);
                dc.setPptstep(0);
                dc.setSteptotal(0);
                dc.setPptslide(1);
                if (dc.getFileid() == 0) {
                    dc.setPagenum(1);
                }
                if (currentFileDoc != null && currentFileDoc.getFileid() == dc.getFileid() && dc.getFileid() == 0) {
                    currentFileDoc = dc;
                }
            }
        }
    }

    public void onUploadPhotos(JSONObject response) {
        ShareDoc docPhoto = null;
        JSONObject data = null;
        try {
            docPhoto = new ShareDoc();
            docPhoto.setSwfpath(response.getString("swfpath"));
            docPhoto.setPagenum(response.getInt("pagenum"));
            docPhoto.setFileid(response.getLong("fileid"));
            docPhoto.setDownloadpath(response.getString("downloadpath"));
            docPhoto.setSize(response.getLong("size"));
            docPhoto.setStatus(response.getInt("status"));
            docPhoto.setFilename(response.getString("filename"));
            docPhoto.setFileprop(response.getInt("fileprop"));
            docPhoto.setDynamicPPT(false);
            docPhoto.setGeneralFile(true);
            docPhoto.setH5Docment(false);
           /* docPhoto.setType(response.getInt("type"));*/
            String fileType = response.getString("filename").substring(response.getString("filename").lastIndexOf(".") + 1);
            if (!TextUtils.isEmpty(fileType)) {
                docPhoto.setFiletype(fileType);
            } else {
                docPhoto.setFiletype("jpg");
            }
            data = Packager.pageSendData(docPhoto);
            data.put("isDel", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onRoomFileChange(docPhoto, false, true, true);
        TKRoomManager.getInstance().pubMsg("DocumentChange", "DocumentChange", "__allExceptSender", data.toString(), false, null, null);
        if (isPhotoClassBegin) {
            TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", data.toString(), true, null, null);
        }
    }

    public void onWhiteBoradReceiveActionCommand(String stateJson) {
        if (callBack != null) {
            callBack.onWhiteBoradReceiveActionCommand(stateJson);
        }
    }

    public void onWhiteBoradSetProperty(String jsonProperty) {
        if (callBack != null) {
            callBack.onWhiteBoradSetProperty(jsonProperty);
        }
    }
}
