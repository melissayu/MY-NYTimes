package com.melissayu.cp.mynytimes.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melissayu.cp.mynytimes.Article;
import com.melissayu.cp.mynytimes.ArticleArrayAdapter;
import com.melissayu.cp.mynytimes.EndlessScrollListener;
import com.melissayu.cp.mynytimes.FilterFragment;
import com.melissayu.cp.mynytimes.FilterFragment.FilterDialogListener;
import com.melissayu.cp.mynytimes.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogListener {

    GridView gvResults;
//    Button btnSearch;
    TextView tvNetworkUnavailable;

    String sortBy="Newest"; // Newest by default
    String beginDate;
    String newsDesk;

    EndlessScrollListener esListener;
    int currentPage;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

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
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });

//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getArticles(true);
//            }
//        });

        esListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
//                loadNextDataFromApi(page);
//                Toast.makeText(getApplicationContext(), "Load more items!", Toast.LENGTH_SHORT).show();
                getArticles(false);

                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        };

        gvResults.setOnScrollListener(esListener);

        if (!isNetworkAvailable()) {
//            Toast.makeText(this, "No Network Available", Toast.LENGTH_LONG).show();
            tvNetworkUnavailable.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return true;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
                if (searchView.getQuery().length() == 0) {
                    getArticles(true);
                }
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
//        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.miFilter:
                filterClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            esListener.resetState();
        }

//        String query = etQuery.getText().toString();

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
                Log.d("DEBUG", "!!! onFailure:"+url);
                Log.d("DEBUG", "!!! onFailure:"+requestParams.toString());
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

                    adapter.addAll(Article.fromJSONArray(articleJSONResults));
                    Log.d("DEBUG", articles.toString());

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
