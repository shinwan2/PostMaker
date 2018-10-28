package com.shinwan2.postmaker.home

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.model.User
import com.shinwan2.postmaker.util.debounceClicks
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.fragment_drawer.displayNameTextView
import kotlinx.android.synthetic.main.fragment_drawer.signOutButton
import javax.inject.Inject

class DrawerFragment : Fragment() {
    @Inject
    lateinit var userService: UserService
    @Inject
    lateinit var authenticationService: AuthenticationService
    @Inject
    lateinit var schedulerManager: SchedulerManager

    private lateinit var compositeDisposable: CompositeDisposable

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        val disposable = userService.getUser()
            .subscribeOn(schedulerManager.backgroundThreadScheduler)
            .observeOn(schedulerManager.uiThreadScheduler)
            .subscribeWith(object : DisposableSingleObserver<User>() {
                override fun onSuccess(user: User) {
                    displayNameTextView.text = user.displayName
                }

                override fun onError(e: Throwable) {
                }
            })
        compositeDisposable.add(disposable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val disposable = signOutButton.debounceClicks().subscribe {
            authenticationService.signOut()
                .subscribeOn(schedulerManager.backgroundThreadScheduler)
                .observeOn(schedulerManager.uiThreadScheduler)
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        showToast(getString(R.string.signout_message_success))
                    }

                    override fun onError(e: Throwable) {
                        showToast(e.message!!)
                    }
                })
        }
        compositeDisposable.add(disposable)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}