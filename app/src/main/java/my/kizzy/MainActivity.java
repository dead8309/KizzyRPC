package my.kizzy;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity{
	EditText editText;
	Button start,stop;
	Context context;
	public static String token;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		editText=findViewById(R.id.token);
		start=findViewById(R.id.start);
		stop=findViewById(R.id.stop);

		start.setOnClickListener((View view)-> {
			token = editText.getText().toString();
			startService(new Intent(context, bgService.class));
		});
		stop.setOnClickListener((View view)-> stopService(new Intent(context,bgService.class)));

	}
}
