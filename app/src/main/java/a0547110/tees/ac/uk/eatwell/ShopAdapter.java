package a0547110.tees.ac.uk.eatwell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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

    }

    @Override
    public int getItemCount() {
        return list.size ();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        RelativeLayout itemLayout;
        TextView Title;

        public MyHolder(View itemView) {
            super (itemView);
            itemLayout = itemView.findViewById (R.id.reclcler_view);
            Title = itemView.findViewById (R.id.card_title);


        }
    }
}