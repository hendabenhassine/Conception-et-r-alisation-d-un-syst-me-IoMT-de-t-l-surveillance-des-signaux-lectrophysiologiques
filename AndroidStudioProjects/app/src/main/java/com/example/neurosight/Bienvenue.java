package com.example.neurosight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Bienvenue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenue);

        // Référencer les vues

        TextView titleTextView = findViewById(R.id.welcome_title);
        TextView subtitleTextView = findViewById(R.id.welcome_subtitle);
        LinearLayout linearLayout = findViewById(R.id.linear_layout);

        // Modifier l'image en arrière-plan


        // Modifier l'espace en haut


        // Modifier le titre


        // Ajouter un espace vertical entre le titre et le sous-titre
        View verticalSpace = new View(this);
        verticalSpace.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 32));
        linearLayout.addView(verticalSpace);

        // Modifier le sous-titre


        Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Passer à la deuxième activité
                Intent intent = new Intent(Bienvenue.this, Authentification.class);
                startActivity(intent);
            }
        });

    }
}
