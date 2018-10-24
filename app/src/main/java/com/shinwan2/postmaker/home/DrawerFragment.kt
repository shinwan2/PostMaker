package com.shinwan2.postmaker.home

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.model.User
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.fragment_drawer.displayNameTextView
import javax.inject.Inject

class DrawerFragment : Fragment() {
    @Inject
    lateinit var userService: UserService
    @Inject
    lateinit var schedulerManager: SchedulerManager

    private var getUserDisposable: Disposable? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserDisposable = userService.getUser()
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableSingleObserver<User>() {
                override fun onSuccess(user: User) {
                    displayNameTextView.text = user.displayName
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drawer, container, false)
    }
}