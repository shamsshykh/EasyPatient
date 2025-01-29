package com.app.easy_patient.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.easy_patient.BuildConfig;
import com.app.easy_patient.R;
import com.app.easy_patient.model.ImageUploadModel;
import com.app.easy_patient.model.StatusModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.app.easy_patient.util.NetworkChecker;
import com.app.easy_patient.util.SharedPrefs;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.easy_patient.util.AppConstants.REQUEST_CODE_KEYS.REQUEST_CODE_GALLERY;
import static com.app.easy_patient.util.AppConstants.REQUEST_CODE_KEYS.REQUEST_CODE_TAKE_PICTURE;

public class UploadProfileImageActivity extends BaseActivity {
    ImageView profileImage;
    Toolbar toolbar;
    SharedPrefs sharedPrefs;
    TextView imgGallery, imgCamera, tvDeletePhoto;
    Button btnConfirm;
    NetworkChecker networkChecker;
    ProgressDialog progressDialog;
    GetDataService api;
    String picturePath = null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_image);
        mContext = this;
        networkChecker = new NetworkChecker(this);
        sharedPrefs = new SharedPrefs(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Photo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgGallery = findViewById(R.id.img_gallery);
        imgCamera = findViewById(R.id.img_camera);
        profileImage = findViewById(R.id.profile_image);
        tvDeletePhoto = findViewById(R.id.tv_delete_photo);
        btnConfirm = findViewById(R.id.btn_confirm);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imageLink = extras.getString("IMAGE_LINK");
            if (sharedPrefs.getGender().equals("m")) {
                Glide.with(mContext)
                        .load(imageLink)
                        .apply(imagePlaceHolder(R.drawable.ic_profile_pic_male))
                        .into(profileImage);
            } else {
                Glide.with(mContext)
                        .load(imageLink)
                        .apply(imagePlaceHolder(R.drawable.profile_pic_female))
                        .into(profileImage);
            }
        }

        btnConfirm.setOnClickListener(v -> {
            if (picturePath != null) {
                File imgFile = new File(picturePath);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", picturePath, RequestBody.create(MediaType.parse("image/jpeg"), imgFile));
                uploadProfilePicture(filePart);
            } else
                finish();
        });

        imgGallery.setOnClickListener(v -> {
            dispatchGalleryIntent();
        });
        imgCamera.setOnClickListener(v -> {
            dispatchTakePictureIntent();

        });
        tvDeletePhoto.setOnClickListener(v -> {
            deleteProfilePicture();
        });

        api = RetrofitClientInstance.getRetrofitInstance(this).create(GetDataService.class);
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Glide.with(mContext)
                        .load(imageBitmap)
                        .into(profileImage);
                galleryAddPic(picturePath);
            } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
                Glide.with(mContext)
                        .load(imageBitmap)
                        .into(profileImage);
            }
        }
    }

    private void uploadProfilePicture(MultipartBody.Part filePart) {
        progressDialog.show();
        Call<ImageUploadModel> uploadImageResponse = api.uploadProfilePicture(filePart);
        uploadImageResponse.enqueue(new Callback<ImageUploadModel>() {
            @Override
            public void onResponse(Call<ImageUploadModel> call, Response<ImageUploadModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getUpload()) {
                        sharedPrefs.setImagePath(picturePath);
                        finish();
                    }
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ImageUploadModel> call, Throwable t) {
                Log.e("Image_upload_Error", Objects.requireNonNull(t.getMessage()));
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void deleteProfilePicture() {
        progressDialog.show();
        Call<StatusModel> deletePictureResponse = api.deleteProfilePicture();
        deletePictureResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        sharedPrefs.setImagePath(null);
                        if (sharedPrefs.getGender().equals("m")) {
                            Glide.with(mContext)
                                    .load(R.drawable.ic_profile_pic_male)
                                    .into(profileImage);
                        } else {
                            Glide.with(mContext)
                                    .load(R.drawable.profile_pic_female)
                                    .into(profileImage);
                        }
                    }
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                Log.e("delete_picture_Error", t.getMessage());
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        picturePath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
            }
        }
    }

    private void dispatchGalleryIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_CODE_GALLERY);
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
