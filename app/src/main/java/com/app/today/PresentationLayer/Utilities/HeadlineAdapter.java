package com.app.today.PresentationLayer.Utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.today.BusinessLayer.Headline;
import com.app.today.R;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HeadlineAdapter extends RecyclerView.Adapter<HeadlineAdapter.HeadlineHolder> {
    private Context context;
    private ArrayList<Headline> headlines;

    public HeadlineAdapter(Context context, ArrayList<Headline> headlines) {
        this.context = context;
        this.headlines = headlines;
    }

    @NonNull
    @Override
    public HeadlineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.headline_pager, parent, false);
        return new HeadlineHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadlineAdapter.HeadlineHolder holder, final int position) {
        holder.headline.setText(headlines.get(position).getTitle());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(headlines.get(position).getBmp());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            holder.thumbnail.setImageBitmap(bmp);
        } catch (NullPointerException | MalformedURLException e) {
            Log.e("? headlines HashMap equals null", e.toString());
        } catch (IOException e) {
            Log.e("? couldn't parse URL stream", ".openConnection() failed", e);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click", "!!!");
                String url = headlines.get(position).getUrl();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return headlines.size();
    }

    static class HeadlineHolder extends RecyclerView.ViewHolder {
        TextView headline;
        ImageView thumbnail;
        CardView view;

        HeadlineHolder(@NonNull View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.headlineText);
            thumbnail = itemView.findViewById(R.id.headlineThumb);
            view = itemView.findViewById(R.id.headlineCard);
        }
    }
}