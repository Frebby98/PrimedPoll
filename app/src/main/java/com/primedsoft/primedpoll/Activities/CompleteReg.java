package com.primedsoft.primedpoll.Activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.primedsoft.primedpoll.Adapter.SelectedInterestAdapter;
import com.primedsoft.primedpoll.Models.Data;
import com.primedsoft.primedpoll.Models.Interest;
import com.primedsoft.primedpoll.R;
import com.primedsoft.primedpoll.api.ApiInterface;
import com.primedsoft.primedpoll.api.RetrofitInstance;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteReg extends AppCompatActivity {

    EditText firstNameEdit, lastNameEdit, phoneEdit, dobEdit;
    FloatingActionButton completeRegFab;
    private DatePickerDialog datePicker;
    private RecyclerView recyclerView;
    private SelectedInterestAdapter adapter;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_registration);
        token = getIntent().getStringExtra("token");
        dobEdit = findViewById(R.id.dob);
        dobEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(dobEdit);
            }
        });
        datePicker(dobEdit);
        recyclerView = findViewById(R.id.selected_interest_recycler);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        firstNameEdit = findViewById(R.id.first_name);
        lastNameEdit = findViewById(R.id.last_name);
        phoneEdit = findViewById(R.id.phone);

        completeRegFab = findViewById(R.id.complete_reg_fab);
        completeRegFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {

                            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(CompleteReg.this);
                            Gson gson = new Gson();
                            String json = sharedPrefs.getString("TAG", "");
                            Type type = new TypeToken<ArrayList<Interest>>() {
                            }.getType();
                            final ArrayList<Interest> arrayList = gson.fromJson(json, type);
                            adapter = new SelectedInterestAdapter(arrayList);
                            ApiInterface apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
                            String first_name = firstNameEdit.getText().toString();
                            String last_name = lastNameEdit.getText().toString();
                            String phone = phoneEdit.getText().toString();
                            String dob = dobEdit.getText().toString();
                            Data data = new Data(first_name, last_name, phone, dob, arrayList);
                            RequestBody body =
                                    RequestBody.create(MediaType.parse("text/plain"), String.valueOf(data));

                            Call<ResponseBody> call = apiInterface.completeReg(token,body);
                            call.enqueue(new Callback<ResponseBody>() {
                                 @Override
                                 public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                     Gson gson = new Gson();
                                     Type type = new TypeToken<ErrorResponse>() {}.getType();
                                     ErrorResponse errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                                 }

                                 @Override
                                 public void onFailure(Call<ResponseBody> call, Throwable t) {

                                 }
                             });

                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();



//             data.setFirst_name(first_name);
//               data.setLast_name(last_name);
//data.setPhone(phone);
//data.setDob(dob);
//data.setUserinterests(arrayList);



//                apiInterface.completeReg(
//                        token,
//                        data.getFirst_name(),
//                        data.getFirst_name(),
//                        data.getPhone(),
//                        data.getDob(),
//                        String.valueOf(data.getUserinterests()))
//                        .enqueue(new Callback<Data>() {
//                            @Override
//                            public void onResponse(Call<Data> call, Response<Data> response) {
//
//                            }
//
//                            @Override
//                            public void onFailure(Call<Data> call, Throwable t) {
//
//                            }
//                        });
//
//
//            }
//        });
            }
        });
    }

    public void datePicker(final EditText date) {
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePicker = new DatePickerDialog(CompleteReg.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });
    }


}
