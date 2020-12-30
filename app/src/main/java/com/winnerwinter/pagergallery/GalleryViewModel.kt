package com.winnerwinter.pagergallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoList = MutableLiveData<List<PhotoItem>>()
    val photoList: LiveData<List<PhotoItem>>
        get() = _photoList
    fun fetchData() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            {
                _photoList.value = Gson().fromJson(it, Pixabay::class.java).photoItems.toList()
            },
            {
                Log.d("hello", it.toString())
            }
        )
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl(): String {
        return "https://pixabay.com/api/?key=19686017-06627c8d07ab2dc0e2bce3743&q=${keyWords.random()}&per_page=100"
    }

    private val keyWords = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
}