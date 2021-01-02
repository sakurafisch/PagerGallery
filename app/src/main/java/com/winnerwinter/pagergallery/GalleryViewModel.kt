package com.winnerwinter.pagergallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.math.ceil


enum class DataStatus() {
    CAN_LOAD_MORE,
    NO_MORE,
    NETWORK_ERROR
}

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _dataStatusLive = MutableLiveData<Int>()
    val dataStatusLive: LiveData<Int> get() = _dataStatusLive
    private val _photoList = MutableLiveData<List<PhotoItem>>()
    val photoList: LiveData<List<PhotoItem>>
        get() = _photoList

    var needToScrollToTop = true

    private var perpage = 50
    private val keyWords = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")

    private var currentPage = 1
    private var totalPage = 1
    private var currentKey = "cat"
    private var isNewQuery = true
    private var isLoading = false

    init {
        resetQuery()
    }

    fun resetQuery() {
        currentPage = 1
        totalPage = 1
        currentKey = keyWords.random()
        isNewQuery = true
        needToScrollToTop = true
        fetchData()
    }

    fun fetchData() {
        if (isLoading) return
        if (currentPage > totalPage) {
            _dataStatusLive.value = DataStatus.NO_MORE.ordinal
            return
        }
        isLoading = true
        val stringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            {
                with(Gson().fromJson(it, Pixabay::class.java)) {
                    totalPage = ceil(totalHits.toDouble() / perpage).toInt()
                    if (isNewQuery) {
                        _photoList.value = photoItems.toList()
                    } else {
                        _photoList.value = arrayListOf(_photoList.value!!, photoItems.toList()).flatten()
                    }
                }
                _dataStatusLive.value = DataStatus.CAN_LOAD_MORE.ordinal
                isLoading = false
                isNewQuery = false
                currentPage++
            },
            {
                _dataStatusLive.value = DataStatus.NETWORK_ERROR.ordinal
                isLoading = false
            }
        )
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=19686017-06627c8d07ab2dc0e2bce3743&q=${keyWords.random()}&per_page=${perpage}&page=${currentPage}"
    }
}