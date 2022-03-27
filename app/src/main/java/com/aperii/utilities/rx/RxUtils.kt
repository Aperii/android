package com.aperii.utilities.rx

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

object RxUtils {

    @Throws(IllegalStateException::class)
    fun <T> Observable<T>.await(): Pair<T?, Throwable?> {

        val latch = CountDownLatch(1)
        val resRef = AtomicReference<T?>()
        val throwableRef = AtomicReference<Throwable?>()

        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onComplete() = latch.countDown()
                override fun onNext(t: T) = resRef.set(t)
                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    throwableRef.set(e)
                    latch.countDown()
                }
            })

        if (latch.count != 0L)
            latch.await()

        return resRef.get() to throwableRef.get()
    }

    fun <T> Observable<T>.observe(onNext: T.() -> Unit) {
        return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
                override fun onNext(t: T) {
                    onNext(t)
                }
            })
    }

    fun <T> Observable<T>.observeAndCatch(onNext: T.() -> Unit, onError: Throwable.() -> Unit) {
        return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {
                    onError(e)
                }

                override fun onNext(t: T) {
                    onNext(t)
                }
            })
    }

    fun stopAll() = RxAndroidPlugins.reset()

}