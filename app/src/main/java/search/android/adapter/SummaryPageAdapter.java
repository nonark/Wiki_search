package search.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import search.android.application.SearchApplication;
import search.android.vo.SummaryPage;
import search.android.wiki_search.R;

/**
 * Created by nhnent on 2017. 5. 2..
 */

public class SummaryPageAdapter extends RecyclerView.Adapter<SummaryPageAdapter.ViewHolder> {

    private SummaryPage headerPage;
    private List<SummaryPage> relatedPages;
    private int headerLayout;
    private int itemLayout;

    private OnRecyclerViewItemClickedListener relatedItemClickedListner;
    private OnRecyclerViewItemClickedListener headerItemClickedLListner;

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_RELATED_PAGE = 1;

    //Getter and Setter

    public List<SummaryPage> getRelatedPages() {
        return relatedPages;
    }
    public void setRelatedPages(List<SummaryPage> relatedPages) {
        this.relatedPages = relatedPages;
    }

    public SummaryPage getHeaderPage() {
        return headerPage;
    }
    public void setHeaderPage(SummaryPage headerPage) {
        this.headerPage = headerPage;
    }

    public int getHeaderLayout() { return headerLayout; }
    public void setHeaderLayout(int headerLayout) { this.headerLayout = headerLayout; }

    public int getItemLayout() {
        return itemLayout;
    }
    public void setItemLayout(int itemLayout) {
        this.itemLayout = itemLayout;
    }

    public OnRecyclerViewItemClickedListener getRelatedItemClickedListner() { return relatedItemClickedListner; }
    public void setRelatedListner(OnRecyclerViewItemClickedListener relatedItemClickedListner) { this.relatedItemClickedListner = relatedItemClickedListner; }

    public OnRecyclerViewItemClickedListener getHeaderItemClickedLListner() { return headerItemClickedLListner; }
    public void setHeaderItemClickedLListner(OnRecyclerViewItemClickedListener headerItemClickedLListner) { this.headerItemClickedLListner = headerItemClickedLListner; }

    public SummaryPageAdapter(List<SummaryPage> relatedPages, int itemLayout) {
        this.relatedPages = relatedPages;
        this.itemLayout = itemLayout;
    }

    public SummaryPageAdapter(SummaryPage headerPage, List<SummaryPage> relatedPages, int headerLayout, int itemLayout) {
        this.headerPage = headerPage;
        this.relatedPages = relatedPages;
        this.headerLayout = headerLayout;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if( viewType == VIEW_TYPE_HEADER ) {
            View view = LayoutInflater.from(parent.getContext()).inflate(headerLayout, parent, false);
            return new ViewHolder(view, headerItemClickedLListner);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view, relatedItemClickedListner);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        SummaryPage wikiPage = null;

        if(headerPage != null) {

            if(position == 0) {
                wikiPage = headerPage;
            } else {
                wikiPage = relatedPages.get(position - 1);
            }

        } else {
            wikiPage = relatedPages.get(position);
        }

        SearchApplication.displayImage(wikiPage.getThumbnailUrl(), holder.thumbnail);
        holder.title.setText(wikiPage.getTitle());
        holder.extract.setText(wikiPage.getExtract());

    }

    @Override
    public int getItemCount() {
        return relatedPages.size() + (headerPage == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0 && headerPage != null) {
            return VIEW_TYPE_HEADER;
        }

        return VIEW_TYPE_RELATED_PAGE;
    }

    /*
         * ViewHolder
         */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView thumbnail;
        public TextView title;
        public TextView extract;
        public OnRecyclerViewItemClickedListener listner;
        public LinearLayout item;

        public ViewHolder(View itemView) {
            this(itemView, null);
        }

        public ViewHolder(View itemView, OnRecyclerViewItemClickedListener listner) {
            super(itemView);

            item = (LinearLayout) itemView.findViewById(R.id.item);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            extract = (TextView) itemView.findViewById(R.id.extract);
            this.listner = listner;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listner != null) {
                listner.onItemClicked(title.getText().toString());
            }
        }
    }

    public interface OnRecyclerViewItemClickedListener {
        void onItemClicked(String searchText);
    }
}
