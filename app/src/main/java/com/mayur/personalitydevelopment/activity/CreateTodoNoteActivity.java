package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.AddNoteListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivityCreateTodoNoteBinding;
import com.mayur.personalitydevelopment.listener.TodoItemDeleteListener;
import com.mayur.personalitydevelopment.models.AddNoteListModel;
import com.mayur.personalitydevelopment.models.TodoListResponse;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class CreateTodoNoteActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener, TodoItemDeleteListener {

    private ActivityCreateTodoNoteBinding binding;
    private ArrayList<AddNoteListModel.AddNoteList> noteArrayList;
    private String note;
    private AddNoteListAdapter addNoteListAdapter;
    private TodoListResponse.Cards selectedItemListObject;
    private int selectedItemPosition;
    private TextView emptyMessageTextView;
    private boolean isForUpdateData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_todo_note);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.todoTitleEditText.requestFocus();
        binding.appbar.addOnOffsetChangedListener(this);
        initV();
        if (getIntent().getSerializableExtra("selectedItem") != null) {
            selectedItemPosition = getIntent().getIntExtra("selectedItemPosition", 0);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.update_todo_activity);
            }
            binding.saveNoteButton.setText(R.string.update_note);
            isForUpdateData = true;
            selectedItemListObject = (TodoListResponse.Cards) getIntent().getSerializableExtra("selectedItem");
            binding.todoTitleEditText.setText(selectedItemListObject.getName());
            for (int i = 0; selectedItemListObject.getNotes().size() > i; i++) {
                AddNoteListModel.AddNoteList addNoteList = new AddNoteListModel().new AddNoteList();
                addNoteList.setNoteTitle(selectedItemListObject.getNotes().get(i).getTitle());
                addNoteList.setNoteCompleted(selectedItemListObject.getNotes().get(i).getIs_checked());
                noteArrayList.add(addNoteList);
            }
        }
        setEmptyMessage();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        binding.todoTitleEditText.requestFocus();
        Log.d("=>>> ", "== " + verticalOffset);
        if (verticalOffset == 0) {
            Log.d("=>>> ", "== Expanded");
//            binding.titleTextView.setVisibility(View.VISIBLE);
            binding.collapsingToolbar.setTitle("");
        } else if (verticalOffset > 50 || verticalOffset < -50) {
            Log.d("=>>> ", "== Collapsed");
//            binding.titleTextView.setVisibility(View.INVISIBLE);

            if (binding.todoTitleEditText.getText().toString().trim().equals("")) {
                binding.collapsingToolbar.setTitle("This is title");
            } else {
                binding.collapsingToolbar.setTitle(binding.todoTitleEditText.getText().toString());
            }
        } else {
            Log.d("=>>> ", "Scrolling");
//            binding.collapsingToolbar.setTitle("Title");
            binding.collapsingToolbar.setTitle("");
        }

    }

    private void initV() {
        noteArrayList = new ArrayList<>();

        addNoteListAdapter = new AddNoteListAdapter(this, noteArrayList, this);
        binding.addNoteRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.addNoteRecyclerView.setAdapter(addNoteListAdapter);

        binding.addTodoImage.setOnClickListener(this);
        binding.saveNoteButton.setOnClickListener(this);
    }

    private void showNewNoteDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.custom_dialog_add_note, viewGroup, false);
        builder.setView(dialogView);

        EditText noteNameEditText = dialogView.findViewById(R.id.taskNameEditText);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button addButton = dialogView.findViewById(R.id.addButton);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note = noteNameEditText.getText().toString().trim();
                if (!note.equals("")) {
                    AddNoteListModel.AddNoteList addNoteList = new AddNoteListModel().new AddNoteList();
                    addNoteList.setNoteTitle(note.trim());
                    addNoteList.setNoteCompleted(false);
                    noteArrayList.add(addNoteList);
                    binding.addNoteRecyclerView.setAdapter(addNoteListAdapter);
                    binding.addNoteRecyclerView.smoothScrollToPosition(noteArrayList.size() - 1);
                    alertDialog.dismiss();
                    setEmptyMessage();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(noteNameEditText.getWindowToken(), 0);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

        noteNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                noteNameEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(noteNameEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        noteNameEditText.requestFocus();
    }

    private void setEmptyMessage() {
        if (noteArrayList.size() > 0) {
            binding.emptyMessageTextView.setVisibility(View.GONE);
        } else {
            binding.emptyMessageTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addTodoImage) {
            showNewNoteDialog(v);
        } else if (v.getId() == R.id.saveNoteButton) {
            if (validateFields()) {
                saveNote();
            }
        }
    }

    private void saveNote() {
        AddNoteListModel addNoteListModel = new AddNoteListModel();
        addNoteListModel.setNoteHeader(binding.todoTitleEditText.getText().toString().trim());
        addNoteListModel.setCourseCategoryId("6");
        addNoteListModel.setAddNoteList(addNoteListAdapter.addNoteList);
        if (isForUpdateData) {
            updateNote(addNoteListModel);
        } else {
            saveNoteToServer(addNoteListModel);
        }
    }

    private void saveNoteToServer(AddNoteListModel addNoteListModel) {
        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.createNote(BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(),
                addNoteListModel),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        displayMessage(R.string.node_created_successfully);
                        finish();
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });

    }

    private void updateNote(AddNoteListModel addNoteListModel) {

        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.updateNoteData(BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(),
                selectedItemListObject.getId() + "",
                addNoteListModel),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        displayMessage(R.string.node_updated_successfully);
                        finish();
                    }

                    @Override
                    public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                    }

                    @Override
                    public void onFailure(Headers headers) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onConnectionFailure() {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "CC Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }

                    @Override
                    public void onException(Headers headers, int StatusCode) {
                        try {
                            Utils.hideDialog();
                            Toast.makeText(getBaseContext(), "EE Failure", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.hideDialog();
                        }
                    }
                });
    }

    private boolean validateFields() {
        if (binding.todoTitleEditText.getText().toString().trim().equals("")) {
            displayMessage(R.string.error_enter_todo_Titile);
            return false;
        } else if (noteArrayList != null && noteArrayList.size() == 0) {
            displayMessage(R.string.error_add_note);
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public void todoItemDeleteListn(ArrayList<AddNoteListModel.AddNoteList> selectedItemList) {
//        noteArrayList.set(selectedItemPosition, selectedItemList)
    }
}
