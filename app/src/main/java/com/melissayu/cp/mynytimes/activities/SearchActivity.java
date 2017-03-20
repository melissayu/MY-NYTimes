package com.melissayu.cp.mynytimes.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melissayu.cp.mynytimes.R;
import com.melissayu.cp.mynytimes.adapters.ArticleRVAdapter;
import com.melissayu.cp.mynytimes.fragments.FilterFragment;
import com.melissayu.cp.mynytimes.fragments.FilterFragment.FilterDialogListener;
import com.melissayu.cp.mynytimes.models.Article;
import com.melissayu.cp.mynytimes.utils.EndlessRecyclerViewScrollListener;
import com.melissayu.cp.mynytimes.utils.ItemClickSupport;
import com.melissayu.cp.mynytimes.utils.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogListener {

//    GridView gvResults;
    RecyclerView gvResults;
    TextView tvNetworkUnavailable;
    StaggeredGridLayoutManager gridLayoutManager;
    SearchView searchView;

    String sortBy="Newest"; // Newest by default
    String beginDate="20000101"; //01-01-200 be default
    String newsDesk;

    private EndlessRecyclerViewScrollListener ervsListener;
    int currentPage;

    ArrayList<Article> articles;
    ArticleRVAdapter adapter;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
        getArticles(true);
    }

    public void setupViews() {
        tvNetworkUnavailable = (TextView) findViewById(R.id.tvNetworkUnavailable);
        gvResults = (RecyclerView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleRVAdapter(this, articles);
        gvResults.setAdapter(adapter);

        ItemClickSupport.addTo(gvResults).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        Article article = articles.get(position);
                        i.putExtra("article", article);
                        startActivity(i);
                    }
                }
        );

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gvResults.setLayoutManager(gridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(30);
        gvResults.addItemDecoration(decoration);

        ervsListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        getArticles(false);
                    }
                };
        gvResults.addOnScrollListener(ervsListener);

        if (!isNetworkAvailable()) {
            tvNetworkUnavailable.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryStr) {
                // perform query here
                query = queryStr;
                getArticles(true);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if (newText.equals("")){
//                    getArticles(true);
//                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.miFilter:
                filterClicked();
                return true;
            case R.id.miFrontPage:
                frontPageClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void frontPageClicked() {
        searchView.setQuery("", true);
        sortBy = "Newest";
        newsDesk = null;
        beginDate="20000101";
        query = "";
        getArticles(true);
    }

    public void filterClicked() {
        FragmentManager fm = getSupportFragmentManager();
        FilterFragment filterDialogFragment = FilterFragment.newInstance(sortBy, beginDate, newsDesk);
        filterDialogFragment.show(fm, "fragment_filter");

    }

    @Override
    public void onFinishFilterDialog(String sortByVal, String beginDateVal, String newsDeskVal) {
        sortBy = sortByVal;
        beginDate = beginDateVal;
        newsDesk = newsDeskVal;
    }

    public void getArticles( Boolean newSearch) {
        if (newSearch) {
            currentPage = 0;
            articles.clear();
            adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
            ervsListener.resetState();

        }

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        final String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        final RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", "28580fe0e09141ceaa0f57d3ab5eb653");
        requestParams.put("page", currentPage);

        if (query != null && query != "") {
            requestParams.put("q", query);
        }

        if (sortBy != null) {
            requestParams.put("sort", sortBy.toLowerCase());
        }
        if (beginDate != null) {
            requestParams.put("begin_date", beginDate);
        }
        if (newsDesk != null) {
            requestParams.put("fq", newsDesk);
        }

        asyncHttpClient.get(url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "onFailure:"+url);
                Log.d("DEBUG", "onFailure:"+requestParams.toString());
                Log.d("DEBUG", errorResponse.toString());
//                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());

                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d("DEBUG", articleJSONResults.toString());

                    articles.addAll(Article.fromJSONArray(articleJSONResults));
                    Log.d("DEBUG", articles.toString());
                    adapter.notifyDataSetChanged();
                    currentPage++;
                } catch (Exception e) {

                }
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


}
