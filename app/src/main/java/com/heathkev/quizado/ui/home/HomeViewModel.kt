package com.heathkev.quizado.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.utils.Utility
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()
    val user = FirebaseAuth.getInstance().currentUser!!

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _categoryList = Utility.getCategoryResults().toMutableList()
    private val _resultList = MutableLiveData<List<Result>>()
    val resultList: LiveData<List<Result>>
        get() = _resultList

    private val _navigateToQuizListModel = MutableLiveData<QuizListModel>()
    val navigateToQuizListModel: LiveData<QuizListModel>
        get() = _navigateToQuizListModel

    init {
        getResults()
    }


    private fun getResults() {
        uiScope.launch {
            val value = withContext(Dispatchers.IO) {
                firebaseRepository.getResultsByUserIdAsync(user.uid)
            }

            parseResults(value)
        }
    }

    private suspend fun parseResults(value: QuerySnapshot?) {
        withContext(Dispatchers.Default) {
            for (doc in value!!) {
                val resultItem = doc.toObject<Result>()

                val userResult = _categoryList.find { it.quiz_category == resultItem.quiz_category }
                _categoryList.remove(userResult)
                _categoryList.add(resultItem)
            }

            _resultList.postValue(_categoryList.sortedByDescending { it.correct })
        }
    }

    fun playQuiz() {
        uiScope.launch {
            val value = withContext(Dispatchers.IO) {
                firebaseRepository.getSingleQuiz()
            }

            withContext(Dispatchers.Default) {
                for (doc in value!!) {
                    val quizItem = doc.toObject<QuizListModel>()
                    _navigateToQuizListModel.postValue(quizItem)
                }
            }
        }
    }

    fun playQuizComplete() {
        _navigateToQuizListModel.value = null
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}