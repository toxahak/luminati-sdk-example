package github.nisrulz.texttospeech;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import io.topvpn.vpn_api.api;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech engine;

    EditText editText;
    Button btn;
    SeekBar seekbarPitch;

    float pitchVal = 0.0f;

    void start_luminati_sdk(Activity ctx){
        // setting up the SDK dialog
        api.set_tos_link("http://mysite.com/tos.html");
        api.set_btn_peer_txt(api.BTN_PEER_TXT.I_AGREE);
        api.set_btn_not_peer_txt(api.BTN_NOT_PEER_TXT.I_DISAGREE);
        // define the actions for handling cases of user selection
        api.set_selection_listener(new api.on_selection_listener(){
            public void on_user_selection(int choice){
                // Pseudocode for handling user selection (not mandatory, can
                // use api.get_user_selection directly instead)
                switch (choice){
                    case api.CHOICE_PEER:
                        // example code that will update local settings
                        Log.e("TTS", "CHOICE PEER!");
                        break;
                    case api.CHOICE_NOT_PEER:
                        // example code that will start
                        // a subscription process
                        Log.e("TTS", "CHOICE NOT PEER!");
                        break;
                    case api.CHOICE_NONE:
                        // example code that will update local settings
                        Log.e("TTS", "CHOICE NONE!");
                        break;
                }
            }
        });
        api.init(ctx); // should be called only after all api.set* were revoked
        api.popup(ctx, false); // popup will be shown once
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ensure calling start_luminati_sdk only once
        if (savedInstanceState!=null)
            return;
        start_luminati_sdk(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        engine = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = engine.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }

        });

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    speak(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(editText.getText().toString());
            }
        });


        seekbarPitch = (SeekBar) findViewById(R.id.seekBar);
        seekbarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pitchVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void speak(String text) {
        if (!engine.isSpeaking()) {
            engine.setPitch(pitchVal);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                engine.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (engine != null) {
            engine.stop();
            engine.shutdown();
        }

        super.onDestroy();
    }
}
