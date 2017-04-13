package com.tjw.template.net;

import com.tjw.template.bean.Repo;
import com.tjw.template.bean.RepoSearchResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Android on 2017/2/27.
 */

public interface GitHubApi {
    
    @GET("users/{user}/repos")
    Observable<List<Repo>> getRepos(@Path("user") String user);
    
    @GET("search/repositories")
    Observable<RepoSearchResult> searchRepos(@Query("q") String keyword);
    
}
