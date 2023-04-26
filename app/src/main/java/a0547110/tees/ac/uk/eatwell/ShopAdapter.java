package a0547110.tees.ac.uk.eatwell;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;


public class ShopAdapter extends RecyclerView.Adapter <ShopAdapter.MyHolder>{
    Context context;
    private List<Shop> list;

    public ShopAdapter(Context context, List<Shop> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.cardview,parent,false);
        MyHolder holder = new MyHolder (view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Shop item = list.get (position);
        holder.Title.setText (item.getName ());
        holder.Address.setText (item.getAddress ());
        holder.Rate.setText (item.getRate().toString());
        holder.image.setImageURL (item.getPhotoUrl ());
        holder.Direction.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+item.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size ();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        RelativeLayout itemLayout;
        MyImageView image;
        TextView Title;
        TextView Address;
        TextView Rate;
        MaterialButton Direction;

        public MyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.reclcler_view);
            image = itemView.findViewById (R.id.card_image);
            Title = itemView.findViewById (R.id.card_title);
            Address = itemView.findViewById (R.id.card_address);
            Rate = itemView.findViewById (R.id.card_rate);
            Direction = itemView.findViewById (R.id.card_direction_button);
        }
    }
}