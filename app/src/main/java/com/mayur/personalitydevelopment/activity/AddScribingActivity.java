package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiCallBack.createScribingCards;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.ScribingChipCategoriesAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.models.Card;
import com.mayur.personalitydevelopment.models.InnerScribingData;
import com.mayur.personalitydevelopment.models.Note;
//import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.Objects;

import customview.FLMFlowLayoutManager;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public class AddScribingActivity extends BaseActivity implements ScribingChipCategoriesAdapter.AdapterListener {

    private EditText edTitle, edCategoryTitle;
    private Card selectedCard;

    public static void start(Activity context, InnerScribingData responseData, int courseCategoryId) {
        Intent starter = new Intent(context, AddScribingActivity.class);
        starter.putExtra("responseData", responseData);
        starter.putExtra("courseCategoryId", courseCategoryId);
        context.startActivityForResult(starter, 1000);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottomsheet_add_scribing);

        InnerScribingData responseData = (InnerScribingData) Objects.requireNonNull(getIntent().getExtras()).getSerializable("responseData");
        findViewById(R.id.btnLogin).setOnClickListener(v -> onDoneClick());
        Toolbar maintoolbar = findViewById(R.id.maintoolbar);
        setSupportActionBar(maintoolbar);
        maintoolbar.setTitle("Add Scribing");
        setTitle("Add Scribing");
        maintoolbar.setNavigationOnClickListener(v -> onBackPressed());
        RecyclerView rvList = findViewById(R.id.rvList);
        edTitle = findViewById(R.id.edTitle);
        edCategoryTitle = findViewById(R.id.edCategoryTitle);

        int item_spacing = getResources().getDimensionPixelSize(R.dimen.spacing_between_items);
        int lines_spacing = getResources().getDimensionPixelSize(R.dimen.spacing_between_lines);

        final FLMFlowLayoutManager flowLayoutManager = new FLMFlowLayoutManager(FLMFlowLayoutManager.VERTICAL, Gravity.CENTER,item_spacing,lines_spacing);


        if (responseData == null || responseData.getCards() == null || responseData.getCards().size() == 0) {
            findViewById(R.id.llCategories).setVisibility(View.GONE);
        } else {
            rvList.setLayoutManager(flowLayoutManager);
            rvList.setAdapter(new ScribingChipCategoriesAdapter(this, responseData.getCards(), this));
        }
    }


    void onDoneClick() {
        try {
//            String categoryTitle = (findViewById(R.id.tiCategoryTitle)).getText();

//            progressBar.setVisibility(View.VISIBLE);
            Utils.showDialog(this);

            String authToken = "";
            if (Constants.getUserData(this) != null) {
                authToken = Constants.getUserData(this).getAuthentication_token();
            }

            boolean isUpdating = false;
            Card card = new Card();
            ArrayList<Note> notes = new ArrayList<>();
            if (selectedCard != null && selectedCard.getName().equalsIgnoreCase(edCategoryTitle.getText().toString())) {
                card.setId(selectedCard.getId());
                notes = selectedCard.getNotes();
                isUpdating = true;
            }
            card.setCourse_category_id(courseCategoryId);
            card.setName(edCategoryTitle.getText().toString());
            Note note = new Note();
            note.setTitle(edTitle.getText().toString());
            notes.add(note);
            card.setNotes(notes);

            connectPost(this, null, createScribingCards(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), card, isUpdating), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
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

    public void onClickEvent(Object courseCategory) {
        selectedCard = (Card) courseCategory;
        edCategoryTitle.setText(((Card) courseCategory).getName());
    }

}
