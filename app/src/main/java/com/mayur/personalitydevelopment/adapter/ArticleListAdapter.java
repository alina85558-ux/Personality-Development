package com.mayur.personalitydevelopment.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.activity.FavouriteActivity;
import com.mayur.personalitydevelopment.activity.FilterResultActivity;
import com.mayur.personalitydevelopment.activity.LikesActivity;
import com.mayur.personalitydevelopment.activity.SearchActivity;
import com.mayur.personalitydevelopment.fragment.Tab1;
import com.mayur.personalitydevelopment.models.AdViewHolder;
import com.mayur.personalitydevelopment.models.Articles;
import com.mayur.personalitydevelopment.viewholder.AdvBannerHolder;
import com.mayur.personalitydevelopment.viewholder.GetToKnowHolder;

import java.util.ArrayList;

public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW_TYPE = 0;

    // The banner ad view type.
    private static final int BANNER_AD_VIEW_TYPE = 1;
    private GetToKnowHolder holderInstance = new GetToKnowHolder();
    private final AdvBannerHolder advBannerHolder = new AdvBannerHolder();
    private ArrayList<Object> articlesList = new ArrayList<>();
    private Activity context;
    private SharedPreferences sp,prefs;
    public SharedPreferences.Editor editor;
    private int selectedArticleID =0;
    private boolean isRefresh;
    private Articles currentSelectedArticle;
    private Tab1 tab1;
    private int isFromMainList = 1;

    public ArticleListAdapter(ArrayList<Object> articlesList, Activity context,Tab1 tab1,int isFromMainList) {
        this.articlesList = articlesList;
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.tab1 = tab1;
        this.isFromMainList = isFromMainList;
    }

    public void setAd(ArrayList<NativeAd> nativeAd) {
        this.articlesList.addAll(nativeAd);
        notifyDataSetChanged();
    }

    public void setObject (ArrayList<Object> object) {
        this.articlesList.addAll(object);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BANNER_AD_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ad_card_view,parent,false);
            return new AdViewHolder(view);
        }
        holderInstance.setItemBinding(context, parent);
        return holderInstance.getHolder();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        if (articlesList.get(position) instanceof Articles) {
            return ITEM_VIEW_TYPE;
        } else {
            return BANNER_AD_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType == BANNER_AD_VIEW_TYPE) {
            AdViewHolder adv =  (AdViewHolder) holder;
            adv.setNativeAd((NativeAd) articlesList.get(position));
            Log.i("TAG", "onBindViewHolder: BANNER_AD_VIEW_TYPE " + position);
        } else {
            final GetToKnowHolder.MyHolder myHolder = holderInstance.castHolder(holder);
            final Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/MRegular.ttf");
            final Articles articlesBean = (Articles) articlesList.get(position);
            Log.i("TAG", "onBindViewHolder: " + position);

            myHolder.tv2.setText(articlesBean.getTopic());
            myHolder.tv2.setTypeface(font);
            myHolder.tvTime.setReferenceTime(articlesBean.getCreated_at());
            myHolder.tvLikes.setText(Utils.convertNumberToCount(/*1099+*/articlesBean.getTotal_likes()) + " Likes");

            if (articlesBean.isArticle_is_locked()) {
                if (articlesBean.isUser_article_is_locked()) {
                    myHolder.img_lock_article.setBackground(context.getResources().getDrawable(R.drawable.lock));
                } else {
                    myHolder.img_lock_article.setBackground(context.getResources().getDrawable(R.drawable.unlock));
                }
            } else {
                myHolder.img_lock_article.setBackground(null);
            }

            RequestOptions options = new RequestOptions();

            final RequestOptions placeholder_error = options.error(R.drawable.temo).placeholder(R.drawable.temo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(context)
                    .load(articlesBean.getPhoto())
                    .apply(placeholder_error).into(myHolder.img2);

            myHolder.itemView.setOnClickListener(view -> {
                currentSelectedArticle = articlesBean;
                if (articlesBean.isArticle_is_locked()) {
                    if (articlesBean.isArticle_is_locked()) {
                        if (articlesBean.isUser_article_is_locked()) {
                            if (isFromMainList == 1) {
                                if (!sp.getBoolean("guest_entry", false)) {
                                    if (tab1.restored_Issubscribed) {
                                        tab1.updateWatchedVideoStatus(articlesBean);
                                    } else {
                                        tab1.callArticleIntent(articlesBean);
                                    }
                                } else {
                                    tab1.callArticleIntent(articlesBean);
                                }
                            } else if (isFromMainList == 2) {
                                if (!sp.getBoolean("guest_entry", false)) {
                                    if (((FilterResultActivity) context).restored_Issubscribed) {
                                        ((FilterResultActivity) context).updateWatchedVideoStatus(articlesBean);
                                    } else {
                                        ((FilterResultActivity) (context)).callArticleIntent(articlesBean);
                                    }
                                } else {
                                    ((FilterResultActivity) (context)).callArticleIntent(articlesBean);
                                }

                            } else if (isFromMainList == 3) {
                                if (((LikesActivity) context).restored_Issubscribed) {
                                    ((LikesActivity) context).updateWatchedVideoStatus(articlesBean.getId());
                                } else {
                                    ((LikesActivity) (context)).callArticleIntent(articlesBean);
                                }
                            } else if (isFromMainList == 4) {
                                if (((FavouriteActivity) context).restored_Issubscribed) {
                                    ((FavouriteActivity) context).updateWatchedVideoStatus(articlesBean.getId());
                                } else {
                                    ((FavouriteActivity) (context)).callArticleIntent(articlesBean);
                                }
                            } else if (isFromMainList == 5) {
                                if (!sp.getBoolean("guest_entry", false)) {
                                    if (((SearchActivity) context).restored_Issubscribed) {
                                        ((SearchActivity) context).updateWatchedVideoStatus(articlesBean.getId());
                                        ((SearchActivity) (context)).callArticleIntent(articlesBean);
                                    } else {
                                        ((SearchActivity) (context)).callArticleIntent(articlesBean);
                                    }
                                } else {
                                    ((SearchActivity) (context)).callArticleIntent(articlesBean);
                                }
                            }
                        } else {
                            if (isFromMainList == 1) {
                                tab1.callArticleIntent(articlesBean);
                            } else if (isFromMainList == 2) {
                                ((FilterResultActivity) context).callArticleIntent(articlesBean);
                            } else if (isFromMainList == 3) {
                                ((LikesActivity) context).callArticleIntent(articlesBean);
                            } else if (isFromMainList == 4) {
                                ((FavouriteActivity) context).callArticleIntent(articlesBean);
                            } else if (isFromMainList == 5) {
                                ((SearchActivity) context).callArticleIntent(articlesBean);
                            }
                        }
                    } else {
                        if (isFromMainList == 1) {
                            tab1.callArticleIntent(articlesBean);
                        } else if (isFromMainList == 2) {
                            ((FilterResultActivity) context).callArticleIntent(articlesBean);
                        } else if (isFromMainList == 3) {
                            ((LikesActivity) context).callArticleIntent(articlesBean);
                        } else if (isFromMainList == 4) {
                            ((FavouriteActivity) context).callArticleIntent(articlesBean);
                        } else if (isFromMainList == 5) {
                            ((SearchActivity) context).callArticleIntent(articlesBean);
                        }
                    }
                } else {
                    if (isFromMainList == 1) {
                        tab1.callArticleIntent(articlesBean);
                    } else if (isFromMainList == 2) {
                        ((FilterResultActivity) context).callArticleIntent(articlesBean);
                    } else if (isFromMainList == 3) {
                        ((LikesActivity) context).callArticleIntent(articlesBean);
                    } else if (isFromMainList == 4) {
                        ((FavouriteActivity) context).callArticleIntent(articlesBean);
                    } else if (isFromMainList == 5) {
                        ((SearchActivity) context).callArticleIntent(articlesBean);
                    }
                }
            });

            if (sp.getBoolean("light", false)) {
                myHolder.cardView.setCardBackgroundColor(Color.parseColor("#464646"));
                myHolder.tv2.setTextColor(Color.parseColor("#ffffff"));
                myHolder.tvTime.setTextColor(Color.parseColor("#ffffff"));
                myHolder.tvLikes.setTextColor(Color.parseColor("#ffffff"));
            } else {
                myHolder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
                myHolder.tv2.setTextColor(Color.parseColor("#000000"));
                myHolder.tvTime.setTextColor(Color.parseColor("#000000"));
                myHolder.tvLikes.setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return articlesList != null ? articlesList.size() : 0;
    }


}