
package com.sstudio.quiz.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Quiz {

    @SerializedName("response_code")
    private Long mResponseCode;
    @SerializedName("results")
    private List<Result> mResults;

    public Long getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(Long responseCode) {
        mResponseCode = responseCode;
    }

    public List<Result> getResults() {
        return mResults;
    }

    public void setResults(List<Result> results) {
        mResults = results;
    }

}
