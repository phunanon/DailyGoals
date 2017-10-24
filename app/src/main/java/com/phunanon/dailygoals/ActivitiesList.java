package com.phunanon.dailygoals;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.solver.SolverVariable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//TODO URGENT add tdb object support, and use
//TODO URGENT change rendering to XML inflation

public class ActivitiesList extends AppCompatActivity {

    protected void moveToTop (int int_index)
    {
        TinyDB tdb = new TinyDB(getApplicationContext());
        ArrayList<String> lts_activities_name = new ArrayList<>(tdb.getListString("activities_name"));
        ArrayList<Integer> lti_activities_daily = new ArrayList<>(tdb.getListInt("activities_daily"));
        ArrayList<Integer> lti_activities_done = new ArrayList<>(tdb.getListInt("activities_done"));
        ArrayList<Long> ltl_activities_date = new ArrayList<>(tdb.getListLong("activities_date"));
        ArrayList<Long> ltl_activities_date_last = new ArrayList<>(tdb.getListLong("activities_date_last"));
        ArrayList<Integer> lti_activities_done_last = new ArrayList<>(tdb.getListInt("activities_done_last"));
        ArrayList<Integer> lti_activities_record = new ArrayList<>(tdb.getListInt("activities_record"));
        lts_activities_name.add(0, lts_activities_name.remove(int_index));
        lti_activities_daily.add(0, lti_activities_daily.remove(int_index));
        lti_activities_done.add(0, lti_activities_done.remove(int_index));
        ltl_activities_date.add(0, ltl_activities_date.remove(int_index));
        ltl_activities_date_last.add(0, ltl_activities_date_last.remove(int_index));
        lti_activities_done_last.add(0, lti_activities_done_last.remove(int_index));
        lti_activities_record.add(0, lti_activities_record.remove(int_index));
        tdb.putListString("activities_name", lts_activities_name);
        tdb.putListInt("activities_daily", lti_activities_daily);
        tdb.putListInt("activities_done", lti_activities_done);
        tdb.putListLong("activities_date", ltl_activities_date);
        tdb.putListLong("activities_date_last", ltl_activities_date_last);
        tdb.putListInt("activities_done_last", lti_activities_done_last);
        tdb.putListInt("activities_record", lti_activities_record);

        ((ScrollView) findViewById(R.id.scv_scoller)).scrollTo(0, 0);
    }

    protected void editDone (int int_index, int int_change, boolean b_relative)
    {
        TinyDB tdb = new TinyDB(getApplicationContext());
        ArrayList<Integer> lti_activities_done = new ArrayList<>(tdb.getListInt("activities_done"));
        int int_prev_done = lti_activities_done.get(int_index);
        lti_activities_done.set(int_index, (b_relative ? lti_activities_done.get(int_index) : 0) + int_change);
        tdb.putListInt("activities_done", lti_activities_done);

        final ArrayList<Long> ltl_activities_date_last = new ArrayList<>(tdb.getListLong("activities_date_last"));
        final ArrayList<Integer> lti_activities_done_last = new ArrayList<>(tdb.getListInt("activities_done_last"));
        if (b_relative) {
            if (DateUtils.isToday(ltl_activities_date_last.get(int_index))) {
                lti_activities_done_last.set(int_index, lti_activities_done_last.get(int_index) + int_change);
            }
        } else {
            ltl_activities_date_last.set(int_index, new Date().getTime());
            lti_activities_done_last.set(int_index, 0);
        }
        tdb.putListLong("activities_date_last", ltl_activities_date_last);
        tdb.putListInt("activities_done_last", lti_activities_done_last);

        moveToTop(int_index);

        listActivities();
    }

    protected void forgetActivity (int int_index)
    {
        TinyDB tdb = new TinyDB(getApplicationContext());
        ArrayList<String> lts_activities_name = new ArrayList<>(tdb.getListString("activities_name"));
        ArrayList<Integer> lti_activities_daily = new ArrayList<>(tdb.getListInt("activities_daily"));
        ArrayList<Integer> lti_activities_done = new ArrayList<>(tdb.getListInt("activities_done"));
        ArrayList<Long> ltl_activities_date = new ArrayList<>(tdb.getListLong("activities_date"));
        ArrayList<Long> ltl_activities_date_last = new ArrayList<>(tdb.getListLong("activities_date_last"));
        ArrayList<Integer> lti_activities_done_last = new ArrayList<>(tdb.getListInt("activities_done_last"));
        ArrayList<Integer> lti_activities_record = new ArrayList<>(tdb.getListInt("activities_record"));
        lts_activities_name.remove(int_index);
        lti_activities_daily.remove(int_index);
        lti_activities_done.remove(int_index);
        ltl_activities_date.remove(int_index);
        ltl_activities_date_last.remove(int_index);
        lti_activities_done_last.remove(int_index);
        lti_activities_record.remove(int_index);
        tdb.putListString("activities_name", lts_activities_name);
        tdb.putListInt("activities_daily", lti_activities_daily);
        tdb.putListInt("activities_done", lti_activities_done);
        tdb.putListLong("activities_date", ltl_activities_date);
        tdb.putListLong("activities_date_last", ltl_activities_date_last);
        tdb.putListInt("activities_done_last", lti_activities_done_last);
        tdb.putListInt("activities_record", lti_activities_record);

        listActivities();
    }

    protected void listActivities ()
    {
        TableLayout tbl_activities = (TableLayout) findViewById(R.id.tbl_activities);
      //Clear previous list
        tbl_activities.removeAllViews();
      //Retrieve saved activities
        TinyDB tdb = new TinyDB(getApplicationContext());
        final ArrayList<String> lts_activities_name = new ArrayList<>(tdb.getListString("activities_name"));
        final ArrayList<Integer> lti_activities_daily = new ArrayList<>(tdb.getListInt("activities_daily"));
        final ArrayList<Integer> lti_activities_done = new ArrayList<>(tdb.getListInt("activities_done"));
        final ArrayList<Long> ltl_activities_date = new ArrayList<>(tdb.getListLong("activities_date"));
        final ArrayList<Long> ltl_activities_date_last = new ArrayList<>(tdb.getListLong("activities_date_last"));
        final ArrayList<Integer> lti_activities_done_last = new ArrayList<>(tdb.getListInt("activities_done_last"));
        final ArrayList<Integer> lti_activities_record = new ArrayList<>(tdb.getListInt("activities_record"));

        for (int s = 0, slen = lts_activities_name.size(); s < slen; ++s)
        {

            if (!DateUtils.isToday(ltl_activities_date_last.get(s))) {
                if (lti_activities_done_last.get(s) > lti_activities_record.get(s)) {
                    lti_activities_record.set(s, lti_activities_done_last.get(s));
                    tdb.putListInt("activities_record", lti_activities_record);
                }
                ltl_activities_date_last.set(s, (new Date()).getTime());
                lti_activities_done_last.set(s, 0);
                tdb.putListLong("activities_date_last", ltl_activities_date_last);
                tdb.putListInt("activities_done_last", lti_activities_done_last);
            }

            int int_goal = (int)Math.round((double)lti_activities_daily.get(s) * ((double)((new Date()).getTime() - ltl_activities_date.get(s)) / 1000/60/60/24));

            DisplayMetrics dm_metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm_metrics);
            float sp = dm_metrics.scaledDensity;
            int sp_128 = (int)(128*sp), sp_64 = (int)(64*sp), sp_48 = (int)(48*sp), sp_32 = (int)(32*sp), sp_16 = (int)(16*sp), sp_8 = (int)(8*sp), sp_4 = (int)(4*sp);

            final TableRow tlr_activity = new TableRow(getApplicationContext());
            LinearLayout ll_activity_vertical = new LinearLayout(getApplicationContext());
            LinearLayout ll_activity_details = new LinearLayout(getApplicationContext());
            LinearLayout ll_activity_datas = new LinearLayout(getApplicationContext());
            LinearLayout ll_activity_of = new LinearLayout(getApplicationContext());
            LinearLayout ll_activity_progress = new LinearLayout(getApplicationContext());
            LinearLayout ll_activity_views = new LinearLayout(getApplicationContext());
            LinearLayout ll_activity_actions = new LinearLayout(getApplicationContext());
            ll_activity_vertical.setOrientation(LinearLayout.VERTICAL);
            ll_activity_details.setOrientation(LinearLayout.VERTICAL);
            ll_activity_datas.setOrientation(LinearLayout.VERTICAL);
            ll_activity_of.setOrientation(LinearLayout.HORIZONTAL);
            ll_activity_progress.setOrientation(LinearLayout.VERTICAL);
            ll_activity_views.setOrientation(LinearLayout.HORIZONTAL);
            ll_activity_actions.setOrientation(LinearLayout.HORIZONTAL);
            ll_activity_datas.addView(ll_activity_of);
            ll_activity_datas.addView(ll_activity_progress);
            ll_activity_views.addView(ll_activity_details);
            ll_activity_views.addView(ll_activity_datas);
            ll_activity_vertical.addView(ll_activity_views);
            ll_activity_vertical.addView(ll_activity_actions);
            tlr_activity.addView(ll_activity_vertical);

            Integer int_daily = lti_activities_daily.get(s);
            Integer int_done = lti_activities_done.get(s);
            Long lng_date = ltl_activities_date.get(s);
            Integer int_progress = int_done - int_goal;
            Integer int_done_today = (DateUtils.isToday(ltl_activities_date_last.get(s)) ? lti_activities_done_last.get(s) : 0);

            tlr_activity.setPadding(16, 16, 16, 16);
            final TextView tv_activity_name = new TextView(getApplicationContext());
            final TextView tv_activity_daily = new TextView(getApplicationContext());
            final TextView tv_activity_since = new TextView(getApplicationContext());
            final TextView tv_activity_done = new TextView(getApplicationContext());
            final TextView tv_activity_of = new TextView(getApplicationContext());
            final TextView tv_activity_goal = new TextView(getApplicationContext());
            final TextView tv_activity_progress = new TextView(getApplicationContext());
            final TextView tv_activity_today = new TextView(getApplicationContext());
            final Button btn_remove_one = new Button(getApplicationContext());
            final Button btn_remove_half = new Button(getApplicationContext());
            final Button btn_add_half = new Button(getApplicationContext());
            final Button btn_add_one = new Button(getApplicationContext());
            final Button btn_set = new Button(getApplicationContext());
            tv_activity_name.setText(lts_activities_name.get(s));
            SimpleDateFormat sdf_format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            tv_activity_daily.setText(int_daily.toString() +" PER DAY");
            tv_activity_since.setText("since "+ sdf_format.format(new Date(lng_date)));
            tv_activity_done.setText(int_done.toString());
            tv_activity_of.setText("of");
            tv_activity_goal.setText(String.valueOf(int_goal));
            tv_activity_progress.setText(String.valueOf(Math.abs(int_progress)) + (int_progress > 0 ? " over!" : (int_progress == 0 ? "" : " to go!" )));
            tv_activity_today.setText(String.valueOf(int_done_today) +" today ✔  (★ "+ String.valueOf(lti_activities_record.get(s)) +")");
            String str_half = String.valueOf(lti_activities_daily.get(s)/2);
            btn_remove_one.setText("-1");
            btn_remove_half.setText("-"+ str_half);
            btn_add_half.setText("+"+ str_half);
            btn_add_one.setText("+1");
            btn_set.setText("...");
            tv_activity_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            tv_activity_daily.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv_activity_done.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tv_activity_of.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tv_activity_goal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tv_activity_progress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tv_activity_today.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tv_activity_daily.setPadding(sp_16, sp_8, sp_8, sp_4);
            tv_activity_since.setPadding(sp_16, 0, 0, sp_8);
            tv_activity_of.setPadding(sp_8, 0, sp_8, 0);
            tv_activity_progress.setPadding(0, 0, sp_8, 0);
            ll_activity_datas.setPadding(sp_16, 0, 0, 0);
            tv_activity_name.setTextColor(0xff000000);
            tv_activity_daily.setTextColor(0xff888888);
            tv_activity_since.setTextColor(0xff888888);
            tv_activity_done.setTextColor(0xff008800);
            tv_activity_of.setTextColor(0xff888888);
            tv_activity_goal.setTextColor(0xff0000aa);
            tv_activity_today.setTextColor(int_done_today == 0 ? 0xffff0000 : (int_done_today >= int_daily ? 0xff008800 : 0xffff8000));
            tv_activity_progress.setTextColor((int_progress >= 0 ? 0xff008800 : 0xffff0000 ));

            View.OnClickListener ocl = new View.OnClickListener() {
                @Override
                public void onClick(View v) { editDone(((Pair<Integer, Integer>)v.getTag()).first, ((Pair<Integer, Integer>)v.getTag()).second, true); }
            };
            btn_remove_one.setTag(new Pair<Integer, Integer>(s, -1));
            btn_remove_one.setOnClickListener(ocl);
            btn_remove_half.setTag(new Pair<Integer, Integer>(s, -int_daily/2));
            btn_remove_half.setOnClickListener(ocl);
            btn_add_one.setTag(new Pair<Integer, Integer>(s, 1));
            btn_add_one.setOnClickListener(ocl);
            btn_add_half.setTag(new Pair<Integer, Integer>(s, int_daily/2));
            btn_add_half.setOnClickListener(ocl);

            btn_set.setTag(new Pair<Integer, Integer>(s, int_done));
            btn_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    final EditText et_done = new EditText(getApplicationContext());
                    et_done.setInputType(InputType.TYPE_CLASS_NUMBER);
                    et_done.setText(((Pair<Integer, Integer>)btn_set.getTag()).second.toString());
                    et_done.setTextColor(0xff000000);
                    new AlertDialog.Builder(ActivitiesList.this)
                        .setTitle("Enter number manually")
                        .setView(et_done)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                editDone(((Pair<Integer, Integer>)btn_set.getTag()).first, Integer.parseInt(et_done.getText().toString()), false);
                                listActivities();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });

            ll_activity_details.addView(tv_activity_name);
            ll_activity_details.addView(tv_activity_daily);
            ll_activity_details.addView(tv_activity_since);
            ll_activity_of.addView(tv_activity_done);
            ll_activity_of.addView(tv_activity_of);
            ll_activity_of.addView(tv_activity_goal);
            ll_activity_progress.addView(tv_activity_progress);
            ll_activity_progress.addView(tv_activity_today);
            ll_activity_actions.addView(btn_remove_one, sp_64, sp_64);
            if (int_daily > 3) {
                ll_activity_actions.addView(btn_remove_half, sp_64, sp_64);
                ll_activity_actions.addView(btn_add_half, sp_64, sp_64);
            }
            ll_activity_actions.addView(btn_add_one, sp_64, sp_64);
            ll_activity_actions.addView(btn_set, sp_64, sp_64);

            tlr_activity.setTag(s);
            tlr_activity.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(ActivitiesList.this)
                        .setTitle("Remove activity")
                        .setMessage("Are you sure you wish delete the activity '"+ tv_activity_name.getText() +"'?")
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                forgetActivity(Integer.parseInt(tlr_activity.getTag().toString()));
                                Snackbar.make(findViewById(R.id.fab_add_activity), "Activity removed", Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                    return false;
                }
            });

            tbl_activities.addView(tlr_activity);
        }
    }

    protected void addActivity (String str_new_activity_name, Integer int_new_activity_daily, Integer int_new_activity_done, Date d_new_activity_date)
    {
        TinyDB tdb = new TinyDB(getApplicationContext());
        ArrayList<String> lts_activities_name = new ArrayList<>(tdb.getListString("activities_name"));
        ArrayList<Integer> lti_activities_daily = new ArrayList<>(tdb.getListInt("activities_daily"));
        ArrayList<Integer> lti_activities_done = new ArrayList<>(tdb.getListInt("activities_done"));
        ArrayList<Long> ltl_activities_date = new ArrayList<>(tdb.getListLong("activities_date"));
        ArrayList<Long> ltl_activities_date_last = new ArrayList<>(tdb.getListLong("activities_date_last"));
        ArrayList<Integer> lti_activities_done_last = new ArrayList<>(tdb.getListInt("activities_done_last"));
        ArrayList<Integer> lti_activities_record = new ArrayList<>(tdb.getListInt("activities_record"));
        lts_activities_name.add(str_new_activity_name);
        lti_activities_daily.add(int_new_activity_daily);
        lti_activities_done.add(int_new_activity_done);
        ltl_activities_date.add(d_new_activity_date.getTime());
        ltl_activities_date_last.add((new Date()).getTime());
        lti_activities_done_last.add(0);
        lti_activities_record.add(0);
        tdb.putListString("activities_name", lts_activities_name);
        tdb.putListInt("activities_daily", lti_activities_daily);
        tdb.putListInt("activities_done", lti_activities_done);
        tdb.putListLong("activities_date", ltl_activities_date);
        tdb.putListLong("activities_date_last", ltl_activities_date_last);
        tdb.putListInt("activities_done_last", lti_activities_done_last);
        tdb.putListInt("activities_record", lti_activities_record);

        listActivities();
    }

    protected void wipeActivities ()
    {
        new AlertDialog.Builder(ActivitiesList.this)
            .setTitle("Wipe activities")
            .setMessage("Are you sure you wish to wipe all activities in your list?")
            .setPositiveButton("Wipe", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    TinyDB tdb = new TinyDB(getApplicationContext());
                    tdb.clear();
                    listActivities();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void setBtnDateText (final Date d_date, final  Button btn_button)
    {
        SimpleDateFormat sdf_format = new SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH);
        btn_button.setText("Pick start ("+ sdf_format.format(d_date) +")");
    }

    private void pickDate (final Date d_target, final Button btn_open_dp_activity_date)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(d_target);
        DatePickerDialog dialog = new DatePickerDialog(ActivitiesList.this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    d_target.setTime(calendar.getTimeInMillis());

                    setBtnDateText(d_target, btn_open_dp_activity_date);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listActivities();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_activity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ll_new_activity = new LinearLayout(getApplicationContext());
                ll_new_activity.setOrientation(LinearLayout.VERTICAL);
                final EditText et_activity_name = new EditText(getApplicationContext());
                final EditText eti_activity_daily = new EditText(getApplicationContext());
                final EditText eti_activity_done = new EditText(getApplicationContext());
                final Button btn_open_dp_activity_date = new Button(getApplicationContext());
                final Date d_activity_date = new Date();
                eti_activity_daily.setInputType(InputType.TYPE_CLASS_NUMBER);
                eti_activity_done.setInputType(InputType.TYPE_CLASS_NUMBER);
                et_activity_name.setHint("Activity name");
                eti_activity_daily.setHint("Activity frequency per day");
                eti_activity_done.setHint("Already done");
                et_activity_name.setTextColor(0xff000000);
                eti_activity_daily.setTextColor(0xff000000);
                eti_activity_done.setTextColor(0xff000000);
                et_activity_name.setHintTextColor(0xff888888);
                eti_activity_daily.setHintTextColor(0xff888888);
                eti_activity_done.setHintTextColor(0xff888888);
                setBtnDateText(d_activity_date, btn_open_dp_activity_date);
                btn_open_dp_activity_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        pickDate(d_activity_date, btn_open_dp_activity_date);
                    }
                });
                ll_new_activity.addView(et_activity_name);
                ll_new_activity.addView(eti_activity_daily);
                ll_new_activity.addView(eti_activity_done);
                ll_new_activity.addView(btn_open_dp_activity_date);


                new AlertDialog.Builder(ActivitiesList.this)
                    .setTitle("Add new daily activity")
                    .setMessage("Please enter details of a new activity.")
                    .setView(ll_new_activity)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            try {
                                addActivity(et_activity_name.getText().toString(), Integer.parseInt(eti_activity_daily.getText().toString()), Integer.parseInt(eti_activity_done.getText().toString()), d_activity_date);
                                Snackbar.make(findViewById(R.id.fab_add_activity), "New activity added!", Snackbar.LENGTH_LONG).show();
                                listActivities();
                            } catch (Exception e) {
                                Snackbar.make(findViewById(R.id.fab_add_activity), e.toString(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });

        final ScrollView scv_scroller = (ScrollView) findViewById(R.id.scv_scoller);
        scv_scroller.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                if (scv_scroller.getScrollY() < 16 && !fab.isShown())
                    fab.show();
                else if (scv_scroller.getScrollY() > 16 && fab.isShown())
                    fab.hide();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activities_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_wipe:
                wipeActivities();
                return true;
            case R.id.action_about:
                new AlertDialog.Builder(ActivitiesList.this)
                    .setTitle("DailyGoals Help & About")
                    .setMessage("Tap and hold an activity to remove it. This app was developed by Patrick Bowen (phunanon).")
                    .setPositiveButton("Cheers", null)
                    .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
