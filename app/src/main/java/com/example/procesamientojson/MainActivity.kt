package com.example.procesamientojson

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.procesamientojson.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException

import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var baseUrl = "https://pokeapi.co/api/v2/pokemon/"
    private lateinit var binding: ActivityMainBinding

    data class Pokemon (val name : String , val url : String)
    private lateinit var pokemons : ArrayList<Pokemon>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pokemons = ArrayList<Pokemon>()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(this, "Consulta iniciada", Toast.LENGTH_SHORT).show()

        binding.btnConsultar.setOnClickListener {
            obtenerPokemonsDesdeAPI { respuesta ->
                val pokemonArray = obtenerPokemonArray(respuesta)
                if (pokemonArray != null) {
                    pokemons.clear()
                    pokemons.addAll(pokemonArray)
                    // Mostrar la lista de nombres de los pokemons

                    var pokemonText = ""
                    for (pokemon in pokemonArray){
                        pokemonText += ObtenerTextoPokemon(pokemon);
                    }

                    binding.txtResolve.text = pokemonText
                } else {
                    binding.txtResolve.text = "Error al obtener los pokemons"
                }
            }
        }
    }

    private fun ObtenerTextoPokemon(pokemon: Pokemon) : String {
        return "Nombre: ${pokemon.name} \nURL: ${pokemon.url} \n"
    }

    private fun obtenerPokemonArray(json: String): ArrayList<Pokemon>? {
        return try {
            val jsonArray = JSONArray(json)
            val pokemonArray = ArrayList<Pokemon>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val url = jsonObject.getString("url")
                pokemonArray.add(Pokemon(name, url))
            }
            pokemonArray
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }

    fun obtenerPokemonsDesdeAPI(callback: (String) -> Unit) {
        val url = "https://pokeapi.co/api/v2/pokemon"
        var txtPokemon: String = ""
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val results = response.getJSONArray("results")
                txtPokemon = results.toString()
                Toast.makeText(this, "Consulta terminada", Toast.LENGTH_SHORT).show()
                callback(txtPokemon)
            },
            { error ->
                txtPokemon += error.toString()
                callback(txtPokemon)
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

}
