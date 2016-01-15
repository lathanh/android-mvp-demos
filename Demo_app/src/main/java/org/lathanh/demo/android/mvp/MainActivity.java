package org.lathanh.demo.android.mvp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import org.lathanh.demo.android.mvp.adapting_demo.*;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_Improvement1Fragment;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_StandardFragment;


public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.app_name);
    setSupportActionBar(toolbar);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    FragmentManager fragmentManager = getSupportFragmentManager();
    switch (id) {
      case R.id.clear:
        Fragment activeFargment =
            fragmentManager.findFragmentById(R.id.container);
        if (activeFargment != null) {
          fragmentManager.beginTransaction()
              .remove(activeFargment)
              .setBreadCrumbTitle(R.string.clear)
              .commit();
        }
        return true;
      case R.id.adaptingDemo_standard:
        fragmentManager.beginTransaction()
            .replace(R.id.container, AdaptingDemo_StandardFragment.newInstance())
            .setBreadCrumbTitle(R.string.adaptingDemo_standardFragment_name)
            .commit();
        return true;
      case R.id.adaptingDemo_improvement1:
        fragmentManager.beginTransaction()
            .replace(R.id.container, AdaptingDemo_Improvement1Fragment.newInstance())
            .setBreadCrumbTitle(R.string.adaptingDemo_improvement1Fragment_name)
            .commit();
        return true;
      case R.id.adaptingDemo_improvement2:
        fragmentManager.beginTransaction()
            .replace(R.id.container, AdaptingDemo_Improvement2Fragment.newInstance())
            .setBreadCrumbTitle(R.string.adaptingDemo_improvement2Fragment_name)
            .commit();
        return true;
      case R.id.dataBindingDemo_standard:
        fragmentManager.beginTransaction()
            .replace(R.id.container, DataBindingDemo_StandardFragment.newInstance())
            .setBreadCrumbTitle(R.string.dataBindingDemo_standardFragment_name)
            .commit();
        return true;
      case R.id.dataBindingDemo_improvement1:
        fragmentManager.beginTransaction()
            .replace(R.id.container, DataBindingDemo_Improvement1Fragment.newInstance())
            .setBreadCrumbTitle(R.string.dataBindingDemo_improvement1Fragment_name)
            .commit();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
