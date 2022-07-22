package com.example.httpsconnection;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {
    SSLSocketFactory socketFactory = null;


    private SSLContext connect() {
        SSLContext sslContext = null;
        CertificateFactory certificateFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        try {
//            AssetManager am = this
            InputStream caInput = getAssets().open("cert.crt");
//            .open("kym.cer");
            BufferedInputStream bis = new BufferedInputStream(caInput);
            Log.d("caInput", String.valueOf(bis));


            Certificate ca = null;
            try {
                ca = certificateFactory.generateCertificate(bis);
                Log.d("Certificate +++++", String.valueOf(ca));
            } catch (Exception e) {
                Log.d(TAG, "connect: -----"+ e);
                e.printStackTrace();
            } finally {
                caInput.close();
                bis.close();
            }
            Log.d(TAG, "connect: holoooooo "+ ca);
            //Create a KeyStore containing trusted CAs
            String keyStoteType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoteType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            //Create a TrustManager that trusts tha CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            //Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            Log.d("debug", String.valueOf(sslContext));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        OkHttpClient client = new OkHttpClient();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            SSLContext sslContext = connect();
            URL url = new URL("https://ccspdev2.m2mlab.cdot.in/D1");
            HostnameVerifier hostnameVerifier = (hostname, session) -> {
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();
                // return hv.verify("test1.ceir.gov.in:8443", session);
                return true;
            };
        final String basicAuth = "Basic" + Base64.encodeToString("cms-key:password".getBytes(), Base64.NO_WRAP);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)
                    url.openConnection();
            httpsURLConnection.setHostnameVerifier(hostnameVerifier);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setUseCaches(false);
        httpsURLConnection.setRequestProperty("Authorization", basicAuth);
        Log.d("debug", basicAuth);
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            Log.d("debug", String.valueOf(httpsURLConnection));
            Log.d(TAG, "responsecode :" + httpsURLConnection.getResponseCode());
            InputStream inputStream=httpsURLConnection.getInputStream();
            Log.d(TAG, "onINput: "+inputStream);
//            OutputStream outputStream = httpsURLConnection.getOutputStream();
//            BufferedWriter bufferedWriter = new BufferedWriter(new
//                    OutputStreamWriter(outputStream, "UTF-8"));
//            JSONObject post_data_json = new JSONObject();
//        post_data_json.put("IMEInumber", IMEInumber);
//        post_data_json.put("UDID", myIMEI);
//            bufferedWriter.write(String.valueOf(post_data_json));
//            bufferedWriter.flush();
//            bufferedWriter.close();
//            outputStream.close();
        }
        catch (Exception e){
            Log.d(TAG, "onCreate: 2222"+ e);
        }







//        loadCertificateData();
//        KeyStore keyStore = null;
//        FileInputStream fis;
//        try {
//            keyStore = KeyStore.getInstance("PKCS12");
//            File file= new File("R.raw.cert");
////            fis = new FileInputStream(file);
////            keyStore.load(fis, "cdot@123".toCharArray());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(TAG, "onCreate: oh");
//        }



    }

//    private void loadCertificateData() {
//        try {
//            File[] pfxFiles = Environment.getExternalStorageDirectory().listFiles(new FileFilter() {
//                public boolean accept(File file) {
//                    Log.d(TAG, "accept: "+ file.getName());
//                    if (file.getName().toLowerCase().endsWith("p12")) {
//                        return true;
//                    }
//                    return false;
//                }
//            });
//
//            InputStream certificateStream = null;
////            if (pfxFiles.length>=1) {
//                certificateStream = new FileInputStream("pk.p12");
////            }
//            Log.d(TAG, "loadCertificateData: "+ certificateStream);
//
//
//            KeyStore keyStore = KeyStore.getInstance("PKCS12");
//            char[] password = "cdot@123".toCharArray();
//            keyStore.load(certificateStream, password);
//
//            System.out.println("I have loaded [" + keyStore.size() + "] certificates");
//
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, password);
//
////            socketFactory = new SSLSocketFactory(keyStore);
//            final String SSL_PROTOCOL = "TLS";
//            SSLContext sslContext=null;
//
//            try {
//                sslContext = SSLContext.getInstance(SSL_PROTOCOL);
//
//                // Initialize the context with your key manager and the default trust manager
//                // and randomness source
//                sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
//            } catch (NoSuchAlgorithmException e) {
//                Log.e(TAG, "Specified SSL protocol not supported! Protocol=" + SSL_PROTOCOL);
//                e.printStackTrace();
//            } catch (KeyManagementException e) {
//                Log.e(TAG, "Error setting up the SSL context!");
//                e.printStackTrace();
//            }
//// Get the socket factory
//            socketFactory = sslContext.getSocketFactory();
//        } catch (Exception e) {
//            // Actually a bunch of catch blocks here, but shortened!
//            Log.d(TAG, "loadCertificateData: oh god" +e );
//        }
//    }


}