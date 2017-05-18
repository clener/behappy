package whitewire.behappy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Claudio on 26-Feb-17.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {

    private List<Message> messagesList;

    @Override
    public ResultsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messagesList.get(position);
        holder.message.setText(message.getMessage());
        holder.date.setText(message.getDate());

        if (message.getMood() == 2) {
            holder.image.setImageResource(R.drawable.sad_face);
        } else {
            holder.image.setImageResource(R.drawable.happy_face);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message, date;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message_text_view);
            date = (TextView) view.findViewById(R.id.date_text_view);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }


    public ResultsAdapter(List<Message> messagesList) {
        this.messagesList = messagesList;
    }
}
