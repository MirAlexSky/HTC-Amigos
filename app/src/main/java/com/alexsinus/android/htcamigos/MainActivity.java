package com.alexsinus.android.htcamigos;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alexsinus.android.htcamigos.model.Employee;
import com.alexsinus.android.htcamigos.model.EmployeeFetcher;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

  ListView listView;
  List<Employee> employees;
  FetchItemTask task;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    listView = findViewById(R.id.listView);

    Object employees = getLastCustomNonConfigurationInstance();
    if (employees instanceof List) {
      this.employees = (List<Employee>) employees;
      removeProgressBar();
      setupAdapter();
    } else {
      task = new FetchItemTask();
      task.execute();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    this.getMenuInflater().inflate(R.menu.activity_main, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_update) {
      reloadEmployees();
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return employees;
  }

  @Override
  protected void onDestroy() {
    if (task != null) {
      task.cancel(true);
    }
    super.onDestroy();
  }

  private void setupAdapter() {
    EmployeeAdapter adapter = new EmployeeAdapter(this, employees);
    listView.setAdapter(adapter);
  }

  private class FetchItemTask extends AsyncTask<Void, Void, List<Employee>> {
    @Override
    protected List<Employee> doInBackground(Void... query) {

      List<Employee> employees = null;

      while (employees == null) {
        try {
          employees = new EmployeeFetcher().fetchEmployees();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (JSONException e) {
          e.printStackTrace();
        }

        if (employees == null) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }

      return employees;
    }

    @Override
    protected void onPostExecute(List<Employee> employees) {
      if (employees == null) {
        return;
      }

      MainActivity.this.employees = employees;
      removeProgressBar();
      setupAdapter();
    }
  }

  private static class EmployeeViewHolder {
    public TextView nameTextView;
    public TextView phoneTextView;
    public FlexboxLayout skillContainer;
  }

  private class EmployeeAdapter extends BaseAdapter {
    private List<Employee> employees;
    private LayoutInflater inflater;

    public EmployeeAdapter(Context context, List<Employee> employees) {
      this.employees = employees;
      inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
      return employees.size();
    }

    @Override
    public Employee getItem(int position) {
      return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
      return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
      EmployeeViewHolder viewHolder;
      Employee employee = employees.get(position);

      if (view == null) {
        view = inflater.inflate(R.layout.employee_item, parent, false);

        viewHolder = new EmployeeViewHolder();
        viewHolder.nameTextView = view.findViewById(R.id.nameTextView);
        viewHolder.phoneTextView = view.findViewById(R.id.phoneTextView);
        viewHolder.skillContainer = view.findViewById(R.id.skillContainer);

        view.setTag(viewHolder);
      } else {
        viewHolder = (EmployeeViewHolder) view.getTag();
      }

      viewHolder.nameTextView.setText(employee.getName());
      viewHolder.phoneTextView.setText(employee.getPhoneNumber());

      viewHolder.skillContainer.removeAllViews();

      List<String> skills = employee.getSkills();

      for (String skill : skills) {
        int solidColor = SkillColor.getSolidColorBySkill(skill.toLowerCase());
        int strokeColor = SkillColor.getStrokeColorBySkill(skill.toLowerCase());

        TextView skillView = (TextView) inflater.inflate(R.layout.skill_item,
                viewHolder.skillContainer, false);
        skillView.setText(skill);

        GradientDrawable background = (GradientDrawable) getResources()
                .getDrawable(R.drawable.shape_skill);
        background.mutate();
        skillView.setBackground(background);

        background.setColor(getResources().getColor(solidColor));
        background.setStroke(2, getResources().getColor(strokeColor));

        viewHolder.skillContainer.addView(skillView);
      }

      return view;
    }
  }

  private void reloadEmployees() {
    if ((task != null) && (task.getStatus() == AsyncTask.Status.RUNNING)) {
      return;
    }

    employees = null;
    listView.setAdapter(null);
    showProgressBar();

    task = new FetchItemTask();
    task.execute();
  }

  private void removeProgressBar() {
    findViewById(R.id.progressBar).setVisibility(ProgressBar.GONE);
  }

  private void showProgressBar() {
    findViewById(R.id.progressBar).setVisibility(ProgressBar.VISIBLE);
  }

  private static class SkillColor {
    private static Map<String, Integer> skillColorShape = new HashMap<>();
    private static Map<String, Integer> skillColorStroke = new HashMap<>();

    static {
      skillColorShape.put("android", R.color.colorSkill1);
      skillColorShape.put("java", R.color.colorSkill2);
      skillColorShape.put("smart-tv", R.color.colorSkill3);
      skillColorShape.put("objective-c", R.color.colorSkill4);
      skillColorShape.put("photoshop", R.color.colorSkill5);
      skillColorShape.put("python", R.color.colorSkill6);
      skillColorShape.put("moviemaker", R.color.colorSkill7);
      skillColorShape.put("groovy", R.color.colorSkill8);
      skillColorShape.put("kotlin", R.color.colorSkill9);
      skillColorShape.put("php", R.color.colorSkill10);
      skillColorShape.put("c#", R.color.colorSkill11);
      skillColorShape.put("default", R.color.colorSkillDef);

      skillColorStroke.put("android", R.color.colorSkill1Stroke);
      skillColorStroke.put("java", R.color.colorSkill2Stroke);
      skillColorStroke.put("smart-tv", R.color.colorSkill3Stroke);
      skillColorStroke.put("objective-c", R.color.colorSkill4Stroke);
      skillColorStroke.put("photoshop", R.color.colorSkill5Stroke);
      skillColorStroke.put("python", R.color.colorSkill6Stroke);
      skillColorStroke.put("moviemaker", R.color.colorSkill7Stroke);
      skillColorStroke.put("groovy", R.color.colorSkill8Stroke);
      skillColorStroke.put("kotlin", R.color.colorSkill9Stroke);
      skillColorStroke.put("php", R.color.colorSkill10Stroke);
      skillColorStroke.put("c#", R.color.colorSkill11Stroke);
      skillColorStroke.put("default", R.color.colorSkillDefStroke);
    }

    public static Integer getSolidColorBySkill(String skill) {
      Integer color = skillColorShape.get(skill);
      if (color == null) {
        color = skillColorShape.get("default");
      }

      return color;
    }

    public static Integer getStrokeColorBySkill(String skill) {
      Integer color = skillColorStroke.get(skill);
      if (color == null) {
        color = skillColorStroke.get("default");
      }

      return color;
    }
  }
}

