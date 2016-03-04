package org.lathanh.demo.android.mvp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Improvement1Fragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Improvement2Fragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_StandardFragment;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_Improvement1Fragment;
import org.lathanh.demo.android.mvp.adapting_demo.data_binding.DataBindingDemo_StandardFragment;

public class MainActivity extends AppCompatActivity {

  /**
   * The {@link #onCreateOptionsMenu(Menu) Options Menu} options and where they
   * should go.
   */
  public enum MenuOption {
    ADAPTING_DEMO_STANDARD(R.id.adaptingDemo_standard) {
      @Override
      public Fragment newInstance() {
        return AdaptingDemo_StandardFragment.newInstance();
      }
    },
    ADAPTING_DEMO_IMPROVEMENT1(R.id.adaptingDemo_improvement1) {
      @Override
      public Fragment newInstance() {
        return AdaptingDemo_Improvement1Fragment.newInstance();
      }
    },
    ADAPTING_DEMO_IMPROVEMENT2(R.id.adaptingDemo_improvement2) {
      @Override
      public Fragment newInstance() {
        return AdaptingDemo_Improvement2Fragment.newInstance();
      }
    },
    DATA_BINDING_DEMO_STANDARD(R.id.dataBindingDemo_standard) {
      @Override
      public Fragment newInstance() {
        return DataBindingDemo_StandardFragment.newInstance();
      }
    },
    DATA_BINDING_DEMO_IMPROVEMENT1(R.id.dataBindingDemo_improvement1) {
      @Override
      public Fragment newInstance() {
        return DataBindingDemo_Improvement1Fragment.newInstance();
      }
    },
    ;

    final int menuId;

    MenuOption(int menuId) {
      this.menuId = menuId;
    }

    public abstract Fragment newInstance();
  }


  //-- 'Activity' methods -----------------------------------------------------

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

    if (id == R.id.clear) {
      Fragment activeFragment =
          fragmentManager.findFragmentById(R.id.container);
      if (activeFragment != null) {
        fragmentManager.beginTransaction()
            .remove(activeFragment)
            .setBreadCrumbTitle(R.string.clear)
            .commit();
      }
      return true;
    } else {
      for (MenuOption menuOption : MenuOption.values()) {
        if (id == menuOption.menuId) {
          fragmentManager.beginTransaction()
              .replace(R.id.container, menuOption.newInstance())
              .setBreadCrumbTitle(item.getTitle())
              .commit();
          return true;
        }
      }
    }

    return super.onOptionsItemSelected(item);
  } // onOptionsItemSelected()

}
