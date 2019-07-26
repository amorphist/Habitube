package az.amorphist.poster;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import javax.inject.Inject;

import az.amorphist.poster.ui.ExploreFragment;
import az.amorphist.poster.ui.NavbarFragment;
import az.amorphist.poster.ui.PostFragment;
import az.amorphist.poster.ui.SearchFragment;
import moxy.MvpAppCompatActivity;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.android.support.SupportAppNavigator;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import toothpick.Toothpick;

import static az.amorphist.poster.di.DI.APP_SCOPE;

public class MainActivity extends MvpAppCompatActivity {

    @Inject NavigatorHolder navigatorHolder;
    @Inject Router router;

    private Navigator navigator = new SupportAppNavigator(this, R.id.fragment_container) {
        @Override
        protected void setupFragmentTransaction(Command command, Fragment currentFragment, Fragment nextFragment, FragmentTransaction fragmentTransaction) {
            if (command instanceof Forward) {
                if (currentFragment instanceof NavbarFragment && nextFragment instanceof PostFragment) {
                    fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                }
                if (currentFragment instanceof PostFragment && nextFragment instanceof PostFragment) {
                    fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                }
                if (currentFragment instanceof SearchFragment && nextFragment instanceof PostFragment) {
                    fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toothpick.inject(this, Toothpick.openScope(APP_SCOPE));
        router.newRootScreen(new Screens.NavbarScreen());
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navigatorHolder.removeNavigator();
    }

}
