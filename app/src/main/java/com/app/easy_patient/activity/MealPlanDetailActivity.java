package com.app.easy_patient.activity;

import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.easy_patient.R;
import com.app.easy_patient.database.EasyPatientDatabase;
import com.app.easy_patient.database.MealPlanDetailDao;
import com.app.easy_patient.database.MealPlanModel;
import com.app.easy_patient.model.StatusModel;
import com.app.easy_patient.network.GetDataService;
import com.app.easy_patient.network.RetrofitClientInstance;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealPlanDetailActivity extends BaseActivity {
    Context mContext;
    Toolbar toolbar;
    MealPlanModel mealPlanDetail;
    TextView tvTitle, tvDate, tvSpecialist, tvInfo;
    ImageView imgDownload, imgShare, imgArchive;
    boolean flag;
    private EasyPatientDatabase db;
    private MealPlanDetailDao mMealPlanDetailDao;
    PDFView pdfView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan_detail);

        mContext = this;
        PRDownloader.initialize(getApplicationContext());
        pdfView = findViewById(R.id.pdf_view);
        toolbar = findViewById(R.id.toolbar);
        db = EasyPatientDatabase.getDatabase(mContext);
        mMealPlanDetailDao = db.mealPlanDetailDao();
        tvTitle = findViewById(R.id.tv_title);
        tvDate = findViewById(R.id.tv_date);
        tvSpecialist = findViewById(R.id.tv_specialist);
        imgDownload = findViewById(R.id.img_download);
        imgShare = findViewById(R.id.img_share);
        imgArchive = findViewById(R.id.img_archive);
        tvInfo = findViewById(R.id.tv_info);

        mealPlanDetail = (MealPlanModel) getIntent().getSerializableExtra("meal_plan_detail");
        flag = getIntent().getBooleanExtra("flag", false);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.meal_plan_str));
        if (flag)
            imgArchive.setImageResource(R.drawable.ic_unarchive_bottom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(mealPlanDetail.getTitle());
        tvDate.setText(mealPlanDetail.getDate());
        tvSpecialist.setText(getString(R.string.doctor_str, mealPlanDetail.getSpecialist()));
        tvInfo.setText(getString(R.string.doctor_name_plus_date_str, mealPlanDetail.getSpecialist().trim(), mealPlanDetail.getDate().trim()));

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createAndSavePdf(mContext, String.valueOf(tvContent.getText()),tvTitle.getText().toString());
                //savePDF(mealPlanDetail.getFile(), getFilePath("Downloaded files"), "mealPlan_"+mealPlanDetail.getId(),mContext);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mealPlanDetail.getFile())));
            }
        });

        imgArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postArchiveStatus();
            }
        });
        imgShare.setOnClickListener(v -> {
            shareIntent();
        });

        //downloadPdfFromInternet(mealPlanDetail.getFile(), getFilePath("Temp"), "backup");
        if (mealPlanDetail.getFile() != null) {
            retrievePDFFromURL(mealPlanDetail.getFile());
        }
    }

    private void shareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mealPlanDetail.getFile());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.document_detail_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                shareIntent();
                return true;
            case R.id.action_archive:
                postArchiveStatus();
                return true;
            case R.id.action_download:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mealPlanDetail.getFile())));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postArchiveStatus() {
        progressDialog = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading_str));
        progressDialog.show();
        Call<StatusModel> OrientationArchiveResponse = null;
        GetDataService api = RetrofitClientInstance.getRetrofitInstance(mContext).create(GetDataService.class);
        if (flag)
            OrientationArchiveResponse = api.putMealPlanAvailable(mealPlanDetail.getId());
        else
            OrientationArchiveResponse = api.postMealPlanArchive(mealPlanDetail.getId());
        OrientationArchiveResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    boolean status = response.body().getStatus();
                    if (status) {
//                        PrescriptionListModel model=prescriptionList.get(position);
                        if (flag) {
                            db.databaseWriteExecutor.execute(() -> {
                                mMealPlanDetailDao.insertMealPlanItem(mealPlanDetail);
                            });
                            imgArchive.setImageResource(R.drawable.ic_bottom_archive);
                            showToast(mContext, getResources().getString(R.string.unarchived_successfully));
                        } else {
                            db.databaseWriteExecutor.execute(() -> {
                                mMealPlanDetailDao.deleteMealPlanItem(mealPlanDetail.getId());
                            });
                            imgArchive.setImageResource(R.drawable.ic_unarchive_bottom);
                            showToast(mContext, getResources().getString(R.string.archived_successfully));
                        }
                        flag = !flag;
                    } else
                        Toast.makeText(mContext,  getString(R.string.error_str), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.server_detail_error_str), Toast.LENGTH_SHORT).show();
                }

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<StatusModel> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void downloadPdfFromInternet(String url, String dirPath, String fileName) {
        PRDownloader.download(
                url,
                dirPath,
                fileName
        ).build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        File downloadedFile = new File(dirPath, fileName);
                        showPdfFromFile(downloadedFile);
                    }

                    @Override
                    public void onError(Error error) {

                    }

                });
    }

    private void showPdfFromFile(File file) {
        pdfView.fromFile(file).onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .password(null)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {

                    }
                }).load();
    }

    InputStream inputStream = null;
    // TODO: Retrieve PDF from URL
    private void retrievePDFFromURL(String urlString){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            // we are using inputstream
            // for getting out PDF.
            try {
                URL url = new URL(urlString);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
            }
            handler.post(() -> {
                showPdfFromFile(inputStream);
            });
        });
    }

    private void showPdfFromFile(InputStream inputStream) {
        pdfView.fromStream(inputStream).onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .password(null)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .autoSpacing(false)
                .spacing(4)

                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {

                    }
                }).load();
    }
}