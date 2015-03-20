package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.adapters.ConversationArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends ListActivity {

    private final Context _context = this;
    private List<Message> _conversation;
    private View _abv;
    private int _loadedMessages;
    private int _additionalMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        _abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(_abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        _abv.findViewById(R.id.action_bar_delete).setVisibility(View.VISIBLE);

        if (getIntent().hasExtra("conversation_title"))
            ((TextView) _abv.findViewById(R.id.action_bar_title)).setText(getIntent().getStringExtra("conversation_title"));

        if (!getIntent().hasExtra("conversation_id")) {
            Toast.makeText(_context, "Error loading conversation", Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(R.layout.activity_conversation);

        _loadedMessages = 0;
        _additionalMessages = 100;
        new HttpConversationGetter().execute(getIntent().getExtras().getInt("conversation_id"));
    }

    private void populate() {

        List<Message> reversed = new ArrayList<Message>();
        for (int i = 0; i < _conversation.size(); i++)
            reversed.add(_conversation.get(_conversation.size() - 1 - i));

        setListAdapter(new ConversationArrayAdapter(this, reversed));
        getListView().setSelection(getListAdapter().getCount() - 1);
    }

    public void sendMessage(View view) {
        // send message in the edit text
        Message sending = new Message();
        sending.setSender(DataManager.getCurUser());
        sending.setSentTime(System.currentTimeMillis());

        EditText et = (EditText) findViewById(R.id.send_message);
        sending.setBody(et.getText().toString());
        et.setText("");

        new HttpMessageSender().execute(sending);
    }

    public void deleteButton(View view) {

        AlertDialog.Builder adb = new AlertDialog.Builder(_context);
        adb
                .setTitle("Are you sure?")
                .setCancelable(true)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(_context, "Message deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        adb.create().show();
    }

    public void goBack(View view) {
        finish();
    }

    private class HttpConversationGetter extends AsyncTask<Integer, Void, Void> {

        private List<Message> lm;

        private void fetchConversation(int id) {

            // get the users
            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/messaging");
            url.append("/" + DataManager.getCurUser().getUsername());
            url.append("/" + DataManager.getCurUser().getUserId());
            url.append("/" + id);
            url.append("?size=" + _loadedMessages);
            url.append("&additional=" + _additionalMessages);

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                ObjectMapper om = new ObjectMapper();
                lm = om.readValue(jsonString, new TypeReference<List<Message>>() {});
            } catch (Exception e) {
                lm = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Integer... params) {
            fetchConversation(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (lm != null) {
                _conversation = lm;
                populate();
            }
            else
                Toast.makeText(_context, "Error retrieving messages", Toast.LENGTH_SHORT).show();
        }
    }

    private class HttpMessageSender extends AsyncTask<Message, Void, Void> {

        private Message message;

        private void message(Message m) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/messaging");
            url.append("/" + DataManager.getCurUser().getUsername());
            url.append("/" + DataManager.getCurUser().getUserId());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(m));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                message = om.readValue(jsonString, Message.class);
            } catch (Exception e) {
                message = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Message... params) {
            message(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (message == null) {
                Toast.makeText(_context, "Message not sent", Toast.LENGTH_LONG).show();

            }
        }
    }
}
