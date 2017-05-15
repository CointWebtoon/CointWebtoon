package com.kwu.cointwebtoon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

public class TypeKitActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //폰트 적용
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "SourceHanSansKR-Bold.ttf"))
                .addBold(Typekit.createFromAsset(this, "SourceHanSansKR-Heavy.ttf"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
