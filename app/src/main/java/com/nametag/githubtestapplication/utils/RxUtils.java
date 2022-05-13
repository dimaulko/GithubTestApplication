package com.nametag.githubtestapplication.utils;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Helper class for Java RX
 */
public class RxUtils {

    private RxUtils() {
        //no instance
    }


    /**
     * Unsubscribe subscription if it is not null
     *
     * @param disposable some disposable
     */
    public static void unsubscribeIfNotNull(Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    /**
     * Returns new Subscription if current is null or unsubscribed
     *
     * @param disposable some subscription
     * @return new Subscription if current is null or unsubscribed
     */
    public static CompositeDisposable getNewCompositeDispIfUnsubscribed(CompositeDisposable disposable) {
        if (disposable == null || disposable.isDisposed()) {
            return new CompositeDisposable();
        }

        return disposable;
    }

    /**
     * ? -> IO -> MAIN
     * Subscribes in the worker IO thread
     * Observes in the Main thread
     *
     * @param observable some observable
     * @param <T>        any type; input type = output type
     * @return input observable, which subscribes in the worker IO thread and observes in the Main thread
     */
    public static <T> Observable<T> ioToMainObservable(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /* ? -> IO -> MAIN
     * Subscribes in the worker IO thread
     * Observes in the Main thread
     *
     * @param observable some observable
     * @param <T>        any type; input type = output type
     * @return input observable, which subscribes in the worker IO thread and observes in the Main thread
     */
    public static <T> Single<T> ioToMainSingle(Single<T> single) {
        return single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * ? -> IO -> IO
     * Subscribes in the worker IO thread
     * Observes in the worker IO thread
     *
     * @param observable some observable
     * @param <T>        any type; input type = output type
     * @return input observable, which subscribes in the worker IO thread and observes in the worker IO thread
     */
    public static <T> Observable<T> toWorkerThread(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> FlowableTransformer<T, T> ioToMainTransformerFlowable() {
        return inObservable -> inObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> ioToMainTransformerComputation() {
        return inObservable -> inObservable
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> ioToMainTransformer() {
        return inObservable -> inObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> toWorkerThreadTransformer() {
        return inObservable -> inObservable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> SingleTransformer<T, T> ioToMainTransformerSingle() {
        return inSingle -> inSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> MaybeTransformer<T, T> ioToMainTransformerMaybe() {
        return inSingle -> inSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> toWorkerThreadTransformerSingle() {
        return inSingle -> inSingle
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static CompletableTransformer ioToMainTransformerCompletable() {
        return inSingle -> inSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Maybe<T> ofNullable(T value) {
        return value == null ? Maybe.empty() : Maybe.just(value);
    }

    public static Completable postToView(View... views) {
        List<Single<Boolean>> singles = new ArrayList<>();
        for (View view : views) {
            singles.add(Single.create(e -> view.post(() -> {
                if (!e.isDisposed()) {
                    e.onSuccess(true);
                }
            })));
        }
        return Single.zip(singles, (Function<Object[], Object>) objects -> true).toCompletable();
    }
}
