package com.denspark.strelets.cinematrix.adapters;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.database.entity.Nameble;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CheckableSpinnerAdapter<T extends Nameble> extends BaseAdapter {

    public static class SpinnerItem<T> {
        private String txt;
        private T item;

        public SpinnerItem() {
        }

        public SpinnerItem(T t, String s) {
            item = t;
            txt = s;
        }

        public String getTxt() {
            return txt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SpinnerItem)) return false;
            SpinnerItem<?> that = (SpinnerItem<?>) o;
            return Objects.equals(txt, that.txt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(txt);
        }
    }

    private Context context;
    private List<T> selected_items;
    private List<SpinnerItem<T>> all_items;
    private String headerText;
    private String allItemsTitle;
    private String selectedItems;

    public CheckableSpinnerAdapter(Context context,
                                   String headerText,
                                   String allItemsTitle,
                                   List<SpinnerItem<T>> all_items,
                                   List<T> selected_items) {
        this.context = context;
        this.headerText = headerText;
        this.allItemsTitle = allItemsTitle;
        this.all_items = all_items;
        this.selected_items = selected_items;
    }

    @Override
    public int getCount() {
        return all_items.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position < 1) {
            return null;
        } else {
            return all_items.get(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final SimpleViewHolder simpleViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.simple_spinner_item, parent, false);

            simpleViewHolder = new SimpleViewHolder();
            simpleViewHolder.mTextView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(simpleViewHolder);
        } else {
            simpleViewHolder = (SimpleViewHolder) convertView.getTag();
        }
        if (position < 1) {
            simpleViewHolder.mTextView.setText(headerText);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final DropDownViewHolder dropDownViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.checkable_spinner_item, parent, false);

            dropDownViewHolder = new DropDownViewHolder();
            dropDownViewHolder.mTextView = convertView.findViewById(R.id.text);
            dropDownViewHolder.mCheckBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(dropDownViewHolder);
        } else {
            dropDownViewHolder = (DropDownViewHolder) convertView.getTag();
        }

        if (position < 1) {
            dropDownViewHolder.mCheckBox.setVisibility(View.GONE);
            dropDownViewHolder.mTextView.setText(allItemsTitle);
        } else {
            final int listPos = position - 1;
            dropDownViewHolder.mCheckBox.setVisibility(View.VISIBLE);
            dropDownViewHolder.mTextView.setText(all_items.get(listPos).txt);

            final T item = all_items.get(listPos).item;
            boolean isSel = selected_items.contains(item);

            dropDownViewHolder.mCheckBox.setOnCheckedChangeListener(null);
            dropDownViewHolder.mCheckBox.setChecked(isSel);

            dropDownViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selected_items.add(item);
                    } else {
                        selected_items.remove(item);
                    }
                }
            });


        }
        dropDownViewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (position < 1){
                    headerText = allItemsTitle;
                    selected_items.clear();
                }
                else {
                    dropDownViewHolder.mCheckBox.toggle();

                    List<String> selectedItemsHeadersList = selected_items.stream()
                            .map(Nameble::getName
                            ).collect(Collectors.toList());

                    selectedItems = TextUtils.join(", ", selectedItemsHeadersList);
                    if (selected_items != null  ) {
                        if (!selected_items.isEmpty()){
                            headerText = selectedItems;
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class SimpleViewHolder {
        private TextView mTextView;
    }

    private class DropDownViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
