package com.example.chop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>{

    private ArrayList<Date_Menu> data = new ArrayList<>();
    ChopChopDB chopDB;
    SQLiteDatabase db;

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView menu_text;
        TextView menu_time;
        TextView menu_sort;
        TextView menu_kcal;

        ItemViewHolder(View itemView) {
            super(itemView);

            chopDB = new ChopChopDB(itemView.getContext());
            db = chopDB.getWritableDatabase();
            menu_time = itemView.findViewById(R.id.menu_time);
            menu_text = itemView.findViewById(R.id.menu_text);
            menu_sort= itemView.findViewById(R.id.menu_sort);
            menu_kcal = itemView.findViewById(R.id.kcal_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    String date = data.get(pos).getDate();
                    Intent i = new Intent(v.getContext(), ResultActivity.class);
                    i.putExtra("date", date);
                    v.getContext().startActivity(i);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    String date = data.get(pos).getDate();
                    String time = data.get(pos).getDate_time();
                    AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                    dlg.setTitle("ChopChop");
                    dlg.setMessage("무엇을 하시겠습니까?");
                    dlg.setIcon(R.mipmap.ic_launcher);
                    dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.execSQL("DELETE FROM DIET WHERE date=='"+date+"' AND time=='"+time+"';");
                            data.remove(pos);
                            notifyItemRemoved(pos);
                            notifyDataSetChanged();
                            Toast.makeText(v.getContext(), "삭제 완료!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(v.getContext(),date+time, Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder inDlg = new AlertDialog.Builder(v.getContext());;
                            View updateView = View.inflate(v.getContext(), R.layout.update_diet_dlg,null);
                            EditText menuEdit = updateView.findViewById(R.id.menuEdit);
                            EditText calEdit = updateView.findViewById(R.id.calEdit);
                            inDlg.setView(updateView);
                            inDlg.setTitle("식단수정");
                            inDlg.setIcon(R.mipmap.ic_launcher);
                            inDlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String menu = menuEdit.getText().toString();
                                    String kcal = calEdit.getText().toString();
                                    if(!menu.equals("")&&!kcal.equals("")) {
                                        db.execSQL("UPDATE DIET SET menu = '" + menu + "', kcal = '" + kcal + "' WHERE date =='" + date + "' AND time =='" + time + "';");
                                        data.get(pos).setKcal(kcal);
                                        data.get(pos).setDate_menu(menu);
                                    }
                                    else if(!menu.equals("")){
                                        db.execSQL("UPDATE DIET SET menu = '"+menu+"'WHERE date =='"+date+"' AND time =='"+time+"';");
                                        data.get(pos).setDate_menu(menu);
                                    }

                                    else if(!kcal.equals("")){
                                        db.execSQL("UPDATE DIET SET kcal = '"+kcal+"' WHERE date =='"+date+"' AND time =='"+time+"';");
                                        data.get(pos).setKcal(kcal);
                                    }
                                    notifyItemChanged(pos);
                                    Toast.makeText(v.getContext(), "수정 완료!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            inDlg.setNegativeButton("취소", null);

                            inDlg.show();
                        }
                    });
                    dlg.show();

                    return false;
                }
            });
        }

        void onBind(Date_Menu item, int position) {
            menu_time.setText(item.getDate_time());
            menu_sort.setText(item.getSort());
            menu_text.setText(item.getDate_menu());
            menu_kcal.setText(item.getKcal());
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(Date_Menu item) {
        data.add(item);
    }

    public void clearItem() {
        data.clear();
    }
}
