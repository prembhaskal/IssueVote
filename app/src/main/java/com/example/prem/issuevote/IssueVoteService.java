package com.example.prem.issuevote;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IssueVoteService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.prem.issuevote.action.FOO";
    private static final String ACTION_BAZ = "com.example.prem.issuevote.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.prem.issuevote.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.prem.issuevote.extra.PARAM2";

    private static final String DATA_NONCE_REGEX = ".*data-nonce=\"(.*?)\".*";
    private static final Pattern DATA_NONCE_MATCHER = Pattern.compile(DATA_NONCE_REGEX);

    private static final String TAG = IssueVoteService.class.getName();

    public IssueVoteService() {
        super("IssueVoteService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, IssueVoteService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, IssueVoteService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };


            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            String nonceId = getNonceId();

            Log.i(TAG, "Nonce id obtained : " + nonceId);

            URL url = new URL("https://cutebabes.in/wp-admin/admin-ajax.php");

            HttpsURLConnection myConn = (HttpsURLConnection) url.openConnection();
            myConn.setRequestMethod("POST");
            myConn.setConnectTimeout(15000);
            myConn.setReadTimeout(10000);
            myConn.setDoInput(true);
            myConn.setDoOutput(true);

            OutputStream outputStream = myConn.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String postData = "action=vote_for_photo&photo_id=1058&nonce_id=" + nonceId;
            Log.i(TAG, "postdata: " + postData);
            bufferedWriter.write(postData);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            myConn.connect();

            Log.i("IssueVoteService", "Connected to the site");

            InputStream inputStream = myConn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.i("IssueVoteService", "read data: " + line);
            }

            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        IssueDataStore.registerAlarmRun(this);
    }

    private String getNonceId() {
        try {
            URL url = new URL("https://cutebabes.in/april-2019/?contest=photo-detail&photo_id=1058");

            HttpsURLConnection myConn = (HttpsURLConnection) url.openConnection();
            myConn.setRequestMethod("GET");
            myConn.setConnectTimeout(15000);
            myConn.setReadTimeout(10000);
            myConn.setDoInput(true);
            myConn.setDoOutput(true);

            myConn.connect();

            Log.i("IssueVoteService", "Connected to the base page");

            InputStream inputStream = myConn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.i("IssueVoteService", "read site data: " + line);
                if (line.matches(".*data-nonce.*")) {
                    Log.i(TAG, "matching data nonce " + line);
                }
                Matcher matcher = DATA_NONCE_MATCHER.matcher(line);
                if (matcher.matches()) {
                    if (matcher.groupCount() > 0) {
                        String nonceId = matcher.group(1);
                        Log.i(TAG, "found data nonce id: "  + nonceId);
                        return nonceId;
                    }
                }
            }

            bufferedReader.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error getting nonce id", e);
        }

        return "";
    }

}
