package com.example.encryption;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
private EditText edittext;
private ListView llistView;
private DatabaseReference databaseReference;
private String stringMessage;
private byte encryptionKey[]={9,115,51,86,105,4,-31,-23,-66,88,17,20,3,-105,119,-53};

private Cipher cipher,decipher;
private SecretKeySpec secretKeySpec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext=(EditText)findViewById(R.id.editTextTextMultiLine);
        llistView=(ListView)findViewById(R.id.listView);

        databaseReference= FirebaseDatabase.getInstance().getReference("Message");
        try {
            cipher=Cipher.getInstance("AES");
            decipher=Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec=new SecretKeySpec(encryptionKey,"AES");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringMessage = snapshot.getValue().toString();
                stringMessage = stringMessage.substring(1, stringMessage.length() - 1);
                String[] stringmessageArray = stringMessage.split(" ,");
                Arrays.sort(stringmessageArray);
                String[] stringfinal = new String[stringmessageArray.length * 2];
                try {
                    for (int i = 0; i < stringmessageArray.length; i++) {

                        String[] stringKeyValue = stringmessageArray[i].split("=", 2);
                        stringfinal[2 * i] = (String) android.text.format.DateFormat.format("dd-MM-YYYY hh:mm:ss", Long.parseLong(stringKeyValue[0]));
                        stringfinal[2 * i + 1] = AESDecription(stringKeyValue[1]);
                    }


            llistView.setAdapter(new ArrayAdapter<String>(MainActivity .this,android.R.layout.listView,stringfinal));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void SendButton(View view){

        Date d=new Date();
databaseReference.child(String.valueOf(d.getTime())).setValue(AESEncryption(edittext.getText().toString()));
edittext.setText("");
    }
    private String AESEncryption(String string){
byte[] stringByte =string.getBytes();
byte[] encryptedbyte=new byte[stringByte.length];

        try {
            cipher.init(cipher.ENCRYPT_MODE,secretKeySpec);
            encryptedbyte=cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String returnString=null;
        try {
           returnString=new String(encryptedbyte,"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;
    }
    private String AESDecription(String string) throws UnsupportedEncodingException{

            byte[] Encryptedbyte=string.getBytes("ISO-8859-1");
           String decryptedstring=string;
           byte[] decryption;
        try {
            decipher.init(cipher.DECRYPT_MODE,secretKeySpec);
            decryption=decipher.doFinal(Encryptedbyte);
            decryptedstring=new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedstring;

    }
}