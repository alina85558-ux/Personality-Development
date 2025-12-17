package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.createScribingCards;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.deleteScribingCards;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.getScribingCards;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ScribingListingCategoriesAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.Card;
import com.mayur.personalitydevelopment.models.InnerScribingData;
import com.mayur.personalitydevelopment.models.Note;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class ScribingActivity extends BaseActivity implements ScribingListingCategoriesAdapter.AdapterListener {

    public static void start(Context context, int courseCategoryId, int categoryId, String course) {
        Intent starter = new Intent(context, ScribingActivity.class);
        starter.putExtra("categoryId", categoryId);
        starter.putExtra("course", course);
        starter.putExtra("courseCategoryId", courseCategoryId);
        context.startActivity(starter);
    }

    private ProgressBar progressBar;
    private RecyclerView rvList;
    private InnerScribingData responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribing);
        String course = getIntent().getStringExtra("course");
        Toolbar maintoolbar = findViewById(R.id.maintoolbar);
        rvList = findViewById(R.id.rvList);
        progressBar = findViewById(R.id.progressBar);
        setSupportActionBar(maintoolbar);
        maintoolbar.setTitle(course);
        setTitle(course);
        maintoolbar.setNavigationOnClickListener(v -> onBackPressed());

        getCards();
    }

    void getCards() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            connectPost(this, null, getScribingCards(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value()), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    responseData = new Gson().fromJson(response, InnerScribingData.class);
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);

                    flatTheListAndUpdateUI(responseData);
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    progressBar.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    private void flatTheListAndUpdateUI(InnerScribingData responseData) {
        ArrayList<Object> items = new ArrayList<>();
        for (int index = 0; index < responseData.getCards().size(); index++) {
            Object item = responseData.getCards().get(index);
            items.add(item);
            for (int noteIndex = 0; noteIndex < ((Card) item).getNotes().size(); noteIndex++) {
                Note note = ((Card) item).getNotes().get(noteIndex);
                items.add(note);
            }
        }

        ScribingListingCategoriesAdapter adapter = new ScribingListingCategoriesAdapter(getBaseContext(), items, ScribingActivity.this);
        rvList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rvList.addItemDecoration(new DividerItemDecoration(getBaseContext(), DividerItemDecoration.HORIZONTAL));
        rvList.setAdapter(adapter);

        /*if (items != null && items.size() > 0) {
            findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onClickEvent(Object courseCategory) {
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        if(courseCategory instanceof Card){
            try{
                final Card selectedCard = (Card) courseCategory;
                connectPost(this, null, deleteScribingCards(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), selectedCard), new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        responseData.getCards().remove(selectedCard);
                        flatTheListAndUpdateUI(responseData);
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
//                    progressBar.setVisibility(View.GONE);
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Utils.hideDialog();
//                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onConnectionFailure() {
//                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Utils.hideDialog();
//                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Utils.hideDialog();
            }
        }else {
            try {
//            String categoryTitle = (findViewById(R.id.tiCategoryTitle)).getText();

//            progressBar.setVisibility(View.VISIBLE);

                Note selectedNote = (Note) courseCategory;

                boolean isUpdating = true;
                Card selectedCard = null;
                for (int index = 0; index < responseData.getCards().size(); index++) {
                    Card card = responseData.getCards().get(index);
                    for (int noteIndex = 0; noteIndex < card.getNotes().size(); noteIndex++) {
                        Note note = card.getNotes().get(noteIndex);
                        if (Objects.equals(note.getId(), selectedNote.getId())) {
                            note.setIsChecked(!note.getIsChecked());
                            selectedCard = card;
                            selectedCard.setCourse_category_id(selectedCard.getId());
                            break;
                        }
                    }
                    if (selectedCard != null) {
                        break;
                    }
                }

                if (selectedCard == null)
                    return;

                connectPost(this, null, createScribingCards(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), selectedCard, isUpdating), new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        flatTheListAndUpdateUI(responseData);
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
//                    progressBar.setVisibility(View.GONE);
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        Utils.hideDialog();
//                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onConnectionFailure() {
//                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        Utils.hideDialog();
//                    progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Utils.hideDialog();
            }
        }
    }

    public void onAddItemClick(View view) {
        AddScribingActivity.start(this, responseData,courseCategoryId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK){
            getCards();
        }
    }
}
