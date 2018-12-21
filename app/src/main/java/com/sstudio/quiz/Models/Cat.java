
package com.sstudio.quiz.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Cat {

    @SerializedName("trivia_categories")
    private List<TriviaCategory> mTriviaCategories;

    public List<TriviaCategory> getTriviaCategories() {
        return mTriviaCategories;
    }

    public void setTriviaCategories(List<TriviaCategory> triviaCategories) {
        mTriviaCategories = triviaCategories;
    }

}
