package com.wewant.moovon.newsfbsdk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wewant.moovon.newsfbsdk.R;
import com.wewant.moovon.newsfbsdk.adapter.CommentAdapter;
import com.wewant.moovon.newsfbsdk.manager.FbManager;
import com.wewant.moovon.newsfbsdk.model.CommentModel;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = CommentsActivity.class.getSimpleName();

    public static final String PARAM_OBJ_ID = "obj_id";

    private String objectId;

    private CommentAdapter mAdapter;
    private ListView mComments;
    private EditText newComment;
    private Button btnSend;

    public static void start(Context context, String objId) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(PARAM_OBJ_ID, objId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("intent is null");
        }
        objectId = intent.getStringExtra(PARAM_OBJ_ID);
        mComments = (ListView) findViewById(R.id.commentsList);
        newComment = (EditText) findViewById(R.id.newComment);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });

        buildCommentsList();
    }

    private void buildCommentsList() {
        mAdapter = new CommentAdapter(getApplicationContext());

        mComments.setAdapter(mAdapter);
        loadComments();
    }

    private void loadComments() {
        FbManager.getInstance(getApplicationContext()).loadLastComments(objectId, new FbManager.OnCommentsLoadListener() {
            @Override
            public void onSuccess(ArrayList<CommentModel> comments) {
                mAdapter.setObjects(comments);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void comment() {
        btnSend.setEnabled(false);
        FbManager.getInstance(getApplicationContext()).comment(
                objectId,
                newComment.getText().toString(),
                new Runnable() {
                    @Override
                    public void run() {
                        newComment.setText("");
                        loadComments();
                        btnSend.setEnabled(true);
                    }
                }
        );
    }
}
