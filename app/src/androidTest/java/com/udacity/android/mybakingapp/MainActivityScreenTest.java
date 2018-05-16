/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.udacity.android.mybakingapp;


import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.mybakingapp.Defines;
import com.udacity.mybakingapp.MainActivity;
import com.udacity.mybakingapp.R;
import com.udacity.mybakingapp.RecipeDetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test demos a user clicking on a GridView item in MenuActivity which opens up the
 * corresponding OrderActivity.
 *
 * This test does not utilize Idling Resources yet. If idling is set in the MenuActivity,
 * then this test will fail. See the IdlingResourcesTest for an identical test that
 * takes into account Idling Resources.
 */


@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {
    private static final String SECOND_STEP_SHORT_DESCRIPTION="2. Melt butter and bittersweet chocolate.";
    private static final String SECOND_CAKE_NAME = "Brownies";

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested will be launched before each test that's annotated
     * with @Test and before methods annotated with @Before. The activity will be terminated after
     * the test and methods annotated with @After are complete. This rule allows you to directly
     * access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    /**
     * Clicks on a GridView item and checks it opens up the OrderActivity with the correct details.
     */
    @Test
    public void TestMainActivity_Navigation() {

        //Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific gridview item and clicks it.
        // onData(anything()).inAdapterView(withId(R.id.rv_recipes_main)).atPosition(1).perform(click());
        //mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        onView(withId(R.id.fragment_main)).check(matches(isDisplayed()));
        onView(withId(R.id.rv_recipes_main)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.fragment_recipe_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_recipe_title)).check(matches(withText(SECOND_CAKE_NAME)));
        onView(withId(R.id.fragment_recipe_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_recipe_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.lv_Steps)).check(matches(isDisplayed()));
        onView(withId(R.id.lv_Steps)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.tv_step_title)).check(matches(withText(SECOND_STEP_SHORT_DESCRIPTION)));
        onView(withId(R.id.exoplayer)).check(matches(isDisplayed()));
    }
}