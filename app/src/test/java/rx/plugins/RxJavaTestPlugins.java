package rx.plugins;

import rx.Scheduler;
import rx.schedulers.Schedulers;

//http://fedepaol.github.io/blog/2015/09/13/testing-rxjava-observables-subscriptions/
public class RxJavaTestPlugins extends RxJavaPlugins {

    private RxJavaTestPlugins() {
        super();
    }

    public static void resetPlugins(){
        getInstance().reset();
    }

    public static void immediateAndroidSchedulers() {
        resetPlugins();
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {

            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }

}
