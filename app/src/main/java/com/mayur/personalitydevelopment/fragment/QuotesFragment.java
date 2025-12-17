package com.mayur.personalitydevelopment.fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.mayur.personalitydevelopment.connection.ApiCallBack.allQuotes;
import static com.mayur.personalitydevelopment.connection.ApiConnection.connectPost;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.adapter.CustomAdapter;
import com.mayur.personalitydevelopment.adapter.NativeAdapter;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.connection.ApiConnection;
import com.mayur.personalitydevelopment.database.ArticleRoomDatabase;
import com.mayur.personalitydevelopment.database.Quote;
import com.mayur.personalitydevelopment.databinding.FragmentQuotesBinding;
import com.mayur.personalitydevelopment.models.Quotes;
import com.mayur.personalitydevelopment.viewholder.QuotesHolder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;

public class QuotesFragment extends Fragment {

    private static final int EXTERNAL_STORAGE_PERMISSION_Constants = 100;
    private static final String TAG = QuotesFragment.class.getSimpleName();
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public Boolean restored_Issubscribed;
    private CustomAdapter customAdapter;
    private FragmentQuotesBinding binding;
    private NativeAdapter nativeAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int totalPage = 0;
    private int current_page = 1;
    private boolean isLoading = false;
    private final List<Quotes.QuotesBean> articlesBeen = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quotes, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("Purchase", MODE_PRIVATE);
        restored_Issubscribed = prefs.getBoolean("Issubscribed", false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sp.edit();

        if (restored_Issubscribed) {
            LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            binding.lvMovies.setLayoutManager(manager);
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(binding.lvMovies);
            setUpAdapter();

            binding.lvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (Utils.isNetworkAvailable(getActivity())) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.lvMovies.getLayoutManager();

                        assert linearLayoutManager != null;
                        int lastvisibleitemposition = linearLayoutManager.findLastVisibleItemPosition();
                        if (lastvisibleitemposition == customAdapter.getItemCount() - 1) {
                            if (!isLoading && current_page <= totalPage) {
                                current_page++;
                                getQuotes();
                            }
                        }
                    } else {
                        totalPage = 0;
                        current_page = 1;
                        isLoading = false;
                        getOfflineQuotes();
                    }

                }
            });
        } else {
            setupViews();
        }

        if (Utils.isNetworkAvailable(getActivity())) {
            Utils.showDialog(getActivity());
            getQuotes();
            setColorData(sp.getBoolean("light", false));
        } else {
            getOfflineQuotes();
        }

        return binding.getRoot();
    }

    private void getOfflineQuotes() {
        if (restored_Issubscribed) {
            binding.progress.setVisibility(View.GONE);
            try {
                ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
                if (db != null) {
                    List<Quote> quotesList = db.quotesDao().getAllQuotes();
                    if (quotesList != null && !quotesList.isEmpty()) {
                        articlesBeen.clear();
                        for (int i = 0; i < quotesList.size(); i++) {
                            Quotes.QuotesBean quoteBean = new Quotes.QuotesBean();
                            Quote quoteDb = new Quote();
                            quoteDb = quotesList.get(i);
                            quoteBean.setTopic("");
                            quoteBean.setId(quoteDb.getId());
                            quoteBean.setImage_url(quoteDb.getImageUrl());
                            quoteBean.setCreated_at(0);
                            quoteBean.setUpdated_at(0);
                            articlesBeen.add(quoteBean);
                        }
                        customAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.hideDialog();
            }
        } else {
            Utils.showToast(getString(R.string.no_internet_connection));
        }
    }

    private void insertOfflineQuotes() {
        if (restored_Issubscribed) {
            ArticleRoomDatabase db = ArticleRoomDatabase.getDatabase(getContext());
            if (db != null) {
                if (!articlesBeen.isEmpty()) {
                    for (int i = 0; i < articlesBeen.size(); i++) {
                        Quotes.QuotesBean quotesBean = articlesBeen.get(i);
                        Quote quote = new Quote();
                        quote.setId(quotesBean.getId());
                        quote.setImageUrl(quotesBean.getImage_url());
                        db.quotesDao().insertQuotes(quote);
                        Log.i(TAG, "insertOfflineQuotes: " + i);
                    }
                    Log.i(TAG, "insertOfflineQuotes: Size " + db.quotesDao().getAllQuotes().size());
                }
            }
        }
    }

    private void setupViews() {

        nativeAdapter = new NativeAdapter(articlesBeen, getActivity());
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        binding.lvMovies.setAdapter(nativeAdapter);
        binding.lvMovies.setItemAnimator(new DefaultItemAnimator());
        binding.lvMovies.setLayoutManager(linearLayoutManager);
        binding.lvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastvisibleitemposition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastvisibleitemposition == nativeAdapter.getItemCount() - 1) {
                    if (!isLoading && current_page <= totalPage) {
                        current_page++;
                        getQuotes();
                    }
                }
            }
        });

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.lvMovies);

    }

    public void onBackPressed() {
        getActivity().finish();
    }

    void setUpAdapter() {

        final QuotesHolder holderInstance = new QuotesHolder();

        final Typeface font = Typeface.createFromAsset(getActivity().getResources().getAssets(), "fonts/MRegular.ttf");
        binding.nodata.setTypeface(font);

        customAdapter = new CustomAdapter(new CustomAdapter.AdapterListener() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                holderInstance.setItemBinding(getActivity(), parent);
                return holderInstance.getHolder();
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

                final QuotesHolder.MyHolder myHolder = holderInstance.castHolder(holder);
                final Quotes.QuotesBean bean = articlesBeen.get(position);

                myHolder.d_btn.setOnClickListener(view -> {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_Constants);
                    } else {
                        Utils.downloadFile(bean.getImage_url(), getActivity());
                    }
                });

                RequestOptions options = new RequestOptions();

                final RequestOptions placeholder_error = options.error(R.drawable.temo)
                        .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(getActivity()).load(bean.getImage_url()).apply(placeholder_error)
                        .into(myHolder.img2);
            }

            @Override
            public int getItemCount() {
                return articlesBeen.size();
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

        });
        binding.lvMovies.setAdapter(customAdapter);
    }

    public void getQuotes() {
        try {

            if (current_page == 1) {
                binding.progress.setVisibility(View.GONE);
            } else {
                binding.progress.setVisibility(View.VISIBLE);
            }

            String authToken = "";
            if (Constants.getUserData(getActivity()) != null) {
                authToken = Constants.getUserData(getActivity()).getAuthentication_token();
            }

            connectPost(getActivity(), null, allQuotes(BaseActivity.getKYC(), authToken, sp.getBoolean("guest_entry", false), Constants.getV6Value(), "" + current_page), new ApiConnection.ConnectListener() {
                @Override
                public void onResponseSuccess(String response, Headers headers, int StatusCode) {
                    Quotes articlesData = new Gson().fromJson(response, Quotes.class);
                    Utils.hideDialog();
                    isLoading = false;
                    binding.progress.setVisibility(View.GONE);
                    totalPage = articlesData.getTotal_pages();
                    articlesBeen.addAll(articlesData.getQuotes());
                    if (!restored_Issubscribed) {
                        nativeAdapter.notifyDataSetChanged();
                    } else {
                        customAdapter.notifyDataSetChanged();
                    }

                    insertOfflineQuotes();
                }

                @Override
                public void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode) {
                    binding.progress.setVisibility(View.GONE);
                    Utils.hideDialog();
                }

                @Override
                public void onFailure(Headers headers) {
                    Utils.hideDialog();
                    binding.progress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionFailure() {
                    binding.progress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "CC Failure", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Headers headers, int StatusCode) {
                    Utils.hideDialog();
                    binding.progress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "EE Failure" + StatusCode, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.hideDialog();
        }
    }

    public void setColorData(boolean light) {
        if (light) {
            binding.nodata.setTextColor(Color.parseColor("#ffffff"));
            binding.lvMovies.setBackgroundColor(Color.parseColor("#363636"));
            binding.rel.setBackgroundColor(Color.parseColor("#363636"));
        } else {
            binding.nodata.setTextColor(Color.parseColor("#000000"));
            binding.lvMovies.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.rel.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        if (!restored_Issubscribed) {
            nativeAdapter.notifyDataSetChanged();
        } else {
            customAdapter.notifyDataSetChanged();
        }
    }

}
