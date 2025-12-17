package com.mayur.personalitydevelopment.adapter;

public class YoutubeCategoryAdapter/* extends RecyclerView.Adapter<YoutubeCategoryAdapter.ViewHolder> */{
//
//    private ArrayList<YoutubeItem> items;
//
//    private Context context;
//    private LayoutInflater layoutInflater;
//    private String course;
//    private AdapterListener listener;
//
//    public YoutubeCategoryAdapter(Context context, ArrayList<YoutubeItem> items, String course, AdapterListener listener) {
//        this.context = context;
//        this.layoutInflater = LayoutInflater.from(context);
//        this.items = items;
//        this.course = course;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public YoutubeCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ViewHolder(layoutInflater.inflate(R.layout.item_youtube_listing, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final YoutubeItem course = items.get(position);
//
//        RequestOptions options = new RequestOptions();
//        final RequestOptions placeholder_error = options.error(R.drawable.temo)
//                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);
//
//        Glide.with(context).load(course.getImageUrl()).apply(placeholder_error)
//                .into(holder.ivCourseImage);
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onClick(course);
//            }
//        });
//
//        holder.youtubeTime.setText(course.getTimeDuration() + "m");
//
//        holder.youtubeTitle.setText("Exercise " + (position + 1));
//    }
//
//    @Override
//    public long getItemId(int position) {
//
//        if (items == null)
//            return 0;
//        return position;
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public interface AdapterListener {
//        void onClick(YoutubeItem musicItem);
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView ivCourseImage;
//        private TextView youtubeTitle, youtubeTime;
//
//        public ViewHolder(View view) {
//            super(view);
//            youtubeTitle = view.findViewById(R.id.youtubeTitle);
//            youtubeTime = view.findViewById(R.id.youtubeTime);
//            ivCourseImage = view.findViewById(R.id.ivCourseImage);
//        }
//    }
}