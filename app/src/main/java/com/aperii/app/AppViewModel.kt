package com.aperii.app

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

abstract class AppViewModel<V>(v: V?) : ViewModel(), AppComponent {
    override val unsubscribeSignal: Subject<Void> = PublishSubject.create()
    private val viewStateSubject: BehaviorSubject<V> = BehaviorSubject.create()

    val viewState: V = viewStateSubject.value

    constructor() : this(null)

    init {
        v?.apply {
            viewStateSubject.onNext(this)
        }
    }

    fun observeViewState(): Observable<V> = viewStateSubject
    private fun modifyPendingViewState(v: V, v2: V) = v2

//    override fun onCleared() {
//        super.onCleared()
//        unsubscribeSignal.onNext()
//    }

    @MainThread()
    fun updateViewState(v: V) {
        viewStateSubject.onNext(modifyPendingViewState(viewState, v)!!)
    }

    fun <T> withViewState(block: (V) -> T): T = block.invoke(viewState!!)
}
