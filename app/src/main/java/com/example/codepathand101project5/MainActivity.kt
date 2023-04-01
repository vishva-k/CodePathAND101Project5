package com.example.codepathand101project5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import java.util.*
import com.google.gson.annotations.SerializedName

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadRandomPokemon()

        val refreshButton = findViewById<Button>(R.id.refreshButton)
        refreshButton.setOnClickListener { loadRandomPokemon() }
    }

    private fun loadRandomPokemon() {
        val randomId = Random().nextInt(898) + 1 // generate a random Pokemon ID between 1 and 898
        val url = "https://pokeapi.co/api/v2/pokemon/$randomId"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val gson = Gson()
                val pokemon = gson.fromJson(response.body!!.charStream(), Pokemon::class.java)

                runOnUiThread {
                    // update UI with Pokemon data
                    val nameTextView = findViewById<TextView>(R.id.nameTextView)
                    nameTextView.text = getString(R.string.name_label, pokemon.name)

                    val weightTextView = findViewById<TextView>(R.id.weightTextView)
                    weightTextView.text = getString(R.string.weight_label, pokemon.weight / 10.0)

                    val heightTextView = findViewById<TextView>(R.id.heightTextView)
                    heightTextView.text = getString(R.string.height_label, pokemon.height / 10.0)

                    val imageView = findViewById<ImageView>(R.id.imageView)
                    Picasso.get().load(pokemon.sprites.frontDefault).into(imageView)
                }
            }
        })
    }
}

data class Pokemon(
    val name: String,
    val weight: Int,
    val height: Int,
    @SerializedName("sprites") val sprites: PokemonSprites
)

data class PokemonSprites(
    @SerializedName("front_default") val frontDefault: String
)