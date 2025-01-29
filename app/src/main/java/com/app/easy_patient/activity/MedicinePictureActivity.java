package com.app.easy_patient.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.easy_patient.R;
import com.app.easy_patient.adapter.MedicineImageAdapter;
import com.app.easy_patient.ktx.ImageViewExtensionKt;
import com.app.easy_patient.ktx.TheKtxKt;
import com.app.easy_patient.model.DefaultImageModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.util.AdapterCallback;
import com.app.easy_patient.util.NetworkChecker;
import com.app.easy_patient.util.SharedPrefs;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.util.FileUtil;

import java.io.File;
import java.util.ArrayList;


public class MedicinePictureActivity extends BaseActivity implements AdapterCallback {
    Integer[] medicineImages = {R.drawable.ic_med_1, R.drawable.ic_med_2, R.drawable.ic_med_3,
            R.drawable.ic_med_4, R.drawable.ic_med_5, R.drawable.ic_med_6, R.drawable.ic_med_7,
            R.drawable.ic_med_8, R.drawable.ic_med_9, R.drawable.ic_med_10, R.drawable.ic_med_11,
            R.drawable.ic_med_12};
    ImageView medicineImage, imgMedicineCustom;
    Toolbar toolbar;
    SharedPrefs sharedPrefs;
    TextView imgGallery, imgCamera;
    Button btnConfirm;
    NetworkChecker networkChecker;
    ProgressDialog progressDialog;
    GetDataService api;
    String picturePath, imageLink, defaultIcon;
    Context mContext;
    RecyclerView recyclerMedicineImage;
    ArrayList<DefaultImageModel> medicineImageList = new ArrayList<>();
    MedicineImageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_picture);
        mContext = this;
        networkChecker = new NetworkChecker(this);
        sharedPrefs = new SharedPrefs(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.photo_medicine_str));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgGallery = findViewById(R.id.img_gallery);
        imgCamera = findViewById(R.id.img_camera);
        medicineImage = findViewById(R.id.medicine_image);
        imgMedicineCustom = findViewById(R.id.medicine_image_custom);
        btnConfirm = findViewById(R.id.btn_confirm);
        recyclerMedicineImage = findViewById(R.id.recycler_image_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        recyclerMedicineImage.setLayoutManager(gridLayoutManager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            defaultIcon = extras.getString("DEFAULT_ICON");
            imageLink = extras.getString("IMAGE_LINK");
            if (!(defaultIcon.equals("0"))) {
                imgMedicineCustom.setVisibility(View.GONE);
                medicineImage.setImageResource(TheKtxKt.medicineIcon(defaultIcon));
            } else if (imageLink != null && imageLink != "") {
                Glide.with(mContext)
                        .load(imageLink)
                        .into(imgMedicineCustom);
            }
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (picturePath != null && picturePath != "") {
//                    if (networkChecker.isConnectingNetwork()) {
//                        File imgFile = new File(picturePath);
//                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", picturePath, RequestBody.create(MediaType.parse("image/jpeg"), imgFile));
//                        uploadMedicinePicture(filePart);
//                    }else
//                        Toast.makeText(mContext,"No Internet",Toast.LENGTH_SHORT).show();
//                } else
                if (picturePath != null && picturePath != "")
                    sharedPrefs.setMedicinePicturePath(picturePath);

                Intent data = new Intent();
                data.putExtra("DEFAULT_ICON", defaultIcon);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        imgGallery.setOnClickListener(v -> {
            galleryLauncher.launch(
                    ImagePicker.with(this)
                            .crop(1f, 1f)
                            .cropOval()	    		//Allow dimmed layer to have a circle inside
                            .cropFreeStyle()
                            .galleryOnly()
                            .cropFreeStyle()
                            .galleryMimeTypes( // no gif images at all
                                    new String[]{"image/png", "image/jpg", "image/jpeg"}
                            )
                            .createIntent()
            );
        });
        imgCamera.setOnClickListener(v -> {
            cameraLauncher.launch(
                    ImagePicker.with(this)
                            .crop(1f, 1f)
                            .cropOval()	    		//Allow dimmed layer to have a circle inside
                            .cropFreeStyle()
                            .cameraOnly()
                            .maxResultSize(1080, 1920, true)
                            .createIntent()
            );
        });

        api = RetrofitClientInstance.getRetrofitInstance(this).create(GetDataService.class);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading_str));
        DefaultImageModel model;
        for (int i = 0; i < medicineImages.length; i++) {
            model = new DefaultImageModel();
            model.setImage(medicineImages[i]);
            medicineImageList.add(model);
        }
        mAdapter = new MedicineImageAdapter(this, medicineImageList);
        recyclerMedicineImage.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClickCallback(int position) {
        picturePath = null;
        defaultIcon = String.valueOf(position + 1);
        clearSelection();
        medicineImageList.get(position).setSelected(true);
        imgMedicineCustom.setVisibility(View.GONE);
        medicineImage.setVisibility(View.VISIBLE);
        medicineImage.setImageResource(medicineImageList.get(position).getImage());
        mAdapter.notifyDataSetChanged();
    }

    public void clearSelection() {
        for (int i = 0; i < medicineImageList.size(); i++) {
            medicineImageList.get(i).setSelected(false);
        }
    }

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    defaultIcon = "0";
                    clearSelection();
                    mAdapter.notifyDataSetChanged();
                    medicineImage.setVisibility(View.GONE);
                    imgMedicineCustom.setVisibility(View.VISIBLE);

                    mImageCaptureUri = result.getData().getData();
                    File file = FileUtil.INSTANCE.getTempFile(this, mImageCaptureUri);
                    picturePath = file.getAbsolutePath();
                    ImageViewExtensionKt.setLocalImage(imgMedicineCustom, mImageCaptureUri, false);
                } else parseError(result);
            }
    );

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    defaultIcon = "0";
                    clearSelection();
                    mAdapter.notifyDataSetChanged();
                    medicineImage.setVisibility(View.GONE);
                    imgMedicineCustom.setVisibility(View.VISIBLE);

                    mImageCaptureUri = result.getData().getData();
                    File file = FileUtil.INSTANCE.getTempFile(this, mImageCaptureUri);
                    picturePath = file.getAbsolutePath();
                    ImageViewExtensionKt.setLocalImage(imgMedicineCustom, mImageCaptureUri, false);
                } else parseError(result);
            }
    );

    private void parseError(ActivityResult activityResult) {
        if (activityResult.getResultCode() == ImagePicker.RESULT_ERROR) {
            showToast(this, ImagePicker.getError(activityResult.getData()));
        } else {
            showToast(this, getString(R.string.task_cancelled_str));
        }
    }
}
