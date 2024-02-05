package com.dev.ToDo;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.Db.DatabaseManager;
import com.dev.Models.ListItem;

import java.util.ArrayList;

public class ListToDoFragment extends Fragment implements ListItemAdapter.OnItemActionListener, ListItemAdapter.OnCheckBoxChangeListener {
    private static ArrayList<ListItem> itemList;
    private static ListItemAdapter adapter;
    public RecyclerView recyclerView;
    public static EditText editTextTitle;
    public static EditText editTextDescription;
    public static Button btnSave;
    public static ImageButton addButton;

    @Override
    public void onEditClick(int position) {
        System.out.println("EDITAR RECIBE POS: " + position);
        System.out.println("EDITAR ID: " + itemList.get(position).getId());
        openCustomDialog(position);
    }

    @Override
    public void onDeleteClick(int position) {
        System.out.println("ELIMINAR RECIBE POS: " + position);
        System.out.println("ELIMINAR ID: " + itemList.get(position).getId());
        /*DatabaseManager dbManager = new DatabaseManager();
        dbManager.open();*/
        // dbManager.delete(listItem.getId());
        itemList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, itemList.size());
        //dbManager.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_to_do, container, false);
        addButton = (ImageButton) view.findViewById(R.id.btn_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomDialog(-1);
            }
        });
//        itemList = loadItemListFromDatabase();
        ArrayList<ListItem> list = new ArrayList<>();
        ListItem item = new ListItem(1, "TAREA 1", "Realizar tarea 1", 0, "13-10-2023", "12:18:00");
        list.add(0, item);
        ListItem item2 = new ListItem(2, "TAREA 2", "Realizar tarea 2", 0, "13-10-2023", "12:20:00");
        list.add(1, item2);
        itemList = list;
        recyclerView = view.findViewById(R.id.recyclerList);
        adapter = new ListItemAdapter(requireContext(), itemList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void openCustomDialog(int pos) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_create_task);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        btnSave = dialog.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClick(pos);
                dialog.dismiss();
            }
        });
        dialog.show();
        editTextTitle = dialog.findViewById(R.id.editTextTitle);
        editTextDescription = dialog.findViewById(R.id.editTextDescription);
        if (pos >= 0) {
            ListItem item = itemList.get(pos);
            editTextTitle.setText(item.getTitle());
            editTextDescription.setText(item.getDescription());
        }
    }

    private ArrayList<ListItem> loadItemListFromDatabase() {
        ArrayList<ListItem> itemList = new ArrayList<>();
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));
                    int check = cursor.getInt(cursor.getColumnIndex("estado"));
                    String fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                    String hora = cursor.getString(cursor.getColumnIndex("hora"));
                    ListItem listItem = new ListItem(id, title, description, check, fecha, hora);
                    itemList.add(listItem);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        dbManager.close();
        return itemList;
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    public void onCheckBoxChange(int position, int newCheck) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            LinearLayout listItemLayout = viewHolder.itemView.findViewById(R.id.yourLinearLayoutId);
            int colorPrimary = ContextCompat.getColor(requireContext(), R.color.primary);
            int colorWhite = ContextCompat.getColor(requireContext(), R.color.white);
            int alpha = (int) (255 * 0.6);
            int backgroundColor = (newCheck == 1) ? ColorUtils.setAlphaComponent(colorPrimary, alpha) : colorWhite;
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(getResources().getDimension(com.intuit.sdp.R.dimen._10sdp));
            gradientDrawable.setStroke((int) getResources().getDimension(com.intuit.sdp.R.dimen._3sdp), colorPrimary);
            gradientDrawable.setColor(backgroundColor);
            listItemLayout.setBackground(gradientDrawable);
        }
    }

    public static void onSaveClick(int position) {
        System.out.println("SAVE====>" + position);
        ListItem listItem;
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        if (position < 0) {
            System.out.println("ENTRA A GUARDAR DE CERO=======>");
            listItem = new ListItem(3, title, description, 0, "13-10-2023", "12:18:00");
            itemList.add(listItem);
            adapter.notifyDataSetChanged();
        } else {
            System.out.println("ENTRA A GUARDAR UNO CREADO=========>");
            listItem = itemList.get(position);
            listItem.setTitle(title);
            listItem.setDescription(description);
            itemList.set(position, listItem);
            adapter.notifyItemChanged(position);
        }
       /* DatabaseManager dbManager = new DatabaseManager();
        dbManager.open();
        dbManager.close();*/
    }
}
