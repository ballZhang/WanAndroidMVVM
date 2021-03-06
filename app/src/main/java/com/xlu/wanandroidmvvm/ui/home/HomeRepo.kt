package com.xlu.wanandroidmvvm.ui.home

import androidx.lifecycle.MutableLiveData
import com.xlu.base_library.base.BaseRepository
import com.xlu.base_library.http.ApiException
import com.xlu.kotlinandretrofit.bean.Article
import com.xlu.kotlinandretrofit.bean.Banner
import com.xlu.wanandroidmvvm.http.ApiService
import com.xlu.wanandroidmvvm.http.RetrofitManager
import kotlinx.coroutines.CoroutineScope

/**
 * @Author xlu
 * @Date 2020/7/22 23:18
 * @Description TODO
 */
class HomeRepo(coroutineScope: CoroutineScope, errorLiveData: MutableLiveData<ApiException>) :
    BaseRepository(coroutineScope, errorLiveData) {

    private var page = 0

    /**
     * 获取首页文章列表， 包括banner
     */
    fun getArticleList(
        isRefresh: Boolean,
        articleLiveData: MutableLiveData<MutableList<Article.Data>>,
        banner: MutableLiveData<MutableList<Banner>>
    ) {
        //仅在第一页或刷新时调用banner和置顶
        if (isRefresh) {
            page = 0
            getBanner(banner)
            getTopArticle(articleLiveData)
        } else {
            page++
            getHomeArticle(articleLiveData)
        }
    }

    /*置顶文章*/
    fun getTopArticle(articleLiveData: MutableLiveData<MutableList<Article.Data>>){
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .getTopList()
                    .data()
            },
            success = {
                getHomeArticle(articleLiveData,it,true)
            }
        )
    }

    /*首页文章*/
    fun getHomeArticle(
        articleLiveData: MutableLiveData<MutableList<Article.Data>>,
        list: MutableList<Article.Data>?= null,
        isRefresh: Boolean = false){
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .getHomeList(page)
                    .data()
            },
            success = {
                list?.addAll(it.datas)
                //数据累加
                articleLiveData.value.apply {
                    //val bakcList = mutableListOf<Article.Data>()
                    if (isRefresh){
                        articleLiveData.postValue(list)
                    }else{
                        articleLiveData.postValue(it.datas.toMutableList())
                    }
                }
            }
        )
    }


    /**
     * 获取banner
     */
    private fun getBanner(banner: MutableLiveData<MutableList<Banner>>) {
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .getBanner()
                    .data()
            },
            success = {
                banner.postValue(it)
            }
        )
    }

    /**
     * 收藏
     */
    fun collect(id:Int,collectLiveData : MutableLiveData<Int>){
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .collect(id)
                    .data(Any::class.java)
            },
            success = {
                collectLiveData.postValue(id)
            }
        )
    }



    /**
     * 取消收藏
     */
    fun unCollect(id:Int,unCollectLiveData : MutableLiveData<Int>){
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .unCollect(id)
                    //如果data可能为空,可通过此方式通过反射生成对象,避免空判断
                    .data(Any::class.java)
            },
            success = {
                unCollectLiveData.postValue(id)
            }
        )
    }



}