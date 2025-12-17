package com.mayur.personalitydevelopment.activity;

import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.TodoListAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiCallBack;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.databinding.ActivityTodoListBinding;
import com.mayur.personalitydevelopment.listener.TodoItemClickListener;
import com.mayur.personalitydevelopment.models.TodoListResponse;

import java.util.ArrayList;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class TodoListActivity extends BaseActivity implements View.OnClickListener, TodoItemClickListener {

    private ActivityTodoListBinding binding;
    private TodoListAdapter todoListAdapter;
    private ArrayList<TodoListResponse.Cards> todoList;
    private int categoryId;

    public static void start(Context context, int courseCategoryId, int categoryId, String course) {
        Intent starter = new Intent(context, TodoListActivity.class);
        starter.putExtra("categoryId", categoryId);
        starter.putExtra("course", course);
        starter.putExtra("courseCategoryId", courseCategoryId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_list);
        categoryId = getIntent().getIntExtra("categoryId", 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.todo_activity);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initV();
        binding.createNoteFloatingButton.setOnClickListener(this);
    }

    private void initV() {
        todoList = new ArrayList<>();
        binding.todoRecycleriew.setHasFixedSize(true);
    }

    private void getTodoList() {
        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.getTodoList(BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value()),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {

                        TodoListResponse.Data result = new Gson().fromJson(response, TodoListResponse.Data.class);
                        todoList = result.getCards();

                        if (todoList.size() > 0) {
                            binding.btnDone.setVisibility(View.VISIBLE);
                            binding.emptyTodoListTextView.setVisibility(View.GONE);
                            todoListAdapter = new TodoListAdapter(TodoListActivity.this, TodoListActivity.this, todoList);
                            binding.todoRecycleriew.setLayoutManager(new LinearLayoutManager(TodoListActivity.this, LinearLayoutManager.VERTICAL, false));
                            binding.todoRecycleriew.setAdapter(todoListAdapter);
                        } else {
                            binding.emptyTodoListTextView.setVisibility(View.VISIBLE);
                            binding.btnDone.setVisibility(View.GONE);
                        }

                        Utils.hideDialog();
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

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, CreateTodoNoteActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTodoList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onItemCheckBoxClickListn(int position, int listPosition, String noteId, String noteItemId, boolean isCompleted) {
        updateNoteItemStatus(position, listPosition, noteItemId, isCompleted);
    }

    @Override
    public void onItemDeleteClickListn(int position, String id) {
        deleteTodo(position, id);
    }

    private void updateNoteItemStatus(int position, int listPosition, String noteId, boolean isChecked) {

        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.updateNoteItemStatus(BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(),
                noteId,
                isChecked),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        todoList.get(position).getNotes().get(listPosition).setIs_checked(isChecked);
                        todoListAdapter.notifyDataSetChanged();
                        displayMessage(R.string.node_updated_successfully);
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

    private void deleteTodo(final int position, String id) {
        Utils.showDialog(this);
        String authToken = "";
        if (Constants.getUserData(this) != null) {
            authToken = Constants.getUserData(this).getAuthentication_token();
        }

        connectPost(this, null, ApiCallBack.deleteTodo(BaseActivity.getKYC(),
                authToken,
                sp.getBoolean("guest_entry", false),
                Constants.getV6Value(),
                id),
                new ApiConnection.ConnectListener() {
                    @Override
                    public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                        Utils.hideDialog();
                        todoList.remove(position);
                        todoListAdapter.notifyDataSetChanged();
                        displayMessage(R.string.node_deleted_successfully);
                        if (todoList.size() > 0) {
                            binding.btnDone.setVisibility(View.VISIBLE);
                            binding.emptyTodoListTextView.setVisibility(View.GONE);
                        } else {
                            binding.emptyTodoListTextView.setVisibility(View.VISIBLE);
                            binding.btnDone.setVisibility(View.GONE);
                        }
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

}
