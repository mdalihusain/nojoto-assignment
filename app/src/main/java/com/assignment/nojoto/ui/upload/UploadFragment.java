package com.assignment.nojoto.ui.upload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.assignment.nojoto.R;
import com.assignment.nojoto.databinding.FragmentHomeBinding;
import com.assignment.nojoto.databinding.FragmentUploadBinding;
import com.assignment.nojoto.ui.home.HomeViewModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    private HomeViewModel homeViewModel;
    private static final int TAKE_PICTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getUploadMessage().observe(getViewLifecycleOwner(), message -> {
            if (!message.equals("")) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        takePhoto();
        binding.uploadAgainButton.setOnClickListener(v -> takePhoto());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        binding.selectedImage.setImageBitmap(photo);
                        File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "pic.jpeg");
                        OutputStream os = null;
                        os = new BufferedOutputStream(new FileOutputStream(file));
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.close();
                        homeViewModel.uploadPhoto(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}