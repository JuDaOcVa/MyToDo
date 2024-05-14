package com.dev.ToDo;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.Db.DatabaseHelper;
import com.dev.Db.DatabaseManager;
import com.dev.Models.ListItem;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListToDoFragment extends Fragment implements ListItemAdapter.OnItemActionListener, ListItemAdapter.OnCheckBoxChangeListener {
    private static ArrayList<ListItem> itemList;
    private static ListItemAdapter adapter;
    public RecyclerView recyclerView;
    public static EditText editTextTitle;
    public static EditText editTextDescription;
    public static EditText editTextEmoji;
    public static Button btnSave;
    public static ImageButton addButton;

    private void loadItemsFromDatabase() {
        itemList = loadItemListFromDatabase();
        adapter = new ListItemAdapter(requireContext(), itemList, this, this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEditClick(int position) {
        openCustomDialog(position);
    }

    @Override
    public void onDeleteClick(int position) {
        DatabaseManager dbManager = new DatabaseManager(requireContext());
        dbManager.open();
        dbManager.delete(itemList.get(position).getId());
        itemList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, itemList.size());
        dbManager.close();
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
        recyclerView = view.findViewById(R.id.recyclerList);
        loadItemsFromDatabase();
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
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
        editTextEmoji = dialog.findViewById(R.id.editTextEmoji);
        editTextEmoji.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isValidEmojiInput(s.toString())) {
                    editTextEmoji.setError("Only emojis allowed");
                    return;
                } else {
                    editTextEmoji.setError(null);
                }
            }
        });
        if (pos >= 0) {
            ListItem item = itemList.get(pos);
            editTextTitle.setText(item.getTitle());
            editTextDescription.setText(item.getDescription());
            editTextEmoji.setText(item.getEmoji());
        }
    }

    private static boolean isValidEmojiInput(String input) {
        Pattern emojiPattern = Pattern.compile("[\\p{So}\\p{Cn}]");
        String[] symbols = input.split("");
        for (String symbol : symbols) {
            if (!emojiPattern.matcher(symbol).matches()) {
                return false;
            }
        }
        return true;
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private ArrayList<ListItem> loadItemListFromDatabase() {
        ArrayList<ListItem> itemList = new ArrayList<>();
        DatabaseManager dbManager = new DatabaseManager(this.getContext());
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                    int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
                    int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION);
                    int emojiIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMOJI);
                    int checkIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_CHECK);
                    int fechaIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_FECHA);
                    int horaIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HORA);

                    if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 && emojiIndex != -1 && fechaIndex != -1 && horaIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        String title = cursor.getString(titleIndex);
                        String description = cursor.getString(descriptionIndex);
                        String emoji = cursor.getString(emojiIndex);
                        int check = cursor.getInt(checkIndex); // Aquí se obtiene el valor de check de la base de datos
                        String fecha = cursor.getString(fechaIndex);
                        String hora = cursor.getString(horaIndex);
                        ListItem listItem = new ListItem(id, title, description, emoji, check, fecha, hora); // Aquí se pasa el valor de check al constructor
                        itemList.add(listItem);
                        if (check == 1) {
                            applyCheckState(itemList.size() - 1, check);
                        }
                    }
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

    public void applyCheckState(int position, int newCheck) {
        if (recyclerView != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                LinearLayout listItemLayout = viewHolder.itemView.findViewById(R.id.yourLinearLayoutId);
                int colorPrimary = ContextCompat.getColor(requireContext(), R.color.primary);
                int colorPictonBlue = ContextCompat.getColor(requireContext(), R.color.picton_blue);
                int colorBlack = ContextCompat.getColor(requireContext(), R.color.black);
                int colorWhite = ContextCompat.getColor(requireContext(), R.color.white);
                int alpha = (int) (255 * 0.6);
                int backgroundColor = (newCheck == 1) ? ColorUtils.setAlphaComponent(colorPrimary, alpha) : colorWhite;
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius(getResources().getDimension(com.intuit.sdp.R.dimen._10sdp));
                gradientDrawable.setStroke((int) getResources().getDimension(com.intuit.sdp.R.dimen._3sdp), colorPrimary);
                gradientDrawable.setColor(backgroundColor);
                listItemLayout.setBackground(gradientDrawable);
                TextView textviewTitle = viewHolder.itemView.findViewById(R.id.textviewTitle);
                TextView textviewDescription = viewHolder.itemView.findViewById(R.id.textviewDescription);
                textviewTitle.setTextColor((newCheck == 1) ? colorWhite : colorPictonBlue);
                textviewDescription.setTextColor((newCheck == 1) ? colorWhite : colorBlack);
            }
        }
    }

    public void onCheckBoxChange(int position, int newCheck) {
        applyCheckState(position, newCheck);
        ListItem listItem = itemList.get(position);
        listItem.setCheck(newCheck);
        DatabaseManager dbManager = new DatabaseManager(requireContext());
        dbManager.open();
        dbManager.update(listItem);
        dbManager.close();
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void onSaveClick(int position) {
        ListItem listItem;
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String emoji = editTextEmoji.getText().toString();
        if(!emoji.isEmpty()) {
            if (!isValidEmojiInput(emoji)) {
                editTextEmoji.setError("Only emojis allowed");
            }
        }
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(btnSave.getContext(), "Title and description can't be empty", Toast.LENGTH_LONG).show();
        } else {
            DatabaseManager dbManager = new DatabaseManager(btnSave.getContext());
            dbManager.open();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String fecha = dateFormat.format(calendar.getTime());
            String hora = timeFormat.format(calendar.getTime());
            if (position < 0) {
                listItem = new ListItem(0, title, description, emoji, 0, fecha, hora);
                dbManager.insert(listItem);
                itemList.add(listItem);
                adapter.notifyDataSetChanged();
            } else {
                listItem = itemList.get(position);
                listItem.setTitle(title);
                listItem.setDescription(description);
                listItem.setEmoji(emoji);
                dbManager.update(listItem);
                itemList.set(position, listItem);
                adapter.notifyItemChanged(position);
            }
            dbManager.close();
        }
    }
}

