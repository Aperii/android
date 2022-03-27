package com.aperii.app

import io.reactivex.subjects.Subject

interface AppComponent {
    val unsubscribeSignal: Subject<Void>
}