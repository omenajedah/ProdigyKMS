package app.dodi.com.prodigykms.util;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.util.SparseBooleanArray;

import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.dodi.com.prodigykms.application.MyApplication;
import app.dodi.com.prodigykms.object.DownloadObject;

/**
 * Created by User on 07/01/2018.
 */

public class DownloaderHelper {

    private Context context;
    private List<DownloadObject> downloadObjectList;
    private DownloadListener downloadListener;

    private final SparseBooleanArray canceledTask = new SparseBooleanArray();
    private final SparseBooleanArray finishedTask = new SparseBooleanArray();

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private int CURRENTDOWNLOAD = -1;

    public DownloaderHelper(Context context) {
        this.context = context;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void start(List<DownloadObject> downloadObjectList) {
        this.downloadObjectList = downloadObjectList;
        executor.submit(new Runnable() {
            @Override
            public void run() {
                startDownload();
            }
        });
    }

    @WorkerThread
    private void startDownload() {
        for (int i=0;i<downloadObjectList.size();i++) {

            if (!canceledTask.get(i, false)) {
                final int finalPos = i;

                if (downloadListener != null)
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            downloadListener.onDownloadProgress(finalPos);
                        }
                    });

                DownloadObject currentDownloaded = downloadObjectList.get(i);
                ANResponse<JSONObject> response = RequestorHelper.get(context)
                        .addRequest(currentDownloaded.getUrl(), currentDownloaded.getParam());
                if (response.isSuccess()) {
                    JSONObject result = response.getResult();
                    downloadObjectList.get(i).setResponse(result);
                    if (downloadListener != null){
                        try {
                            downloadListener.onDownloadSuccess(i, getHandler());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    final ANError error = response.getError();
                    if (downloadListener != null) {
                        getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                downloadListener.onDownloadError(finalPos, error);
                            }
                        });
                    }
                }
            }

            if (i+1 == downloadObjectList.size() && downloadListener != null) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onDownloadsFinished();
                    }
                });
            }
        }
    }

    private android.os.Handler getHandler() {
        return new android.os.Handler(Looper.getMainLooper());
    }



    public void cancel(int pos) {
        synchronized (canceledTask) {
            canceledTask.put(pos, true);
        }
    }

    public void cancelAll() {
        synchronized (canceledTask) {
            if (CURRENTDOWNLOAD != -1 && CURRENTDOWNLOAD+1 != downloadObjectList.size()&&!executor.isShutdown()) {
                executor.shutdown();
                executor.shutdownNow();
            }
        }
    }



    public interface DownloadListener {
        void onDownloadProgress(int pos);
        void onDownloadSuccess(int pos, android.os.Handler mainThread);
        void onDownloadError(int pos, ANError error);
        void onDownloadsFinished();
    }
}
