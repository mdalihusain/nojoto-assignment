package com.assignment.nojoto.ui.home;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> mPosts;
    private MutableLiveData<String> uploadMessage;
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    private final OkHttpClient client = new OkHttpClient();

    public LiveData<ArrayList<String>> getPosts() {
        // CHECKING IF THE METHOD IS BEING CALLED FOR THE FIRST TIME
        if (mPosts == null) {
            mPosts = new MutableLiveData<ArrayList<String>>();
            fetchPosts();
        }
        return mPosts;
    }

    public void fetchPosts() {
        ArrayList<String> posts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            posts.add("");
        }
        mPosts.postValue(posts);
    }

    public LiveData<String> getUploadMessage() {
        if (uploadMessage==null){
            uploadMessage = new MutableLiveData<>();
            uploadMessage.postValue("");
        }
        return uploadMessage;
    }

    public void uploadPhoto(File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", "media")
                        .addFormDataPart("image", "pic.jpeg",
                                RequestBody.create(file, MEDIA_TYPE_JPEG))
                        .build();

                Request request = new Request.Builder()
                        .url("https://dev.nojoto.com/api/beta/content.php?cid=7ec99b415af3e88205250e3514ce0fa7")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    Log.d("UPLOADIMAGE", response.body().string());
                    uploadMessage.postValue("Image Uploaded Successfully!");
                } catch (IOException e) {
                    Log.d("UPLOADIMAGEERROR", e.getMessage());
                    uploadMessage.postValue("Image Upload Error!");
                }
            }
        }).start();
    }


}