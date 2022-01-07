package bkacad.com.bkacadplayer2.adapters;

import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import bkacad.com.bkacadplayer2.R;
import bkacad.com.bkacadplayer2.domains.Song;

public class SongAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    android.content.Context context;
    android.view.LayoutInflater layoutInflater;
    java.util.ArrayList<Song> songs = new ArrayList<>();
    public static java.lang.String TAG = "SongAdapter";
    public MyItemClickListener myItemClickListener;

    public SongAdapter(android.content.Context context, java.util.ArrayList<Song> songs) {
        this.context = context;
        this.layoutInflater = (android.view.LayoutInflater) context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        this.songs = songs;
    }


    @java.lang.Override
    public void onBindViewHolder(@androidx.annotation.NonNull SongViewHolder holder,
                                 int position) {
        Song song = songs.get(position);
        holder.tvName.setText(song.getName());
        if (song.isActive()) {
            holder.itemView.setBackground(context.getDrawable(R.drawable.border_active_item_song));
        } else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.border_item_song));
        }
    }

    @androidx.annotation.NonNull
    @java.lang.Override
    public SongViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
        android.view.View v = layoutInflater.inflate(R.layout.item_song, parent, false);
        SongViewHolder myViewHolder = new SongViewHolder(v);
        return myViewHolder;
    }

    @java.lang.Override
    public int getItemCount() {
        return this.songs.size();
    }

    public class SongViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        android.widget.TextView tvName;
        LinearLayout itemView;

        public SongViewHolder(@androidx.annotation.NonNull android.view.View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.itemView = (LinearLayout) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.setOnItemClick(songs.get(getPosition()), getPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public interface MyItemClickListener {
        void setOnItemClick(Song song, int activeSongIndex);
    }
}