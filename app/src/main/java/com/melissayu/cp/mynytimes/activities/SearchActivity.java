package com.melissayu.cp.mynytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melissayu.cp.mynytimes.Article;
import com.melissayu.cp.mynytimes.ArticleArrayAdapter;
import com.melissayu.cp.mynytimes.FilterFragment;
import com.melissayu.cp.mynytimes.FilterFragment.FilterDialogListener;
import com.melissayu.cp.mynytimes.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogListener {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    String sortBy;
    String beginDate;
    String newsDesk;


    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
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
        Toast.makeText(this, "Hi, " + sortByVal, Toast.LENGTH_SHORT).show();
        sortBy = sortByVal;
        beginDate = beginDateVal;
        newsDesk = newsDeskVal;
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", "28580fe0e09141ceaa0f57d3ab5eb653");
        requestParams.put("q", query);
        requestParams.put("page", 0);

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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());

                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d("DEBUG", articleJSONResults.toString());

                    adapter.addAll(Article.fromJSONArray(articleJSONResults));
                    Log.d("DEBUG", articles.toString());
                } catch (Exception e) {

                }
            }
        });
    }

}
