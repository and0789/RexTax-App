package com.task.dynamicregex.utils;

import com.task.dynamicregex.entities.CaseIdentity;
import com.task.dynamicregex.entities.Result;
import com.task.dynamicregex.entities.SocialMedia;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class Common {

    public static SocialMedia SOCIALMEDIA;
    public static File SELECTED_FILE;
    public static CaseIdentity CASE_IDENTITY;
    public static String HASH_CODE;
    public static ObservableList<Result> RESULTS = FXCollections.observableArrayList();
    public static int THREADS;
    public static int SELECTED_REGEX = 1;
    public static int RESULT_COUNT = 0;
    public static double ELAPSED_TIME;

}
