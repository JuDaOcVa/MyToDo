package com.dev.ToDo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dev.Models.ListItem;

import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {
    private Context context;
    private List<ListItem> itemList;
    private OnItemActionListener itemActionListener;
    private OnCheckBoxChangeListener checkBoxChangeListener;

    public interface OnItemActionListener {
        void onEditClick(int position);

        void onDeleteClick(int position);
    }

    public interface OnCheckBoxChangeListener {
        void onCheckBoxChange(int position, int newCheck);
    }

    public ListItemAdapter(Context context, List<ListItem> itemList, OnItemActionListener itemActionListener, OnCheckBoxChangeListener checkBoxChangeListener) {
        this.context = context;
        this.itemList = itemList;
        this.itemActionListener = itemActionListener;
        this.checkBoxChangeListener = checkBoxChangeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem listItem = itemList.get(position);
        holder.titleTextView.setText(listItem.getTitle());
        holder.descriptionTextView.setText(listItem.getDescription());
        holder.emojiTextView.setText(listItem.getEmoji());

        // Configura el estado del checkbox
        if (listItem.getCheck() == 1) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemActionListener != null) {
                    itemActionListener.onEditClick(position);
                }
            }
        });

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemActionListener != null) {
                    itemActionListener.onDeleteClick(position);
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int newCheck = isChecked ? 1 : 0;
                if (checkBoxChangeListener != null) {
                    checkBoxChangeListener.onCheckBoxChange(position, newCheck);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView idTextView;
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView emojiTextView;
        public ImageButton editImageButton;
        public ImageButton deleteImageButton;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.textviewId);
            titleTextView = itemView.findViewById(R.id.textviewTitle);
            descriptionTextView = itemView.findViewById(R.id.textviewDescription);
            emojiTextView = itemView.findViewById(R.id.textviewEmoji);
            descriptionTextView.setSelected(true);
            editImageButton = itemView.findViewById(R.id.btn_edit);
            deleteImageButton = itemView.findViewById(R.id.btn_delete);
            checkBox = itemView.findViewById(R.id.round_checkbox);
        }
    }
}
