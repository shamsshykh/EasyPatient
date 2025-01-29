package com.app.easy_patient.activity;

import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.easy_patient.R;
import com.app.easy_patient.database.EasyPatientDatabase;
import com.app.easy_patient.database.OrientationDetailDao;
import com.app.easy_patient.database.OrientationListModel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrientationDetailActivity extends BaseActivity {
    Context mContext;
    Toolbar toolbar;
    OrientationListModel orientationDetail;
    TextView tvTitle, tvDate, tvSpecialist, tvInfo;
    ImageView imgDownload, imgShare, imgArchive;
    boolean flag;
    private EasyPatientDatabase db;
    private OrientationDetailDao mOrientationDetailDao;
    PDFView pdfView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_detail);

        mContext = this;
        PRDownloader.initialize(getApplicationContext());
        pdfView = findViewById(R.id.pdf_view);
        toolbar = findViewById(R.id.toolbar);
        db = EasyPatientDatabase.getDatabase(mContext);
        mOrientationDetailDao = db.orientationDetailDao();
        tvTitle = findViewById(R.id.tv_title);
        tvDate = findViewById(R.id.tv_date);
        tvSpecialist = findViewById(R.id.tv_specialist);
        imgDownload = findViewById(R.id.img_download);
        imgShare = findViewById(R.id.img_share);
        imgArchive = findViewById(R.id.img_archive);
        tvInfo = findViewById(R.id.tv_info);

        orientationDetail = (OrientationListModel) getIntent().getSerializableExtra("orientation_detail");
        flag = getIntent().getBooleanExtra("flag", false);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.orientations_detail_str));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(orientationDetail.getTitle());
        tvDate.setText(orientationDetail.getDate());
        tvSpecialist.setText(getString(R.string.doctor_str, orientationDetail.getSpecialist()));
        tvInfo.setText(getString(R.string.doctor_name_plus_date_str, orientationDetail.getSpecialist().trim(), orientationDetail.getDate().trim()));

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createAndSavePdf(mContext, String.valueOf(tvContent.getText()),tvTitle.getText().toString());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(orientationDetail.getFile())));
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

        // downloadPdfFromInternet(orientationDetail.getFile(), getFilePath("Temp"), "backup");
        if (orientationDetail.getFile() != null) {
            retrievePDFFromURL(orientationDetail.getFile());
        }
    }

    private void shareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, orientationDetail.getFile());
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(orientationDetail.getFile())));
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
            OrientationArchiveResponse = api.putOrientationsAvailable(orientationDetail.getId());
        else
            OrientationArchiveResponse = api.postOrientationsArchive(orientationDetail.getId());
        OrientationArchiveResponse.enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                if (response.isSuccessful()) {
                    boolean status = response.body().getStatus();
                    if (status) {
//                            OrientationListModel model = orientationList.get(position);
                        if (flag) {
                            db.databaseWriteExecutor.execute(() -> {
                                mOrientationDetailDao.insertOrientationItem(orientationDetail);
                            });
                            imgArchive.setImageResource(R.drawable.ic_bottom_archive);
                            showToast(mContext,getResources().getString(R.string.unarchived_successfully));
                        } else {
                            db.databaseWriteExecutor.execute(() -> {
                                mOrientationDetailDao.deleteOrientationItem(orientationDetail.getId());
                            });
                            imgArchive.setImageResource(R.drawable.ic_unarchive_bottom);
                            showToast(mContext,getResources().getString(R.string.archived_successfully));
                        }

                        flag = !flag;
                    } else
                        Toast.makeText(mContext, getString(R.string.error_str), Toast.LENGTH_SHORT).show();
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
                .autoSpacing(false)
                .spacing(4)
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
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {

                    }
                }).load();
    }
}
